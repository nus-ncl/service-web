package sg.ncl.exceptions;

/**
 * @author Te Ye
 */
public class InvalidPasswordException extends Exception {
    public InvalidPasswordException(final String message) {
        super(message);
    }
}