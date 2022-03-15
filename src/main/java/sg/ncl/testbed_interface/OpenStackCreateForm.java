package sg.ncl.testbed_interface;

import javax.validation.constraints.Size;

public class OpenStackCreateForm {
    @Size(min=1, message="Invalid email/password.")
    private String loginPassword;

    public OpenStackCreateForm() {

    }

    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

}
