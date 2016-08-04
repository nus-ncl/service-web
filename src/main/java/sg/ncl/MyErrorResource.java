package sg.ncl;

/**
 * Class to hold the customised error messages thrown by services-in-one
 * @author Te Ye
 */
public class MyErrorResource {

    private String exceptionName;

    /**
     * Converts and returns exceptions thrown from services-in-one
     * Exception thrown from services-in-one is in this form [class.exceptionName]
     * e.g. sg.ncl.service.user.exceptions.UserNotFoundException to UserNotFoundException
     * @return the exceptionName only
     */
    public String getExceptionName() {
        return exceptionName.substring(exceptionName.lastIndexOf('.') + 1);
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }
}
