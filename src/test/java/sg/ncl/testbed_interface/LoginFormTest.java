package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class LoginFormTest {

    @Test
    public void testGetLoginEmail() {
        final LoginForm loginForm = new LoginForm();
        assertThat(loginForm.getLoginEmail(), is(nullValue()));
    }

    @Test
    public void testSetLoginEmail() {
        final LoginForm loginForm = new LoginForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        loginForm.setLoginEmail(str);
        assertThat(loginForm.getLoginEmail(), is(str));
    }

    @Test
    public void testGetPassword() {
        final LoginForm loginForm = new LoginForm();
        assertThat(loginForm.getLoginEmail(), is(nullValue()));
    }

    @Test
    public void testSetPassword() {
        final LoginForm loginForm = new LoginForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        loginForm.setLoginPassword(str);
        assertThat(loginForm.getLoginPassword(), is(str));
    }

    @Test
    public void testGetErrorMsg() {
        final LoginForm loginForm = new LoginForm();
        assertThat(loginForm.getErrorMsg(), is(nullValue()));
    }

    @Test
    public void testSetErrorMsg() {
        final LoginForm loginForm = new LoginForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        loginForm.setErrorMsg(str);
        assertThat(loginForm.getErrorMsg(), is(str));
    }

}
