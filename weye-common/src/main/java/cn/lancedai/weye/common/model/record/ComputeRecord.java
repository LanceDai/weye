package cn.lancedai.weye.common.model.record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计算任务结果
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ComputeRecord extends BaseRecord {
    private int computeRuleId;
    private double data;

    public ComputeRecord(int computeRuleId, double data) {
        this.computeRuleId = computeRuleId;
        this.data = data;
    }

    // 解决scala混编 lombok不起效的问题

    public ComputeRecord() {
    }

    public int getComputeRuleId() {
        return computeRuleId;
    }

    public void setComputeRuleId(int computeRuleId) {
        this.computeRuleId = computeRuleId;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }
}
