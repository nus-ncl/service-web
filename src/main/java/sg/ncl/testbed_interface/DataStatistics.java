package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by jng on 21/10/16.
 */
@Getter
@Setter
public class DataStatistics implements Serializable {

    private String id;
    private String userId;
    private ZonedDateTime date;

}
