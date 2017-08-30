package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
@Setter
@Slf4j
public class PublicUser implements Serializable {

    @NotEmpty
    @Pattern(regexp = "[A-Za-z -']")
    private String fullName;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "[A-Za-z -']")
    private String jobTitle;
    @NotEmpty
    @Pattern(regexp = "[A-Za-z -']")
    private String institution;
    @NotEmpty
    @Pattern(regexp = "[A-Za-z -']")
    private String country;

}
