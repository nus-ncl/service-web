package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * Created by dcsjnh on 12/5/2017.
 */
@Getter
@Setter
@Slf4j
public class DataCategory implements Serializable {

    private Long id;
    private String name;
    private String description;

}
