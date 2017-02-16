package sg.ncl.testbed_interface;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignUpMergedForm {

    private static final String DEFAULT = "default";
	
	// Account Details Fields
    @Size(min=1, message="Email cannot be empty")
    private String email;

    @Size(min=8, message="Password must have at least 8 characters")
    @Pattern.List({
            @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit"),
            @Pattern(regexp = "(?=.*[a-zA-Z]).+", message = "Password must contain one alphabet"),
            @Pattern(regexp = "[^\\s]+", message = "Password cannot contain whitespace"),
            @Pattern(regexp = "[^&<>\"]+", message = "Password cannot contain '&', '<', '>', '\"'")
    })
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
    private String institutionAbbreviation = "defaultAbbrev";

    @NotEmpty(message = "Website cannot be empty")
    private String website = "http://default.com";

    @NotEmpty(message = "Address 1 cannot be empty")
    private String address1 = DEFAULT;

    private String address2 = DEFAULT;

    @NotEmpty(message = "Country cannot be empty")
    private String country;

    @NotEmpty(message = "City cannot be empty")
    private String city = DEFAULT;

    @NotEmpty(message = "Province cannot be empty")
    private String province = DEFAULT;

    @Pattern(regexp="^[0-9]*$", message = "Postal code cannot have special characters" )
    private String postalCode = "00000000";
    
    // Create New Team Fields
    @Pattern(regexp="^[a-zA-Z0-9-]*$", message="Team name cannot have special characters")
    private String teamName;

    private String teamDescription;
    
    private String teamWebsite = "http://default.com";
    private String teamOrganizationType;
    
    // defaults to public
    private String isPublic = "PUBLIC";

    private boolean hasAcceptTeamOwnerPolicy;
    
    // Join New Team Fields
    @Pattern(regexp="^[a-zA-Z0-9-]*$", message="Team name cannot have special characters")
    private String joinTeamName;

    private String joinTeamReason;

    // A way to display error messages for create new team form
    // Required as the controller cannot use redirectFlashAttributes to display errors; will cause the form fields to reset
    private String errorTeamName;
    private String errorTeamDescription;
    private String errorTeamWebsite;
    private String errorTeamOwnerPolicy;

    private boolean isValid;

	public SignUpMergedForm() {
		
	}

    public SignUpMergedForm(String country, String institution, String jobTitle, String confirmPassword, String password, String postalCode) {
        this.country = country;
        this.institution = institution;
        this.jobTitle = jobTitle;
        this.confirmPassword = confirmPassword;
        this.password = password;
        this.postalCode = postalCode;
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

    public boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    @AssertTrue(message="Passwords should matched")
    public boolean isValid() {
        isValid = this.password.equals(this.confirmPassword);
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
        this.website = website.startsWith("http") ? website : "http://" + website;
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
        this.teamWebsite = teamWebsite.startsWith("http") ? teamWebsite : "http://" + teamWebsite;
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

    public String getJoinTeamReason() {
        return joinTeamReason;
    }

    public void setJoinTeamReason(String joinTeamReason) {
        this.joinTeamReason = joinTeamReason;
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

    public String getErrorTeamName() {
        return errorTeamName;
    }

    public void setErrorTeamName(String errorTeamName) {
        this.errorTeamName = errorTeamName;
    }
}
