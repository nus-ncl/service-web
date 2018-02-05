package sg.ncl.webssh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * References:
 * [1] https://www.mkyong.com/spring/spring-and-java-thread-example/
 * [2] http://codenav.org/code.html?project=/com/nitorcreations/willow-servers/0.1&path=/Source%20Packages/com.nitorcreations.willow.ssh/SecureShellWS.java
 * [3] https://stackoverflow.com/questions/37307697/scheduled-websocket-push-with-springboot
 */

@Component
@Scope("prototype")
@Slf4j
public class SentOutputTask implements Runnable {

    static final int BUFFER_LEN = 4 * 1024;

    @Autowired
    private SimpMessagingTemplate template;

    private InputStream output;
    private String user;
    private String qualified;

    public void setOutput(InputStream output, String user, String qualified) {
        this.output = output;
        this.user = user;
        this.qualified = qualified;
    }

    @Override
    public void run() {
        byte[] buf = new byte[BUFFER_LEN];
        int read;

        try {
            while ((read = output.read(buf)) != -1) {
                if (read > 0) {
                    template.convertAndSend("/shell/output/" + user + "@" + qualified, new String(buf, 0, read, Charset.forName("UTF-8")));
                }
                Thread.sleep(100);
            }
        } catch (IOException e) {
            log.info("Failed to read data from ssh: {}", e);
        } catch (InterruptedException e) {
            log.info("Interrupted while waiting for data: {}", e);
            Thread.currentThread().interrupt();
        }
    }
}
