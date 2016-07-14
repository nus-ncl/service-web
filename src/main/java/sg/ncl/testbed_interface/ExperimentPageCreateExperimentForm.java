package sg.ncl.testbed_interface;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ExperimentPageCreateExperimentForm {

    @NotNull(message = "Team cannot be empty")
    @Size(min = 1, message = "Team cannot be empty")
    private String teamId;

    private String teamName;

    private String teamNameWithId;

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

    void setTeamId(String teamId) {
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

    public String getTeamNameWithId() {
        return teamNameWithId;
    }

    public void setTeamNameWithId(String teamNameWithId) {
        this.teamNameWithId = teamNameWithId;
        // separate team name and team id
        // cannot split by '-' because id has '-'
        String[] parts = teamNameWithId.split(":");
        this.teamName = parts[0];
        this.teamId = parts[1];
    }

    public String getTeamName() {
        return teamName;
    }

    void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
