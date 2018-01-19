package sg.ncl.webssh;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = SshProperties.PREFIX)
@Getter
@Setter
public class SshProperties {

    public static final String PREFIX = "ncl.web.ssh.deter.user";

    private String host;
    private String port;
}
