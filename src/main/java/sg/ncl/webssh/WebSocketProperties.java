package sg.ncl.webssh;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = WebSocketProperties.PREFIX)
@Getter
@Setter
public class WebSocketProperties {

    public static final String PREFIX = "ncl.web.cors";

    private String httpMode;
    private String domain;
}
