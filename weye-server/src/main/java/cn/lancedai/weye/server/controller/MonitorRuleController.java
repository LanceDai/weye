package cn.lancedai.weye.server.controller;

import cn.lancedai.weye.common.model.record.ComputeRecord;
import cn.lancedai.weye.common.model.record.CustomCommandCollectorRecord;
import cn.lancedai.weye.common.model.record.HttpRequestCollectorRecord;
import cn.lancedai.weye.common.model.record.ServerCollectorRecord;
import cn.lancedai.weye.common.model.rule.MonitorRule;
import cn.lancedai.weye.common.tool.TimeTool;
import cn.lancedai.weye.server.VO.ChartData;
import cn.lancedai.weye.server.VO.ChartItem;
import cn.lancedai.weye.server.service.MonitorRuleService;
import cn.lancedai.weye.server.service.WebAppService;
import cn.lancedai.weye.server.service.record.ComputeRecordService;
import cn.lancedai.weye.server.service.record.CustomCommandCollectorRecordService;
import cn.lancedai.weye.server.service.record.HttpRequestCollectorRecordService;
import cn.lancedai.weye.server.service.record.ServerCollectorRecordService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/monitorRule", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class MonitorRuleController {
    private final MonitorRuleService monitorRuleService;
    private final WebAppService webAppService;
    private final ServerCollectorRecordService serverCollectorRecordService;
    private final HttpRequestCollectorRecordService httpRequestCollectorRecordService;
    private final CustomCommandCollectorRecordService customCommandCollectorRecordService;
    private final ComputeRecordService computeRecordService;

    @Autowired
    public MonitorRuleController(MonitorRuleService monitorRuleService, WebAppService webAppService, ServerCollectorRecordService serverCollectorRecordService, HttpRequestCollectorRecordService httpRequestCollectorRecordService, CustomCommandCollectorRecordService customCommandCollectorRecordService, ComputeRecordService computeRecordService) {
        this.monitorRuleService = monitorRuleService;
        this.webAppService = webAppService;
        this.serverCollectorRecordService = serverCollectorRecordService;
        this.httpRequestCollectorRecordService = httpRequestCollectorRecordService;
        this.customCommandCollectorRecordService = customCommandCollectorRecordService;
        this.computeRecordService = computeRecordService;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<List<MonitorRule>> getAllMonitorRule() {
        return ResponseEntity.ok(monitorRuleService.list());
    }

    @GetMapping("/all/{webAppId}")
    @Transactional
    public ResponseEntity<List<MonitorRule>> getAllMonitorRuleByWebAppId(@PathVariable int webAppId) {
        return ResponseEntity.ok(monitorRuleService.lambdaQuery().eq(MonitorRule::getWebAppId, webAppId).list());
    }

    @GetMapping("/{monitorRuleId}")
    @Transactional
    public ResponseEntity<MonitorRule> getMonitorRuleById(@PathVariable int monitorRuleId) {
        return ResponseEntity.ok(monitorRuleService.getById(monitorRuleId));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Integer> addMonitorRule(@RequestBody MonitorRule monitorRule) throws Exception {
        if ("server".equalsIgnoreCase(monitorRule.getSourceType())) {
            monitorRule.setSourceId(webAppService.getById(monitorRule.getWebAppId()).getServerId());
        } else if ("http".equalsIgnoreCase(monitorRule.getSourceType())) {
            monitorRule.setSourceId(monitorRule.getWebAppId());
        } else {
            monitorRule.setItem("data");
        }

        if (StringUtils.isEmpty(monitorRule.getOutputDataName())) {
            monitorRule.setOutputDataName(monitorRule.getName());
        }
        monitorRuleService.save(monitorRule);
        return ResponseEntity.ok(monitorRule.getId());
    }

    @DeleteMapping("/{monitorRuleId}")
    @Transactional
    public ResponseEntity<String> deleteMonitorRule(@PathVariable int monitorRuleId) {
        monitorRuleService.removeById(monitorRuleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Transactional
    public ResponseEntity<String> updateMonitorRule(@RequestBody MonitorRule monitorRule) {
        monitorRuleService.saveOrUpdate(monitorRule);
        return ResponseEntity.ok().build();
    }


    @Data
    @AllArgsConstructor
    static class RecordItem {
        private Timestamp timestamp;
        private Double data;
    }

    /**
     * 实时数据展示
     *
     * @param monitorRuleId 监控规则Id
     * @return 图表数据集
     */
    @GetMapping("/record/{monitorRuleId}")
    @Transactional
    public ResponseEntity<ChartData> getMonitorChartData(@PathVariable int monitorRuleId) {
        val monitorRule = monitorRuleService.getById(monitorRuleId);
        List<RecordItem> recordItemList;
        if ("server".equalsIgnoreCase(monitorRule.getSourceType())) {
            recordItemList = serverCollectorRecordService.lambdaQuery()
                    .eq(ServerCollectorRecord::getServerId, monitorRule.getSourceId())
                    .orderByDesc(ServerCollectorRecord::getTimestamp)
                    .last("limit 10")
                    .list()
                    .stream()
                    .map(record -> new RecordItem(record.getTimestamp(), getItem(ServerCollectorRecord.class, record, monitorRule.getItem())))
                    .collect(Collectors.toList());
        } else if ("http".equalsIgnoreCase(monitorRule.getSourceType())) {
            recordItemList = httpRequestCollectorRecordService.lambdaQuery()
                    .eq(HttpRequestCollectorRecord::getWebAppId, monitorRule.getSourceId())
                    .orderByDesc(HttpRequestCollectorRecord::getTimestamp)
                    .last("limit 10")
                    .list()
                    .stream()
                    .map(record -> new RecordItem(record.getTimestamp(), getItem(HttpRequestCollectorRecord.class, record, monitorRule.getItem())))
                    .collect(Collectors.toList());
        } else if ("command".equalsIgnoreCase(monitorRule.getSourceType())) {
            recordItemList = customCommandCollectorRecordService.lambdaQuery()
                    .eq(CustomCommandCollectorRecord::getSubscribeId, monitorRule.getSourceId())
                    .orderByDesc(CustomCommandCollectorRecord::getTimestamp)
                    .last("limit 10")
                    .list()
                    .stream()
                    .map(record -> new RecordItem(record.getTimestamp(), getItem(CustomCommandCollectorRecord.class, record, monitorRule.getItem())))
                    .collect(Collectors.toList());
        } else {
            // compute 数据
            recordItemList = computeRecordService.lambdaQuery()
                    .eq(ComputeRecord::getComputeRuleId, monitorRule.getSourceId())
                    .orderByDesc(ComputeRecord::getTimestamp)
                    .last("limit 10")
                    .list()
                    .stream()
                    .map(record -> new RecordItem(record.getTimestamp(), getItem(ComputeRecord.class, record, monitorRule.getItem())))
                    .collect(Collectors.toList());
        }

        // 处理timestamp
        val dateTimeFormatter = TimeTool.createFormatsByTimestampList(recordItemList.stream()
                .map(RecordItem::getTimestamp)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(createChartDate(recordItemList, dateTimeFormatter));
    }

    @GetMapping("/dataRangeRecord/{monitorRuleId}")
    @Transactional
    public ResponseEntity<ChartData> getChartDataByItem(@PathVariable int monitorRuleId, long startDate, long endDate) {
        val startDateTimestamp = new Timestamp(startDate);
        val endDateTimestamp = new Timestamp(endDate);
        val monitorRule = monitorRuleService.getById(monitorRuleId);
        List<RecordItem> recordItemList;
        if ("server".equalsIgnoreCase(monitorRule.getSourceType())) {
            recordItemList = serverCollectorRecordService.lambdaQuery()
                    .eq(ServerCollectorRecord::getServerId, monitorRule.getSourceId())
                    .gt(ServerCollectorRecord::getTimestamp, startDateTimestamp)
                    .lt(ServerCollectorRecord::getTimestamp, endDateTimestamp)
                    .orderByDesc(ServerCollectorRecord::getTimestamp)
                    .last("limit 10")
                    .list()
                    .stream()
                    .map(record -> new RecordItem(record.getTimestamp(), getItem(ServerCollectorRecord.class, record, monitorRule.getItem())))
                    .collect(Collectors.toList());
        } else if ("http".equalsIgnoreCase(monitorRule.getSourceType())) {
            recordItemList = httpRequestCollectorRecordService.lambdaQuery()
                    .eq(HttpRequestCollectorRecord::getWebAppId, monitorRule.getSourceId())
                    .gt(HttpRequestCollectorRecord::getTimestamp, startDateTimestamp)
                    .lt(HttpRequestCollectorRecord::getTimestamp, endDateTimestamp)
                    .orderByDesc(HttpRequestCollectorRecord::getTimestamp)
                    .last("limit 10")
                    .list()
                    .stream()
                    .map(record -> new RecordItem(record.getTimestamp(), getItem(HttpRequestCollectorRecord.class, record, monitorRule.getItem())))
                    .collect(Collectors.toList());
        } else if ("command".equalsIgnoreCase(monitorRule.getSourceType())) {
            recordItemList = customCommandCollectorRecordService.lambdaQuery()
                    .eq(CustomCommandCollectorRecord::getSubscribeId, monitorRule.getSourceId())
                    .gt(CustomCommandCollectorRecord::getTimestamp, startDateTimestamp)
                    .lt(CustomCommandCollectorRecord::getTimestamp, endDateTimestamp)
                    .orderByDesc(CustomCommandCollectorRecord::getTimestamp)
                    .last("limit 10")
                    .list()
                    .stream()
                    .map(record -> new RecordItem(record.getTimestamp(), getItem(CustomCommandCollectorRecord.class, record, monitorRule.getItem())))
                    .collect(Collectors.toList());
        } else {
            // compute 数据
            recordItemList = computeRecordService.lambdaQuery()
                    .eq(ComputeRecord::getComputeRuleId, monitorRule.getSourceId())
                    .gt(ComputeRecord::getTimestamp, startDateTimestamp)
                    .lt(ComputeRecord::getTimestamp, endDateTimestamp)
                    .orderByDesc(ComputeRecord::getTimestamp)
                    .last("limit 10")
                    .list()
                    .stream()
                    .map(record -> new RecordItem(record.getTimestamp(), getItem(ComputeRecord.class, record, monitorRule.getItem())))
                    .collect(Collectors.toList());
        }
        val dateTimeFormatter = TimeTool.createFormatsByTimestampList(Arrays.asList(startDateTimestamp, endDateTimestamp));
        return ResponseEntity.ok(createChartDate(recordItemList, dateTimeFormatter));
    }

    private ChartData createChartDate(List<RecordItem> recordItemList, DateTimeFormatter dateFormat) {

        val chartItemList = recordItemList.stream().map(
                record -> new ChartItem(
                        TimeTool.formatTimeStamp(record.getTimestamp(), dateFormat),
                        record.getData()
                )).collect(Collectors.toList());

        val columns = new ArrayList<String>();
        columns.add("timestamp");
        columns.add("data");
        return new ChartData(columns, normalize(chartItemList));
    }

    // normalize 解决时间戳重复问题, 并对其进行倒置
    private List<ChartItem> normalize(List<ChartItem> chartItemList) {
        List<ChartItem> resList = new ArrayList<>();
        int start = 0, end = 1, len = chartItemList.size();
        ChartItem startItem = chartItemList.get(0);
        ChartItem endItem;
        double sum = startItem.getData();
        while (end < len) {
            endItem = chartItemList.get(end);
            if (endItem.getTimestamp().equals(startItem.getTimestamp())) {
                sum += endItem.getData();
            } else {
                resList.add(new ChartItem(startItem.getTimestamp(), sum / (end - start + 1)));
                start = end;
                startItem = endItem;
                sum = endItem.getData();
            }
            end++;
        }
        resList.add(new ChartItem(startItem.getTimestamp(), sum / (len - start + 1)));
        Collections.reverse(resList);
        return resList;
    }

    private <T, R extends Number> R getItem(Class<T> clazz, T obj, String item) {
        try {
            val fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().equalsIgnoreCase(item) &&
                        (field.getType().isPrimitive() ||
                                Number.class.isAssignableFrom(field.getType())
                        )
                ) {
                    return (R) field.get(obj);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        log.error("unknown item {}", item);
        return null;
    }
}
