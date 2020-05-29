package cn.lancedai.weye.server;

import cn.lancedai.weye.server.config.ServerConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(ServerConfig.class)
@MapperScan(basePackages = "cn.lancedai.weye.server.mapper")
//开启基于注解的定时任务
@EnableScheduling
public class WeyeApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeyeApplication.class, args);
    }
}
