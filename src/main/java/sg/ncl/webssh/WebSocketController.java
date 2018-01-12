package sg.ncl.webssh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
 * [7] https://github.com/skavanagh/KeyBox-OpenShift/blob/master/src/main/java/com/keybox/manage/socket/SecureShellWS.java
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
    public void handleInput(WebSocketInput input) {
        Boolean ctrlKey = input.getCtrlKey();
        String key = input.getKey();
        Integer keyCode = input.getKeyCode();
        if (!ctrlKey && ((keyCode >= 65 && keyCode <= 90) || keyCode == 219 || keyCode == 221)) {
            keyCode = 0;
        }
        if (keyMap.containsKey(keyCode)) {
            try {
                webSocketBean.getInputToShell().write(keyMap.get(keyCode));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            webSocketBean.getInputToShell().print(key);
        }
    }

    /**
     * Maps key press events to the ascii values
     */
    private static Map<Integer, byte[]> keyMap = new HashMap<>();

    static {
        //ESC
        keyMap.put(27, new byte[]{(byte) 0x1b});
        //ENTER
        keyMap.put(13, new byte[]{(byte) 0x0d});
        //LEFT
        keyMap.put(37, new byte[]{(byte) 0x1b, (byte) 0x4f, (byte) 0x44});
        //UP
        keyMap.put(38, new byte[]{(byte) 0x1b, (byte) 0x4f, (byte) 0x41});
        //RIGHT
        keyMap.put(39, new byte[]{(byte) 0x1b, (byte) 0x4f, (byte) 0x43});
        //DOWN
        keyMap.put(40, new byte[]{(byte) 0x1b, (byte) 0x4f, (byte) 0x42});
        //BS
        keyMap.put(8, new byte[]{(byte) 0x7f});
        //TAB
        keyMap.put(9, new byte[]{(byte) 0x09});
        //CTR
        keyMap.put(17, new byte[]{});
        //DEL
        keyMap.put(46, "\033[3~".getBytes());
        //CTR-A
        keyMap.put(65, new byte[]{(byte) 0x01});
        //CTR-B
        keyMap.put(66, new byte[]{(byte) 0x02});
        //CTR-C
        keyMap.put(67, new byte[]{(byte) 0x03});
        //CTR-D
        keyMap.put(68, new byte[]{(byte) 0x04});
        //CTR-E
        keyMap.put(69, new byte[]{(byte) 0x05});
        //CTR-F
        keyMap.put(70, new byte[]{(byte) 0x06});
        //CTR-G
        keyMap.put(71, new byte[]{(byte) 0x07});
        //CTR-H
        keyMap.put(72, new byte[]{(byte) 0x08});
        //CTR-I
        keyMap.put(73, new byte[]{(byte) 0x09});
        //CTR-J
        keyMap.put(74, new byte[]{(byte) 0x0A});
        //CTR-K
        keyMap.put(75, new byte[]{(byte) 0x0B});
        //CTR-L
        keyMap.put(76, new byte[]{(byte) 0x0C});
        //CTR-M
        keyMap.put(77, new byte[]{(byte) 0x0D});
        //CTR-N
        keyMap.put(78, new byte[]{(byte) 0x0E});
        //CTR-O
        keyMap.put(79, new byte[]{(byte) 0x0F});
        //CTR-P
        keyMap.put(80, new byte[]{(byte) 0x10});
        //CTR-Q
        keyMap.put(81, new byte[]{(byte) 0x11});
        //CTR-R
        keyMap.put(82, new byte[]{(byte) 0x12});
        //CTR-S
        keyMap.put(83, new byte[]{(byte) 0x13});
        //CTR-T
        keyMap.put(84, new byte[]{(byte) 0x14});
        //CTR-U
        keyMap.put(85, new byte[]{(byte) 0x15});
        //CTR-V
        keyMap.put(86, new byte[]{(byte) 0x16});
        //CTR-W
        keyMap.put(87, new byte[]{(byte) 0x17});
        //CTR-X
        keyMap.put(88, new byte[]{(byte) 0x18});
        //CTR-Y
        keyMap.put(89, new byte[]{(byte) 0x19});
        //CTR-Z
        keyMap.put(90, new byte[]{(byte) 0x1A});
        //CTR-[
        keyMap.put(219, new byte[]{(byte) 0x1B});
        //CTR-]
        keyMap.put(221, new byte[]{(byte) 0x1D});
        //INSERT
        keyMap.put(45, "\033[2~".getBytes());
        //PG UP
        keyMap.put(33, "\033[5~".getBytes());
        //PG DOWN
        keyMap.put(34, "\033[6~".getBytes());
        //END
        keyMap.put(35, "\033[4~".getBytes());
        //HOME
        keyMap.put(36, "\033[1~".getBytes());
    }
}
