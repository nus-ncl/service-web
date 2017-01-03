package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class UserTest {

    @Test
    public void testGetId() {
        final User2 user = new User2();
        assertThat(user.getId(), is(nullValue()));
    }

    @Test
    public void testSetId() {
        final User2 user = new User2();
        final String id = RandomStringUtils.randomAlphanumeric(20);
        user.setId(id);
        assertThat(user.getId(), is(id));
    }

    @Test
    public void testGetEmailVerified() {
        final User2 user = new User2();
        assertThat(user.getEmailVerified(), is(false));
    }

    @Test
    public void testSetEmailVerified() {
        final User2 user = new User2();
        user.setEmailVerified(true);
        assertThat(user.getEmailVerified(), is(true));
    }

    @Test
    public void testGetFirstName() {
        final User2 user = new User2();
        assertThat(user.getFirstName(), is(nullValue()));
    }

    @Test
    public void testSetFirstName() {
        final User2 user = new User2();
        final String name = RandomStringUtils.randomAlphanumeric(20);
        user.setFirstName(name);
        assertThat(user.getFirstName(), is(name));
    }

    @Test
    public void testGetLastName() {
        final User2 user = new User2();
        assertThat(user.getLastName(), is(nullValue()));
    }

    @Test
    public void testSetLastName() {
        final User2 user = new User2();
        final String name = RandomStringUtils.randomAlphanumeric(20);
        user.setLastName(name);
        assertThat(user.getLastName(), is(name));
    }

    @Test
    public void testGetJobTitle() {
        final User2 user = new User2();
        assertThat(user.getJobTitle(), is(nullValue()));
    }

    @Test
    public void testSetJobTitle() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setJobTitle(str);
        assertThat(user.getJobTitle(), is(str));
    }

    @Test
    public void testGetEmail() {
        final User2 user = new User2();
        assertThat(user.getEmail(), is(nullValue()));
    }

    @Test
    public void testSetEmail() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setEmail(str);
        assertThat(user.getEmail(), is(str));
    }

    @Test
    public void testGetPhone() {
        final User2 user = new User2();
        assertThat(user.getPhone(), is(nullValue()));
    }

    @Test
    public void testSetPhone() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setPhone(str);
        assertThat(user.getPhone(), is(str));
    }

    @Test
    public void testGetAddress1() {
        final User2 user = new User2();
        assertThat(user.getAddress1(), is(nullValue()));
    }

    @Test
    public void testSetAddress1() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setAddress1(str);
        assertThat(user.getAddress1(), is(str));
    }

    @Test
    public void testGetAddress2() {
        final User2 user = new User2();
        assertThat(user.getAddress2(), is(nullValue()));
    }

    @Test
    public void testSetAddress2() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setAddress2(str);
        assertThat(user.getAddress2(), is(str));
    }

    @Test
    public void testGetCountry() {
        final User2 user = new User2();
        assertThat(user.getCountry(), is(nullValue()));
    }

    @Test
    public void testSetCountry() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setCountry(str);
        assertThat(user.getCountry(), is(str));
    }

    @Test
    public void testGetCity() {
        final User2 user = new User2();
        assertThat(user.getCity(), is(nullValue()));
    }

    @Test
    public void testSetCity() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setCity(str);
        assertThat(user.getCity(), is(str));
    }

    @Test
    public void testGetRegion() {
        final User2 user = new User2();
        assertThat(user.getRegion(), is(nullValue()));
    }

    @Test
    public void testSetRegion() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setRegion(str);
        assertThat(user.getRegion(), is(str));
    }

    @Test
    public void testGetPostalCode() {
        final User2 user = new User2();
        assertThat(user.getPostalCode(), is(nullValue()));
    }

    @Test
    public void testSetPostalCode() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setPostalCode(str);
        assertThat(user.getPostalCode(), is(str));
    }

    @Test
    public void testGetInstitution() {
        final User2 user = new User2();
        assertThat(user.getInstitution(), is(nullValue()));
    }

    @Test
    public void testSetInstitution() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setInstitution(str);
        assertThat(user.getInstitution(), is(str));
    }

    @Test
    public void testGetInstitutionAbbrev() {
        final User2 user = new User2();
        assertThat(user.getInstitutionAbbreviation(), is(nullValue()));
    }

    @Test
    public void testSetInstitutionAbbrev() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setInstitutionAbbreviation(str);
        assertThat(user.getInstitutionAbbreviation(), is(str));
    }

    @Test
    public void testGetInstitutionWeb() {
        final User2 user = new User2();
        assertThat(user.getInstitutionWeb(), is(nullValue()));
    }

    @Test
    public void testSetInstitutionWeb() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setInstitutionWeb(str);
        assertThat(user.getInstitutionWeb(), is(str));
    }

    @Test
    public void testGetStatus() {
        final User2 user = new User2();
        assertThat(user.getStatus(), is(nullValue()));
    }

    @Test
    public void testSetStatus() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setStatus(str);
        assertThat(user.getStatus(), is(str));
    }

    @Test
    public void testGetUserType() {
        final User2 user = new User2();
        assertThat(user.getRoles(), is(nullValue()));
    }

    @Test
    public void testSetUserType() {
        final User2 user = new User2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        user.setRoles(str);
        assertThat(user.getRoles(), is(str));
    }

    @Test
    public void testIsPasswordValidMinimumLength() {
        final User2 user = new User2();
        user.setPassword("");
        assertThat(user.isPasswordValid(), is(false));
    }

    @Test
    public void testIsPasswordValidNoDigit() {
        final User2 user = new User2();
        user.setPassword("aaaaaaaa");
        assertThat(user.isPasswordValid(), is(false));
    }

    @Test
    public void testIsPasswordValidNoAlphabet() {
        final User2 user = new User2();
        user.setPassword("12345678");
        assertThat(user.isPasswordValid(), is(false));
    }

    @Test
    public void testIsPasswordValidNoWhitespace() {
        final User2 user = new User2();
        user.setPassword("1234 5678");
        assertThat(user.isPasswordValid(), is(false));
    }

    @Test
    public void testIsPasswordValidLowerCase() {
        final User2 user = new User2();
        user.setPassword("aaaaaaa1");
        assertThat(user.isPasswordValid(), is(true));
    }

    @Test
    public void testIsPasswordValidUpperCase() {
        final User2 user = new User2();
        user.setPassword("AAAAAAA1");
        assertThat(user.isPasswordValid(), is(true));
    }

    @Test
    public void testIsPasswordValidPlus() {
        final User2 user = new User2();
        user.setPassword("AAAAAAA1+");
        assertThat(user.isPasswordValid(), is(true));
    }

    @Test
    public void testIsPasswordValidPlusAmpersands() {
        final User2 user = new User2();
        user.setPassword("AAAAAAA1+&");
        assertThat(user.isPasswordValid(), is(false));
    }

    @Test
    public void testIsPasswordValidAmpersands() {
        final User2 user = new User2();
        user.setPassword("AAAAAAA1&");
        assertThat(user.isPasswordValid(), is(false));
    }

    @Test
    public void testIsPasswordValidLessThanSign() {
        final User2 user = new User2();
        user.setPassword("AAAAAAA1<");
        assertThat(user.isPasswordValid(), is(false));
    }

    @Test
    public void testIsPasswordValidGreaterThanSign() {
        final User2 user = new User2();
        user.setPassword("AAAAAAA1>");
        assertThat(user.isPasswordValid(), is(false));
    }

    @Test
    public void testIsPasswordValidDoubleQuote() {
        final User2 user = new User2();
        user.setPassword("AAAAAAA1\"");
        assertThat(user.isPasswordValid(), is(false));
    }

}
