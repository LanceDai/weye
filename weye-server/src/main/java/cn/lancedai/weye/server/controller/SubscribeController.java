package cn.lancedai.weye.server.controller;

import cn.lancedai.weye.common.model.CustomCommand;
import cn.lancedai.weye.common.model.relation.CustomCommandSubscribe;
import cn.lancedai.weye.server.VO.SubscribeCommandInfo;
import cn.lancedai.weye.server.service.CustomCommandService;
import cn.lancedai.weye.server.service.CustomCommandSubscribeService;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 使WEB应用订阅相关内容， 当前只实现自定义命令
 */
@RestController
@RequestMapping(value = "subscribe", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubscribeController {

    private final CustomCommandSubscribeService customCommandSubscribeService;
    private final CustomCommandService customCommandService;

    public SubscribeController(CustomCommandSubscribeService customCommandSubscribeService, CustomCommandService customCommandService) {
        this.customCommandSubscribeService = customCommandSubscribeService;
        this.customCommandService = customCommandService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Integer> subscribe(@RequestBody CustomCommandSubscribe subscribe) {
        customCommandSubscribeService.save(subscribe);
        return ResponseEntity.ok(subscribe.getId());
    }

    @GetMapping
    public ResponseEntity<List<SubscribeCommandInfo>> getCommandOptions(@RequestParam int webAppId) {

        return ResponseEntity.ok(customCommandSubscribeService.getSubscribeCommandInfos(webAppId));
    }

    @GetMapping("/all/{webAppId}")
    public ResponseEntity<List<SubscribeCommandInfo>> getSubscribeCommandByWebAppId(@PathVariable int webAppId) {
        return ResponseEntity.ok(customCommandSubscribeService
                .lambdaQuery()
                .eq(CustomCommandSubscribe::getWebAppId, webAppId)
                .list()
                .stream()
                .map(subscribe -> new SubscribeCommandInfo(subscribe.getId(), customCommandService.getById(subscribe.getCustomCommandId())))
                .collect(Collectors.toList())
        );
    }

    @DeleteMapping("/{subscribeId}")
    @Transactional
    public ResponseEntity<String> cancelSubScribe(@PathVariable int subscribeId) {
        customCommandSubscribeService.removeById(subscribeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/others/{webAppId}")
    public ResponseEntity<List<CustomCommand>> getOtherSubscribeCommandByWebAppId(@PathVariable int webAppId) {
        val subscribeList = customCommandSubscribeService
                .lambdaQuery()
                .eq(CustomCommandSubscribe::getWebAppId, webAppId)
                .list().stream()
                .map(CustomCommandSubscribe::getCustomCommandId)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customCommandService
                .list()
                .stream()
                .filter(command -> !subscribeList.contains(command.getId()))
                .collect(Collectors.toList())
        );
    }
}
