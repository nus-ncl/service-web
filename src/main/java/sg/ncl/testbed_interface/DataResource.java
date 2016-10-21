package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by jng on 20/10/16.
 */
@Getter
@Setter
public class DataResource implements Serializable {

    private String id;
    private String uri;

}
