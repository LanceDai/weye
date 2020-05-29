package cn.lancedai.weye.server.agent;

import cn.lancedai.weye.server.agent.handler.AgentHandler;
import cn.lancedai.weye.server.config.ServerConfig;
import cn.lancedai.weye.server.service.ServerInfoService;
import cn.lancedai.weye.server.service.ServerService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 与Agent保持链接的处理中心
 */
@Component
@Slf4j
public class ProcessCenter {
    private final ThreadPoolTaskScheduler serverTaskExecutor;
    private final ServerConfig serverConfig;
    private final ServerService serverService;
    private final ServerInfoService serverInfoService;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public ProcessCenter(ThreadPoolTaskScheduler serverTaskExecutor, ServerConfig serverConfig, ServerService serverService, ServerInfoService serverInfoService) {
        this.serverTaskExecutor = serverTaskExecutor;
        this.serverConfig = serverConfig;
        this.serverService = serverService;
        this.serverInfoService = serverInfoService;
    }

    /**
     * 启动服务器等待
     */
    @PostConstruct
    public void startServer() {
        int port = serverConfig.getProcessCenterPort();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addFirst(new LoggingHandler())
                                .addLast(
                                        // 添加对象解码器 负责对序列化POJO对象进行解码
                                        // 设置对象序列化最大长度为1M 防止内存溢出
                                        // 设置线程安全的WeakReferenceMap对类加载器进行缓存
                                        // 支持多线程并发访问 放至内存溢出
                                        new ObjectDecoder(
                                                1024 * 1024,
                                                ClassResolvers
                                                        .weakCachingConcurrentResolver(
                                                                this.getClass().getClassLoader()
                                                        )
                                        )
                                )
                                //添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
                                .addLast(new ObjectEncoder())
                                // 处理网络IO
                                .addLast(new AgentHandler(serverService, serverInfoService, serverConfig));
                    }
                });
        serverTaskExecutor.submit(() -> {
            try {
                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.info("close");
            }
        });
    }

    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
