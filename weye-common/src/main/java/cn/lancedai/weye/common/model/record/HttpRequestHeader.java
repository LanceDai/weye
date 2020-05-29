package cn.lancedai.weye.common.model.record;

import cn.lancedai.weye.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class HttpRequestHeader extends BaseModel {
    private int httpRequestCollectorRecordId;
    private String headerName;
    private String headerValue;

    public HttpRequestHeader(int httpRequestRecordCollectorId, String headerName, String headerValue) {
        this.httpRequestCollectorRecordId = httpRequestRecordCollectorId;
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    public HttpRequestHeader(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }
}
