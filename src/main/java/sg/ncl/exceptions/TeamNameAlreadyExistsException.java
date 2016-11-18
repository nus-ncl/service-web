package sg.ncl.exceptions;


/**
 * @author Te Ye
 */
public class TeamNameAlreadyExistsException extends Exception {

    public TeamNameAlreadyExistsException() {
        super("Team name already exists");
    }

    public TeamNameAlreadyExistsException(String message) {
        super(message);
    }
}
