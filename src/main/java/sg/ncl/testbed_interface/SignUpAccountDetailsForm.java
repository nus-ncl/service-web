package sg.ncl.testbed_interface;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Should probably inherit from LoginForm but for simplicity create another form
 * @author yeoteye
 *
 */
public class SignUpAccountDetailsForm {
    
    @NotNull(message="Email cannot be empty")
    @Size(min=1, message="Email cannot be empty")
    private String email;
    
    @NotNull(message="Password cannot be empty")
    @Size(min=1, message="Password cannot be empty")
    private String password;
    
    private String confirmPassword;
    private String errorMsg = null;
    
    public SignUpAccountDetailsForm() {    
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    public boolean isPasswordMatch() {
        if (!password.equals(confirmPassword)) {
            return false;
        }
        return true;
    }
}
