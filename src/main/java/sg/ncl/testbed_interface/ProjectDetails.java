package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ProjectDetails implements Serializable {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-d-yyyy");

    private Integer id;
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

    public ProjectDetails() {
        this.dateCreated = formatter.format(ZonedDateTime.now());
    }

    public ZonedDateTime getZonedDateCreated() {
        return ZonedDateTime.parse(dateCreated, formatter);
    }

    public void setZonedDateCreated(ZonedDateTime zonedDateCreated) {
        this.dateCreated = formatter.format(zonedDateCreated);
    }

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
