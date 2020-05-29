package cn.lancedai.weye.server.service;

import cn.lancedai.weye.common.exception.SQLExecuteErrorException;
import cn.lancedai.weye.common.exception.UnKnownSourceTypeException;
import cn.lancedai.weye.common.model.rule.WarnRule;
import cn.lancedai.weye.server.mapper.WarnRuleMapper;
import cn.lancedai.weye.server.util.CommonUtils;
import cn.lancedai.weye.server.util.FlinkUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class WarnRuleService extends ServiceImpl<WarnRuleMapper, WarnRule> {
    private final FlinkUtils flinkUtils;

    public WarnRuleService(FlinkUtils flinkUtils) {
        this.flinkUtils = flinkUtils;
    }

    public void saveWithException(WarnRule entity) throws UnKnownSourceTypeException, SQLExecuteErrorException {
        CommonUtils.throwSQLThrowable(super.save(entity), this.getClass(), "saveWithException", entity);
        flinkUtils.submitWarnJob(CommonUtils.getRecordClass(entity.getSourceType()), entity);
    }

    public void removeById(int id) throws SQLExecuteErrorException {
        CommonUtils.throwSQLThrowable(super.removeById(id), this.getClass(), "removeById", id);
        flinkUtils.removeWarnJob(id);
    }

    @PostConstruct
    private void sync() throws UnKnownSourceTypeException {
        flinkUtils.syncWarn(list());
    }
}
