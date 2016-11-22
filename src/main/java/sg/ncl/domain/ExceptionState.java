package sg.ncl.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Te Ye
 */
// arrange in alphabet order for ease of tracking
public enum ExceptionState {
    ADAPTER_DETERLAB_CONNECT_EXCEPTION("AdapterDeterlabConnectException"),
    APPLY_NEW_PROJECT_EXCEPTION("ApplyNewProjectException"),
    CREDENTIALS_NOT_FOUND_EXCEPTION("CredentialsNotFoundException"),
    CREDENTIALS_UPDATE_EXCEPTION("CredentialsUpdateException"),
    DATASET_NAME_IN_USE_EXCEPTION("DatasetNameInUseException"),
    DETERLAB_OPERATION_FAILED_EXCEPTION("DeterLabOperationFailedException"),
    EMAIL_ALREADY_EXISTS_EXCEPTION("EmailAlreadyExistsException"),
    EMAIL_NOT_VERIFIED_EXCEPTION("EmailNotVerifiedException"),
    EXP_DELETE_EXCEPTION("ExpDeleteException"),
    EXP_START_EXCEPTION("ExpStartException"),
    EXP_NAME_ALREADY_EXISTS_EXCEPTION("ExpNameAlreadyExistsException"),
    EXPERIMENT_NAME_IN_USE_EXCEPTION("ExperimentNameInUseException"),
    FORBIDDEN_EXCEPTION("ForbiddenException"),
    ID_NULL_OR_EMPTY_EXCEPTION("IdNullOrEmptyException"),
    INVALID_CREDENTIALS_EXCEPTION("InvalidCredentialsException"),
    INVALID_PASSWORD_EXCEPTION("InvalidPasswordException"),
    INVALID_TEAM_NAME_EXCEPTION("InvalidTeamNameException"),
    INVALID_TEAM_STATUS_EXCEPTION("InvalidTeamStatusException"),
    JOIN_PROJECT_EXCEPTION("JoinProjectException"),
    NS_FILE_PARSE_EXCEPTION("NSFileParseException"),
    OBJECT_OPTIMISTIC_LOCKING_FAILURE_EXCEPTION("ObjectOptimisticLockingFailureException"),
    PASSWORD_RESET_REQUEST_TIMEOUT_EXCEPTION("PasswordResetRequestTimeoutException"),
    PASSWORD_RESET_REQUEST_NOT_FOUND_EXCEPTION("PasswordResetRequestNotFoundException"),
    REGISTER_TEAM_NAME_DUPLICATE_EXCEPTION("TeamNameDuplicateException"),
    TEAM_NAME_ALREADY_EXISTS_EXCEPTION("TeamNameAlreadyExistsException"),
    TEAM_NOT_FOUND_EXCEPTION("TeamNotFoundException"),
    USER_NOT_APPROVED_EXCEPTION("UserNotApprovedException"),
    USER_NOT_FOUND_EXCEPTION("UserNotFoundException"),
    USERNAME_ALREADY_EXISTS_EXCEPTION("UsernameAlreadyExistsException");

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