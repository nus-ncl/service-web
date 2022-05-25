package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class GpuUserForm {

    @Pattern(regexp = "[a-z]+", message = "Username must contain only lowercase alphabets")
    private String username;

    @Pattern(regexp = "[A-Za-z -']+", message = "Full name must contain only alphabetic characters, hyphens and apostrophes")
    private String fullname;

    @Size(min=8, message="Password must have at least 8 characters")
    @Pattern.List({
            @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit"),
            @Pattern(regexp = "(?=.*[a-zA-Z]).+", message = "Password must contain one alphabet"),
            @Pattern(regexp = "[^\\s]+", message = "Password cannot contain whitespace"),
            @Pattern(regexp = "[^&<>|/`'\"\\\\]+", message = "Password cannot contain '&' '<' '>' '|' '/' '\\' '`' '\'' '\"'")
    })
    private String password;

    private String confirmPassword;

    @AssertTrue(message = "Passwords should match")
    public boolean isValid() {
        return this.password.equals(this.confirmPassword);
    }

    public String toString() {
        return "GpuUserForm{username='" + username +
                "', fullname='" + fullname +
                "', password='" + password +
                "', confirm='" + confirmPassword + "'}";
    }
}
