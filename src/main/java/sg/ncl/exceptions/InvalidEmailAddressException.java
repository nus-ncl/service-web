package sg.ncl.exceptions;

/**
 * @author: Tran Ly Vu
 */
public class InvalidEmailAddressException extends Exception {
    public InvalidEmailAddressException(final String message) {
        super(message);
    }
}
