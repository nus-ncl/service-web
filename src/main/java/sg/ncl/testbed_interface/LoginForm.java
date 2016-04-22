package sg.ncl.testbed_interface;

public class LoginForm {
    private String loginEmail;
    // not supposed to be in clear text but for modelling purpose
    private String loginPassword;
    private String errorMsg = null;
    
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
}
