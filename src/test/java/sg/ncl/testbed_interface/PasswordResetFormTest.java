package sg.ncl.testbed_interface;

import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Te Ye
 */
public class PasswordResetFormTest {

    @Test
    public void testPasswordNotMatch() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567a");
        passwordResetForm.setPassword2("1234567b");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password not match!");
    }

    @Test
    public void testPasswordMinimumLength() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567");
        passwordResetForm.setPassword2("1234567");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password must be at least 8 characters with at least one of digit and alphabet and cannot contain any whitespaces");
    }

    @Test
    public void testPasswordWhitespace() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("12345678 ");
        passwordResetForm.setPassword2("12345678 ");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password must be at least 8 characters with at least one of digit and alphabet and cannot contain any whitespaces");
    }

    @Test
    public void testPasswordNoDigit() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("aaaaaaaa");
        passwordResetForm.setPassword2("aaaaaaaa");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password must be at least 8 characters with at least one of digit and alphabet and cannot contain any whitespaces");
    }

    @Test
    public void testPasswordNoAlphabet() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("12345678");
        passwordResetForm.setPassword2("12345678");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password must be at least 8 characters with at least one of digit and alphabet and cannot contain any whitespaces");
    }

    @Test
    public void testPasswordLowercaseGood() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567a");
        passwordResetForm.setPassword2("1234567a");
        assertThat(passwordResetForm.isPasswordOk()).isTrue();
    }

    @Test
    public void testPasswordUppercaseGood() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567A");
        passwordResetForm.setPassword2("1234567A");
        assertThat(passwordResetForm.isPasswordOk()).isTrue();
    }

    @Test
    public void testPasswordWithAmpersands() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567A&");
        passwordResetForm.setPassword2("1234567A&");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password cannot contain &, <, >, \"");
    }

    @Test
    public void testPasswordWithLessThanSign() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567A<");
        passwordResetForm.setPassword2("1234567A<");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password cannot contain &, <, >, \"");
    }

    @Test
    public void testPasswordWithGreaterThanSign() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567A>");
        passwordResetForm.setPassword2("1234567A>");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password cannot contain &, <, >, \"");
    }

    @Test
    public void testPasswordWithDoubleQuote() {
        final PasswordResetForm passwordResetForm = new PasswordResetForm();
        passwordResetForm.setPassword1("1234567A\"");
        passwordResetForm.setPassword2("1234567A\"");
        assertThat(passwordResetForm.isPasswordOk()).isFalse();
        assertThat(passwordResetForm.getErrMsg()).isEqualTo("Password cannot contain &, <, >, \"");
    }
}
