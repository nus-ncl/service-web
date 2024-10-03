package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactUsForm {
    String fullName;
    String email;
    String message;
    String g_captcha;

    public ContactUsForm(){}

    public ContactUsForm(String fullName, String email, String message,String g_captcha) {
        this.fullName = fullName;
        this.email = email;
        this.message = message;
        this.g_captcha=g_captcha;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getG_captcha() {
        return g_captcha;
    }

    public void setG_captcha(String g_captcha) {
        this.g_captcha = g_captcha;
    }
}
