package sg.ncl.webssh;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private static int count = 0;

    private Properties config;
    private JSch jSch;
    private Session session;
    private Channel channel;
    private PrintStream inputToShell;

    @Autowired
    ApplicationContext context;

    @PostConstruct
    public void init() {
        jSch = new JSch();
        config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        log.info("Init Bean #{}", ++count);
    }

    public void connect(String socketId, String user, String host, String port, String pass) {
        log.info("Bean #{} [Session '{}', User '{}', Host '{}', Port '{}', Password '{}']", count, socketId, user, host, port, pass);
        try {
            session = jSch.getSession(user, host, Integer.parseInt(port));
            session.setPassword(pass);
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("shell");
//            ((ChannelShell) channel).setAgentForwarding(true);
//            ((ChannelShell) channel).setPtyType("vt102");
            channel.connect();
        } catch (JSchException jsche) {
            jsche.printStackTrace();
        }
        try {
            ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
            SentOutputTask sentOutputTask = (SentOutputTask) context.getBean("sentOutputTask");
            sentOutputTask.setOutput(new BufferedInputStream(channel.getInputStream(), BUFFER_LEN));
            taskExecutor.execute(sentOutputTask);
            inputToShell = new PrintStream(channel.getOutputStream(), true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        log.info("Connected!");
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
        log.info("Destroy Bean #{}", count);
    }
}
