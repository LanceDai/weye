package cn.lancedai.weye.server.service;

import cn.lancedai.weye.common.model.relation.CustomCommandSubscribe;
import cn.lancedai.weye.server.VO.SubscribeCommandInfo;
import cn.lancedai.weye.server.mapper.relation.CustomCommandSubscribeMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class CustomCommandSubscribeService extends ServiceImpl<CustomCommandSubscribeMapper, CustomCommandSubscribe> {
    private final ServerService serverService;

    public CustomCommandSubscribeService(ServerService serverService) {
        this.serverService = serverService;
    }


    @Override
    public boolean removeById(Serializable id) {
        try {
            val entity = baseMapper.getFullSubscribe((Integer) id);
            serverService.removeCustomCommandCollector(entity.getServerId(), (Integer) id);
            return super.removeById(id);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean save(CustomCommandSubscribe subscribe) {
        try {
            super.save(subscribe);
            // 建立一个DTO， 查询获取serverId， command，
            val fullSubscribe = baseMapper.getFullSubscribe(subscribe.getId());
            serverService.addCustomCommandCollector(fullSubscribe.getServerId(), fullSubscribe.getSubscribeId(), fullSubscribe.getCommand(), fullSubscribe.getDuration());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SubscribeCommandInfo> getSubscribeCommandInfos(int webAppId){
        return baseMapper.getSubscribeCommandInfos(webAppId);
    }
}
