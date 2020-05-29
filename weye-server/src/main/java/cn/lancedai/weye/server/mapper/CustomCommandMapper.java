package cn.lancedai.weye.server.mapper;

import cn.lancedai.weye.common.model.CustomCommand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CustomCommandMapper extends BaseMapper<CustomCommand> {

}
