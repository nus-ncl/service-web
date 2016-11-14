package sg.ncl.exceptions;


/**
 * @author Te Ye
 */
public class RegisterTeamNameDuplicateException extends Exception {

    public RegisterTeamNameDuplicateException() {
        super("Team name duplicate entry found");
    }

    public RegisterTeamNameDuplicateException(String message) {
        super(message);
    }
}
