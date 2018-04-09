package sg.ncl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = NetworkToolProperties.PREFIX)
@Getter
@Setter
public class NetworkToolProperties {

    public static final String PREFIX = "ncl.network.tool.python";

    private String command;
    private String version;
    private String program;
    private String option;
    private String temp;
}
