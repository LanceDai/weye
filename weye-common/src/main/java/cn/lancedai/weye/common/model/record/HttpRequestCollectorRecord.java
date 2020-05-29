package cn.lancedai.weye.common.model.record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * web应用的请求数据
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class HttpRequestCollectorRecord extends BaseRecord {
    private int webAppId;
    private String requestUrl;
    private String requestMethod;
    private String requestIp;
    private int srcPort;
    private String httpVersion;


    public HttpRequestCollectorRecord(int webAppId, String requestUrl, String requestMethod, String requestIp, int srcPort, String httpVersion) {
        this.webAppId = webAppId;
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
        this.requestIp = requestIp;
        this.srcPort = srcPort;
        this.httpVersion = httpVersion;
    }
}
