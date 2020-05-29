package cn.lancedai.weye.common.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WebApp extends BaseModel {
    private String name;
    private int port;
    private int serverId;
}
