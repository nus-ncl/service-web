package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Te Ye on 04-Apr-18.
 *
 * nodes_id_list is separated by comma
 */
@Getter
@Setter
public class ReservationStatusForm {
    private String teamId;
//    private String nodes_id_list;
    private Integer numNodes;
    private String machineType;
}
