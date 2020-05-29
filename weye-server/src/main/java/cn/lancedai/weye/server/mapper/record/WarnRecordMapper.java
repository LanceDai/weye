package cn.lancedai.weye.server.mapper.record;

import cn.lancedai.weye.common.model.record.WarnRecord;
import cn.lancedai.weye.server.DTO.FullWarnRecord;
import cn.lancedai.weye.server.VO.WarnMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WarnRecordMapper extends BaseMapper<WarnRecord> {

    List<FullWarnRecord> getUnhandledFullWarnRecords();

    List<FullWarnRecord> getUnhandledFullWarnRecordsWithNum(@Param("num") int num);

    Integer getUnhandledFullWarnRecordCount();

    List<WarnMsg> getAllWarnMsgByWebAppId(int webAppId);
}
