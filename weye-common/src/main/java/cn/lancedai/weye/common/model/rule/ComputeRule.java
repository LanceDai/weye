package cn.lancedai.weye.common.model.rule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计算规则
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ComputeRule extends BaseRule {
    @TableId(type = IdType.AUTO)
    private int id;

    private String name;

    /**
     * 数据源类型
     * 1. serverSource 包含服务器信息
     * 2. webAppSource 包含Http请求信息
     * 3. ExecuteSource 自定义执行命令
     * 4. ComputeSource 计算数据源
     */
    private String sourceType;
    /**
     * 数据来源
     * 数据源类型
     * 1. serverId
     * 2. webAppId
     * 3. ExecuteCommandSourceId
     * 4. ComputeRuleId
     */
    private int sourceId;

    // 筛选条件
    /**
     * 筛选项
     */
    private String filterItem;
    /**
     * 筛选操作
     * 1. >
     * 2. >=
     * 3. <
     * 4. <=
     * 4. =
     */
    private String filterOp;

    /**
     * 筛选比较值
     */
    private String filterValue;
    /**
     * 数据项 1,2 是选择数据源数据的其中一项
     * 3，4 是选择多个中的一个
     */
    private String item;

    /**
     * 聚合操作
     * 1. count
     * 2. avg
     * 3. min
     * 4. max
     * 5. sum
     */
    private String op;
    // 时间量与时间单位共同组成时间范围
    // 如 1 hour -> 1小时
    /**
     * 时间量
     */
    private Long time;
    /**
     * 时间单位
     * 1. d
     * 2. h
     * 3. ms
     */
    private String timeUnit;

    public ComputeRule(String name, String sourceType, int sourceId, String filterItem, String filterOp, String filterValue, String item, String op, Long time, String timeUnit, int webAppId) {
        this.name = name;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.filterItem = filterItem;
        this.filterOp = filterOp;
        this.filterValue = filterValue;
        this.item = item;
        this.op = op;
        this.time = time;
        this.timeUnit = timeUnit;
        this.webAppId = webAppId;
    }

    // 解决与scala混编时， lombok不起效的问题

    public ComputeRule(int id, String name, String sourceType, int sourceId, String filterItem, String filterOp, String filterValue, String item, String op, Long time, String timeUnit, int webAppId) {
        this.id = id;
        this.name = name;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.filterItem = filterItem;
        this.filterOp = filterOp;
        this.filterValue = filterValue;
        this.item = item;
        this.op = op;
        this.time = time;
        this.timeUnit = timeUnit;
        this.webAppId = webAppId;
    }

    public ComputeRule() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getFilterItem() {
        return filterItem;
    }

    public void setFilterItem(String filterItem) {
        this.filterItem = filterItem;
    }

    public String getFilterOp() {
        return filterOp;
    }

    public void setFilterOp(String filterOp) {
        this.filterOp = filterOp;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getWebAppId() {
        return webAppId;
    }

    public void setWebAppId(int webAppId) {
        this.webAppId = webAppId;
    }
}
