package sg.ncl;

/**
 * Class to hold the customised error messages thrown by services-in-one
 * @author Te Ye
 */
public class MyErrorResource {

    private String error;
    private String message;
    private String localizedMessage;

    /**
     * Converts and returns exceptions thrown from services-in-one
     * Exception thrown from services-in-one is in this form [class.exceptionName]
     * e.g. sg.ncl.service.user.exceptions.UserNotFoundException to UserNotFoundException
     * @return the exceptionName only
     */
    public String getError() {
        return error.substring(error.lastIndexOf('.') + 1);
    }

    public void setError(String error) {
        this.error = error;
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
