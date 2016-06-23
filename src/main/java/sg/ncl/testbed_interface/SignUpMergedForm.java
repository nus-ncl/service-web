package sg.ncl.testbed_interface;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignUpMergedForm {
	
	// Account Details Fields
	@NotNull(message="Email cannot be empty")
    @Size(min=1, message="Email cannot be empty")
    private String email;
    
    @NotNull(message="Password cannot be empty")
    @Size(min=1, message="Password cannot be empty")
    private String password;
    
    private String confirmPassword;
    private String errorMsg = null;
    
	// Personal Details Fields
    private String name;
    private String firstName;
    private String lastName;
    private String phone;
    private String jobTitle;
    private String institution;
    private String institutionAbbreviation;
    private String website;
    private String address1;
    private String address2;
    private String country;
    private String city;
    private String province;
    private String postalCode;
    
    // Create New Team Fields
    @Size(min=1, message="Team name cannot be empty")
    @Pattern(regexp="^[a-zA-Z0-9]*$", message="Team name cannot have special characters")
    private String teamName;
    
    @Size(min=1, message="Team description cannot be empty")
    private String teamDescription;
    
    private String teamWebsite;
    private String teamOrganizationType;
    
    // defaults to public
    private String isPublic = "PUBLIC";
    
    @AssertTrue(message="Please read and accept the team owner policy")
    private boolean hasAcceptTeamOwnerPolicy;
    
    // Join New Team Fields
    @Size(min = 1, message="Team name cannot be empty")
    @Pattern(regexp="^[a-zA-Z0-9]*$", message="Team name cannot have special characters")
    private String joinTeamName;

	public SignUpMergedForm() {
		
	}
	
	//--------------------------------------- Account Details ---------------------------------------

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    public boolean isPasswordMatch() {
        if (!password.equals(confirmPassword)) {
            return false;
        }
        return true;
    }
    
    //--------------------------------------- Personal Details ---------------------------------------

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    public String getInstitutionAbbreviation() {
        return institutionAbbreviation;
    }
    public void setInstitutionAbbreviation(String institutionAbbreviation) {
        this.institutionAbbreviation = institutionAbbreviation;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    //--------------------------------------- Create New Team Details ---------------------------------------
    
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
    
    //--------------------------------------- Join Team Details ---------------------------------------
	public String getJoinTeamName() {
		return joinTeamName;
	}

	public void setJoinTeamName(String joinTeamName) {
		this.joinTeamName = joinTeamName;
	}
}
