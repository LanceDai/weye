package cn.lancedai.weye.server.util;

import cn.lancedai.weye.common.exception.UnKnownSourceTypeException;
import cn.lancedai.weye.common.model.record.BaseRecord;
import cn.lancedai.weye.common.model.rule.ComputeRule;
import cn.lancedai.weye.common.model.rule.WarnRule;
import cn.lancedai.weye.common.tool.SSHTool;
import cn.lancedai.weye.common.tool.StringTool;
import cn.lancedai.weye.server.config.FlinkServerConfig;
import cn.lancedai.weye.server.config.ServerConfig;
import cn.lancedai.weye.server.service.ComputeRuleService;
import cn.lancedai.weye.server.service.WarnRuleService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 用于提交Flink作业
 */
@Component
@Slf4j
public class FlinkUtils {
    private final FlinkServerConfig flinkServerConfig;
    private final ServerConfig serverConfig;

    public FlinkUtils(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.flinkServerConfig = serverConfig.getFlinkServerConfig();
    }

    //启动时检查相应flink任务是否都已启动

    public void syncCompute(List<ComputeRule> computeRules) throws UnKnownSourceTypeException {
        val session = SSHTool.open(
                flinkServerConfig.getUsername(),
                flinkServerConfig.getPassword(),
                flinkServerConfig.getSshHost(),
                flinkServerConfig.getSshPort()
        );
        for (ComputeRule computeRule : computeRules) {
            val jobId = "COMPUTE-" + computeRule.getId();
            // 先找出对应的运行ID
            val runJobId = session.execute(true, flinkServerConfig.getExecutePath() + "/bin/flink",
                    new String[]{
                            "list",
                            "| grep '" + jobId + "'",
                            "| awk -F \":\" '{ print $4 }'"
                    }
            );
            if (StringUtils.isEmpty(runJobId)) {
                //创建任务
                submitComputeJob(CommonUtils.getRecordClass(computeRule.getSourceType()), computeRule);
            }
        }
        session.close();
    }
    public void syncWarn(List<WarnRule> warnRules) throws UnKnownSourceTypeException {
        val session = SSHTool.open(
                flinkServerConfig.getUsername(),
                flinkServerConfig.getPassword(),
                flinkServerConfig.getSshHost(),
                flinkServerConfig.getSshPort()
        );
        for (WarnRule warnRule : warnRules) {
            val jobId = "WARN-" + warnRule.getId();
            // 先找出对应的运行ID
            val runJobId = session.execute(true, flinkServerConfig.getExecutePath() + "/bin/flink",
                    new String[]{
                            "list",
                            "| grep '" + jobId + "'",
                            "| awk -F \":\" '{ print $4 }'"
                    }
            );
            if (StringUtils.isEmpty(runJobId)) {
                //创建任务
                submitWarnJob(CommonUtils.getRecordClass(warnRule.getSourceType()), warnRule);
            }
        }
        session.close();
    }
    // 提交Job
    public <T extends BaseRecord> void submitComputeJob(Class<T> recordClass, ComputeRule computeRule) {
        val jobName = (StringTool.humpToChar(recordClass.getSimpleName(), "-") + "-compute").toLowerCase();
        submitJob(jobName, computeRule.getId());
    }

    public <T extends BaseRecord> void submitWarnJob(Class<T> recordClass, WarnRule warnRule) {
        val jobName = (StringTool.humpToChar(recordClass.getSimpleName(), "-") + "-warn").toLowerCase();
        submitJob(jobName, warnRule.getId());
    }


    public void removeComputeJob(int computeRuleId) {
        removeJob("COMPUTE-" + computeRuleId);
    }

    public void removeWarnJob(int warnRuleId) {
        removeJob("WARN-" + warnRuleId);
    }

    private void removeJob(String jobId) {
        val session = SSHTool.open(
                flinkServerConfig.getUsername(),
                flinkServerConfig.getPassword(),
                flinkServerConfig.getSshHost(),
                flinkServerConfig.getSshPort()
        );
        // 先找出对应的运行ID
        val runJobId = session.execute(true, flinkServerConfig.getExecutePath() + "/bin/flink",
                new String[]{
                        "list",
                        "| grep '" + jobId + "'",
                        "| awk -F \":\" '{ print $4 }'"
                }
        );
        if (!StringUtils.isEmpty(runJobId.trim())) {
            val executeRes = session.execute(true, flinkServerConfig.getExecutePath() + "/bin/flink",
                    new String[]{
                            "cancel",
                            runJobId.trim()
                    }
            );
            log.debug("remove Job => res = {}", executeRes);
        } else {
            log.debug("flink job with name: {} is down", jobId);
        }
        session.close();
    }

    private void submitJob(String jobName, int ruleId) {
        val session = SSHTool.open(
                flinkServerConfig.getUsername(),
                flinkServerConfig.getPassword(),
                flinkServerConfig.getSshHost(),
                flinkServerConfig.getSshPort()
        );
//        http://www.resource.lancedai.cn/flink_job/weye-flink-execute-command-record-compute-0.0.1.jar
        val fullJobName = "weye-flink-" + jobName + "-" + serverConfig.getVersion() + ".jar";
        session.downloadIfNotExist(
                "/tmp/",
                fullJobName,
                flinkServerConfig.getJobDownloadUrl() + "/" + fullJobName,
                true
        );

        val command = String.format("%s run -d %s %s %s %s %s %s",
                flinkServerConfig.getExecutePath() + "/bin/flink",
                "/tmp/" + fullJobName,
                serverConfig.kafkaBootstrapServers,
                ruleId,
                serverConfig.getDbUsername(),
                serverConfig.getDbPassword(),
                serverConfig.getDbUrl()
        );
        log.debug("execute command: {}", command);
        val res2 = session.execute(true, command, new String[]{});
        log.debug("submit flink job: res => {}", res2);
        session.close();
    }
}
