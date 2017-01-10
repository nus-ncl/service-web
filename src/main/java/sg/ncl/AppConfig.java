package sg.ncl;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;

import javax.validation.constraints.NotNull;

/**
 * @author by Te Ye
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
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(getClass().getClassLoader().toString() + "messages.properties");
        return messageSource;
    }
}
