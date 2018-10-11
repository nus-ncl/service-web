package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProjectUsage implements Serializable {

    private Integer id;
    private Integer month;
    private Integer usage;
}
