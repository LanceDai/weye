package cn.lancedai.weye.common.model.record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 默认数据源， 即服务器的基本信息
 * 服务器产生数据， 每10秒采集一次
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServerCollectorRecord extends BaseRecord {

    private int serverId;
    /**
     * CPU使用率
     */
    private double cpuUsage;
    /**
     * 内存使用率
     */
    private double memUsage;
    /**
     * 线程数
     */
    private int threadCount;
    /**
     * CPU温度
     */
    private double cpuTemperature;

    /**
     * 上下文交换次数
     */
    private Long contextSwitches;
    /**
     * 中断次数
     */
    private Long interrupts;

    /**
     * 下载速率
     */
    private Long recvSpeed;
    /**
     * 上传速率
     */
    private Long sendSpeed;

    public ServerCollectorRecord(int serverId, double cpuUsage, double memUsage, int threadCount, double cpuTemperature, Long contextSwitches, Long interrupts, Long recvSpeed, Long sendSpeed) {
        this.serverId = serverId;
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
        this.threadCount = threadCount;
        this.cpuTemperature = cpuTemperature;
        this.contextSwitches = contextSwitches;
        this.interrupts = interrupts;
        this.recvSpeed = recvSpeed;
        this.sendSpeed = sendSpeed;
    }

    public ServerCollectorRecord() {
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(double memUsage) {
        this.memUsage = memUsage;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public double getCpuTemperature() {
        return cpuTemperature;
    }

    public void setCpuTemperature(double cpuTemperature) {
        this.cpuTemperature = cpuTemperature;
    }

    public Long getContextSwitches() {
        return contextSwitches;
    }

    public void setContextSwitches(Long contextSwitches) {
        this.contextSwitches = contextSwitches;
    }

    public Long getInterrupts() {
        return interrupts;
    }

    public void setInterrupts(Long interrupts) {
        this.interrupts = interrupts;
    }

    public Long getRecvSpeed() {
        return recvSpeed;
    }

    public void setRecvSpeed(Long recvSpeed) {
        this.recvSpeed = recvSpeed;
    }

    public Long getSendSpeed() {
        return sendSpeed;
    }

    public void setSendSpeed(Long sendSpeed) {
        this.sendSpeed = sendSpeed;
    }
}
