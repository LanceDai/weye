package cn.lancedai.weye.server.VO;

import cn.lancedai.weye.common.model.Server;
import cn.lancedai.weye.common.model.ServerInfo;
import cn.lancedai.weye.common.tool.StringTool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullServerInfo {
    private int id;
    private String name;
    private String ip;
    private int agentStatus;
    private String apiKey;
    private String processorInfo;
    private String systemVersion;
    private String systemFamily;
    private int bitness;
    /**
     * normalize
     */
    private String totalMem;

    public static FullServerInfo apply(Server server, ServerInfo serverInfo) {
        return FullServerInfo.builder()
                .id(server.getId())
                .name(server.getName())
                .ip(server.getIp())
                .agentStatus(server.getAgentStatus())
                .apiKey(server.getApiKey())
                .processorInfo(serverInfo.getProcessorInfo())
                .systemFamily(serverInfo.getSystemFamily())
                .systemVersion(serverInfo.getSystemVersion())
                .totalMem(StringTool.normalizedByte(serverInfo.getTotalMem()))
                .bitness(serverInfo.getBitness())
                .build();
    }
}
