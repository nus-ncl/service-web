package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookNodeForm {

    private String typeofBook;
    private String name;
    private String email;
    private String startDate;
    private String endDate;
    private String message;
    String g_captcha;

    public BookNodeForm(String type, String name, String email, String startDate,
                        String endDate, String message, String g_captcha) {
        this.typeofBook = type;
        this.name = name;
        this.email = email;
        this.startDate = startDate;
        this.endDate = endDate;
        this.message = message;
        this.g_captcha = g_captcha;
    }

    public BookNodeForm() {
        //Not using this method for now.
    }

    public String getTypeofBook() {
        return typeofBook;
    }

    public void setTypeofBook(String typeofBook) {
        this.typeofBook = typeofBook;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMessage() {
        return message;
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
