package sg.ncl.testbed_interface;

public class LoginForm {
    private String email;
    // not supposed to be in clear text but for modelling purpose
    private String password;
    private String errorMsg = null;
    
    public LoginForm() {
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
