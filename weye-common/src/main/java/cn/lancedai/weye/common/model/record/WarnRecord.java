package cn.lancedai.weye.common.model.record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报警信息自由组装
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WarnRecord extends BaseRecord {
    private int warnRuleId;
    private int recordId;
    // 有无解决
    private int status;

    public WarnRecord(int warnRuleId, int recordId) {
        this.warnRuleId = warnRuleId;
        this.recordId = recordId;
        this.status = 0;
    }

    public WarnRecord() {
    }


    public int getWarnRuleId() {
        return warnRuleId;
    }

    public void setWarnRuleId(int warnRuleId) {
        this.warnRuleId = warnRuleId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
