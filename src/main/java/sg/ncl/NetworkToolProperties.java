package sg.ncl;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = NetworkToolProperties.PREFIX)
@Getter
@Setter
public class NetworkToolProperties {

    public static final String PREFIX = "ncl.network.tool";

    private Boolean enabled;
    private Map<String, String> python;
    private Map<String, String> adapter;

    public String getCommand() {
        return python.get("command");
    }

    public String getVersion() {
        return python.get("version");
    }

    public String getProgram() {
        return python.get("program");
    }

    public String getOption() {
        return python.get("option");
    }

    public String getTemp() {
        return python.get("temp");
    }

    public Boolean isAdapterEnabled() {
        return adapter.get("enabled").equals("true");
    }

    public String getAdapterHost() {
        return adapter.get("host");
    }

    public Integer getAdapterPort() {
        return Integer.parseInt(adapter.get("port"));
    }
}
