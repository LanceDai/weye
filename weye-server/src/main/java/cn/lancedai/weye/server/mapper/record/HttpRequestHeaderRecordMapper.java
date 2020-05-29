package cn.lancedai.weye.server.mapper.record;

import cn.lancedai.weye.common.model.record.HttpRequestHeader;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface HttpRequestHeaderRecordMapper extends BaseMapper<HttpRequestHeader> {
}
