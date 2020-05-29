package cn.lancedai.weye.server.service.record;

import cn.lancedai.weye.common.model.record.WarnRecord;
import cn.lancedai.weye.common.emuration.TimePattern;
import cn.lancedai.weye.common.tool.TimeTool;
import cn.lancedai.weye.server.DTO.FullWarnRecord;
import cn.lancedai.weye.server.VO.UnhandledWarnMsg;
import cn.lancedai.weye.server.VO.WarnMsg;
import cn.lancedai.weye.server.mapper.record.WarnRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarnRecordService extends ServiceImpl<WarnRecordMapper, WarnRecord> {

    public List<UnhandledWarnMsg> getUnhandledFullWarnRecords() {
        return getBaseMapper().getUnhandledFullWarnRecords().stream()
                .map(fullWarnRecord -> new UnhandledWarnMsg(
                                fullWarnRecord.getWarnRecordId(),
                                fullWarnRecordToUnhandledWarnMsg(fullWarnRecord)
                        )
                ).collect(Collectors.toList());
    }

    private String fullWarnRecordToUnhandledWarnMsg(FullWarnRecord record) {
        return String.format("[%s]--%s: %s",
                TimeTool.formatTimeStamp(record.getTimestamp(), TimePattern.HH_MM_SS),
                record.getWebAppName(),
                record.getWarnMsg()
        );
    }

    public Integer getUnhandledFullWarnRecordCount() {
        return baseMapper.getUnhandledFullWarnRecordCount();
    }

    public List<WarnMsg> getAllWarnMsgByWebAppId(int webAppId) {
        return baseMapper.getAllWarnMsgByWebAppId(webAppId);
    }
}
