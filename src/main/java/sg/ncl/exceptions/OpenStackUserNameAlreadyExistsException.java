package sg.ncl.exceptions;

/**
 * Author: Tran Ly Vu
 */

public class OpenStackUserNameAlreadyExistsException extends Exception {
    public OpenStackUserNameAlreadyExistsException(String message) {
        super(message);
    }
}
