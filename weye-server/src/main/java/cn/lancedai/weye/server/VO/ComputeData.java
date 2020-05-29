package cn.lancedai.weye.server.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComputeData {
    private int recordId;
    private Double data;
    private Timestamp timestamp;
    private int computeRuleId;
    private String computeRuleName;
}
