package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

@Getter
@Setter
public class ProjectDetails implements Serializable {

    private Integer id = 0;
    @NotEmpty
    private String organisationType;
    @NotEmpty
    private String organisationName;
    @NotEmpty
    private String projectName;
    @NotEmpty
    private String owner;
    @NotEmpty
    private String dateCreated;
    private boolean education;
    private boolean serviceTool;
    private String supportedBy;

    @Override
    public String toString() {
        return "ProjectDetails{id=" + id +
                ",organisationType=" + organisationType +
                ",organisationName=" + organisationName +
                ",projectName=" + projectName +
                ",owner=" + owner +
                ",dateCreated=" + dateCreated +
                ",education=" + education +
                ",serviceTool=" + serviceTool +
                ",supportedBy=" + supportedBy + "}";
    }
}
