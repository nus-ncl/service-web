package sg.ncl.validation;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by dcsyeoty on 03-Jan-17.
 */
@Slf4j
public class Validator {

    private Validator() {

    }

    /**
     * Checks whether the string contains certain html special characters, &, < , >, "
     * @param str the string to be checked
     * @return True if the string does not contain any of the special characters, false otherwise
     */
    public static boolean isSafeHtmlCharacters(final String str) {
        return str.matches("[^&<>\"]+");
    }

    /**
     * Baseline password check, ensure password is at least 8 characters, contain at least 1 digit and 1 alphabet, and no whitespace
     * @param str
     * @return True if password passes the check, false otherwise
     */
    public static boolean isValidPassword(final String str) {
        // at least 8 characters
        // contain at least 1 digit
        // contain at least 1 alphabet
        // does not contain whitespace
        return str.length() >= 8 &&
                str.matches("(?=.*[0-9]).+") &&
                str.matches("(?=.*[a-zA-Z]).+") &&
                str.matches("[^\\s]+");
    }

}
