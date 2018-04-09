package sg.ncl.testbed_interface;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TeamPageApplyTeamForm {

    @Size(min = 2, max = 12, message = "Team name must be within 2 to 12 characters")
    @Pattern(regexp="^[a-zA-Z0-9-]*$", message ="Team name cannot have special characters")
    private String teamName;
    
    @NotEmpty(message="Team description cannot be empty")
    private String teamDescription;

    @NotEmpty(message = "Team website cannot be empty")
    private String teamWebsite = "http://default.com";

    private String teamOrganizationType;
    
    @NotNull(message="Please select one of them")
    private String isPublic = TeamVisibility.PUBLIC.toString();

    private boolean isClass;
    
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

    public boolean getIsClass() {return isClass;}

    
    @Override
    public String toString() {
        return  "\n" + "Name: " + teamName + "\n" +
                "Description: " + teamDescription + "\n" +
                "Website: " + teamWebsite + "\n" +
                "Organization Type: " + teamOrganizationType + "\n" +
                "Visibility: " + isPublic + "\n" +
                "Class" + isClass;
    }
}
