package cn.lancedai.weye.server.config;

import lombok.Data;

@Data
public class FlinkServerConfig {
    private String sshHost;
    private int sshPort;
    private String username;
    private String password;
    private String executePath;
    private String jobDownloadUrl;
}
