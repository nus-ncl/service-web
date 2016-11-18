package sg.ncl.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Te Ye
 */
public enum ExceptionState {
    ADAPTER_DETERLAB_CONNECTION_FAILED_EXCEPTION("AdapterDeterLabConnectionFailedException"),
    ADAPTER_DETERLAB_OPERATION_FAILED_EXCEPTION("AdapterDeterLabOperationFailedException"),
    APPLY_NEW_PROJECT_EXCEPTION("ApplyNewProjectException"),
    CREDENTIALS_NOT_FOUND_EXCEPTION("CredentialsNotFoundException"),
    CREDENTIALS_UPDATE_EXCEPTION("CredentialsUpdateException"),
    EMAIL_ALREADY_EXISTS_EXCEPTION("EmailAlreadyExistsException"),
    EXPERIMENT_NAME_ALREADY_EXISTS_EXCEPTION("ExperimentNameAlreadyExistsException"),
    EXPERIMENT_DELETE_EXCEPTION("ExperimentDeleteException"),
    EXPERIMENT_START_EXCEPTION("ExperimentStartException"),
    FORBIDDEN_EXCEPTION("ForbiddenException"),
    INVALID_CREDENTIALS_EXCEPTION("InvalidCredentialsException"),
    INVALID_TEAM_NAME_EXCEPTION("InvalidTeamNameException"),
    INVALID_TEAM_STATUS_EXCEPTION("InvalidTeamStatusException"),
    INVALID_USER_STATUS_EXCEPTION("InvalidUserStatusException"),
    JOIN_PROJECT_EXCEPTION("JoinProjectException"),
    NS_FILE_PARSE_EXCEPTION("NSFileParseException"),
    PASSWORD_RESET_REQUEST_NOT_FOUND_EXCEPTION("PasswordResetRequestNotFoundException"),
    PASSWORD_RESET_REQUEST_TIMEOUT_EXCEPTION("PasswordResetRequestTimeoutException"),
    TEAM_ID_NULL_OR_EMPTY_EXCEPTION("TeamIdNullOrEmptyException"),
    TEAM_NAME_ALREADY_EXISTS_EXCEPTION("TeamNameAlreadyExistsException"),
    TEAM_NOT_FOUND_EXCEPTION("TeamNotFoundException"),
    USERNAME_ALREADY_EXISTS_EXCEPTION("UsernameAlreadyExistsException"),
    USER_ID_NULL_OR_EMPTY_EXCEPTION("UserIdNullOrEmptyException"),
    USER_NOT_FOUND_EXCEPTION("UserNotFoundException"),
    DATASET_NAME_IN_USE_EXCEPTION("DatasetNameInUseException");


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