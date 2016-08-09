package sg.ncl.testbed_interface;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignUpMergedForm {
	
	// Account Details Fields
    @Size(min=1, message="Email cannot be empty")
    private String email;

    @Size(min=1, message="Password cannot be empty")
    private String password;

    private String confirmPassword;
    private String errorMsg = null;
    
	// Personal Details Fields
    @Size(min=1, message = "First name cannot be empty")
    private String firstName;

    @Size(min=1, message ="Last name cannot be empty")
    private String lastName;

    @Pattern(regexp="^[0-9]*$", message = "Phone cannot have special characters" )
    @Range(min=6, message="Phone minimum 6 digits")
    private String phone;

    @NotEmpty(message = "Job title cannot be empty")
    private String jobTitle;

    @NotEmpty(message = "Institution cannot be empty")
    private String institution;

    @NotEmpty(message = "Institution Abbreviation cannot be empty")
    private String institutionAbbreviation;

    @NotEmpty(message = "Website cannot be empty")
    private String website;

    @NotEmpty(message = "Address 1 cannot be empty")
    private String address1;

    private String address2;

    @NotEmpty(message = "Country cannot be empty")
    private String country;

    @NotEmpty(message = "City cannot be empty")
    private String city;

    @NotEmpty(message = "Province cannot be empty")
    private String province;

    @Pattern(regexp="^[0-9]*$", message = "Postal code cannot have special characters" )
    @Range(min=6, message="Postal code minimum 6 digits")
    private String postalCode;
    
    // Create New Team Fields
    @Pattern(regexp="^[a-zA-Z0-9]*$", message="Team name cannot have special characters")
    private String teamName;

    private String teamDescription;
    
    private String teamWebsite;
    private String teamOrganizationType;
    
    // defaults to public
    private String isPublic = "PUBLIC";

    private boolean hasAcceptTeamOwnerPolicy;
    
    // Join New Team Fields
    @Pattern(regexp="^[a-zA-Z0-9]*$", message="Team name cannot have special characters")
    private String joinTeamName;

    // A way to display error messages for create new team form
    // Required as the controller cannot use redirectFlashAttributes to display errors; will cause the form fields to reset
    private String errorTeamDescription;
    private String errorTeamWebsite;
    private String errorTeamOwnerPolicy;

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

    @AssertTrue(message="Password should matched")
    public boolean isValid() {
        return this.password.equals(this.confirmPassword);
    }
    
    //--------------------------------------- Personal Details ---------------------------------------
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

    //--------------------------------------- Errors ---------------------------------------
    public String getErrorTeamDescription() {
        return errorTeamDescription;
    }

    public void setErrorTeamDescription(String errorTeamDescription) {
        this.errorTeamDescription = errorTeamDescription;
    }

    public String getErrorTeamWebsite() {
        return errorTeamWebsite;
    }

    public void setErrorTeamWebsite(String errorTeamWebsite) {
        this.errorTeamWebsite = errorTeamWebsite;
    }

    public String getErrorTeamOwnerPolicy() {
        return errorTeamOwnerPolicy;
    }

    public void setErrorTeamOwnerPolicy(String errorTeamOwnerPolicy) {
        this.errorTeamOwnerPolicy = errorTeamOwnerPolicy;
    }
}
