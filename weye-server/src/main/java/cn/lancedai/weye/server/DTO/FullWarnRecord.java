package cn.lancedai.weye.server.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullWarnRecord {
    private int warnRecordId;
    private Timestamp timestamp;
    private String webAppName;
    private String warnRuleName;
    private String warnMsg;
}
