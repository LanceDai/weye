package cn.lancedai.weye.server.controller;

import cn.lancedai.weye.common.exception.SQLExecuteErrorException;
import cn.lancedai.weye.common.model.rule.ComputeRule;
import cn.lancedai.weye.server.VO.ComputeData;
import cn.lancedai.weye.server.service.ComputeRuleService;
import cn.lancedai.weye.server.service.WebAppService;
import cn.lancedai.weye.server.service.record.ComputeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/computeRule", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ComputeRuleController {
    private final ComputeRuleService computeRuleService;
    private final ComputeRecordService computeRecordService;
    private final WebAppService webAppService;

    @Autowired
    public ComputeRuleController(ComputeRuleService computeRuleService, ComputeRecordService computeRecordService, WebAppService webAppService) {
        this.computeRuleService = computeRuleService;
        this.computeRecordService = computeRecordService;
        this.webAppService = webAppService;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<List<ComputeRule>> getAllComputeRule() {
        return ResponseEntity.ok(computeRuleService.list());
    }

    @GetMapping("/all/{webAppId}")
    @Transactional
    public ResponseEntity<List<ComputeRule>> getAllComputeRuleByWebAppId(@PathVariable int webAppId) {
        return ResponseEntity.ok(computeRuleService.lambdaQuery().eq(ComputeRule::getWebAppId, webAppId).list());
    }

    @GetMapping("/{compureRuleId}")
    @Transactional
    public ResponseEntity<ComputeRule> getComputeRuleById(@PathVariable int compureRuleId) {
        return ResponseEntity.ok(computeRuleService.getById(compureRuleId));
    }

    @PostMapping
    public ResponseEntity<Integer> addComputeRule(@RequestBody ComputeRule computeRule) throws Exception {
        if ("server".equalsIgnoreCase(computeRule.getSourceType())) {
            computeRule.setSourceId(webAppService.getById(computeRule.getWebAppId()).getServerId());
        } else if ("http".equalsIgnoreCase(computeRule.getSourceType())) {
            computeRule.setSourceId(computeRule.getWebAppId());
        } else {
            computeRule.setItem("data");
        }

        computeRuleService.saveWithException(computeRule);
        return ResponseEntity.ok(computeRule.getId());
    }

    @DeleteMapping("/{computeRuleId}")
    @Transactional
    public ResponseEntity<String> deleteComputeRule(@PathVariable int computeRuleId) throws SQLExecuteErrorException {
        computeRuleService.removeById(computeRuleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Transactional
    public ResponseEntity<String> updateComputeRule(@RequestBody ComputeRule computeRule) {
        computeRuleService.saveOrUpdate(computeRule);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ComputeRule>> getComputeOptions(@RequestParam int webAppId) {
        return ResponseEntity.ok(computeRuleService.lambdaQuery().eq(ComputeRule::getWebAppId, webAppId).list());
    }

    // 计算结果
    @GetMapping("/record/all/{webAppId}")
    public ResponseEntity<List<ComputeData>> getAllComputeDataByWebAppId(@PathVariable int webAppId) {
        return ResponseEntity.ok(computeRecordService.getAllComputeRecordByWebAppId(webAppId));
    }
}
