package sg.ncl.webssh;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = VncProperties.PREFIX)
@Getter
@Setter
public class VncProperties {

    public static final String PREFIX = "ncl.web.vnc";

    private String http;
    private String host;
    private String port;
}
