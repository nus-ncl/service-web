package sg.ncl;

/**
 * Class to hold the customised error messages thrown by services-in-one
 * @author Te Ye
 */
public class MyErrorResource {

    private String name;
    private String message;
    private String localizedMessage;

    /**
     * Converts and returns exceptions thrown from services-in-one
     * Exception thrown from services-in-one is in this form [class.exceptionName]
     * e.g. sg.ncl.service.user.exceptions.UserNotFoundException to UserNotFoundException
     * @return the exceptionName only
     */
    public String getName() {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocalizedMessage() {
        return localizedMessage;
    }

    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }
}
