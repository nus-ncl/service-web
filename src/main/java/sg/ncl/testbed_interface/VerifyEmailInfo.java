package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import static sg.ncl.validation.Validator.isSafeHtmlCharacters;
import static sg.ncl.validation.Validator.isValidPassword;

@Getter
@Setter
public class VerifyEmailInfo {

    private String key;
    private String password;
    private String type;
    private String errMsg;
    private String uuid;
    private String email;

    private boolean isPasswordValid() {
        if (!isValidPassword(this.getPassword())) {
            this.setErrMsg("Password must be at least 8 characters with at least one of digit and alphabet and cannot contain any whitespaces");
            return false;
        }

        if (!isSafeHtmlCharacters(this.getPassword())) {
            this.setErrMsg("Password cannot contain & < > | / \\ ` ' \"");
            return false;
        }
        return true;
    }
}
