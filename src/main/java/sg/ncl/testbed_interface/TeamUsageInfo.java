package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dcszwang on 10/31/2016.
 */
@Getter
@Setter
public class TeamUsageInfo {

    private String id;

    private String name;

    private String usage; // in unit of node x hour, or "?" for unknown

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}
