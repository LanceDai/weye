package cn.lancedai.weye.server.controller;

import cn.lancedai.weye.common.model.WebApp;
import cn.lancedai.weye.common.model.record.HttpRequestCollectorRecord;
import cn.lancedai.weye.server.VO.UnhandledWarnMsg;
import cn.lancedai.weye.server.VO.WebAppInfo;
import cn.lancedai.weye.server.service.WebAppService;
import cn.lancedai.weye.server.service.record.WarnRecordService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/webApp", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebAppController {

    private final WebAppService webAppService;
    private final WarnRecordService warnRecordServer;

    public WebAppController(WebAppService webAppService, WarnRecordService warnRecordServer) {
        this.webAppService = webAppService;
        this.warnRecordServer = warnRecordServer;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<List<WebApp>> getAllWebApps() {
        return ResponseEntity.ok(webAppService.list());
    }

    @GetMapping("/{webAppId}")
    @Transactional
    public ResponseEntity<WebApp> getWebAppById(@PathVariable int webAppId) {
        return ResponseEntity.ok(webAppService.getById(webAppId));
    }

    /**
     * @param webApp 包含 webApp名字 运行端口 服务器ID
     * @return 新增加的WebAppID
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Integer> addWebApp(@RequestBody WebApp webApp) throws Throwable {
        webAppService.saveWithException(webApp);
        return ResponseEntity.ok(webApp.getId());
    }

    @DeleteMapping("/{webAppId}")
    @Transactional
    public ResponseEntity<String> deleteWebApp(@PathVariable int webAppId) throws Throwable {
        webAppService.removeById(webAppId);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Transactional
    public ResponseEntity<String> updateWebApp(@RequestBody WebApp webApp) {
        webAppService.saveOrUpdate(webApp);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/infos")
    @Transactional
    public ResponseEntity<List<WebAppInfo>> webAppInfos() {
        return ResponseEntity.ok(webAppService.getWebAppInfos());
    }
    @GetMapping("/detail/{webAppId}")
    @Transactional
    public ResponseEntity<WebAppInfo> webAppDetail(@PathVariable int webAppId) {
        return ResponseEntity.ok(webAppService.getWebAppDetailById(webAppId));
    }

    /**
     * @return 正常运行的WebApp的数目, 判断依据是有没有对应的告警信息
     */
    @GetMapping("/success")
    @Transactional
    public ResponseEntity<Integer> successWebApp() {
        return ResponseEntity.ok(webAppService.getSuccessWebAppCounts());
    }

    /**
     * @return 异常运行的WebApp的数目, 有异常信息的WEB应用的个数
     */
    @GetMapping("/warn")
    @Transactional
    public ResponseEntity<Integer> wrongWebApp() {
        return ResponseEntity.ok(webAppService.getWarnWebAppCounts());
    }


    // 总体未处理告警信息获取
    @GetMapping("/allWarnMsg")
    public ResponseEntity<List<UnhandledWarnMsg>> getAllUnhandledWarnMsg() {
        return ResponseEntity.ok(warnRecordServer.getUnhandledFullWarnRecords());
    }

    @GetMapping("/options")
    @Transactional
    public ResponseEntity<List<String>> getHttpOptions(){
        return ResponseEntity.ok(Arrays.stream(HttpRequestCollectorRecord.class.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .map(Field::getName)
                .filter(name ->
                        !"timestamp".equalsIgnoreCase(name)
                                && !"id".equalsIgnoreCase(name)
                                && !"webAppId".equalsIgnoreCase(name)
                ).collect(Collectors.toList())
        );
    }
}
