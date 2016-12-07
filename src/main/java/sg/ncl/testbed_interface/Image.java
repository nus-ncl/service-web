package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import sg.ncl.domain.ImageVisibility;

import javax.validation.constraints.Size;

/**
 * Created by dcsyeoty on 29-Oct-16.
 */
@Getter
@Setter
public class Image {

    private String id;
    private String teamId;
    private String nodeId;

    @Size(min = 2, message = "Image name too short, minimum 2 characters")
    private String imageName;

    private String description = "default";
    private String currentOS = "Ubuntu1404-64-STD";
    private ImageVisibility visibility = ImageVisibility.PRIVATE;

}
