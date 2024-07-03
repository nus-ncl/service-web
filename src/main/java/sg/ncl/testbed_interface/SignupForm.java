package sg.ncl.testbed_interface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.IOException;
import java.time.ZonedDateTime;

public class SignupForm {

    private static final String DEFAULT = "default";

    @NotEmpty
    private String username;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String confirmPassword;
    @NotEmpty
    private String phone;
    @NotEmpty
    private String jobTitle;
    @NotEmpty
    private String institution;
    @NotEmpty
    private String country;

    private String errorMsg = null;

    private String institutionWeb = "http://default.com";
    private String institutionAbbreviation = "defaultAbbrev";
    private String zipCode ="00000000";
    private String address2 = DEFAULT;
    private String city  = DEFAULT;
    private String address1 = DEFAULT;
    private String region = DEFAULT;

    public String getInstitutionWeb() {
        return institutionWeb;
    }

    public void setInstitutionWeb(String institutionWeb) {
        this.institutionWeb = institutionWeb;
    }

    public String getInstitutionAbbreviation() {
        return institutionAbbreviation;
    }

    public void setInstitutionAbbreviation(String institutionAbbreviation) {
        this.institutionAbbreviation = institutionAbbreviation;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public ZonedDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ZonedDateTime date = mapper.readValue(applicationDate, ZonedDateTime.class);
        this.applicationDate = date;
    }

    private ZonedDateTime applicationDate;


    public SignupForm() {

    }

    public SignupForm(String userName, String firstName, String lastName,
                      String email, String password, String confirmPassword,
                      String phone, String jobTitle, String institution,
                      String country) {

        this.username = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phone = phone;
        this.jobTitle = jobTitle;
        this.institution = institution;
        this.country = country;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
