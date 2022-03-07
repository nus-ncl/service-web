package sg.ncl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Te Ye
 * For declaring global variables shared between controllers
 */
@ConfigurationProperties(prefix = WebProperties.PREFIX)
@Getter
@Setter
public class WebProperties {

    public static final String PREFIX = "ncl.web.service.shared";

    private String sessionRoles;
    private String sessionEmail;
    private String sessionUserId;
    private String sessionUserFirstName;
    private String sessionJwtToken;
    private String sessionOsToken;
    private String sessionUserAccount;
}
