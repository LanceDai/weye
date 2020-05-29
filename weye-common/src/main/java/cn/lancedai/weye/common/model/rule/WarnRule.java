package cn.lancedai.weye.common.model.rule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WarnRule extends BaseRule {
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 告警规则名
     * 用于构建告警消息
     */
    private String name;

    /**
     * 数据源类型
     * 1. server包含服务器信息
     * 2. web 包含Http请求信息
     * 3. command 自定义执行命令
     * 4. compute 计算数据源
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


    // 比较条件
    /**
     * 数据项 1,2 是选择数据源数据的其中一项
     * 3，4 是选择多个中的一个
     */
    private String compareItem;
    /**
     * 比较操作
     * 1. >
     * 2. >=
     * 3. <
     * 4. <=
     * 4. =
     */
    private String compareOp;

    /**
     * 筛选比较值
     */
    private String compareValue;

    /**
     * 告警方式
     * 1. mail
     * 2. webhook
     */
    private String warnWay;

    /**
     * 告警链接
     * 1. mail => 邮箱地址
     * 2. webHook => web url
     */
    private String warnUrl;
    /**
     * 告警内容
     */
    private String warnMsg;

    // 解决与scala混编时， lombok不起效的问题

    public WarnRule(int id, String name, String sourceType, int sourceId, String compareItem, String compareOp, String compareValue, String warnWay, String warnUrl, String warnMsg) {
        this.id = id;
        this.name = name;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.compareItem = compareItem;
        this.compareOp = compareOp;
        this.compareValue = compareValue;
        this.warnWay = warnWay;
        this.warnUrl = warnUrl;
        this.warnMsg = warnMsg;
    }

    public WarnRule() {
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

    public String getCompareItem() {
        return compareItem;
    }

    public void setCompareItem(String compareItem) {
        this.compareItem = compareItem;
    }

    public String getCompareOp() {
        return compareOp;
    }

    public void setCompareOp(String compareOp) {
        this.compareOp = compareOp;
    }

    public String getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(String compareValue) {
        this.compareValue = compareValue;
    }

    public String getWarnWay() {
        return warnWay;
    }

    public void setWarnWay(String warnWay) {
        this.warnWay = warnWay;
    }

    public String getWarnUrl() {
        return warnUrl;
    }

    public void setWarnUrl(String warnUrl) {
        this.warnUrl = warnUrl;
    }

    public String getWarnMsg() {
        return warnMsg;
    }

    public void setWarnMsg(String warnMsg) {
        this.warnMsg = warnMsg;
    }
}
