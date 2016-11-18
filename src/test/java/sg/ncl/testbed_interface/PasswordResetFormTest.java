package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class PasswordResetFormTest {

    @Test
    public void testPasswordNotMatch() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567a");
        passwordResetForm.setPassword2("1234567b");
        assertThat(passwordResetForm.isPasswordOk(), is(false));
        assertThat(passwordResetForm.getErrMsg(), is("Password not match!"));
    }

    @Test
    public void testPasswordMinimumLength() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567");
        passwordResetForm.setPassword2("1234567");
        assertThat(passwordResetForm.isPasswordOk(), is(false));
        assertThat(passwordResetForm.getErrMsg(), is("Password too short! Minimal 8 characters."));
    }

    @Test
    public void testPasswordWhitespace() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("12345678 ");
        passwordResetForm.setPassword2("12345678 ");
        assertThat(passwordResetForm.isPasswordOk(), is(false));
        assertThat(passwordResetForm.getErrMsg(), is("Password cannot contain whitespace!"));
    }

    @Test
    public void testPasswordNoDigit() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("aaaaaaaa");
        passwordResetForm.setPassword2("aaaaaaaa");
        assertThat(passwordResetForm.isPasswordOk(), is(false));
        assertThat(passwordResetForm.getErrMsg(), is("Password must contain at least 1 digit."));
    }

    @Test
    public void testPasswordNoAlphabet() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("12345678");
        passwordResetForm.setPassword2("12345678");
        assertThat(passwordResetForm.isPasswordOk(), is(false));
        assertThat(passwordResetForm.getErrMsg(), is("Password must contain at least 1 alphabet."));
    }

    @Test
    public void testPasswordLowercaseGood() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567a");
        passwordResetForm.setPassword2("1234567a");
        assertThat(passwordResetForm.isPasswordOk(), is(true));
    }

    @Test
    public void testPasswordUppercaseGood() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567A");
        passwordResetForm.setPassword2("1234567A");
        assertThat(passwordResetForm.isPasswordOk(), is(true));
    }
}
