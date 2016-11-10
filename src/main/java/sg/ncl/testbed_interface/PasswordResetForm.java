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

    public String errMsg;
}
