package sg.ncl.testbed_interface;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TeamPageJoinTeamForm {
    
    @Size(min = 1, message="Team name cannot be empty")
    @Pattern(regexp="^[a-zA-Z0-9-]*$", message="Team name cannot have special characters")
    private String teamName;

    private String joinTeamReason;
    
    public TeamPageJoinTeamForm() {
    }
    
    public String getTeamName() {
        return teamName;
    }
    
    public void setTeamName(String name) {
        this.teamName = name;
    }
    
    @Override
    public String toString() {
        return "\n" + "Team name requesting to join: " + teamName + "\n";
    }

    public String getJoinTeamReason() {
        return joinTeamReason;
    }

    public void setJoinTeamReason(String joinTeamReason) {
        this.joinTeamReason = joinTeamReason;
    }
}
