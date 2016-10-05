package sg.ncl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

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

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
