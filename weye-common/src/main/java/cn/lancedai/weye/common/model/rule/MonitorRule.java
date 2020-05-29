package cn.lancedai.weye.common.model.rule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorRule extends BaseRule {
    @TableId(type = IdType.AUTO)
    private int id;


    private String name;
    /**
     * 数据源类型
     * 1. server 包含服务器信息
     * 2. http Http请求信息
     * 3. command 自定义执行命令
     * 4. compute 计算数据源
     */
    private String sourceType;

    /**
     * 数据来源
     * 数据源类型
     * 1. serverId
     * 2. webAppId
     * 3. customCommandSourceId
     * 4. computeRuleId
     */
    private int sourceId;

    /**
     * 数据项 1,2 是选择数据源数据的其中一项
     * 3，4 是选择多个中的一个
     */
    private String item;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 输出数据名字
     */
    private String outputDataName;
}
