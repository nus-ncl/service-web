package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dcszwang on 11/7/2016.
 */
@Setter
@Getter
public class PasswordResetRequestForm {

    private String email;

    private String errMsg;
}
