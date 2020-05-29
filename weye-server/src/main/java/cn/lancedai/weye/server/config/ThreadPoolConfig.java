package cn.lancedai.weye.server.config;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskScheduler serverTaskExecutor() {
        val taskExecutor = new ThreadPoolTaskScheduler();
        taskExecutor.setPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        taskExecutor.setThreadNamePrefix("ServerTask--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        taskExecutor.setAwaitTerminationSeconds(10);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
