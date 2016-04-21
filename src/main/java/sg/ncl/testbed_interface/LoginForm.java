package sg.ncl.testbed_interface;

public class LoginForm {
    private String EMAIL;
    private String PASSWORD;
    
    public LoginForm() {
    }
    
    public String getEmail() {
        return EMAIL;
    }
    
    public String getPassword() {
        return PASSWORD;
    }
    
    public void setEmail(String email) {
        this.EMAIL = email;
    }
    
    public void setPassword(String password) {
        this.PASSWORD = password;
    }
}
