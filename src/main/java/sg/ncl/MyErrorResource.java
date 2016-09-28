package sg.ncl;

/**
 * Class to hold the customised error messages thrown by services-in-one
 * @author Te Ye
 */
public class MyErrorResource {

    // note: variable names must match those of sio
    private String error;
    private String message;
    private String localizedMessage;

    /**
     * @return the full error name, e.g. sg.ncl.service.user.exceptions.UserNotFoundException
     */
    public String getError() {
        return error;
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
