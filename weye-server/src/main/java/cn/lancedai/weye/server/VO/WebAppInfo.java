package cn.lancedai.weye.server.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebAppInfo {
    private int id;
    private String name;
    private int port;
    private int warnMsgNum;
    private int agentStatus;
    private int serverId;
    private String serverIp;
    private String serverName;
}
