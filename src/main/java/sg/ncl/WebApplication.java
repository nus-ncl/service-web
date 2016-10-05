package sg.ncl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@SpringBootApplication
public class WebApplication {

    public static String ROOT = "uploaded-dataset-dir";
	public static String EXP_CONFIG_DIR = "uploaded-exp-config";
    
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
    
    @Bean
    CommandLineRunner init() {
        return (String[] args) -> {
            new File(ROOT).mkdir();
            new File(EXP_CONFIG_DIR).mkdir();
        };
    }

    // required for autowiring of RestTemplate
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ConnectionProperties connectionProperties() {
        return new ConnectionProperties();
    }
}
