package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static sg.ncl.validation.Validator.isSafeHtmlCharacters;
import static sg.ncl.validation.Validator.isValidPassword;

/**
 * Created by Tran Ly Vu on 5/11/2018.
 */
@Getter
@Setter
public class
StudentPasswordResetForm {
    private String key;
    private String uid;

    @Size(min=1, message = "First name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9 .&-]*$", message = "First name cannot have special characters")
    private String firstName;


    @Size(min=1, message ="Last name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9 .&-]*$", message = "Last name cannot have special characters")
    private String lastName;

    @Pattern(regexp="^[0-9]*$", message = "Phone cannot have special characters" )
    @Range(min=6, message="Phone minimum 6 digits")
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
