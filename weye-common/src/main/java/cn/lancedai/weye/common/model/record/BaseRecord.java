package cn.lancedai.weye.common.model.record;

import cn.lancedai.weye.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author 60384
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseRecord extends BaseModel {
    protected Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
    // 解决scala混编 lombok不起效的问题

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
