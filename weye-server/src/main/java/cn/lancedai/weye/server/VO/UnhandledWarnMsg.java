package cn.lancedai.weye.server.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 未处理的报警信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnhandledWarnMsg {
    private int id;
    private String msg;
}
