package cn.lancedai.weye.common.model.record;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 执行任务结果
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CustomCommandCollectorRecord extends BaseRecord {
    private int subscribeId;
    private double data;

    public CustomCommandCollectorRecord(int subscribeId, double data) {
        this.subscribeId = subscribeId;
        this.data = data;
    }
}
