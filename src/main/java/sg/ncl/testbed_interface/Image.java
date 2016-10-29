package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import sg.ncl.domain.ImageVisibility;

/**
 * Created by dcsyeoty on 29-Oct-16.
 */
@Getter
@Setter
public class Image {

    private String id;
    private String teamId;
    private String nodeId;
    private String imageName;
    private String description;
    private ImageVisibility visibility;

}
