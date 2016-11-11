package sg.ncl.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Te Ye
 */
public enum ExceptionState {
    USER_NOT_FOUND_EXCEPTION("UserNotFoundException"),
    CREDENTIALS_UPDATE_EXCEPTION("CredentialsUpdateException"),
    NS_FILE_PARSE_EXCEPTION("NSFileParseException"),
    EXP_NAME_ALREADY_EXISTS_EXCEPTION("ExpNameAlreadyExistsException"),
    TEAM_NOT_FOUND_EXCEPTION("TeamNotFoundException"),
    APPLY_NEW_PROJECT_EXCEPTION("ApplyNewProjectException"),
    REGISTER_TEAM_NAME_DUPLICATE_EXCEPTION("RegisterTeamNameDuplicateException"),
    JOIN_PROJECT_EXCEPTION("JoinProjectException"),
    EXP_START_EXCEPTION("ExpStartException"),
    EXP_DELETE_EXCEPTION("ExpDeleteException"),
    ID_NULL_OR_EMPTY_EXCEPTION("IdNullOrEmptyException"),
    INVALID_TEAM_STATUS_EXCEPTION("InvalidTeamStatusException"),
    EXPERIMENT_NAME_IN_USE_EXCEPTION("ExperimentNameInUseException"),
    USERNAME_ALREADY_EXISTS_EXCEPTION("UsernameAlreadyExistsException"),
    CREDENTIALS_NOT_FOUND_EXCEPTION("CredentialsNotFoundException"),
    EMAIL_NOT_VERIFIED_EXCEPTION("EmailNotVerifiedException"),
    USER_NOT_APPROVED_EXCEPTION("UserNotApprovedException"),
    INVALID_CREDENTIALS_EXCEPTION("InvalidCredentialsException"),
    EMAIL_ALREADY_EXISTS_EXCEPTION("EmailAlreadyExistsException"),
    FORBIDDEN_EXCEPTION("ForbiddenException"),
    PASSWORD_RESET_REQUEST_TIMEOUT_EXCEPTION("PasswordResetRequestTimeoutException"),
    PASSWORD_RESET_REQUEST_NOT_FOUND_EXCEPTION("PasswordResetRequestNotFoundException"),
    ADAPTER_DETERLAB_CONNECT_EXCEPTION("AdapterDeterlabConnectException");

    private static final Map<String, ExceptionState> map = new HashMap<>();

    static {
        for (ExceptionState e : ExceptionState.values()) {
            map.put(e.getExceptionName(), e);
        }
    }

    @Getter
    private String exceptionName;

    ExceptionState(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    /**
     * Converts and returns exceptions thrown from services-in-one
     * Exception thrown from services-in-one is in this form [class.exceptionName]
     * e.g. sg.ncl.service.user.exceptions.UserNotFoundException to UserNotFoundException
     * @return the exceptionName only
     */
    public static ExceptionState parseExceptionState(String exceptionName) {
        return  map.get(exceptionName.substring(exceptionName.lastIndexOf('.') + 1));
    }

}