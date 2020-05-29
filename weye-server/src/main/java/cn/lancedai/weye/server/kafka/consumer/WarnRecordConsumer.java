package cn.lancedai.weye.server.kafka.consumer;

import cn.lancedai.weye.common.model.record.*;
import cn.lancedai.weye.common.model.rule.WarnRule;
import cn.lancedai.weye.server.service.WarnRuleService;
import cn.lancedai.weye.server.service.record.ComputeRecordService;
import cn.lancedai.weye.server.service.record.CustomCommandCollectorRecordService;
import cn.lancedai.weye.server.service.record.HttpRequestCollectorRecordService;
import cn.lancedai.weye.server.service.record.ServerCollectorRecordService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class WarnRecordConsumer {
    private final WarnRuleService warnRuleService;
    private final ServerCollectorRecordService serverCollectorRecordService;
    private final HttpRequestCollectorRecordService httpRequestCollectorRecordService;
    private final CustomCommandCollectorRecordService customCommandCollectorRecordService;
    private final ComputeRecordService computeRecordService;

    public WarnRecordConsumer(WarnRuleService warnRuleService, ServerCollectorRecordService serverCollectorRecordService, HttpRequestCollectorRecordService httpRequestCollectorRecordService, CustomCommandCollectorRecordService customCommandCollectorRecordService, ComputeRecordService computeRecordService) {
        this.warnRuleService = warnRuleService;
        this.serverCollectorRecordService = serverCollectorRecordService;
        this.httpRequestCollectorRecordService = httpRequestCollectorRecordService;
        this.customCommandCollectorRecordService = customCommandCollectorRecordService;
        this.computeRecordService = computeRecordService;
    }

    @KafkaListener(topics = {"WARN-RECORD"}, groupId = "WARN-RECORD-CONSUMER")
    public void consumer(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            kafkaMessage.ifPresent(msg -> {
                if (msg instanceof WarnRecord) {
                    val warnRecord = (WarnRecord) msg;
                    int warnRuleId = warnRecord.getWarnRuleId();
                    val warnRule = warnRuleService.getById(warnRuleId);
                    val warnMsg = createWarnMsg(warnRecord, warnRule);
                    switch (warnRule.getWarnWay().toLowerCase()) {
                        case "email":
                            sendWarnEmail(warnRule, warnMsg);
                            break;
                        case "webhook":
                            sendWarnWebHook(warnRule, warnMsg);
                            break;
                        default:
                            log.debug("unknown warnWay");
                    }
                }
            });
        }
    }

    private void sendWarnWebHook(WarnRule warnRule, String warnMsg) {
        log.debug("send {} to {} webHook", warnMsg, warnRule.getWarnUrl());
    }

    private void sendWarnEmail(WarnRule warnRule, String warnMsg) {
        log.debug("send {} to {} Email", warnMsg, warnRule.getWarnUrl());
    }

    private String createWarnMsg(WarnRecord warnRecord, WarnRule warnRule) {
        switch (warnRule.getSourceType().toLowerCase()) {
            case "server": {
                ServerCollectorRecord record = serverCollectorRecordService.getById(warnRecord.getRecordId());
                return createWarnMsg(ServerCollectorRecord.class, record, warnRule);
            }
            case "http": {
                HttpRequestCollectorRecord record = httpRequestCollectorRecordService.getById(warnRecord.getRecordId());
                return createWarnMsg(HttpRequestCollectorRecord.class, record, warnRule);
            }
            case "compute": {
                ComputeRecord record = computeRecordService.getById(warnRecord.getRecordId());
                return createWarnMsg(ComputeRecord.class, record, warnRule);
            }
            case "command": {
                CustomCommandCollectorRecord record = customCommandCollectorRecordService.getById(warnRecord.getRecordId());
                return createWarnMsg(CustomCommandCollectorRecord.class, record, warnRule);
            }
            default:
                log.debug("unknown sourceType: {}", warnRule.getSourceType());
                return "";
        }
    }

    private <T extends BaseRecord> String createWarnMsg
            (Class<T> clazz,
             T record,
             WarnRule warnRule) {
        Map<String, String> jsonMap = new HashMap<>(2);
        // 发生时间
        jsonMap.put("time", record.getTimestamp().toLocalDateTime().toString());
        // 情况阐述
        jsonMap.put("reason",
                String.format("%s %s %s: %s", getItem(
                        clazz, record, warnRule.getCompareItem()),
                        warnRule.getCompareOp(),
                        warnRule.getCompareValue(),
                        warnRule.getWarnMsg()
                )
        );
        return JSON.toJSONString(jsonMap);
    }

    private <T> String getItem(Class<T> clazz, T obj, String item) {
        try {
            Field field = clazz.getDeclaredField(item);
            field.setAccessible(true);
            return String.valueOf(field.get(obj));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }
}
