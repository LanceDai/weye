package cn.lancedai.weye.server.service;

import cn.lancedai.weye.common.model.ServerInfo;
import cn.lancedai.weye.server.mapper.ServerInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServerInfoService extends ServiceImpl<ServerInfoMapper, ServerInfo> {
}
