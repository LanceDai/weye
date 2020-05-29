package cn.lancedai.weye.server.mapper;

import cn.lancedai.weye.common.model.ServerInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ServerInfoMapper extends BaseMapper<ServerInfo> {
}
