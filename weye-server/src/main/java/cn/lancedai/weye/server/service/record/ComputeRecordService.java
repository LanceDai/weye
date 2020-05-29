package cn.lancedai.weye.server.service.record;

import cn.lancedai.weye.common.model.record.ComputeRecord;
import cn.lancedai.weye.server.VO.ComputeData;
import cn.lancedai.weye.server.mapper.record.ComputeRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComputeRecordService extends ServiceImpl<ComputeRecordMapper, ComputeRecord> {
    public List<ComputeData> getAllComputeRecordByWebAppId(int webAppId) {
        return baseMapper.getAllComputeRecordByWebAppId(webAppId);
    }
}
