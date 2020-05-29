package cn.lancedai.weye.server.agent.handler;

import cn.lancedai.weye.common.NTO.*;
import cn.lancedai.weye.common.model.ServerInfo;
import cn.lancedai.weye.common.exception.UnValidAgentException;
import cn.lancedai.weye.server.config.ServerConfig;
import cn.lancedai.weye.server.service.ServerInfoService;
import cn.lancedai.weye.server.service.ServerService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 实现一个小型的滑动窗口？？？
 */
@Slf4j
public class AgentHandler extends SimpleChannelInboundHandler<AgentResponse> {

    private final ServerService serverService;
    private final ServerInfoService serverInfoService;
    private final ServerConfig serverConfig;

    private final ConcurrentHashMap<Integer, OperationFuture> windows = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    private ChannelHandlerContext ctx;
    private int serverId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        log.debug("agent is online: {}", ctx.channel().remoteAddress());
        this.ctx.writeAndFlush(new ApiKeyCheckRequest());
    }

    public AgentHandler(ServerService serverService, ServerInfoService serverInfoService, ServerConfig serverConfig) {
        this.serverService = serverService;
        this.serverInfoService = serverInfoService;
        this.serverConfig = serverConfig;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AgentResponse msg) {
        if (msg instanceof ApiKeyCheckResponse) {
            handlerInit((ApiKeyCheckResponse) msg);
        } else if (msg instanceof ServerInfoResponse) {
            handlerServerInfo((ServerInfoResponse) msg);
        } else if (msg instanceof StatusResponse) {
            StatusResponse status = (StatusResponse) msg;
            windows.remove(status.id()).handleResponse(status);
        } else if (msg instanceof SyncResponse) {
            handlerSync((SyncResponse) msg);
        } else {
            log.debug("unknown msg type => {}", msg.getClass().getName());
        }
    }

    private void handlerSync(SyncResponse msg) {
        try {
            serverService.sync(serverId, msg.customCommandCollectors(), msg.httpRequestCollectors());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void handlerServerInfo(ServerInfoResponse msg) {
        // 记录保存服务器配置信息
        log.debug("save server config, {}", msg.serverInfo());
        // 判断是否记录已存在
        val info = serverInfoService.lambdaQuery().eq(ServerInfo::getServerId, msg.serverInfo().getServerId()).one();
        if (info != null) {
            msg.serverInfo().setId(info.getId());
        }
        serverInfoService.saveOrUpdate(msg.serverInfo());

        // 开始进行状态同步
        this.ctx.writeAndFlush(new SyncRequest());
    }

    private void handlerInit(ApiKeyCheckResponse response) {
        try {
            val apiKey = response.apiKey();
            val server = serverService.addChannel(apiKey, this);
            this.serverId = server.getId();
            // 判断指定IP与连接IP是否一致
            val channelIp = ((InetSocketAddress) this.ctx.channel().remoteAddress()).getHostString();
            if (channelIp.equals(server.getIp())) {
                this.ctx.writeAndFlush(new InitRequest(server.getIp(), server.getId(), serverConfig.getKafkaBootstrapServers()));
            } else {
                this.ctx.writeAndFlush(new DownRequest(String.format("ip 不符合， channel 链接IP 为 %s, server固定IP 为 %s", channelIp, server.getIp())));
            }
        } catch (UnValidAgentException e) {
            log.error(e.getMessage());
            this.ctx.writeAndFlush(new DownRequest(e.getMessage()));
            // 关闭非法链接
            this.ctx.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            log.debug("agent is offline: {}", this.ctx.channel().remoteAddress());
            System.out.println("agent is offline: " + this.ctx.channel().remoteAddress());
            //删除对应记录
            this.ctx = null;
            serverService.removeChannel(serverId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public StatusResponse addHttpRequestCollector(int webAppId, int port) {
        return agentOp(new AddHttpRequestCollector(counter.getAndIncrement(), webAppId, port));
    }

    public StatusResponse removeHttpRequestCollector(int webAppId) {
        return agentOp(new RemoveHttpRequestCollector(counter.getAndIncrement(), webAppId));
    }

    public StatusResponse addCustomCommandCollector(int executeCommandSourceSubscribeId, String command, String duration) {
        return agentOp(new AddCustomCommandCollector(counter.getAndIncrement(), executeCommandSourceSubscribeId, command, duration));
    }

    public StatusResponse removeCustomCommandCollector(int executeCommandSourceId) {
        return agentOp(new RemoveCustomCommandCollector(counter.getAndIncrement(), executeCommandSourceId));
    }

    public StatusResponse testCommand(String command) {
        return agentOp(new TestCustomCommand(counter.getAndIncrement(), command));
    }

    private StatusResponse agentOp(OperationRequest request) {
        val opFuture = new OperationFuture();
        this.ctx.writeAndFlush(request);
        windows.put(request.id(), opFuture);
        return opFuture.get();
    }


    private static class OperationFuture {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition receive = lock.newCondition();
        private volatile StatusResponse res;

        public void handleResponse(StatusResponse statusResponse) {
            log.debug("statusReponse id: {}", statusResponse.id());
            this.res = statusResponse;
            this.wakeUp();
        }

        private void wakeUp() {
            lock.lock();
            try {
                receive.signal();
            } finally {
                lock.unlock();
            }
        }

        public StatusResponse get() {
            if (null == res) {
                lock.lock();
                try {
                    while (res == null) {
                        receive.await();
                    }
                    return res;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
            return res;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        log.error("throwable type: {}, message {}", cause.getClass().getName(), cause.getMessage());
    }
}