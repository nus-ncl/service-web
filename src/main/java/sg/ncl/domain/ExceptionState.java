package sg.ncl.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Te Ye, Vu
 */
public enum ExceptionState {
    ADAPTER_CONNECTION_EXCEPTION("AdapterConnectionException"),
    ADAPTER_INTERNAL_ERROR_EXCEPTION("AdapterInternalErrorException"),
    APPLY_NEW_PROJECT_EXCEPTION("ApplyNewProjectException"),
    CREDENTIALS_NOT_FOUND_EXCEPTION("CredentialsNotFoundException"),
    CREDENTIALS_UPDATE_EXCEPTION("CredentialsUpdateException"),
    DATA_ACCESS_REQUEST_NOT_FOUND_EXCEPTION("DataAccessRequestNotFoundException"),
    DATA_NAME_ALREADY_EXISTS_EXCEPTION("DataNameAlreadyExistsException"),
    DATA_NOT_FOUND_EXCEPTION("DataNotFoundException"),
    DATA_NOT_MATCH_EXCEPTION("DataNotMatchException"),
    DATA_RESOURCE_ALREADY_EXISTS_EXCEPTION("DataResourceAlreadyExistsException"),
    DATA_RESOURCE_NOT_FOUND_EXCEPTION("DataResourceNotFoundException"),
    DATA_RESOURCE_DELETE_EXCEPTION("DataResourceDeleteException"),
    DETERLAB_OPERATION_FAILED_EXCEPTION("DeterLabOperationFailedException"),
    EMAIL_ALREADY_EXISTS_EXCEPTION("EmailAlreadyExistsException"),
    EMAIL_NOT_VERIFIED_EXCEPTION("EmailNotVerifiedException"),
    EXPERIMENT_MODIFY_EXCEPTION("ExperimentModifyException"),
    EXPERIMENT_NAME_ALREADY_EXISTS_EXCEPTION("ExperimentNameAlreadyExistsException"),
    EXPERIMENT_DELETE_EXCEPTION("ExperimentDeleteException"),
    EXPERIMENT_START_EXCEPTION("ExperimentStartException"),
    FORBIDDEN_EXCEPTION("ForbiddenException"),
    IMAGE_NOT_FOUND_EXCEPTION("ImageNotFoundException"),
    INSUFFICIENT_PERMISSION_EXCEPTION("InsufficientPermissionException"),
    INSUFFICIENT_QUOTA_EXCEPTION("InsufficientQuotaException"),
    INVALID_CREDENTIALS_EXCEPTION("InvalidCredentialsException"),
    INVALID_EMAIL_ADDRESS_EXCEPTION("InvalidEmailAddressException"),
    INVALID_PASSWORD_EXCEPTION("InvalidPasswordException"),
    INVALID_STATUS_TRANSITION_EXCEPTION("InvalidStatusTransitionException"),
    INVALID_TEAM_NAME_EXCEPTION("InvalidTeamNameException"),
    INVALID_TEAM_STATUS_EXCEPTION("InvalidTeamStatusException"),
    INVALID_USER_STATUS_EXCEPTION("InvalidUserStatusException"),
    INVALID_USERNAME_EXCEPTION("InvalidUsernameException"),
    JOIN_PROJECT_EXCEPTION("JoinProjectException"),
    NS_FILE_PARSE_EXCEPTION("NSFileParseException"),
    OBJECT_OPTIMISTIC_LOCKING_FAILURE_EXCEPTION("ObjectOptimisticLockingFailureException"),
    PASSWORD_RESET_REQUEST_NOT_FOUND_EXCEPTION("PasswordResetRequestNotFoundException"),
    PASSWORD_RESET_REQUEST_NOT_MATCH_EXCEPTION("PasswordResetRequestNotMatchException"),
    PASSWORD_RESET_REQUEST_TIMEOUT_EXCEPTION("PasswordResetRequestTimeoutException"),
    PASSWORD_NULL_OR_EMPTY_EXCEPTION("PasswordNullOrEmptyException"),
    START_DATE_AFTER_END_DATE_EXCEPTION("StartDateAfterEndDateException"),
    TEAM_ID_NULL_OR_EMPTY_EXCEPTION("TeamIdNullOrEmptyException"),
    TEAM_MEMBER_ALREADY_EXISTS_EXCEPTION("TeamMemberAlreadyExistsException"),
    TEAM_NAME_NULL_OR_EMPTY_EXCEPTION("TeamNameNullOrEmptyException"),
    TEAM_NAME_ALREADY_EXISTS_EXCEPTION("TeamNameAlreadyExistsException"),
    TEAM_NOT_FOUND_EXCEPTION("TeamNotFoundException"),
    TEAM_QUOTA_OUT_OF_RANGE_EXCEPTION("TeamQuotaOutOfRangeException"),
    UPLOAD_ALREADY_EXISTS_EXCEPTION("UploadAlreadyExistsException"),
    USERNAME_ALREADY_EXISTS_EXCEPTION("UsernameAlreadyExistsException"),
    USER_ALREADY_IN_TEAM_EXCEPTION("UserAlreadyInTeamException"),
    USER_ID_NULL_OR_EMPTY_EXCEPTION("UserIdNullOrEmptyException"),
    USER_IS_NOT_DELETABLE_EXCEPTION("UserIsNotDeletableException"),
    USER_NOT_FOUND_EXCEPTION("UserNotFoundException"),
    VERIFICATION_PASSWORD_NOT_MATCH_EXCEPTION("VerificationPasswordNotMatchException"),
    PROJECT_DETAILS_NOT_FOUND_EXCEPTION("ProjectDetailsNotFoundException"),
    PROJECT_NAME_ALREADY_EXISTS_EXCEPTION("ProjectNameAlreadyExistsException"),
    PROJECT_USAGE_NOT_FOUND_EXCEPTION("ProjectUsageNotFoundException"),
    PROJECT_USAGE_ALREADY_EXISTS_EXCEPTION("ProjectUsageAlreadyExistsException"),
    NODES_RESERVATION_ALREADY_EXISTS_EXCEPTION("NodesReservationAlreadyExistsException"),
    NODES_RESERVATION_NOT_FOUND_EXCEPTION("NodesReservationNotFoundException");

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