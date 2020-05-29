package cn.lancedai.weye.server.kafka.consumer;

import cn.lancedai.weye.common.NTO.FullHttpRequestCollectorRecord;
import cn.lancedai.weye.common.model.record.BaseRecord;
import cn.lancedai.weye.common.model.record.CustomCommandCollectorRecord;
import cn.lancedai.weye.common.model.record.HttpRequestCollectorRecord;
import cn.lancedai.weye.common.model.record.ServerCollectorRecord;
import cn.lancedai.weye.common.model.rule.ComputeRule;
import cn.lancedai.weye.common.model.rule.WarnRule;
import cn.lancedai.weye.common.tool.StringTool;
import cn.lancedai.weye.server.service.ComputeRuleService;
import cn.lancedai.weye.server.service.WarnRuleService;
import cn.lancedai.weye.server.service.record.CustomCommandCollectorRecordService;
import cn.lancedai.weye.server.service.record.HttpRequestCollectorRecordService;
import cn.lancedai.weye.server.service.record.HttpRequestHeaderRecordService;
import cn.lancedai.weye.server.service.record.ServerCollectorRecordService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用来处理Agent发来的数据
 */
@Component
@Slf4j
public class DispatchConsumer {

    private final CustomCommandCollectorRecordService customCommandCollectorRecordService;
    private final ServerCollectorRecordService serverCollectorRecordService;
    private final HttpRequestCollectorRecordService httpRequestCollectorRecordService;
    private final HttpRequestHeaderRecordService httpRequestHeaderRecordService;
    private final ComputeRuleService computeRuleService;
    private final WarnRuleService warnRuleService;
    private final KafkaTemplate<String, BaseRecord> recordKafkaSender;

    public DispatchConsumer(
            CustomCommandCollectorRecordService customCommandCollectorRecordService,
            ServerCollectorRecordService serverCollectorRecordService,
            HttpRequestCollectorRecordService httpRequestCollectorRecordService,
            HttpRequestHeaderRecordService httpRequestHeaderRecordService,
            ComputeRuleService computeRuleService, WarnRuleService warnRuleService, KafkaTemplate<String, BaseRecord> recordKafkaSender) {
        this.customCommandCollectorRecordService = customCommandCollectorRecordService;
        this.serverCollectorRecordService = serverCollectorRecordService;
        this.httpRequestCollectorRecordService = httpRequestCollectorRecordService;
        this.httpRequestHeaderRecordService = httpRequestHeaderRecordService;
        this.computeRuleService = computeRuleService;
        this.warnRuleService = warnRuleService;
        this.recordKafkaSender = recordKafkaSender;
    }


    @KafkaListener(topics = {"AGENT-RECORD"}, groupId = "RECORD-CONSUMER")
    public void consumer(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            kafkaMessage.ifPresent(msg -> {
                if (msg instanceof ServerCollectorRecord) {
                    handleServerRecord((ServerCollectorRecord) msg);
                } else if (msg instanceof FullHttpRequestCollectorRecord) {
                    handleHttpRequestRecord((FullHttpRequestCollectorRecord) msg);
                } else if (msg instanceof CustomCommandCollectorRecord) {
                    handleCustomCommandCollectorRecord((CustomCommandCollectorRecord) msg);
                } else {
                    log.warn("unknown record type: {}", record.getClass().getName());
                }
            });
        } else {
            log.debug("empty value");
        }
    }

    // 用来发送给Flink作业, 现在直接入库
    private void handleCustomCommandCollectorRecord(CustomCommandCollectorRecord msg) {
        log.debug("custom command record: {}", msg);
        // 判断是否有后置处理, 发送给对应的topic
        if (hasNextHandler("command", msg.getSubscribeId())) {
            String topic = createSourceTopic(CustomCommandCollectorRecord.class, msg.getSubscribeId());
            recordKafkaSender.send(topic, msg);
        }
        //再入库
        customCommandCollectorRecordService.save(msg);
    }

    private void handleHttpRequestRecord(FullHttpRequestCollectorRecord msg) {
        log.debug("http record: {}", msg);
        // 判断是否有后置处理, 发送给对应的topic
        if (hasNextHandler("http", msg.httpRequestCollectorRecord().getWebAppId())) {
            String topic = createSourceTopic(HttpRequestCollectorRecord.class, msg.httpRequestCollectorRecord().getWebAppId());
            recordKafkaSender.send(topic, msg);
        }
        //再入库
        val httpRequestCollectorRecord = msg.httpRequestCollectorRecord();
        val httpRequestHeaders = msg.httpRequestHeaderRecords();
        httpRequestCollectorRecordService.save(httpRequestCollectorRecord);
        httpRequestHeaderRecordService.saveOrUpdateBatch(
                Arrays.stream(httpRequestHeaders)
                        .peek(httpRequestHeader -> httpRequestHeader.setHttpRequestCollectorRecordId(httpRequestCollectorRecord.getId()))
                        .collect(Collectors.toList())
        );
    }


    private void handleServerRecord(ServerCollectorRecord msg) {
//        log.debug("server record: {}", msg);
        // 判断是否有后置处理, 发送给对应的topic
        if (hasNextHandler("server", msg.getServerId())) {
            String topic = createSourceTopic(ServerCollectorRecord.class, msg.getServerId());
            recordKafkaSender.send(topic, msg);
        }
        //再入库
        serverCollectorRecordService.save(msg);
    }


    private boolean hasNextHandler(String sourceType, int sourceId) {
        int computeHandlerNum = computeRuleService
                .lambdaQuery().eq(ComputeRule::getSourceType, sourceType)
                .eq(ComputeRule::getSourceId, sourceId).count();
        int warnHandlerNum = warnRuleService
                .lambdaQuery().eq(WarnRule::getSourceType, sourceType)
                .eq(WarnRule::getSourceId, sourceId).count();
//        log.debug("sourceType:{}, " +
//                        "sourceId:{}, " +
//                        "compute handler num: {}, " +
//                        "warn handler num: {}",
//                sourceId,
//                sourceId,
//                computeHandlerNum,
//                warnHandlerNum
//        );
        return computeHandlerNum > 0 || warnHandlerNum > 0;
    }

    private <T extends BaseRecord> String createSourceTopic(Class<T> sourceClass, int sourceId) {
        return String.format("%s-%d", StringTool.humpToChar(sourceClass.getSimpleName(), "-"), sourceId);
    }

}
