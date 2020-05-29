package cn.lancedai.weye.server.mapper;

import cn.lancedai.weye.common.model.Server;
import cn.lancedai.weye.common.model.WebApp;
import cn.lancedai.weye.server.VO.SubscribeCommandInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ServerMapper extends BaseMapper<Server> {
    List<WebApp> findAllWebAppByServerId(int serverId);
    List<SubscribeCommandInfo> findAllCustomCommandByServerId(int serverId);
}
