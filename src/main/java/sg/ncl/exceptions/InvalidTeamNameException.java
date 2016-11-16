package sg.ncl.exceptions;

/**
 * Created by dcszwang on 11/16/2016.
 */
public class InvalidTeamNameException extends Exception {
    public InvalidTeamNameException(final String message) {
        super(message);
    }
}