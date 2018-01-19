package sg.ncl.webssh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

/**
 * References:
 * [1] http://www.sergialmar.com/2014/03/detect-websocket-connects-and-disconnects-in-spring-4/
 * [2] https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket
 */

@Slf4j
public class SocketChannelInterceptor extends ChannelInterceptorAdapter {

    @Autowired
    private WebSocketBean webSocketBean;

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);

        // ignore non-STOMP messages like heartbeat messages
        if (stompHeaderAccessor.getCommand() == null) {
            return;
        }

        String sessionId = stompHeaderAccessor.getSessionId();

        switch (stompHeaderAccessor.getCommand()) {
            case CONNECT:
                String user = stompHeaderAccessor.getNativeHeader("user").get(0);
                String pass = stompHeaderAccessor.getNativeHeader("pass").get(0);
                String qualified = stompHeaderAccessor.getNativeHeader("qualified").get(0);
                log.info("STOMP Connect [socket: {}, user: {}, domain: {}]", sessionId, user, qualified);
                webSocketBean.connect(user, pass);
                webSocketBean.getInputToShell().println("ssh " + qualified);
                break;
            case CONNECTED:
                log.info("STOMP Connected [sessionId: {}]", sessionId);
                break;
            case DISCONNECT:
                log.info("STOMP Disconnect [sessionId: {}]", sessionId);
                break;
            default:
                break;
        }
    }
}
