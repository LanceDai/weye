package cn.lancedai.weye.server.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewServerInfo {
    private String apiKey;
    private String processCenterHost;
}
