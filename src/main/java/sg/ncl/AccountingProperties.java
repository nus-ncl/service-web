package sg.ncl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Tran Ly Vu
 * For declaring global accounting variables
 */
@ConfigurationProperties(prefix = AccountingProperties.PREFIX)
@Getter
@Setter
public class AccountingProperties {

    public static final String PREFIX = "ncl.web.service.accounting";

    private String charges;
}
