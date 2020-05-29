package cn.lancedai.weye.server.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DestroyTask implements ApplicationListener<ContextClosedEvent> {


    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.debug("server is close");
    }
}
