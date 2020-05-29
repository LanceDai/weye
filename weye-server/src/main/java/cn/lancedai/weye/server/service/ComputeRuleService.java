package cn.lancedai.weye.server.service;

import cn.lancedai.weye.common.exception.SQLExecuteErrorException;
import cn.lancedai.weye.common.exception.UnKnownSourceTypeException;
import cn.lancedai.weye.common.model.rule.ComputeRule;
import cn.lancedai.weye.server.mapper.ComputeRuleMapper;
import cn.lancedai.weye.server.util.CommonUtils;
import cn.lancedai.weye.server.util.FlinkUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ComputeRuleService extends ServiceImpl<ComputeRuleMapper, ComputeRule> {
    private final FlinkUtils flinkUtils;

    public ComputeRuleService(FlinkUtils flinkUtils) {
        this.flinkUtils = flinkUtils;
    }

    public void saveWithException(ComputeRule entity) throws SQLExecuteErrorException, UnKnownSourceTypeException {
        try {
            // 需要先提交事务， 才有值
            CommonUtils.throwSQLThrowable(super.save(entity), this.getClass(), "saveWithException", entity);
            flinkUtils.submitComputeJob(CommonUtils.getRecordClass(entity.getSourceType()), entity);
        } catch (Exception e) {
            e.printStackTrace();
            CommonUtils.throwSQLThrowable(super.removeById(entity.getId()), this.getClass(), "removeById", entity.getId());
            throw e;
        }
    }

    public void removeById(int id) throws SQLExecuteErrorException {
        CommonUtils.throwSQLThrowable(super.removeById(id), this.getClass(), "removeById", id);
        flinkUtils.removeComputeJob(id);
    }

    @PostConstruct
    private void sync() throws UnKnownSourceTypeException {
        flinkUtils.syncCompute(list());
    }
}
