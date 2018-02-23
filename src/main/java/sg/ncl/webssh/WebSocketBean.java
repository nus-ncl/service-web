package sg.ncl.webssh;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import static sg.ncl.webssh.SentOutputTask.BUFFER_LEN;

/**
 * Singleton class to hold a ssh session.
 *
 * References:
 * [1] http://www.jcraft.com/jsch/
 * [2] https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket
 * [3] http://codenav.org/code.html?project=/com/nitorcreations/willow-servers/0.1&path=/Source%20Packages/com.nitorcreations.willow.ssh/SecureShellWS.java
 * [4] https://www.mkyong.com/spring/spring-and-java-thread-example/
 * [5] https://stackoverflow.com/questions/25789245/how-to-get-jsch-shell-command-output-in-string
 */

@Component
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class WebSocketBean {

    private Session session;
    private Channel channel;
    private PrintStream inputToShell;

    @Autowired
    ApplicationContext context;

    @Autowired
    SshProperties sshProperties;

    @Autowired
    PtyProperties ptyProperties;

    public void connect(String user, String pass, String qualified) {
        JSch jSch = new JSch();
        MyUserInfo userInfo = new MyUserInfo();
        userInfo.setPassword(pass);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        try {
            session = jSch.getSession(user, sshProperties.getHost(), Integer.parseInt(sshProperties.getPort()));
            session.setUserInfo(userInfo);
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("shell");
            ((ChannelShell) channel).setPtyType(
                    ptyProperties.getType(),
                    ptyProperties.getCols(),
                    ptyProperties.getRows(),
                    ptyProperties.getWpix(),
                    ptyProperties.getHpix());
            channel.connect();
        } catch (JSchException jsche) {
            log.error("jsch connect: {}", jsche);
        }

        try {
            ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
            SentOutputTask sentOutputTask = (SentOutputTask) context.getBean("sentOutputTask");
            sentOutputTask.setOutput(new BufferedInputStream(channel.getInputStream(), BUFFER_LEN), user, qualified);
            taskExecutor.execute(sentOutputTask);
            inputToShell = new PrintStream(channel.getOutputStream(), true);
        } catch (IOException ioe) {
            log.error("get channel stream: {}", ioe);
        }
    }

    public PrintStream getInputToShell() {
        return inputToShell;
    }

    @PreDestroy
    public void destroy() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        session = null;
    }
}
