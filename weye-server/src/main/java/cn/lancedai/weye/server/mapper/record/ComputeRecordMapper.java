package cn.lancedai.weye.server.mapper.record;

import cn.lancedai.weye.common.model.record.ComputeRecord;
import cn.lancedai.weye.server.VO.ComputeData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 60384
 */
@Mapper
@Repository
public interface ComputeRecordMapper extends BaseMapper<ComputeRecord> {
    List<ComputeData> getAllComputeRecordByWebAppId(int webAppId);
}
