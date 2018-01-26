package sg.ncl.exceptions;

/**
 * Author: Tran Ly Vu
 */

public class OpenStackUserNotFoundException extends Exception {
    public OpenStackUserNotFoundException (String message) {
        super(message);
    }
}
