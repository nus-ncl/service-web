package sg.ncl.testbed_interface;

import java.io.Serializable;

/**
 * @author Te Ye
 */
public class User2 implements Serializable {

    private String id;
    private boolean emailVerified;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String email;
    private String phone;
    private String address1;
    private String address2;
    private String country;
    private String city;
    private String region;
    private String postalCode;
    private String institution;
    private String institutionAbbreviation;
    private String institutionWeb;
    private String status;
    private String roles;

    private String password;
    private String confirmPassword;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInstitutionWeb() {
        return institutionWeb;
    }

    public void setInstitutionWeb(String institutionWeb) {
        this.institutionWeb = institutionWeb;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
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

    public boolean isPasswordMatch() {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        } else if (!password.equals(confirmPassword)) {
            return false;
        }
        return true;
    }

    public boolean isPasswordValid() {
        // more than 8 characters
        // contain at least 1 digit
        // contain at least 1 alphabet
        // does not contain whitespace
        return (password.length() >= 8 &&
                password.matches("(?=.*[0-9]).+") &&
                password.matches("(?=.*[a-zA-Z]).+") &&
                password.matches("[^\\s]+"));
    }
}
