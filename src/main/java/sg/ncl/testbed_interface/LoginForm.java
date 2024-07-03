package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

import static sg.ncl.validation.Validator.isSafeHtmlCharacters;
import static sg.ncl.validation.Validator.isValidPassword;

@Getter
@Setter

public class LoginForm {

    @Size(min=1, message="Invalid email/password.")
    private String loginEmail;
    // not supposed to be in clear text but for modelling purpose
    @Size(min=1, message="Invalid email/password.")
    private String loginPassword;
    private String errorMsg = null;
    // for csrf implementation//
    private String csrfToken=null;
    private String key;
    private String type;
    private String errMsg;
    private String uuid;
    private String email;
    
    public LoginForm() {
        //Not using this method for now.
    }
    
    public String getLoginEmail() {
        return loginEmail;
    }
    
    public String getLoginPassword() {
        return loginPassword;
    }
    
    public void setLoginEmail(String email) {
        this.loginEmail = email;
    }
    
    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    private boolean isPasswordValid() {
        if (!isValidPassword(this.getLoginPassword())) {
            this.setErrMsg("Password must be at least 8 characters with at least one of digit and alphabet and cannot contain any whitespaces");
            return false;
        }

        if (!isSafeHtmlCharacters(this.getLoginPassword())) {
            this.setErrMsg("Password cannot contain & < > | / \\ ` ' \"");
            return false;
        }
        return true;
    }
}
