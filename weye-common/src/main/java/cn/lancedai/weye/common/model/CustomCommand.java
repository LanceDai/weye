package cn.lancedai.weye.common.model;

import lombok.*;

/**
 * 一条命令一个值
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomCommand extends BaseModel {
    private String name;
    private String command;
    private String duration;
}
