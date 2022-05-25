package sg.ncl.testbed_interface;

import javax.validation.constraints.Size;

public class OpenStackCreateForm {
    @Size(min=1, message="Invalid email/password.")
    private String loginPassword;

    private String errMsg = null;

    private int isActivate = 0;

    public OpenStackCreateForm() {
    //Not using this method for now.
    }

    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setErrMsg(String error) {
        this.errMsg = error;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setIsActivate(int isActivate) {
        this.isActivate = isActivate;
    }

    public int getIsActivate() {
        return this.isActivate;
    }

}
