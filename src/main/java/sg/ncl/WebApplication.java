package sg.ncl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import sg.ncl.webssh.SshProperties;
import sg.ncl.webssh.PtyProperties;
import sg.ncl.webssh.VncProperties;
import sg.ncl.webssh.WebSocketProperties;

@SpringBootApplication
public class WebApplication {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ConnectionProperties connectionProperties() {
        return new ConnectionProperties();
    }

    @Bean
    WebProperties webProperties() {
        return new WebProperties();
    }

    @Bean
    AccountingProperties accountingProperties() {
        return new AccountingProperties();
    }

    @Bean
    VncProperties vncProperties() {
        return new VncProperties();
    }

    @Bean
    SshProperties sshProperties() {
        return new SshProperties();
    }

    @Bean
    PtyProperties ptyProperties() {
        return new PtyProperties();
    }

    @Bean
    WebSocketProperties webSocketProperties() {
        return new WebSocketProperties();
    }

    @Bean
    GpuProperties gpuProperties() {
        return new GpuProperties();
    }

    @Bean
    NetworkToolProperties networkToolProperties() {
        return new NetworkToolProperties();
    }

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(WebApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }

}
