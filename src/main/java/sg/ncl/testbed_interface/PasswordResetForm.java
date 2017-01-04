package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import static sg.ncl.validation.Validator.isSafeHtmlCharacters;
import static sg.ncl.validation.Validator.isValidPassword;

/**
 * Created by dcszwang on 11/7/2016.
 */
@Getter
@Setter
public class PasswordResetForm {

    private String key;
    private String password1;
    private String password2;
    private String errMsg;

    public boolean isPasswordOk() {
        // order of check determines which error message to display first
        if (!isPasswordMatch() || !isPasswordValid()) {
            return false;
        }
        this.setErrMsg("");
        return true;
    }

    private boolean isPasswordMatch() {
        if(!this.getPassword1().equals(this.getPassword2())) {
            this.setErrMsg("Password not match!");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid() {
        if (!isValidPassword(this.getPassword1())) {
            this.setErrMsg("Password must be at least 8 characters with at least one of digit and alphabet and cannot contain any whitespaces");
            return false;
        }

        if (!isSafeHtmlCharacters(this.getPassword1())) {
            this.setErrMsg("Password cannot contain &, <, >, \"");
            return false;
        }
        return true;
    }
}
