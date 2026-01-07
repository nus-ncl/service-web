package sg.ncl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.validation.constraints.NotNull;

/**
 * @author by Te Ye
 * @updated by James, 27-Dec-2017
 *
 * References:
 * [1] https://www.mkyong.com/spring/spring-and-java-thread-example/
 */
@Configuration("sg.ncl.AppConfig")
public class AppConfig {

    @Bean
    public AppErrorController appErrorController(@NotNull ErrorAttributes errorAttributes) {
        return new AppErrorController(errorAttributes);
    }

    // thymleaf-layout-dialect
    // for fragments
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(10);
        pool.setMaxPoolSize(100);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule for ZonedDateTime support
        mapper.registerModule(new JavaTimeModule());
        // Polymorphic deserialization is disabled by default in Jackson
        // We explicitly ensure it stays disabled by not calling activateDefaultTyping()
        // This prevents deserialization vulnerabilities
        return mapper;
    }
}
