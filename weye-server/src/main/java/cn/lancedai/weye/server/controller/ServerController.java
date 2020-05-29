package cn.lancedai.weye.server.controller;

import cn.lancedai.weye.common.model.Server;
import cn.lancedai.weye.common.model.ServerInfo;
import cn.lancedai.weye.common.model.record.ServerCollectorRecord;
import cn.lancedai.weye.common.exception.SQLExecuteErrorException;
import cn.lancedai.weye.common.tool.NetWorkTool;
import cn.lancedai.weye.common.tool.TimeTool;
import cn.lancedai.weye.server.VO.*;
import cn.lancedai.weye.server.service.ServerInfoService;
import cn.lancedai.weye.server.service.ServerService;
import cn.lancedai.weye.server.service.WebAppService;
import cn.lancedai.weye.server.service.record.ServerCollectorRecordService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/server", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServerController {

    private final ServerService serverService;
    private final ServerInfoService serverInfoService;
    private final ServerCollectorRecordService serverCollectorRecordService;
    private final WebAppService webAppService;

    public ServerController(ServerService serverService, ServerInfoService serverInfoService, ServerCollectorRecordService serverCollectorRecordService, WebAppService webAppService) {
        this.serverService = serverService;
        this.serverInfoService = serverInfoService;
        this.serverCollectorRecordService = serverCollectorRecordService;
        this.webAppService = webAppService;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<List<Server>> getAllServer() {
        return ResponseEntity.ok(serverService.list());
    }

    @GetMapping("/detail/{serverId}")
    @Transactional
    public ResponseEntity<FullServerInfo> getServerById(@PathVariable int serverId) {
        Server server = serverService.getById(serverId);
        ServerInfo serverInfo = serverInfoService.lambdaQuery().eq(ServerInfo::getServerId, serverId).one();
        return ResponseEntity.ok(FullServerInfo.apply(server, serverInfo));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<NewServerInfo> addServer(@RequestBody Server server) throws Exception {
        val ip = server.getIp();
        val name = server.getName();
        if (StringUtils.isEmpty(ip) || (!NetWorkTool.isIPv4(ip) && !NetWorkTool.isIPv6(ip))) {
            throw new Exception("please input a valid ip");
        }
        if (StringUtils.isEmpty(name)) {
            server.setName(ip);
        }
        server.setApiKey(UUID.randomUUID().toString());
        server.setAgentStatus(3);
        return ResponseEntity.ok(serverService.saveWithException(server));
    }

    @DeleteMapping("/{serverId}")
    @Transactional
    public ResponseEntity<String> deleteServer(@PathVariable int serverId) throws SQLExecuteErrorException {
        serverService.removeById(serverId);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Transactional
    public ResponseEntity<String> updateServer(@RequestBody Server server) {
        serverService.saveOrUpdate(server);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/webApps/{serverId}")
    @Transactional
    public ResponseEntity<List<WebAppInfo>> getWebAppInfoByServerId(@PathVariable("serverId") int serverId) {
        return ResponseEntity.ok(webAppService.getWebAppInfosByServerId(serverId));
    }

    /**
     * 返回可选项，用于服务器的监控图表
     */
    @GetMapping("/options")
    @Transactional
    public ResponseEntity<List<String>> getServerOptions() {
        return ResponseEntity.ok(Arrays.stream(ServerCollectorRecord.class.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .map(Field::getName)
                .filter(name ->
                        !"timestamp".equalsIgnoreCase(name)
                                && !"id".equalsIgnoreCase(name)
                                && !"serverId".equalsIgnoreCase(name)
                ).collect(Collectors.toList())
        );
    }

    @GetMapping("/record/{serverId}/{item}")
    @Transactional
    public ResponseEntity<ChartData> getChartDataByItem(@PathVariable int serverId, @PathVariable String item) {
        val recordList = serverCollectorRecordService
                .lambdaQuery()
                .eq(ServerCollectorRecord::getServerId, serverId)
                .orderByDesc(ServerCollectorRecord::getTimestamp)
                .last("limit 10")
                .list();
        // 处理timestamp
        val dateTimeFormatter = TimeTool.createFormatsByTimestampList(recordList.stream()
                .map(ServerCollectorRecord::getTimestamp)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(createChartDate(recordList, item, dateTimeFormatter));
    }

    @GetMapping("/dataRangeRecord/{serverId}/{item}")
    @Transactional
    public ResponseEntity<ChartData> getChartDataByItem(@PathVariable int serverId, @PathVariable String item, long startDate, long endDate) {
        val startDateTimestamp = new Timestamp(startDate);
        val endDateTimestamp = new Timestamp(endDate);
        val recordList = serverCollectorRecordService
                .lambdaQuery()
                .eq(ServerCollectorRecord::getServerId, serverId)
                .gt(ServerCollectorRecord::getTimestamp, startDateTimestamp)
                .lt(ServerCollectorRecord::getTimestamp, endDateTimestamp)
                .orderByDesc(ServerCollectorRecord::getTimestamp)
                .list();
        val dateTimeFormatter = TimeTool.createFormatsByTimestampList(Arrays.asList(startDateTimestamp, endDateTimestamp));
        return ResponseEntity.ok(createChartDate(recordList, item, dateTimeFormatter));
    }

    private ChartData createChartDate(List<ServerCollectorRecord> recordList, String item, DateTimeFormatter dateFormat) {

        val chartItemList = recordList.stream()
                .map(record -> new ChartItem(
                        TimeTool.formatTimeStamp(record.getTimestamp(), dateFormat),
                        getItem(record, item)
                )).collect(Collectors.toList());
        Collections.reverse(chartItemList);
        val columns = new ArrayList<String>();
        columns.add("timestamp");
        columns.add("data");
        return new ChartData(columns, chartItemList);
    }

    private Double getItem(ServerCollectorRecord obj, String item) {
        try {
            val fields = ServerCollectorRecord.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().equalsIgnoreCase(item) &&
                        (field.getType().isPrimitive() ||
                                Number.class.isAssignableFrom(field.getType())
                        )
                ) {
                    return Double.valueOf(field.get(obj).toString());
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
