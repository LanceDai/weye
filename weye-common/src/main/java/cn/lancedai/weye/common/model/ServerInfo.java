package cn.lancedai.weye.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerInfo extends BaseModel {
    private int serverId;
    private String processorInfo;
    private String systemVersion;
    private String systemFamily;
    private int bitness;
    private long totalMem;

    public ServerInfo(int serverId, String processorInfo, String systemVersion, String systemFamily, int bitness, long totalMem) {
        this.serverId = serverId;
        this.processorInfo = processorInfo;
        this.systemVersion = systemVersion;
        this.systemFamily = systemFamily;
        this.bitness = bitness;
        this.totalMem = totalMem;
    }

    public ServerInfo() {
    }


    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getProcessorInfo() {
        return processorInfo;
    }

    public void setProcessorInfo(String processorInfo) {
        this.processorInfo = processorInfo;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getSystemFamily() {
        return systemFamily;
    }

    public void setSystemFamily(String systemFamily) {
        this.systemFamily = systemFamily;
    }

    public int getBitness() {
        return bitness;
    }

    public void setBitness(int bitness) {
        this.bitness = bitness;
    }

    public long getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(long totalMem) {
        this.totalMem = totalMem;
    }
}
