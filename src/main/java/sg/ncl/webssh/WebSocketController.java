package sg.ncl.webssh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * This controller is for a web socket to ssh into a node. The library used is JSch (Java Secure Channel).
 *
 * References:
 * [1] http://www.jcraft.com/jsch/
 * [2] http://kimrudolph.de/blog/spring-4-websockets-tutorial
 * [3] http://www.sergialmar.com/2014/03/detect-websocket-connects-and-disconnects-in-spring-4/
 * [4] https://docs.spring.io/spring-session/docs/current/reference/html5/guides/websocket.html
 * [5] https://stackoverflow.com/questions/25789245/how-to-get-jsch-shell-command-output-in-string
 * [6] https://stackoverflow.com/questions/28387157/multiple-rooms-in-spring-using-stomp
 * [7] https://stackoverflow.com/questions/29899720/how-to-execute-interactive-commands-and-read-their-sparse-output-from-java-using
 * [8] https://stackoverflow.com/questions/41286714/get-websocket-session-data-in-spring-when-handing-stomp-send
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
    public void handleInput(String command) {
        log.info("Handle Input '{}'", command);
        if (command != null && !command.isEmpty()) {
            webSocketBean.getInputToShell().println(command);
        }
    }
}
