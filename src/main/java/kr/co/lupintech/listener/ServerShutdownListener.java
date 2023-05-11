package kr.co.lupintech.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import kr.co.lupintech.service.SseService;

@Component
public class ServerShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private final SseService sseService;

    public ServerShutdownListener(SseService emitterUtil) {
        this.sseService = emitterUtil;
    }
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 서버 종료 시 실행될 코드

        sseService.removeAll();

    }
}
