package sg.ncl.testbed_interface;

import javax.validation.constraints.Size;

public class LoginForm {

    @Size(min=1, message="Invalid email/password.")
    private String loginEmail;
    // not supposed to be in clear text but for modelling purpose

    @Size(min=1, message="Invalid email/password.")
    private String loginPassword;
    private String errorMsg = null;



    // for csrf implementation//
    private String csrfToken=null;
    
    public LoginForm() {
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
}
