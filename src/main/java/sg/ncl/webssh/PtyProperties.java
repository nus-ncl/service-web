package sg.ncl.webssh;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = PtyProperties.PREFIX)
@Getter
@Setter
public class PtyProperties {

    public static final String PREFIX = "ncl.web.ssh.terminal";

    private String type;
    private Integer cols;
    private Integer rows;
    private Integer wpix;
    private Integer hpix;
}
