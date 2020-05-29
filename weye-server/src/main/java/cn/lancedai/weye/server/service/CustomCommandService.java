package cn.lancedai.weye.server.service;

import cn.lancedai.weye.common.model.CustomCommand;
import cn.lancedai.weye.server.mapper.CustomCommandMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class CustomCommandService extends ServiceImpl<CustomCommandMapper, CustomCommand> {

}
