package cn.lancedai.weye.server.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全订阅属性，包含订阅的WEB_APP所在服务器，订阅的自定义命令，执行频率
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullSubscribe {
    private int subscribeId;
    private int serverId;
    private String command;
    private String duration;
}
