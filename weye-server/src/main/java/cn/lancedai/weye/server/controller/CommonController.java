package cn.lancedai.weye.server.controller;

import cn.lancedai.weye.common.exception.UserLoginErrorException;
import cn.lancedai.weye.common.tool.StoreTool;
import cn.lancedai.weye.common.tool.StringTool;
import cn.lancedai.weye.common.tool.TimeTool;
import cn.lancedai.weye.server.config.ServerConfig;
import cn.lancedai.weye.server.enumerate.Property;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 公共接口，以及总体数据查询方法
 */
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Data
public class CommonController {
    private final ServerConfig serverConfig;
    private final ConcurrentHashMap<Property, String> serverProperties;


    @Autowired
    public CommonController(ConcurrentHashMap<Property, String> serverProperties, ServerConfig serverConfig) {
        this.serverProperties = serverProperties;
        this.serverConfig = serverConfig;
    }


    /**
     * 登录
     *
     * @param digest 由用户名和密码组成的数字签名
     * @return 响应
     */
    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestParam String digest) {
        //获取用户名， 密码 加密结果， 比较是否一致
        val res = serverProperties.get(Property.DIGEST);
        if (res.equals(digest)) {
            val token = StringTool.generateRandomValue();
            StoreTool.set(Property.TOKEN.name(), token,
                    TimeTool.stringToDuration(serverConfig.getExpirationTime())
            );
            return ResponseEntity.ok(token);
        } else {
            throw new UserLoginErrorException();
        }
    }

    /**
     * 登录状态判断
     * 本质是个空方法， 如果能通过filter， 那说明已登录
     *
     * @return 响应
     */
    @PostMapping(value = "/isLogin")
    public ResponseEntity<String> isLogin() {
        log.debug("is Login");
        return ResponseEntity.ok().build();
    }

    /**
     * 登出
     */
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        StoreTool.clear(Property.TOKEN.name());
        log.debug("user is login out");
        return ResponseEntity.ok().build();
    }

}
