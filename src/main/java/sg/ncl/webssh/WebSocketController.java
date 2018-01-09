package sg.ncl.webssh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * This controller is for a web socket to ssh into a node. The library used is JSch (Java Secure Channel).
 *
 * References:
 * [1] http://kimrudolph.de/blog/spring-4-websockets-tutorial
 * [2] http://www.sergialmar.com/2014/03/detect-websocket-connects-and-disconnects-in-spring-4/
 * [3] https://docs.spring.io/spring-session/docs/current/reference/html5/guides/websocket.html
 * [4] https://stackoverflow.com/questions/28387157/multiple-rooms-in-spring-using-stomp
 * [5] https://stackoverflow.com/questions/29899720/how-to-execute-interactive-commands-and-read-their-sparse-output-from-java-using
 * [6] https://stackoverflow.com/questions/41286714/get-websocket-session-data-in-spring-when-handing-stomp-send
 */

@Controller
@Slf4j
public class WebSocketController {

    private WebSocketBean webSocketBean;

    @Autowired
    public WebSocketController(WebSocketBean webSocketBean) {
        this.webSocketBean = webSocketBean;
    }

    @MessageMapping("/input")
    public void handleInput(@Payload String command, SimpMessageHeaderAccessor headerAccessor) {
        log.info("onHandleInput [socket session: {}, spring session: {}, command: {}]",
                headerAccessor.getSessionId(), headerAccessor.getSessionAttributes().get("sessionId").toString(), command);
        if (command != null && !command.isEmpty()) {
            webSocketBean.getInputToShell().println(command);
        }
    }
}
