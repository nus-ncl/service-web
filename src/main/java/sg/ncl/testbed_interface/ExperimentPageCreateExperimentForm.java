package sg.ncl.testbed_interface;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ExperimentPageCreateExperimentForm {

    @NotNull(message = "Team cannot be empty")
    @Size(min = 1, message = "Team cannot be empty")
    private String teamId;

    @NotNull(message = "Name cannot be empty")
    @Size(min = 1, message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Description cannot be empty")
    @Size(min = 1, message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Network Configuration cannot be empty")
    @Size(min = 1, message = "Network Configuration cannot be empty")
    private String nsFile;

    private String nsFileContent;
    private Integer idleSwap;
    private Integer maxDuration;

    public ExperimentPageCreateExperimentForm() {}

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNsFile() {
        return nsFile;
    }

    public void setNsFile(String nsFile) {
        this.nsFile = nsFile;
    }

    public String getNsFileContent() {
        return nsFileContent;
    }

    public void setNsFileContent(String nsFileContent) {
        this.nsFileContent = nsFileContent;
    }

    public Integer getIdleSwap() {
        return idleSwap;
    }

    public void setIdleSwap(Integer idleSwap) {
        this.idleSwap = idleSwap;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }
}
