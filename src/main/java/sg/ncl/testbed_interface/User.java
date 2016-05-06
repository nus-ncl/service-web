package sg.ncl.testbed_interface;

/**
 * 
 * User model
 * @author yeoteye
 * 
 */
public class User {
    
    private int userId;
    private String email = "";
    private String name = "";
    private String password = "";
    private String confirmPassword = "";
    private String role; // normal, admin
    private String jobTitle = "";
    private String institution = "";
    private String institutionAbbreviation = "";
    private String website = "";
    private String address1 = "";
    private String address2 = "";
    private String country = "";
    private String city = "";
    private String province = "";
    private String postalCode = "";
    private boolean isEmailVerified;
    
    public User() {
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }
    
    @Override
    public String toString() {
    	return "userId: " + userId + "\n" +
    			"name: " + name + "\n";
    }
    
    // should not allow user to edit email address
    public boolean updateEmail(String editedEmail) {
    	if (email.equals(editedEmail)) {
    		return false;
    	} else {
        	email = editedEmail;
        	return true;
    	}
    }
    
    public boolean updateName(String editedName) {
    	if (name.equals(editedName)) {
    		return false;
    	} else {
    		name = editedName;
        	return true;
    	}
    }
    
    public boolean updatePassword(String editedPassword) {
    	if (password.equals(editedPassword) || editedPassword.isEmpty()) {
    		return false;
    	} else {
    		password = editedPassword;
    		confirmPassword = editedPassword;
        	return true;
    	}
    }
    
    public boolean updateJobTitle(String editedJobTitle) {
    	if (jobTitle.equals(editedJobTitle)) {
    		return false;
    	} else {
    		jobTitle = editedJobTitle;
        	return true;
    	}
    }
    
    public boolean updateInstitution(String editedInstitution) {
    	if (institution.equals(editedInstitution)) {
    		return false;
    	} else {
    		institution = editedInstitution;
        	return true;
    	}
    }
    
    public boolean updateInstitutionAbbreviation(String editedInstitutionAbbreviation) {
    	if (institutionAbbreviation.equals(editedInstitutionAbbreviation)) {
    		return false;
    	} else {
    		institutionAbbreviation = editedInstitutionAbbreviation;
        	return true;
    	}
    }
    
    public boolean updateWebsite(String editedWebsite) {
    	if (website.equals(editedWebsite)) {
    		return false;
    	} else {
    		website = editedWebsite;
        	return true;
    	}
    }
    
    public boolean updateAddress1(String editedAddress) {
    	if (address1.equals(editedAddress)) {
    		return false;
    	} else {
    		address1 = editedAddress;
        	return true;
    	}
    }
    
    public boolean updateAddress2(String editedAddress) {
    	if (address2.equals(editedAddress)) {
    		return false;
    	} else {
    		address2 = editedAddress;
        	return true;
    	}
    }
    
    public boolean updateCountry(String editedCountry) {
    	if (country.equals(editedCountry)) {
    		return false;
    	} else {
    		country = editedCountry;
        	return true;
    	}
    }
    
    public boolean updateCity(String editedCity) {
    	if (city.equals(editedCity)) {
    		return false;
    	} else {
    		city = editedCity;
        	return true;
    	}
    }
    
    public boolean updateProvince(String editedProvince) {
    	if (province.equals(editedProvince)) {
    		return false;
    	} else {
    		province = editedProvince;
        	return true;
    	}
    }
    
    public boolean updatePostalCode(String editedPostalCode) {
    	if (postalCode.equals(editedPostalCode)) {
    		return false;
    	} else {
    		postalCode = editedPostalCode;
        	return true;
    	}
    }
    
    public boolean isPasswordMatch() {
        if (!password.equals(confirmPassword)) {
            return false;
        }
        return true;
    }
}
