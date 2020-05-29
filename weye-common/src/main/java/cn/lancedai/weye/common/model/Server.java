package cn.lancedai.weye.common.model;

import lombok.*;

/**
 * 用户所有的服务器
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Server extends BaseModel {
    /**
     * 默认与IP一致
     */
    private String name;
    private String ip;
    /**
     * Agent状态
     */
    private int agentStatus;

    /**
     * 唯一标志， 用来确定Agnet与Server之间的对应关系
     */
    private String apiKey;

    // 再加一堆配置信息

}
