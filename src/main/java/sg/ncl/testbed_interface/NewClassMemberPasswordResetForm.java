package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import static sg.ncl.validation.Validator.isSafeHtmlCharacters;
import static sg.ncl.validation.Validator.isValidPassword;

/**
 * Created by Tran Ly Vu on 5/11/2018.
 */
@Getter
@Setter
public class
NewClassMemberPasswordResetForm {
    private String key;
    private String uid;
    private String firstName;
    private String lastName;
    private String phone;
    private String password1;
    private String password2;
    private String errMsg;
    private String successMsg;

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
            this.setErrMsg("Password does not match!");
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
