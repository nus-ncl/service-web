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

    private Long id;
    private String uri;
    private boolean malicious;
    private boolean scanned;

}
