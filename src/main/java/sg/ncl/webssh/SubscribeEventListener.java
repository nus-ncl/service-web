package sg.ncl.webssh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * References:
 * [1] http://www.devglan.com/spring-boot/spring-session-stomp-websocket
 */

@Component
@Slf4j
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    @Override
    public void onApplicationEvent(SessionSubscribeEvent sessionSubscribeEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());
        log.info("onSubscribe [socket session: {}, spring session: {}]",
                headerAccessor.getSessionId(), headerAccessor.getSessionAttributes().get("sessionId").toString());
    }
}
