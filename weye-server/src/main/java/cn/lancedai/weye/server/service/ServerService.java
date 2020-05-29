package cn.lancedai.weye.server.service;

import cn.lancedai.weye.common.NTO.FailureResponse;
import cn.lancedai.weye.common.NTO.StatusResponse;
import cn.lancedai.weye.common.NTO.SuccessResponse;
import cn.lancedai.weye.common.model.Server;
import cn.lancedai.weye.common.model.WebApp;
import cn.lancedai.weye.common.exception.AgentOffLineException;
import cn.lancedai.weye.common.exception.SQLExecuteErrorException;
import cn.lancedai.weye.common.exception.UnValidAgentException;
import cn.lancedai.weye.common.tool.NetWorkTool;
import cn.lancedai.weye.server.VO.NewServerInfo;
import cn.lancedai.weye.server.VO.SubscribeCommandInfo;
import cn.lancedai.weye.server.agent.handler.AgentHandler;
import cn.lancedai.weye.server.config.ServerConfig;
import cn.lancedai.weye.server.mapper.ServerMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.lancedai.weye.server.util.CommonUtils.throwSQLThrowable;

@Service
@Slf4j
public class ServerService extends ServiceImpl<ServerMapper, Server> {

    /**
     * server 与 channel 映射关系记录， 用来实现双向
     */
    private final Map<Integer, AgentHandler> serverChannelMap = new ConcurrentHashMap<>();

    private final ServerConfig serverConfig;
    private final ThreadPoolTaskScheduler serverTaskExecutor;

    public ServerService(ServerConfig serverConfig, ThreadPoolTaskScheduler serverTaskExecutor) {
        this.serverConfig = serverConfig;
        this.serverTaskExecutor = serverTaskExecutor;
    }

    public void removeById(Integer id) throws SQLExecuteErrorException {
        // 触发对应的Agent下线
        removeChannel(id);
        throwSQLThrowable(super.removeById(id), this.getClass(), "removeById", id);
    }

    private final static int PING_TIME_OUT = 2000;

    public NewServerInfo saveWithException(Server server) throws Exception {
        // server attribute test
        val ip = server.getIp();
        val address = InetAddress.getByName(ip);
        if (!address.isReachable(PING_TIME_OUT)) {
            throw new Exception("Server ip is not reach in " + PING_TIME_OUT + "ms");
        }
        throwSQLThrowable(super.save(server), this.getClass(), "saveWithTest", server);
        return new NewServerInfo(server.getApiKey(), NetWorkTool.getCorrespondingHost(ip) + ":" + serverConfig.getProcessCenterPort());
    }

    public Server addChannel(String apiKey, AgentHandler handler) throws UnValidAgentException {
        //记录下来
        val server = lambdaQuery().eq(Server::getApiKey, apiKey).one();
        if (server == null) {
            throw new UnValidAgentException();
        } else {
            val serverId = server.getId();
            lambdaUpdate().eq(Server::getId, serverId)
                    .set(Server::getAgentStatus, 1)
                    .update();
            serverChannelMap.put(serverId, handler);
            return server;
        }
    }

    public void removeChannel(int serverId) throws SQLExecuteErrorException {
        //说明存在， 修改Agent记录
        if (serverChannelMap.remove(serverId) != null) {
            throwSQLThrowable(lambdaUpdate()
                            .eq(Server::getId, serverId)
                            .set(Server::getAgentStatus, 0)
                            .update(),
                    this.getClass(),
                    "removeChannel",
                    serverId
            );
        }
    }


    private AgentHandler getChannelByServerId(int serverId) throws AgentOffLineException {
        //找到对应channel
        val handler = serverChannelMap.get(serverId);
        //如果Agent不在线，抛出异常
        if (handler == null) {
            throw new AgentOffLineException();
        } else {
            return handler;
        }
    }

    public void addHttpRequestCollector(int serverId, int id, int port) throws Throwable {
        handlerOp(serverId, handler -> handler.addHttpRequestCollector(id, port));
    }

    public void removeHttpRequestCollector(int serverId, int id) throws Throwable {
        handlerOp(serverId, handler -> handler.removeHttpRequestCollector(id));
    }

    public void addCustomCommandCollector(int serverId, int customCommandSubscribeId, String command, String duration) throws Throwable {
        handlerOp(serverId, handler -> handler.addCustomCommandCollector(customCommandSubscribeId, command, duration));
    }

    public void removeCustomCommandCollector(int serverId, int customCommandId) throws Throwable {
        handlerOp(serverId, handler -> handler.removeCustomCommandCollector(customCommandId));
    }

    public String testCommand(int serverId, String command) throws Throwable {
        return String.valueOf(handlerOp(serverId, handler -> handler.testCommand(command)));
    }

    private Object handlerOp(int serverId, Function<AgentHandler, StatusResponse> f) throws Throwable {
        return statusResponseHandler(f.apply(getChannelByServerId(serverId)));
    }

    private Object statusResponseHandler(StatusResponse response) throws Throwable {
        if (response instanceof FailureResponse) {
            throw new Throwable(((FailureResponse) response).errMsg());
        } else {
            return ((SuccessResponse<?>) response).data();
        }
    }

    // 关闭任务， 将所有在线的Agent状态变为上线
    @PreDestroy
    private void destroy() {
        updateBatchById(lambdaQuery()
                .eq(Server::getAgentStatus, 1)
                .list()
                .stream()
                .peek(server -> server.setAgentStatus(0))
                .collect(Collectors.toList())
        );
    }

    public void sync(int serverId, int[] customCommandCollectors, int[] httpRequestCollectors) throws Throwable {
        long timeout = 5000;
        // 找出对应的所有web应用
        val webApps = baseMapper.findAllWebAppByServerId(serverId);
        // 如果agent传来的HttpRequestCollectorsId不存在于数据库记录中, 则删除
        for (int id : httpRequestCollectors) {
            if (webApps.stream().noneMatch(webApp -> webApp.getId() == id)) {
                serverTaskExecutor.execute(() -> {
                    try {
                        removeHttpRequestCollector(serverId, id);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }, timeout);
            }
        }
        // 将多余的数据库记录添加
        for (WebApp webApp : webApps) {
            if (Arrays.binarySearch(httpRequestCollectors, webApp.getId()) == -1) {
                serverTaskExecutor.execute(() -> {
                    try {
                        addHttpRequestCollector(serverId, webApp.getId(), webApp.getPort());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }, timeout);
            }
        }
        // 找出对应的所有web应用
        val customCommands = baseMapper.findAllCustomCommandByServerId(serverId);
        // 如果agent传来的HttpRequestCollectorsId不存在于数据库记录中, 则删除
        for (int id : customCommandCollectors) {
            if (customCommands.stream().noneMatch(customCommand -> customCommand.getSubscribeId() == id)) {
                serverTaskExecutor.execute(() -> {
                    try {
                        removeCustomCommandCollector(serverId, id);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }, timeout);
            }
        }
        // 将多余的数据库记录添加
        for (SubscribeCommandInfo subscribeCommandInfo : customCommands) {
            if (Arrays.binarySearch(customCommandCollectors, subscribeCommandInfo.getSubscribeId()) == -1) {
                serverTaskExecutor.execute(() -> {
                    try {
                        addCustomCommandCollector(serverId, subscribeCommandInfo.getSubscribeId(), subscribeCommandInfo.getCommand(), subscribeCommandInfo.getDuration());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }, timeout);
            }
        }
    }
}
