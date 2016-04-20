package sg.ncl.testbed_interface;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TeamPageApplyTeamForm {
    
    @Size(min=1, message="Team name cannot be empty")
    @Pattern(regexp="^[a-zA-Z0-9]*$", message="Team name cannot have special characters")
    private String teamName;
    
    @Size(min=1, message="Team description cannot be empty")
    private String teamDescription;
    
    private String teamWebsite;
    private String teamOrganizationType;
    
    @NotNull(message="Please select one of them")
    private String isPublic = null;
    
    @AssertTrue(message="Please read and accept the team owner policy")
    private boolean hasAcceptTeamOwnerPolicy;
    
    public TeamPageApplyTeamForm() {   
    }
    
    public String getTeamName() {
        return teamName;
    }
    
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    public String getTeamDescription() {
        return teamDescription;
    }
    
    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }
    
    public String getTeamWebsite() {
        return teamWebsite;
    }
    
    public void setTeamWebsite(String teamWebsite) {
        this.teamWebsite = teamWebsite;
    }
    
    public String getTeamOrganizationType() {
        return teamOrganizationType;
    }
    
    public void setTeamOrganizationType(String teamOrganizationType) {
        this.teamOrganizationType = teamOrganizationType;
    }
    
    public String getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }
    
    public boolean getHasAcceptTeamOwnerPolicy() {
        return hasAcceptTeamOwnerPolicy;
    }
    
    public void setHasAcceptTeamOwnerPolicy(boolean hasAcceptTeamOwnerPolicy) {
        this.hasAcceptTeamOwnerPolicy = hasAcceptTeamOwnerPolicy;
    }
    
    @Override
    public String toString() {
        return  "\n" + "Name: " + teamName + "\n" +
                "Description: " + teamDescription + "\n" +
                "Website: " + teamWebsite + "\n" +
                "Organization Type: " + teamOrganizationType + "\n" +
                "Visibility: " + isPublic + "\n" +
                "Accepted Policy? " + hasAcceptTeamOwnerPolicy;
    }
}
