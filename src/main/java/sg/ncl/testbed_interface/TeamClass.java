package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: Tran Ly Vu
 */
@Getter
@Setter
public class TeamClass implements Serializable {
    private String id;
    private String teamId;
    private String endDate;
    private String numberOfStudents;
}
