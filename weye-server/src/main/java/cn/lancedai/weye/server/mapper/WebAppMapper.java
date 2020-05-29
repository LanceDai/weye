package cn.lancedai.weye.server.mapper;

import cn.lancedai.weye.common.model.WebApp;
import cn.lancedai.weye.server.VO.WebAppInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WebAppMapper extends BaseMapper<WebApp> {
    List<WebAppInfo> getWebAppInfos();

    List<WebAppInfo> getWebAppInfosByServerId(int serverId);

    Integer getSuccessWebAppCounts();

    Integer getWarnWebAppCounts();

    WebAppInfo getWebAppDetailById(int webAppId);
}
