package sg.ncl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = GpuProperties.PREFIX)
@Getter
@Setter
public class GpuProperties {

    public static final String PREFIX = "ncl.web.gpu";

    private List<Domain> domains = new ArrayList<>();

    @Getter
    @Setter
    public static class Domain {
        private String host;
        private String port;

        @Override
        public String toString() {
            return "Domain{host='" + host + "', port='" + port + "'}";
        }
    }
}
