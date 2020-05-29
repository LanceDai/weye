package cn.lancedai.weye.server.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarnMsg {
    private int id;
    private Timestamp timestamp;
    private String warnRuleName;
    private int warnRuleId;
    private int status;
    private String msg;
}
