package cn.lancedai.weye.server.mapper.record;

import cn.lancedai.weye.common.model.record.CustomCommandCollectorRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CustomCommandCollectorRecordMapper extends BaseMapper<CustomCommandCollectorRecord> {
}
