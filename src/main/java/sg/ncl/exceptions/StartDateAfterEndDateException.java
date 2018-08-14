package sg.ncl.exceptions;

public class StartDateAfterEndDateException extends Exception {

    public StartDateAfterEndDateException(String message) {
        super(message);
    }
}
