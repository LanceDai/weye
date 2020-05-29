package cn.lancedai.weye.server.mapper.relation;

import cn.lancedai.weye.common.model.relation.CustomCommandSubscribe;
import cn.lancedai.weye.server.DTO.FullSubscribe;
import cn.lancedai.weye.server.VO.SubscribeCommandInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CustomCommandSubscribeMapper extends BaseMapper<CustomCommandSubscribe> {
    FullSubscribe getFullSubscribe(int subscribeId);

    List<SubscribeCommandInfo> getSubscribeCommandInfos(int webAppId);
}
