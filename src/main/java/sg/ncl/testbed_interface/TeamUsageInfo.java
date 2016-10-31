package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dcszwang on 10/31/2016.
 */
@Getter
@Setter
public class TeamUsageInfo {

    public String id;

    public String name;

    public String usage; // in unit of node x hour, or "?" for unknown
}
