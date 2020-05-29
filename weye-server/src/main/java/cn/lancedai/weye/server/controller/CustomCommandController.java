package cn.lancedai.weye.server.controller;

import cn.lancedai.weye.common.model.CustomCommand;
import cn.lancedai.weye.server.service.CustomCommandService;
import cn.lancedai.weye.server.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/customCommand/", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CustomCommandController {

    private final CustomCommandService customCommandService;

    private final ServerService serverService;

    public CustomCommandController(CustomCommandService customCommandService, ServerService serverService) {
        this.customCommandService = customCommandService;
        this.serverService = serverService;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<List<CustomCommand>> getAllCustomCommand() {
        return ResponseEntity.ok(customCommandService.list());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Integer> addCustomCommand(@RequestBody CustomCommand command) {
        //持久化记录至数据库
        customCommandService.save(command);
        return ResponseEntity.ok(command.getId());
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Integer> updateCustomCommand(@RequestBody CustomCommand command) {
        //持久化记录至数据库
        customCommandService.updateById(command);
        return ResponseEntity.ok(command.getId());
    }

    @PostMapping("/test")
    @Transactional
    public ResponseEntity<String> commandTest(int serverId, String command) throws Throwable {
        return ResponseEntity.ok(serverService.testCommand(serverId, command));
    }

    @DeleteMapping("/{customCommandId}")
    @Transactional
    public ResponseEntity<String> deleteCustomCommand(@PathVariable int customCommandId) {
        customCommandService.removeById(customCommandId);
        return ResponseEntity.ok().build();
    }
}
