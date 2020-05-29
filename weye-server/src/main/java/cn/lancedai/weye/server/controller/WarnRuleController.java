package cn.lancedai.weye.server.controller;

import cn.lancedai.weye.common.exception.SQLExecuteErrorException;
import cn.lancedai.weye.common.model.record.WarnRecord;
import cn.lancedai.weye.common.model.rule.WarnRule;
import cn.lancedai.weye.server.VO.WarnMsg;
import cn.lancedai.weye.server.service.WarnRuleService;
import cn.lancedai.weye.server.service.WebAppService;
import cn.lancedai.weye.server.service.record.WarnRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/warnRule", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class WarnRuleController {
    private final WarnRuleService warnRuleService;
    private final WarnRecordService warnRecordService;
    private final WebAppService webAppService;

    @Autowired
    public WarnRuleController(WarnRuleService warnRuleService, WarnRecordService warnRecordService, WebAppService webAppService) {
        this.warnRuleService = warnRuleService;
        this.warnRecordService = warnRecordService;
        this.webAppService = webAppService;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<List<WarnRule>> getAllWarnRule() {
        return ResponseEntity.ok(warnRuleService.list());
    }

    @GetMapping("/all/{webAppId}")
    @Transactional
    public ResponseEntity<List<WarnRule>> getAllWarnRuleByWebAppId(@PathVariable int webAppId) {
        return ResponseEntity.ok(warnRuleService.lambdaQuery().eq(WarnRule::getWebAppId, webAppId).list());
    }

    @GetMapping("/{warnRuleId}")
    @Transactional
    public ResponseEntity<WarnRule> getWarnRuleById(@PathVariable int warnRuleId) {
        return ResponseEntity.ok(warnRuleService.getById(warnRuleId));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Integer> addWarnRule(@RequestBody WarnRule warnRule) throws Exception {
        if ("server".equalsIgnoreCase(warnRule.getSourceType())) {
            warnRule.setSourceId(webAppService.getById(warnRule.getWebAppId()).getServerId());
        } else if ("http".equalsIgnoreCase(warnRule.getSourceType())) {
            warnRule.setSourceId(warnRule.getWebAppId());
        } else {
            warnRule.setCompareItem("data");
        }
        warnRuleService.saveWithException(warnRule);
        return ResponseEntity.ok(warnRule.getId());
    }

    @DeleteMapping("/{warnRuleId}")
    @Transactional
    public ResponseEntity<String> deleteWarnRule(@PathVariable int warnRuleId) throws SQLExecuteErrorException {
        warnRuleService.removeById(warnRuleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Transactional
    public ResponseEntity<String> updateWarnRule(@RequestBody WarnRule warnRule) {
        warnRuleService.saveOrUpdate(warnRule);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/record/all/{webAppId}")
    @Transactional
    public ResponseEntity<List<WarnMsg>> getAllWarnMsgByWebAppId(@PathVariable int webAppId) {
        return ResponseEntity.ok(warnRecordService.getAllWarnMsgByWebAppId(webAppId));
    }

    @PostMapping("/record/handle/{recordId}")
    @Transactional
    public ResponseEntity<List<WarnMsg>> handleWarnMsg(@PathVariable int recordId) {
        warnRecordService.lambdaUpdate().set(WarnRecord::getStatus, 1).update();
        return ResponseEntity.ok().build();
    }
}
