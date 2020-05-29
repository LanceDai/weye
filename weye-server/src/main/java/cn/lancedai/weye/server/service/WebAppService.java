package cn.lancedai.weye.server.service;

import cn.lancedai.weye.common.model.WebApp;
import cn.lancedai.weye.server.VO.WebAppInfo;
import cn.lancedai.weye.server.mapper.WebAppMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.lancedai.weye.server.util.CommonUtils.throwSQLThrowable;

@Service
public class WebAppService extends ServiceImpl<WebAppMapper, WebApp> implements IService<WebApp> {

    private final ServerService serverService;

    public WebAppService(ServerService serverService) {
        this.serverService = serverService;
    }

    public void removeById(int id) throws Throwable {
        //获取ServerId
        val serverId = lambdaQuery().eq(WebApp::getId, id).one().getServerId();
        serverService.removeHttpRequestCollector(serverId, id);
        // 删除相关数据源
        throwSQLThrowable(super.removeById(id), this.getClass(), "removeId", id);
    }

    @Transactional
    public void saveWithException(WebApp entity) throws Throwable {
        try {
            // 发送至对应Agent 添加port数据源
            throwSQLThrowable(super.save(entity), this.getClass(), "save", entity);
            serverService.addHttpRequestCollector(entity.getServerId(), entity.getId(), entity.getPort());
        } catch (Throwable e) {
            if (entity.getId() != 0) {
                super.removeById(entity.getId());
            }
            throw e;
        }
    }

    public List<WebAppInfo> getWebAppInfos() {
        return baseMapper.getWebAppInfos();
    }

    public List<WebAppInfo> getWebAppInfosByServerId(int serverId) {
        return baseMapper.getWebAppInfosByServerId(serverId);
    }

    public Integer getSuccessWebAppCounts() {
        return baseMapper.getSuccessWebAppCounts();
    }

    public Integer getWarnWebAppCounts() {
        return baseMapper.getWarnWebAppCounts();
    }

    public WebAppInfo getWebAppDetailById(int webAppId) {
        return baseMapper.getWebAppDetailById(webAppId);
    }
}
