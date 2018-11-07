package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class SignUpMergedFormTest {

    private Validator validator;

    @Before
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testGetEmail() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getEmail(), is(nullValue()));
    }

    @Test
    public void testSetEmail() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setEmail(str);
        assertThat(one.getEmail(), is(str));
    }

    @Test
    public void testGetPassword() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getPassword(), is(nullValue()));
    }

    @Test
    public void testSetPassword() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setPassword(str);
        assertThat(one.getPassword(), is(str));
    }

    @Test
    public void testGetConfirmPassword() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getConfirmPassword(), is(nullValue()));
    }

    @Test
    public void testSetConfirmPassword() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setConfirmPassword(str);
        assertThat(one.getConfirmPassword(), is(str));
    }

    @Test
    public void testPasswordMinimumCharacters() {
        // craft from constructor to ensure only the password field will give errors
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "123456a", "123456a", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Password must have at least 8 characters")));
    }

    @Test
    public void testPasswordAtLeastOneDigit() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "abcdefgh", "abcdefgh", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Password must contain one digit")));
    }

    @Test
    public void testPasswordAtLeastOneAlphabet() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "12345678", "12345678", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Password must contain one alphabet")));
    }

    @Test
    public void testPasswordWithWhitespace() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567a ", "1234567a ", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Password cannot contain whitespace")));
    }

    @Test
    public void testPasswordUpperCaseGood() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567A", "1234567A", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations, is(empty()));
    }

    @Test
    public void testPasswordLowerCaseGood() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567a", "1234567a", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations, is(empty()));
    }

    @Test
    public void testPasswordWithSpecialCharacters() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567a!", "1234567a!", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations, is(empty()));
    }

    @Test
    public void testPasswordWithPlus() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567a+", "1234567a+", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations, is(empty()));
    }

    @Test
    public void testPasswordWithAmpersands() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567a&", "1234567a&", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Password cannot contain '&' '<' '>' '|' '/' '\\' '`' '\'' '\"'")));
    }

    @Test
    public void testPasswordWithLessThanSign() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567a<", "1234567a<", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Password cannot contain '&' '<' '>' '|' '/' '\\' '`' '\'' '\"'")));
    }

    @Test
    public void testPasswordWithGreaterThanSign() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567a>", "1234567a>", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Password cannot contain '&' '<' '>' '|' '/' '\\' '`' '\'' '\"'")));
    }

    @Test
    public void testPasswordWithDoubleQuote() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "job", "1234567a\"", "1234567a\"", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Password cannot contain '&' '<' '>' '|' '/' '\\' '`' '\'' '\"'")));
    }

    @Test
    public void testGetErrorMsg() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getErrorMsg(), is(nullValue()));
    }

    @Test
    public void testSetErrorMsg() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setErrorMsg(str);
        assertThat(one.getErrorMsg(), is(str));
    }

    @Test
    public void testGetFirstName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getFirstName(), is(nullValue()));
    }

    @Test
    public void testSetFirstName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setFirstName(str);
        assertThat(one.getFirstName(), is(str));
    }

    @Test
    public void testGetLastName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getLastName(), is(nullValue()));
    }

    @Test
    public void testSetLastName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setLastName(str);
        assertThat(one.getLastName(), is(str));
    }

    @Test
    public void testGetPhone() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getPhone(), is(nullValue()));
    }

    @Test
    public void testSetPhone() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setPhone(str);
        assertThat(one.getPhone(), is(str));
    }

    @Test
    public void testGetJobTitle() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getJobTitle(), is(nullValue()));
    }

    @Test
    public void testSetJobTitle() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setJobTitle(str);
        assertThat(one.getJobTitle(), is(str));
    }

    @Test
    public void testJobValidate() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "institution", "", "1234567a", "1234567a", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Job title cannot be empty")));
    }

    @Test
    public void testGetInstitution() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getInstitution(), is(nullValue()));
    }

    @Test
    public void testSetInstitution() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setInstitution(str);
        assertThat(one.getInstitution(), is(str));
    }

    @Test
    public void testGetInstitutionAbbreviation() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getInstitutionAbbreviation(), is("defaultAbbrev"));
    }

    @Test
    public void testSetInstitutionAbbreviation() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setInstitutionAbbreviation(str);
        assertThat(one.getInstitutionAbbreviation(), is(str));
    }

    @Test
    public void testInstitutionValidate() {
        final SignUpMergedForm one = new SignUpMergedForm("country", "", "job", "1234567a", "1234567a", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Institution cannot be empty")));
    }

    @Test
    public void testGetWebsite() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getWebsite(), is("http://default.com"));
    }

    @Test
    public void testSetWebsite() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = "http://" + RandomStringUtils.randomAlphanumeric(20);
        one.setWebsite(str);
        assertThat(one.getWebsite(), is(str));
    }

    @Test
    public void testGetAddress1() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getAddress1(), is("default"));
    }

    @Test
    public void testSetAddress1() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setAddress1(str);
        assertThat(one.getAddress1(), is(str));
    }

    @Test
    public void testGetAddress2() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getAddress2(), is("default"));
    }

    @Test
    public void testSetAddress2() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setAddress2(str);
        assertThat(one.getAddress2(), is(str));
    }

    @Test
    public void testGetCountry() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getCountry(), is(nullValue()));
    }

    @Test
    public void testSetCountry() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setCountry(str);
        assertThat(one.getCountry(), is(str));
    }

    @Test
    public void testCountryValidate() {
        final SignUpMergedForm one = new SignUpMergedForm("", "institution", "job", "1234567a", "1234567a", "123456");
        Set<ConstraintViolation<SignUpMergedForm>> constraintViolations = validator.validate(one);
        assertThat(constraintViolations.size(), is(1));
        constraintViolations.forEach(violation -> assertThat(violation.getMessageTemplate(), is("Country cannot be empty")));
    }

    @Test
    public void testGetCity() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getCity(), is("default"));
    }

    @Test
    public void testSetCity() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setCity(str);
        assertThat(one.getCity(), is(str));
    }

    @Test
    public void testGetProvince() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getProvince(), is("default"));
    }

    @Test
    public void testSetProvince() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setProvince(str);
        assertThat(one.getProvince(), is(str));
    }

    @Test
    public void testGetPostalCode() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getPostalCode(), is("00000000"));
    }

    @Test
    public void testSetPostalCode() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setPostalCode(str);
        assertThat(one.getPostalCode(), is(str));
    }

    //--------------------------------------------
    // Create new team
    //--------------------------------------------
    @Test
    public void testGetTeamName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getTeamName(), is(nullValue()));
    }

    @Test
    public void testSetTeamName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamName(str);
        assertThat(one.getTeamName(), is(str));
    }

    @Test
    public void testGetTeamDescription() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getTeamDescription(), is(nullValue()));
    }

    @Test
    public void testSetTeamDescription() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamDescription(str);
        assertThat(one.getTeamDescription(), is(str));
    }

    @Test
    public void testGetTeamWebsite() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getTeamWebsite(), is("http://default.com"));
    }

    @Test
    public void testSetTeamWebsite() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = "http://" + RandomStringUtils.randomAlphanumeric(20);
        one.setTeamWebsite(str);
        assertThat(one.getTeamWebsite(), is(str));
    }

    @Test
    public void testGetTeamOrganizationType() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getTeamOrganizationType(), is(nullValue()));
    }

    @Test
    public void testSetTeamOrganizationType() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamOrganizationType(str);
        assertThat(one.getTeamOrganizationType(), is(str));
    }

    @Test
    public void testGetTeamIsPublic() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getIsPublic(), is("PUBLIC"));
    }

    @Test
    public void testSetTeamIsPublic() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setIsPublic(str);
        assertThat(one.getIsPublic(), is(str));
    }

    @Test
    public void testGetHasAcceptPolicy() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getHasAcceptTeamOwnerPolicy(), is(false));
    }

    @Test
    public void testSetHasAcceptPolicy() {
        final SignUpMergedForm one = new SignUpMergedForm();
        one.setHasAcceptTeamOwnerPolicy(true);
        assertThat(one.getHasAcceptTeamOwnerPolicy(), is(true));
    }

    //--------------------------------------------
    // Join existing team
    //--------------------------------------------
    @Test
    public void testGetJoinTeamName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getJoinTeamName(), is(nullValue()));
    }

    @Test
    public void testSetJoinTeamName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setJoinTeamName(str);
        assertThat(one.getJoinTeamName(), is(str));
    }

    //--------------------------------------------
    // Errors
    //--------------------------------------------
    @Test
    public void testGetErrorTeamName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getErrorTeamName(), is(nullValue()));
    }

    @Test
    public void testSetErrorTeamName() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setErrorTeamName(str);
        assertThat(one.getErrorTeamName(), is(str));
    }

    @Test
    public void testGetErrorTeamDescription() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getErrorTeamDescription(), is(nullValue()));
    }

    @Test
    public void testSetErrorTeamDescription() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setErrorTeamDescription(str);
        assertThat(one.getErrorTeamDescription(), is(str));
    }

    @Test
    public void testGetErrorTeamWebsite() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getErrorTeamWebsite(), is(nullValue()));
    }

    @Test
    public void testSetErrorTeamWebsite() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setErrorTeamWebsite(str);
        assertThat(one.getErrorTeamWebsite(), is(str));
    }

    @Test
    public void testGetErrorTeamOwnerPolicy() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getErrorTeamOwnerPolicy(), is(nullValue()));
    }

    @Test
    public void testSetErrorTeamOwnerPolicy() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setErrorTeamOwnerPolicy(str);
        assertThat(one.getErrorTeamOwnerPolicy(), is(str));
    }

    @Test
    public void testGetIsValid() {
        final SignUpMergedForm one = new SignUpMergedForm();
        assertThat(one.getIsValid(), is(false));
    }

    @Test
    public void testSetIsValid() {
        final SignUpMergedForm one = new SignUpMergedForm();
        one.setIsValid(true);
        assertThat(one.getIsValid(), is(true));
    }

    @Test
    public void testIsValidPasswordGood() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setPassword(str);
        one.setConfirmPassword(str);
        assertThat(one.isValid(), is(true));
    }
    @Test
    public void testIsValidPasswordBad() {
        final SignUpMergedForm one = new SignUpMergedForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        final String str2 = RandomStringUtils.randomAlphanumeric(20);
        one.setPassword(str);
        one.setConfirmPassword(str2);
        assertThat(one.isValid(), is(false));
    }

}
