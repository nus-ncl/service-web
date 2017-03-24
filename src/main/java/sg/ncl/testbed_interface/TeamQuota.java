package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Tran Ly Vu, James
 */

@Getter
@Setter
public class TeamQuota implements Serializable {
    private String id;
    private String teamId;
    private String budget;
    private String amountUsed;
    private String resourcesLeft;
}
