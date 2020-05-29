package cn.lancedai.weye.common.model.relation;

import cn.lancedai.weye.common.model.BaseModel;
import lombok.*;

/**
 * 订阅数据源
 * 只执行一次， 由Actor端保证
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomCommandSubscribe extends BaseModel {
    private int webAppId;
    private int customCommandId;
}
