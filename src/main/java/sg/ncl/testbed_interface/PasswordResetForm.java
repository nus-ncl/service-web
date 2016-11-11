package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dcszwang on 11/7/2016.
 */
@Getter
@Setter
public class PasswordResetForm {

    private String password1;
    private String password2;
    private String errMsg;

    public boolean isPasswordOk() {
        if(!this.getPassword1().equals(this.getPassword2())) {
            this.setErrMsg("Password not match!");
            return false;
        }

        if(this.getPassword1().trim().length() < 8) {
            this.setErrMsg("Password too short! Minimal 8 characters.");
            return false;
        }

        if(this.getPassword1().contains(" ")) {
            this.setErrMsg("Password cannot contain whitespace!");
            return false;
        }

        this.setErrMsg("");
        return true;
    }
}
