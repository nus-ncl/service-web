package sg.ncl;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * @author by Te Ye
 */
@Configuration("sg.ncl.AppConfig")
public class AppConfig {

    @Inject
    private ErrorAttributes errorAttributes;

    @Inject
    private WebProperties webProperties;

    @Bean
    public AppErrorController appErrorController() {
        return new AppErrorController(errorAttributes, webProperties);
    }

    // thymleaf-layout-dialect
    // for fragments
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
