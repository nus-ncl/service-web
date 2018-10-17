package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectDetails implements Serializable {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy");

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
    private List<ProjectUsage> projectUsages;

    public ProjectDetails() {
        this.dateCreated = formatter.format(ZonedDateTime.now());
        this.projectUsages = new ArrayList<>();
    }

    public ZonedDateTime getZonedDateCreated() {
        return ZonedDateTime.of(LocalDate.parse(dateCreated, formatter), LocalTime.now(), ZoneId.of("Asia/Singapore"));
    }

    public void setZonedDateCreated(ZonedDateTime zonedDateCreated) {
        this.dateCreated = formatter.format(zonedDateCreated);
    }

    public void addProjectUsage(ProjectUsage projectUsage) {
        this.projectUsages.add(projectUsage);
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProjectDetails that = (ProjectDetails) o;
        return id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
