package cn.lancedai.weye.server.service.record;

import cn.lancedai.weye.common.model.record.HttpRequestHeader;
import cn.lancedai.weye.server.mapper.record.HttpRequestHeaderRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HttpRequestHeaderRecordService extends ServiceImpl<HttpRequestHeaderRecordMapper, HttpRequestHeader> {
}
