package sg.ncl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import sg.ncl.domain.*;
import sg.ncl.exceptions.*;
import sg.ncl.testbed_interface.*;
import sg.ncl.testbed_interface.Image;
import sg.ncl.webssh.PtyProperties;
import sg.ncl.webssh.VncProperties;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.SecureRandom;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static sg.ncl.domain.ExceptionState.*;

/**
 *
 * Spring Controller
 * Direct the views to appropriate locations and invoke the respective REST API
 *
 * @author Cassie, Desmond, Te Ye, Vu
 */
@Controller
@Slf4j
public class MainController {

    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String APPLICATION_FORCE_DOWNLOAD = "application/force-download";
    private static final String AUTHORIZATION = "Authorization";
    private static final String SESSION_LOGGED_IN_USER_ID = "loggedInUserId";

    private TeamManager teamManager = TeamManager.getInstance();
//    private UserManager userManager = UserManager.getInstance();
//    private ExperimentManager experimentManager = ExperimentManager.getInstance();
//    private DomainManager domainManager = DomainManager.getInstance();
//    private DatasetManager datasetManager = DatasetManager.getInstance();
//    private NodeManager nodeManager = NodeManager.getInstance();

    private static final String CONTACT_EMAIL = "support@ncl.sg";

    private static final String UNKNOWN = "?";
    private static final String MESSAGE = "message";
    private static final String MESSAGE_SUCCESS = "messageSuccess";
    private static final String EXPERIMENT_MESSAGE = "exp_message";
    private static final String ERROR_PREFIX = "Error: ";
    private static final String USER_PREFIX = "User ";
    private static final String USER_STR = " user ";
    private static final String REFRESH = ". Please refresh the page again. If the error persists, please contact ";

    private static final String MESSAGE_DELETE_IMAGE_SUCCESS = "message_success";
    private static final String MESSAGE_DELETE_IMAGE_FAILURE = "message_failure";
    private static final String MESSAGE_DELETE_IMAGE_FAILURE_LIST = "message_failure_list";
    private static final String MESSAGE_DELETE_IMAGE_WARNING = "message_warning";
    // error messages
    private static final String MAX_DURATION_ERROR = "Auto-shutdown hours must be an integer without any decimals";
    private static final String ERROR_CONNECTING_TO_SERVICE_TELEMETRY = "Error connecting to service-telemetry: {}";
    private static final String ERR_SERVER_OVERLOAD = "There is a problem with your request. Please contact " + CONTACT_EMAIL;
    private static final String CONNECTION_ERROR = "Connection Error";
    private final String permissionDeniedMessage = "Permission denied. If the error persists, please contact " + CONTACT_EMAIL;
    private static final String ERR_START_DATE_AFTER_END_DATE = "End date must be after start date";
    private static final String ERR_INVALID_CREDENTIALS = "Login failed: Invalid email/password.";

    // for user dashboard hashmap key values
    private static final String USER_DASHBOARD_APPROVED_TEAMS = "numberOfApprovedTeam";
    private static final String USER_DASHBOARD_RUNNING_EXPERIMENTS = "numberOfRunningExperiments";
    private static final String USER_DASHBOARD_FREE_NODES = "freeNodes";
    private static final String USER_DASHBOARD_TOTAL_NODES = "totalNodes";
    private static final String USER_DASHBOARD_GLOBAL_IMAGES = "globalImagesMap";
    private static final String USER_DASHBOARD_LOGGED_IN_USERS_COUNT = "loggedInUsersCount";
    private static final String USER_DASHBOARD_RUNNING_EXPERIMENTS_COUNT = "runningExperimentsCount";

    private static final String DETER_UID = "deterUid";

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");

    private static final Pattern VALID_IMAGE_NAME = Pattern.compile("^[a-zA-Z0-9\\-]+$");

    private static final String FORGET_PSWD_PAGE = "password_reset_email";
    private static final String FORGET_PSWD_NEW_PSWD_PAGE = "password_reset_new_password";
    private static final String NO_PERMISSION_PAGE = "nopermission";
    private static final String STUDENT_RESET_PSWD = "student_reset_password";

    private static final String SIGNUP_PAGE = "signup2";
    private static final String SIGNUP_MERGED_FORM = "signUpMergedForm";
    private static final String LOGIN_PAGE = "login";
    private static final String EXPERIMENTS = "experiments";

    private static final String PSWD = "password";
    private static final String FNAME = "firstName";
    private static final String LNAME = "lastName";
    private static final String JOB_TITLE = "jobTitle";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String INSTITUTION = "institution";
    private static final String INSTITUTION_ABBREVIATION = "institutionAbbreviation";
    private static final String INSTITUTION_WEB = "institutionWeb";
    private static final String ADDRESS = "address";
    private static final String ORGANISATION_TYPE = "organisationType";
    private static final String ORGANISATION_NAME = "organisationName";
    private static final String ADDRESS1 = "address1";
    private static final String ADDRESS2 = "address2";
    private static final String COUNTRY = "country";
    private static final String REGION = "region";
    private static final String CITY = "city";
    private static final String ZIP_CODE = "zipCode";

    private static final String USER_DETAILS = "userDetails";
    private static final String APPLICATION_DATE = "applicationDate";
    private static final String TEAM_NAME = "teamName";
    private static final String TEAM_ID = "teamId";
    private static final String NODE_ID = "nodeId";
    private static final String PERMISSION_DENIED = "Permission denied";
    private static final String TEAM_NOT_FOUND = "Team not found";
    private static final String NOT_FOUND = " not found.";
    private static final String EMAIL_ADDRESS_IS_NOT_VALID = "Email address is not valid";

    private static final String QUOTA = "quota";
    private static final String EDIT_BUDGET = "editBudget";
    private static final String ORIGINAL_BUDGET = "originalBudget";

    private static final String REDIRECT_SIGNUP = "redirect:/signup2";
    private static final String REDIRECT_EXPERIMENTS = "redirect:/experiments";
    private static final String REDIRECT_CREATE_EXPERIMENT = "redirect:/experiments/create";
    private static final String REDIRECT_UPDATE_EXPERIMENT = "redirect:/update_experiment/";
    private static final String REDIRECT_TEAM_PROFILE_TEAM_ID = "redirect:/team_profile/{teamId}";
    private static final String REDIRECT_TEAM_PROFILE = "redirect:/team_profile/";
    private static final String REDIRECT_INDEX_PAGE = "redirect:/";
    private static final String REDIRECT_TEAM_USAGE = "redirect:/admin/usage";
    private static final String REDIRECT_ENERGY_USAGE = "redirect:/admin/energy";
    private static final String REDIRECT_TEAMS="redirect:/teams";
    private static final String REDIRECT_APPROVE_NEW_USER = "redirect:/approve_new_user";
    private static final String REDIRECT_ADMIN = "redirect:/admin";
    private static final String REDIRECT_ADD_MEMBER = "redirect:/add_member";
    private static final String REDIRECT_UNAUTHOURIZED_ACCESS = "redirect:/unauthorized_access";

    // remove members from team profile; to display the list of experiments created by user
    private static final String REMOVE_MEMBER_UID = "removeMemberUid";
    private static final String REMOVE_MEMBER_NAME = "removeMemberName";

    private static final String MEMBER_TYPE = "memberType";
    private static final String MEMBER_STATUS = "memberStatus";

    // admin update data resource to track what fields have been updated
    private static final String ORIGINAL_DATARESOURCE = "original_dataresource";

    private static final String NOT_APPLICABLE = "N.A.";

    private static final String DATA_ID = "dataId";
    private static final String COUNT = "count";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String USER_ID = "userId";
    private static final String OS = "os";
    private static final String QUALIFIED_NAME = "qualifiedName";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_DATE = "createdDate";
    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    private static final String MAX_DURATION = "maxDuration";
    private static final String STATUS = "status";
    private static final String SUCCESS = "success";
    private static final String TEAMS = "teams";
    private static final String MEMBERS = "members";
    private static final String ORIGINAL_TEAM = "originalTeam";

    private static final String LOG_IOEXCEPTION = "IOException {}";

    // nodes reservation
    private static final String ALL_TEAMS = "allTeams";
    private static final String NODES_RESERVATION_FAIL = "nodes reservation FAIL";
    private static final String RESERVATION_STATUS_FORM = "reservationStatusForm";
    private static final String RESERVED = "reserved";

    private static final String WEBSITE = "website";
    private static final String VISIBILITY = "visibility";
    private static final String IS_CLASS = "isClass";
    private static final String KEY = "key";

    private static final String TAG_ERRORS = "Error(s):";
    private static final String TAG_UL = "<ul class=\"fa-ul\">";
    private static final String TAG_LI = "<li><i class=\"fa fa-exclamation-circle\"></i> ";
    private static final String TAG_SPACE = " ";
    private static final String TAG_LI_CLOSE = "</li>";
    private static final String TAG_UL_CLOSE = "</ul>";

    private static final String KEY_PROJECT_DETAILS_ID = "projectDetailsId";
    private static final String KEY_PROJECT_NAME = "projectName";
    private static final String KEY_MONTH_YEAR = "monthYear";
    private static final String KEY_MONTHLY_USAGE = "monthlyUsage";
    private static final String KEY_USAGE = "usage";
    private static final String KEY_INCURRED = "incurred";
    private static final String KEY_WAIVED = "waived";
    private static final String KEY_PROJECT = "project";
    private static final String KEY_QUERY = "query";
    private static final String KEY_DATE_CREATED = "dateCreated";
    private static final String KEY_OWNER = "owner";

    private static final String ADMIN_MONTHLY_USAGE_CONTRIBUTE = "admin_monthly_usage_contribute";
    private static final String ADMIN_MONTHLY_CONTRIBUTE = "admin_monthly_contribute";


    @ModelAttribute
    public void setXFrameResponseHeader(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
    }

    @Autowired
    protected RestTemplate restTemplate;

    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    protected ConnectionProperties properties;

    @Inject
    protected WebProperties webProperties;

    @Inject
    protected AccountingProperties accountingProperties;

    @Inject
    protected HttpSession httpScopedSession;

    @Inject
    protected PtyProperties ptyProperties;
    @Inject
    protected VncProperties vncProperties;

    @Inject
    protected GpuProperties gpuProperties;

    @Inject
    protected NetworkToolProperties networkToolProperties;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/overview")
    public String overview() {
        return "overview";
    }

    @RequestMapping("/community")
    public String community() {
        return "community";
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    @RequestMapping("/event")
    public String event() {
        return "event";
    }

    @RequestMapping("/plan")
    public String plan() {
        return "plan";
    }

    @RequestMapping("/career")
    public String career() {
        return "career";
    }

    @RequestMapping("/pricing")
    public String pricing() {
        return "pricing";
    }

    @RequestMapping("/resources")
    public String resources() {
        return "resources";
    }

    @RequestMapping("/research")
    public String research() {
        return "research";
    }

   /* @RequestMapping("/otherPublication")
    public String otherPublication() {
        return "other_publication";
    }*/

    @RequestMapping("/nclpublications")
    public String publications(){
        return "ncl_publications";
    }

    @RequestMapping("/otherNewsLinks")
    public String otherNewsLinks() {
        return "other_newslinks";
    }

    /*@RequestMapping("/news_updates")
    public String news_updates() {
        return "news_updates";
    }*/

    @RequestMapping("/collaborators")
    public String news_updates() {
        return "collaborators";
    }


    @RequestMapping("/unauthorized_access")
    public String unauthorizedAccess() {
        return "unauthorized_access";
    }

    @RequestMapping("/people")
    public String people() {
        return "people";
    }

    @RequestMapping("/calendar")
    public String calendar(Model model,
                           @RequestParam(value = "start", required = false) String start,
                           @RequestParam(value = "end", required = false) String end,
                           final RedirectAttributes redirectAttributes,
                           HttpSession session) throws IOException, StartDateAfterEndDateException, WebServiceRuntimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = (start == null) ? LocalDate.now() : LocalDate.parse(start, formatter);
        LocalDate endDate = (end == null) ? startDate.plusDays(7) : LocalDate.parse(end, formatter);
        LocalDate maxDate = startDate.plusDays(30);

        if (endDate.compareTo(maxDate) > 0) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Period selected is more than 30 days.");
            return "redirect:/calendar";
        }

        start = startDate.format(formatter);
        end = endDate.format(formatter);

        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity response = restTemplate.exchange(properties.getUsageCalendar("startDate=" + start, "endDate=" + end), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();
        JSONObject jsonObject = new JSONObject(responseBody);

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
            if (exceptionState == START_DATE_AFTER_END_DATE_EXCEPTION) {
                log.warn("Get calendar : Start date after end date error");
                throw new StartDateAfterEndDateException(ERR_START_DATE_AFTER_END_DATE);
            } else {
                log.warn("Get calendar : unknown error");
                throw new WebServiceRuntimeException(ERR_SERVER_OVERLOAD);
            }
        } else {
            List<String> dates = getDates(start, end, formatter);
            Map<String, List<Integer>> projectUsages = new HashMap<>();
            jsonObject.keys().forEachRemaining(
                    key -> {
                        JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray((String) key).toString());
                        List<Integer> usages = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            usages.add(jsonArray.getInt(i));
                        }
                        projectUsages.put((String) key, usages);
                    }
            );
            model.addAttribute("dates", dates);
            model.addAttribute("projectUsages", projectUsages);
        }

        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "calendar";
    }

    @RequestMapping("/updates")
    public String updates() {
        return "updates";
    }

    @RequestMapping("/tutorials/createaccount")
    public String createAccount() {
        return "createaccount";
    }

    @RequestMapping("/tutorials/createexperiment")
    public String createExperimentTutorial() {
        return "createexperiment";
    }

    @RequestMapping("/tutorials/loadimage")
    public String loadimage() {
        return "loadimage";
    }

    @RequestMapping("/tutorials/saveimage")
    public String saveimage() {
        return "saveimage";
    }

    @RequestMapping("/tutorials/applyteam")
    public String applyteam() {
        return "applyteam";
    }

    @RequestMapping("/tutorials/jointeam")
    public String jointeam() {
        return "jointeam";
    }

    @RequestMapping("/tutorials/usenode")
    public String usenode() {
        return "usenode";
    }

    @RequestMapping("/tutorials/usessh")
    public String usessh() {
        return "usessh";
    }

    @RequestMapping("/tutorials/usescp")
    public String usescp() {
        return "usescp";
    }

    @RequestMapping("/tutorials/usegui")
    public String usegui() {
        return "usegui";
    }

    @RequestMapping("/tutorials/manageresource")
    public String manageresource() {
        return "manageresource";
    }

    @RequestMapping("/tutorials/testbedinfo")
    public String testbedinfo() {
        return "testbedinfo";
    }

    @RequestMapping("/tutorials/createcustom")
    public String createcustom() {
        return "createcustom";
    }

    @RequestMapping("/error_openstack")
    public String error_openstack() {
        return "error_openstack";
    }

    @RequestMapping("/accessexperiment")
    public String accessexperiment() {
        return "accessexperiment";
    }

    @RequestMapping("/resource2")
    public String resource2() {
        return "resource2";
    }

    @RequestMapping("/tutorials")
    public String tutorials() {
        return "tutorials";
    }

    @RequestMapping("/maintainance")
    public String maintainance() {
        return "maintainance";
    }

    @RequestMapping(value = "/networkTool", method = RequestMethod.GET)
    public String networkTopologyTool() {
        if (networkToolProperties.getEnabled()) {
            return "network_diagram";
        } else {
            return NO_PERMISSION_PAGE;
        }
    }

    @RequestMapping(value = "/networkTool", method = RequestMethod.POST)
    public @ResponseBody String networkTopologyAnalysis(@RequestParam("jsonText") String jsonText) {
        JSONObject jsonObject = new JSONObject();
        StringBuilder nsBuilder = new StringBuilder();
        StringBuilder logBuilder = new StringBuilder();
        if (networkToolProperties.isAdapterEnabled()) {
            String url = "http://" + networkToolProperties.getAdapterHost() + ":" + networkToolProperties.getAdapterPort() + "/netdef";
            jsonObject.put("jsonText", jsonText);
            HttpEntity<String> request = createHttpEntityWithBodyNoAuthHeader(jsonObject.toString());
            ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            JSONObject responseBody = new JSONObject(response.getBody().toString());
            jsonObject.put("nsText", responseBody.getString("nsText"));
            jsonObject.put("logText", responseBody.getString("logText"));
        } else {
            analyzeJsonText(jsonText, nsBuilder, logBuilder);
            jsonObject.put("nsText", nsBuilder.toString());
            jsonObject.put("logText", logBuilder.toString());
        }
        return jsonObject.toString();
    }

    private void analyzeJsonText(@RequestParam("jsonText") String jsonText, StringBuilder nsBuilder, StringBuilder logBuilder) {
        String nsfilename = null;
        String filename = networkToolProperties.getTemp() + System.currentTimeMillis() + ".json";
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(jsonText);
        } catch (IOException ioe) {
            log.error(ioe.toString());
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    networkToolProperties.getCommand(),
                    networkToolProperties.getVersion(),
                    networkToolProperties.getProgram(),
                    networkToolProperties.getOption(),
                    filename);
            final Process p = pb.start();
            nsfilename = generateNSfile(p.getInputStream(), logBuilder);
        } catch (IOException ioe) {
            log.error(ioe.toString());
        }
        Path filePath = Paths.get(filename);
        if (filePath.toFile().exists()) {
            try {
                Files.delete(filePath);
            } catch (IOException ioe) {
                log.error(ioe.toString());
            }
        }
        if (nsfilename != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(nsfilename))) {
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    nsBuilder.append(line).append("&#010;");
                }
            } catch (IOException ioe) {
                log.error(ioe.toString());
            }
        }
    }

    private String generateNSfile(InputStream inputStream, StringBuilder logBuilder) {
        String nsfilename = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.contains("Produce NSfile")) {
                    Pattern pattern = Pattern.compile("\\[([^]]+)\\]");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        nsfilename = matcher.group(1) + "/NSfile.txt";
                    }
                }
                logBuilder.append(line).append("&#010;");
            }
        } catch (IOException ioe) {
            log.error(ioe.toString());
        }
        return nsfilename;
    }

    @RequestMapping("/testbedInformation")
    public String testbedInformation(Model model) throws IOException {
        model.addAttribute(USER_DASHBOARD_GLOBAL_IMAGES, getGlobalImages());
        return "testbed_information";
    }

    // get all the nodes' status
    // there are three types of status
    // "free" : node is free
    // "in_use" : node is in use
    // "reload" : node is in process of freeing or unknown status
    // "reserved" : node is pre-reserved for a project
    @RequestMapping("/testbedNodesStatus")
    public String testbedNodesStatus(Model model) throws IOException {
        // get number of active users and running experiments
        Map<String, String> testbedStatsMap = getTestbedStats();
        testbedStatsMap.put(USER_DASHBOARD_FREE_NODES, "0");
        testbedStatsMap.put(USER_DASHBOARD_TOTAL_NODES, "0");

        Map<String, List<Map<String, String>>> nodesStatus = getNodesStatus();
        Map<String, Map<String, Long>> nodesStatusCount = new HashMap<>();

        countNodeStatus(testbedStatsMap, nodesStatus, nodesStatusCount);

        model.addAttribute("nodesStatus", nodesStatus);
        model.addAttribute("nodesStatusCount", nodesStatusCount);

        model.addAttribute(USER_DASHBOARD_LOGGED_IN_USERS_COUNT, testbedStatsMap.get(USER_DASHBOARD_LOGGED_IN_USERS_COUNT));
        model.addAttribute(USER_DASHBOARD_RUNNING_EXPERIMENTS_COUNT, testbedStatsMap.get(USER_DASHBOARD_RUNNING_EXPERIMENTS_COUNT));
        model.addAttribute(USER_DASHBOARD_FREE_NODES, testbedStatsMap.get(USER_DASHBOARD_FREE_NODES));
        model.addAttribute(USER_DASHBOARD_TOTAL_NODES, testbedStatsMap.get(USER_DASHBOARD_TOTAL_NODES));
        return "testbed_nodes_status";
    }

    @RequestMapping(value = "/orderform/download", method = RequestMethod.GET)
    public void OrderForm_v1Download(HttpServletResponse response) throws OrderFormDownloadException, IOException {
        InputStream stream = null;
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        try {
            stream = getClass().getClassLoader().getResourceAsStream("downloads/order_form.pdf");
            response.setContentType(APPLICATION_FORCE_DOWNLOAD);
            response.setHeader(CONTENT_DISPOSITION, "attachment; filename=order_form.pdf");
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            log.info("Error for download orderform.");
            throw new OrderFormDownloadException("Error for download orderform.");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

    }

    @RequestMapping("/contactus")
    public String contactus() {
        return "contactus";
    }

    @RequestMapping("/notfound")
    public String redirectNotFound(HttpSession session) {
        if (session.getAttribute(ID) != null && !session.getAttribute(ID).toString().isEmpty()) {
            // user is already logged on and has encountered an error
            // redirect to dashboard
            return "redirect:/dashboard";
        } else {
            // user have not logged on before
            // redirect to home page
            return REDIRECT_INDEX_PAGE;
        }
    }
    //added to fix the cross site request forgery issue 
    private String generateCSRFToken() {
        SecureRandom entropy = new SecureRandom();

        byte secureBytes[] = new byte[20];
        entropy.nextBytes(secureBytes);
        String token=Base64.encodeBase64String(secureBytes);
        token=token.replaceAll("\\/","@");
        return token;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return LOGIN_PAGE;
    }

    @RequestMapping(value = "/emailVerification", params = {ID, EMAIL, "key"})
    public String verifyEmail(@NotNull @RequestParam(ID) final String id,
                              @NotNull @RequestParam(EMAIL) final String emailBase64,
                              @NotNull @RequestParam("key") final String key) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode keyObject = objectMapper.createObjectNode();
        keyObject.put("key", key);

        HttpEntity<String> request = new HttpEntity<>(keyObject.toString(), headers);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        final String link = properties.getSioRegUrl() + "/users/" + id + "/emails/" + emailBase64;
        // log.info("Activation link: {}, verification key {}", link, key);
        ResponseEntity response = restTemplate.exchange(link, HttpMethod.PUT, request, String.class);

        if (RestUtil.isError(response.getStatusCode())) {
            log.error("Activation of user {} failed.", id);
            return "email_validation_failed";
        } else {
            log.info("Activation of user {} completed.", id);
            return "email_validation_ok";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginSubmit(
            @Valid
            @ModelAttribute("loginForm") LoginForm loginForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session, final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        if (bindingResult.hasErrors()) {
            loginForm.setErrorMsg(ERR_INVALID_CREDENTIALS);
            return LOGIN_PAGE;
        }
        String inputEmail = loginForm.getLoginEmail();
        String inputPwd = loginForm.getLoginPassword();
        if (inputEmail.trim().isEmpty() || inputPwd.trim().isEmpty()) {
            loginForm.setErrorMsg("Email or Password cannot be empty!");
            return LOGIN_PAGE;
        }

        String plainCreds = inputEmail + ":" + inputPwd;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        ResponseEntity response;

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Basic " + base64Creds);

        HttpEntity<String> request = new HttpEntity<>(headers);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        try {
            response = restTemplate.exchange(properties.getSioAuthUrl(), HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio authentication service: {}", e);
            loginForm.setErrorMsg(ERR_SERVER_OVERLOAD);
            return LOGIN_PAGE;
        }

        String jwtTokenString = response.getBody().toString();
        // log.info("token string {}", jwtTokenString);
        if (jwtTokenString == null || jwtTokenString.isEmpty()) {
            log.warn("login failed for {}: unknown response code", loginForm.getLoginEmail());
            loginForm.setErrorMsg(ERR_INVALID_CREDENTIALS);
            return LOGIN_PAGE;
        }
        if (RestUtil.isError(response.getStatusCode())) {
            try {
                MyErrorResource error = objectMapper.readValue(jwtTokenString, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == ExceptionState.CREDENTIALS_NOT_FOUND_EXCEPTION) {
                    log.warn("login failed for {}: credentials not found", loginForm.getLoginEmail());
                    loginForm.setErrorMsg("Login failed: Account does not exist. Please register.");
                    return LOGIN_PAGE;
                }
                log.warn("login failed for {}: {}", loginForm.getLoginEmail(), error.getError());
                loginForm.setErrorMsg(ERR_INVALID_CREDENTIALS);
                return LOGIN_PAGE;
            } catch (IOException ioe) {
                log.warn(LOG_IOEXCEPTION, ioe);
                throw new WebServiceRuntimeException(ioe.getMessage());
            }
        }

        JSONObject tokenObject = new JSONObject(jwtTokenString);
        String token = tokenObject.getString("token");
        String id = tokenObject.getString("id");
        String role = "";
        if (tokenObject.getJSONArray("roles") != null) {
            role = tokenObject.getJSONArray("roles").get(0).toString();
        }

        if (token.trim().isEmpty() || id.trim().isEmpty() || role.trim().isEmpty()) {
            log.warn("login failed for {}: empty id {} or token {} or role {}", loginForm.getLoginEmail(), id, token, role);
            loginForm.setErrorMsg(ERR_INVALID_CREDENTIALS);
            return LOGIN_PAGE;
        }

        //set the csrf token //
        String csrfToken=generateCSRFToken();
        session.setAttribute("csrfToken", csrfToken);
        loginForm.setErrorMsg("csrfToken");
         // now check user status to decide what to show to the user
        return checkUserStatus(loginForm, session, redirectAttributes, token, id, role);
    }

    private String checkUserStatus(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                                   HttpSession session, RedirectAttributes redirectAttributes,
                                   String token, String id, String role) {
        User2 user = invokeAndExtractUserInfo(id);

        try {
            String userStatus = user.getStatus();
            boolean emailVerified = user.getEmailVerified();

            if (UserStatus.FROZEN.toString().equals(userStatus)) {
                log.warn("User {} login failed: account has been frozen", id);
                loginForm.setErrorMsg("Login Failed: Account Frozen. Please contact " + CONTACT_EMAIL);
                return LOGIN_PAGE;
            } else if (!emailVerified || (UserStatus.CREATED.toString()).equals(userStatus)) {
                redirectAttributes.addAttribute("statuschecklist", userStatus);
                log.info("User {} not validated, redirected to email verification page", id);
                return "redirect:/email_checklist";
            } else if ((UserStatus.PENDING.toString()).equals(userStatus)) {
                redirectAttributes.addAttribute("statuschecklist", userStatus);
                log.info("User {} not approved, redirected to application pending page", id);
                return "redirect:/email_checklist";
            } else if ((UserStatus.APPROVED.toString()).equals(userStatus)) {
                // set session variables
                setSessionVariables(session, loginForm.getLoginEmail(), id, user.getFirstName(), role, token);
                log.info("login success for {}, id: {}", loginForm.getLoginEmail(), id);
                return "redirect:/dashboard";
            } else {
                log.warn("login failed for user {}: account is rejected or closed", id);
                loginForm.setErrorMsg("Login Failed: Account Rejected/Closed.");
                return LOGIN_PAGE;
            }
        } catch (Exception e) {
            log.warn("Error parsing json object for user: {}", e.getMessage());
            loginForm.setErrorMsg(ERR_SERVER_OVERLOAD);
            return LOGIN_PAGE;
        }
    }

    // triggered when user clicks "Forget Password?"
    @RequestMapping("/password_reset_email")
    public String passwordResetEmail(Model model) {
        model.addAttribute("passwordResetRequestForm", new PasswordResetRequestForm());
        return FORGET_PSWD_PAGE;
    }

    // triggered when user clicks "Send Reset Email" button
    @PostMapping("/password_reset_request")
    public String sendPasswordResetRequest(
            @ModelAttribute("passwordResetRequestForm") PasswordResetRequestForm passwordResetRequestForm
    ) throws WebServiceRuntimeException {
        String email = passwordResetRequestForm.getEmail();
        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            passwordResetRequestForm.setErrMsg("Please provide a valid email address");
            return FORGET_PSWD_PAGE;
        }

        JSONObject obj = new JSONObject();
        obj.put("username", email);

        log.info("Connecting to sio for password reset email: {}", email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(obj.toString(), headers);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = null;
        try {
            response = restTemplate.exchange(properties.getPasswordResetRequestURI(), HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            log.warn("Cannot connect to sio for password reset email: {}", e);
            passwordResetRequestForm.setErrMsg("Cannot connect. Server may be down!");
            return FORGET_PSWD_PAGE;
        }

        if (RestUtil.isError(response.getStatusCode())) {
            log.warn("Server responded error for password reset email: {}", response.getStatusCode());
            passwordResetRequestForm.setErrMsg("Email not registered. Please use a different email address.");
            return FORGET_PSWD_PAGE;
        }

        log.info("Password reset email sent for {}", email);
        return "password_reset_email_sent";
    }

    // triggered when user clicks password reset link in the email
    @RequestMapping(path = "/passwordReset", params = {"key"})
    public String passwordResetNewPassword(@NotNull @RequestParam("key") final String key, Model model) {
        PasswordResetForm form = new PasswordResetForm();
        form.setKey(key);
        model.addAttribute("passwordResetForm", form);
        // redirect to the page for user to enter new password
        return FORGET_PSWD_NEW_PSWD_PAGE;
    }

    // actual call to sio to reset password
    @RequestMapping(path = "/password_reset")
    public String resetPassword(@ModelAttribute("passwordResetForm") PasswordResetForm passwordResetForm) throws IOException {
        if (!passwordResetForm.isPasswordOk()) {
            return FORGET_PSWD_NEW_PSWD_PAGE;
        }

        JSONObject obj = new JSONObject();
        obj.put("key", passwordResetForm.getKey());
        obj.put("new", passwordResetForm.getPassword1());

        log.info("Connecting to sio for password reset, key = {}", passwordResetForm.getKey());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(obj.toString(), headers);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = null;
        try {
            response = restTemplate.exchange(properties.getPasswordResetURI(), HttpMethod.PUT, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio for password reset! {}", e);
            passwordResetForm.setErrMsg("Cannot connect to server! Please try again later.");
            return FORGET_PSWD_NEW_PSWD_PAGE;
        }

        if (RestUtil.isError(response.getStatusCode())) {
            EnumMap<ExceptionState, String> exceptionMessageMap = new EnumMap<>(ExceptionState.class);
            exceptionMessageMap.put(PASSWORD_RESET_REQUEST_TIMEOUT_EXCEPTION, "Password reset request timed out. Please request a new reset email.");
            exceptionMessageMap.put(PASSWORD_RESET_REQUEST_NOT_FOUND_EXCEPTION, "Invalid password reset request. Please request a new reset email.");
            exceptionMessageMap.put(ADAPTER_CONNECTION_EXCEPTION, "Server-side error. Please contact " + CONTACT_EMAIL);

            MyErrorResource error = objectMapper.readValue(response.getBody().toString(), MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            final String errMsg = exceptionMessageMap.get(exceptionState) == null ? ERR_SERVER_OVERLOAD : exceptionMessageMap.get(exceptionState);
            passwordResetForm.setErrMsg(errMsg);
            log.warn("Server responded error for password reset: {}", exceptionState.toString());
            return FORGET_PSWD_NEW_PSWD_PAGE;
        }
        log.info("Password was reset, key = {}", passwordResetForm.getKey());
        return "password_reset_success";
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getDeterUid(session.getAttribute(webProperties.getSessionUserId()).toString()), HttpMethod.GET, request, String.class);

        String responseBody = response.getBody().toString();
        try {
            if (RestUtil.isError(response.getStatusCode())) {
                log.error("No user exists : {}", session.getAttribute(webProperties.getSessionUserId()));
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                model.addAttribute(DETER_UID, CONNECTION_ERROR);
            } else {
                log.info("Show the deter user id: {}", responseBody);
                model.addAttribute(DETER_UID, responseBody);
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        // retrieve user dashboard stats
        Map<String, Integer> userDashboardMap = getUserDashboardStats(session.getAttribute(webProperties.getSessionUserId()).toString());
        List<TeamUsageInfo> usageInfoList = getTeamsUsageStatisticsForUser(session.getAttribute(webProperties.getSessionUserId()).toString());
        model.addAttribute("userDashboardMap", userDashboardMap);
        model.addAttribute("usageInfoList", usageInfoList);

        try {
            response = restTemplate.exchange(properties.getDiskStatistics() + "/users/" + session.getAttribute(webProperties.getSessionUserId()).toString(), HttpMethod.GET, request, String.class);
            responseBody = response.getBody().toString();
            log.info(responseBody);
            JSONObject jsonObject = new JSONObject(responseBody);
            model.addAttribute("spaceSize", jsonObject.getString("spaceSize"));
            model.addAttribute("directory", jsonObject.getString("directory"));
            model.addAttribute("alert", jsonObject.getString("alert"));
            model.addAttribute("quota", jsonObject.getString("quota"));
        } catch (Exception e) {
            log.error("Unable to get user disk usage: {}", session.getAttribute(webProperties.getSessionUserId()));
            model.addAttribute("alert", "info");
        }

        return "dashboard";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        removeSessionVariables(session);
        return REDIRECT_INDEX_PAGE;
    }

    //--------------------------Sign Up Page--------------------------
    //Comment out as per G/J request to remove log in for users momentary
    @RequestMapping(value = "/signup2", method = RequestMethod.GET)
    public String signup2(Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        //Dev 1216 - disabling the create account functionality temporarily and redirecting to a diff page//
        return "page_unavailable";

        /*if (inputFlashMap != null) {
            log.debug((String) inputFlashMap.get(MESSAGE));
            model.addAttribute(SIGNUP_MERGED_FORM, (SignUpMergedForm) inputFlashMap.get(SIGNUP_MERGED_FORM));
        } else {
            log.debug("InputFlashMap is null");
            model.addAttribute(SIGNUP_MERGED_FORM, new SignUpMergedForm());
        }

        return SIGNUP_PAGE;
        */
    }

    @RequestMapping(value = "/signup2", method = RequestMethod.POST)
    public String validateDetails(
            @Valid
            @ModelAttribute(SIGNUP_MERGED_FORM) SignUpMergedForm signUpMergedForm,
            BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        // Dev 1216 - disabling the create account functionality temporarily and redirecting to a diff page//
         return "page_unavailable";


       /* if (bindingResult.hasErrors() || !signUpMergedForm.getIsValid()) {
            log.warn("Register form has errors {}", signUpMergedForm.toString());
            return SIGNUP_PAGE;
        }

        if (!signUpMergedForm.getHasAcceptTeamOwnerPolicy()) {
            signUpMergedForm.setErrorTeamOwnerPolicy("Please accept the team owner policy");
            log.warn("Policy not accepted");
            return SIGNUP_PAGE;
        }

        // get form fields
        // craft the registration json
        JSONObject mainObject = new JSONObject();
        JSONObject credentialsFields = new JSONObject();
        credentialsFields.put("username", signUpMergedForm.getEmail().trim());
        credentialsFields.put(PSWD, signUpMergedForm.getPassword());

        // create the user JSON
        JSONObject userFields = new JSONObject();
        JSONObject userDetails = new JSONObject();
        JSONObject addressDetails = new JSONObject();

        userDetails.put(FNAME, signUpMergedForm.getFirstName().trim());
        userDetails.put(LNAME, signUpMergedForm.getLastName().trim());
        userDetails.put(JOB_TITLE, signUpMergedForm.getJobTitle().trim());
        userDetails.put(EMAIL, signUpMergedForm.getEmail().trim());
        userDetails.put(PHONE, signUpMergedForm.getPhone().trim());
        userDetails.put(INSTITUTION, signUpMergedForm.getInstitution().trim());
        userDetails.put(INSTITUTION_ABBREVIATION, signUpMergedForm.getInstitutionAbbreviation().trim());
        userDetails.put(INSTITUTION_WEB, signUpMergedForm.getWebsite().trim());
        userDetails.put(ADDRESS, addressDetails);

        addressDetails.put(ADDRESS1, signUpMergedForm.getAddress1().trim());
        addressDetails.put(ADDRESS2, signUpMergedForm.getAddress2().trim());
        addressDetails.put(COUNTRY, signUpMergedForm.getCountry().trim());
        addressDetails.put(REGION, signUpMergedForm.getProvince().trim());
        addressDetails.put(CITY, signUpMergedForm.getCity().trim());
        addressDetails.put(ZIP_CODE, signUpMergedForm.getPostalCode().trim());

        userFields.put(USER_DETAILS, userDetails);
        userFields.put(APPLICATION_DATE, ZonedDateTime.now());

        JSONObject teamFields = new JSONObject();

        // add all to main json
        mainObject.put("credentials", credentialsFields);
        mainObject.put("user", userFields);
        mainObject.put("team", teamFields);

        // check if user chose create new team or join existing team by checking team name
        String createNewTeamName = signUpMergedForm.getTeamName().trim();
        String joinNewTeamName = signUpMergedForm.getJoinTeamName().trim();

        if (!createNewTeamName.isEmpty()) {
            return checkNewTeamForm(signUpMergedForm, redirectAttributes, mainObject, teamFields, createNewTeamName);
        } else if (!joinNewTeamName.isEmpty()) {
            return checkJoinTeamForm(signUpMergedForm, redirectAttributes, mainObject, teamFields, joinNewTeamName);
        } else {
            log.warn("Signup unreachable statement");

            // logic error not suppose to reach here
            // possible if user fill up create new team but without the team name
            redirectAttributes.addFlashAttribute("signupError", "There is a problem when submitting your form. Please re-enter and submit the details again.");
            redirectAttributes.addFlashAttribute(SIGNUP_MERGED_FORM, signUpMergedForm);
            return REDIRECT_SIGNUP;
        }*/
    }

    private String checkJoinTeamForm(@Valid @ModelAttribute(SIGNUP_MERGED_FORM) SignUpMergedForm signUpMergedForm, RedirectAttributes redirectAttributes, JSONObject mainObject, JSONObject teamFields, String joinNewTeamName) throws WebServiceRuntimeException {
        log.info("Signup join team name {}", joinNewTeamName);
        // get the team JSON from team name
        Team2 joinTeamInfo;

        try {
            joinTeamInfo = getTeamIdByName(signUpMergedForm.getJoinTeamName().trim());
        } catch (TeamNotFoundException | AdapterConnectionException e) {
            redirectAttributes.addFlashAttribute(MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute(SIGNUP_MERGED_FORM, signUpMergedForm);
            return REDIRECT_SIGNUP;
        }

        teamFields.put("id", joinTeamInfo.getId());

        // set the flag to indicate to controller that it is joining an existing team
        mainObject.put("isJoinTeam", true);

        try {
            registerUserToDeter(mainObject);
        } catch (
                TeamNotFoundException |
                        AdapterConnectionException |
                        TeamNameAlreadyExistsException |
                        UsernameAlreadyExistsException |
                        EmailAlreadyExistsException |
                        InvalidTeamNameException |
                        InvalidPasswordException |
                        DeterLabOperationFailedException e) {
            redirectAttributes.addFlashAttribute(MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute(SIGNUP_MERGED_FORM, signUpMergedForm);
            return REDIRECT_SIGNUP;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            redirectAttributes.addFlashAttribute(SIGNUP_MERGED_FORM, signUpMergedForm);
            return REDIRECT_SIGNUP;
        }

        log.info("Signup join team success");
        log.info("jointeam info: {}", joinTeamInfo);
        redirectAttributes.addFlashAttribute("team", joinTeamInfo);
        return "redirect:/join_application_submitted";
    }

    private String checkNewTeamForm(@Valid @ModelAttribute(SIGNUP_MERGED_FORM) SignUpMergedForm signUpMergedForm, RedirectAttributes redirectAttributes, JSONObject mainObject, JSONObject teamFields, String createNewTeamName) {
        log.info("Signup new team name {}", createNewTeamName);
        boolean errorsFound = false;

        if (createNewTeamName.length() < 2 || createNewTeamName.length() > 12) {
            errorsFound = true;
            signUpMergedForm.setErrorTeamName("Team name must be 2 to 12 alphabetic/numeric characters");
        }

        if (signUpMergedForm.getTeamDescription() == null || signUpMergedForm.getTeamDescription().isEmpty()) {
            errorsFound = true;
            signUpMergedForm.setErrorTeamDescription("Team description cannot be empty");
        }

        if (signUpMergedForm.getTeamWebsite() == null || signUpMergedForm.getTeamWebsite().isEmpty()) {
            errorsFound = true;
            signUpMergedForm.setErrorTeamWebsite("Team website cannot be empty");
        }

        if (errorsFound) {
            log.warn("Signup new team error {}", signUpMergedForm.toString());
            // clear join team name first before submitting the form
            signUpMergedForm.setJoinTeamName(null);
            return SIGNUP_PAGE;
        } else {

            teamFields.put("name", signUpMergedForm.getTeamName().trim());
            teamFields.put(DESCRIPTION, signUpMergedForm.getTeamDescription().trim());
            teamFields.put(WEBSITE, signUpMergedForm.getTeamWebsite().trim());
            teamFields.put(ORGANISATION_TYPE, signUpMergedForm.getTeamOrganizationType());
            teamFields.put(VISIBILITY, signUpMergedForm.getIsPublic());
            teamFields.put(IS_CLASS, signUpMergedForm.getIsClass());
            mainObject.put("isJoinTeam", false);
            try {
                registerUserToDeter(mainObject);
            } catch (
                    TeamNotFoundException |
                            TeamNameAlreadyExistsException |
                            UsernameAlreadyExistsException |
                            EmailAlreadyExistsException |
                            InvalidTeamNameException |
                            InvalidPasswordException |
                            DeterLabOperationFailedException e) {
                redirectAttributes.addFlashAttribute(MESSAGE, e.getMessage());
                redirectAttributes.addFlashAttribute(SIGNUP_MERGED_FORM, signUpMergedForm);
                return REDIRECT_SIGNUP;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                redirectAttributes.addFlashAttribute(SIGNUP_MERGED_FORM, signUpMergedForm);
                return REDIRECT_SIGNUP;
            }

            log.info("Signup new team success");
            return "redirect:/team_application_submitted";
        }
    }

    /**
     * Use when registering new accounts
     *
     * @param mainObject A JSONObject that contains user's credentials, personal details and team application details
     */
    private void registerUserToDeter(JSONObject mainObject) throws
            WebServiceRuntimeException,
            TeamNotFoundException,
            AdapterConnectionException,
            TeamNameAlreadyExistsException,
            UsernameAlreadyExistsException,
            EmailAlreadyExistsException,
            InvalidTeamNameException,
            InvalidPasswordException,
            DeterLabOperationFailedException {
        HttpEntity<String> request = createHttpEntityWithBodyNoAuthHeader(mainObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getSioRegUrl(), HttpMethod.POST, request, String.class);

        String responseBody = response.getBody().toString();

        log.info("Register user to deter response: {}", responseBody);

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);

                log.warn("Register user exception error: {}", error.getError());

                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                String email = mainObject.getJSONObject("user").getJSONObject(USER_DETAILS).getString(EMAIL);

                switch (exceptionState) {
                    case DETERLAB_OPERATION_FAILED_EXCEPTION:
                        log.warn("Register new user failed on DeterLab: {}", error.getMessage());
                        throw new DeterLabOperationFailedException(ERROR_PREFIX + (error.getMessage().contains("unknown error") ? ERR_SERVER_OVERLOAD : error.getMessage()));
                    case TEAM_NAME_ALREADY_EXISTS_EXCEPTION:
                        log.warn("Register new users new team request : team name already exists");
                        throw new TeamNameAlreadyExistsException("Team name already exists");
                    case INVALID_TEAM_NAME_EXCEPTION:
                        log.warn("Register new users new team request : team name invalid");
                        throw new InvalidTeamNameException("Invalid team name: must be 6-12 alphanumeric characters only");
                    case INVALID_PASSWORD_EXCEPTION:
                        log.warn("Register new users new team request : invalid password");
                        throw new InvalidPasswordException("Password is too simple");
                    case USERNAME_ALREADY_EXISTS_EXCEPTION:
                        // throw from user service
                        log.warn("Register new users : email already exists: {}", email);
                        throw new UsernameAlreadyExistsException(ERROR_PREFIX + email + " already in use.");
                    case EMAIL_ALREADY_EXISTS_EXCEPTION:
                        // throw from adapter deterlab
                        log.warn("Register new users : email already exists: {}", email);
                        throw new EmailAlreadyExistsException(ERROR_PREFIX + email + " already in use.");
                    default:
                        log.warn("Registration or adapter connection fail");
                        // possible sio or adapter connection fail
                        throw new AdapterConnectionException(ERR_SERVER_OVERLOAD);
                }
            } else {
                // do nothing
                log.info("Not an error for status code: {}", response.getStatusCode());
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    /**
     * Use when users register a new account for joining existing team
     *
     * @param teamName The team name to join
     * @return the team id from sio
     */
    private Team2 getTeamIdByName(String teamName) throws WebServiceRuntimeException, TeamNotFoundException, AdapterConnectionException {
        // FIXME check if team name exists
        // FIXME check for general exception?
        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getTeamByName(teamName), HttpMethod.GET, request, String.class);

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == ExceptionState.TEAM_NOT_FOUND_EXCEPTION) {
                    log.warn("Get team by name : team name error");
                    throw new TeamNotFoundException("Team name " + teamName + " does not exists");
                } else {
                    log.warn("Team service or adapter connection fail");
                    // possible sio or adapter connection fail
                    throw new AdapterConnectionException(ERR_SERVER_OVERLOAD);
                }

            } else {
                return extractTeamInfo(responseBody);
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    //--------------------------Account Settings Page--------------------------
    @RequestMapping(value = "/account_settings", method = RequestMethod.GET)
    public String accountDetails(Model model, HttpSession session) throws WebServiceRuntimeException {

        String userId_uri = properties.getSioUsersUrl() + session.getAttribute("id");
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(userId_uri, HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                log.error("No user to edit : {}", session.getAttribute("id"));
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                throw new RestClientException("[" + error.getError() + "] ");
            } else {
                User2 user2 = extractUserInfo(responseBody);
                // need to do this so that we can compare after submitting the form
                session.setAttribute(webProperties.getSessionUserAccount(), user2);
                model.addAttribute("editUser", user2);
                return "account_settings";
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

    }

    @RequestMapping(value = "/account_settings", method = RequestMethod.POST)
    public String editAccountDetails(
            @ModelAttribute("editUser") User2 editUser,
            final RedirectAttributes redirectAttributes,
            HttpSession session) throws WebServiceRuntimeException {

        String editPhrase = "editPhrase";

        if (checkEditUserFields(editUser, redirectAttributes, editPhrase)) {
            session.removeAttribute(webProperties.getSessionUserAccount());
            return "redirect:/account_settings";
        } else {
            // used to compare original and edited User2 objects
            User2 originalUser = (User2) session.getAttribute(webProperties.getSessionUserAccount());

            JSONObject userObject = new JSONObject();
            JSONObject userDetails = new JSONObject();
            JSONObject address = new JSONObject();

            userDetails.put(FNAME, editUser.getFirstName());
            userDetails.put(LNAME, editUser.getLastName());
            userDetails.put(EMAIL, editUser.getEmail());
            userDetails.put(PHONE, editUser.getPhone());
            userDetails.put(JOB_TITLE, editUser.getJobTitle());
            userDetails.put(ADDRESS, address);
            userDetails.put(INSTITUTION, editUser.getInstitution());
            userDetails.put(INSTITUTION_ABBREVIATION, originalUser.getInstitutionAbbreviation());
            userDetails.put(INSTITUTION_WEB, originalUser.getInstitutionWeb());

            address.put(ADDRESS1, originalUser.getAddress1());
            address.put(ADDRESS2, originalUser.getAddress2());
            address.put(COUNTRY, editUser.getCountry());
            address.put(CITY, originalUser.getCity());
            address.put(REGION, originalUser.getRegion());
            address.put(ZIP_CODE, originalUser.getPostalCode());

            userObject.put(USER_DETAILS, userDetails);

            String userId_uri = properties.getSioUsersUrl() + session.getAttribute(webProperties.getSessionUserId());

            HttpEntity<String> request = createHttpEntityWithBody(userObject.toString());
            restTemplate.exchange(userId_uri, HttpMethod.PUT, request, String.class);

            checkUserUpdate(editUser, redirectAttributes, originalUser);

            // credential service change password
            if (editUser.isPasswordMatch()) {
                JSONObject credObject = new JSONObject();
                credObject.put(PSWD, editUser.getPassword());

                HttpEntity<String> credRequest = createHttpEntityWithBody(credObject.toString());
                restTemplate.setErrorHandler(new MyResponseErrorHandler());
                ResponseEntity response = restTemplate.exchange(properties.getUpdateCredentials(session.getAttribute("id").toString()), HttpMethod.PUT, credRequest, String.class);
                String responseBody = response.getBody().toString();

                try {
                    if (RestUtil.isError(response.getStatusCode())) {
                        MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                        redirectAttributes.addFlashAttribute(editPhrase, "fail");
                    } else {
                        redirectAttributes.addFlashAttribute(editPhrase, SUCCESS);
                    }
                } catch (IOException e) {
                    throw new WebServiceRuntimeException(e.getMessage());
                } finally {
                    session.removeAttribute(webProperties.getSessionUserAccount());
                }
            }
        }
        return "redirect:/account_settings";
    }

    private void checkUserUpdate(@ModelAttribute("editUser") User2 editUser, RedirectAttributes redirectAttributes, User2 originalUser) {
        if (!originalUser.getFirstName().equals(editUser.getFirstName())) {
            redirectAttributes.addFlashAttribute("editFirstName", SUCCESS);
        }
        if (!originalUser.getLastName().equals(editUser.getLastName())) {
            redirectAttributes.addFlashAttribute("editLastName", SUCCESS);
        }
        if (!originalUser.getPhone().equals(editUser.getPhone())) {
            redirectAttributes.addFlashAttribute("editPhone", SUCCESS);
        }
        if (!originalUser.getJobTitle().equals(editUser.getJobTitle())) {
            redirectAttributes.addFlashAttribute("editJobTitle", SUCCESS);
        }
        if (!originalUser.getInstitution().equals(editUser.getInstitution())) {
            redirectAttributes.addFlashAttribute("editInstitution", SUCCESS);
        }
        if (!originalUser.getCountry().equals(editUser.getCountry())) {
            redirectAttributes.addFlashAttribute("editCountry", SUCCESS);
        }
    }

    private boolean checkEditUserFields(@ModelAttribute("editUser") User2 editUser, RedirectAttributes redirectAttributes, String editPhrase) {
        boolean errorsFound = false;
        if (!errorsFound && editUser.getFirstName().isEmpty()) {
            redirectAttributes.addFlashAttribute("editFirstName", "fail");
            errorsFound = true;
        }

        if (!errorsFound && editUser.getLastName().isEmpty()) {
            redirectAttributes.addFlashAttribute("editLastName", "fail");
            errorsFound = true;
        }

        if (!errorsFound && (editUser.getPhone().isEmpty() || editUser.getPhone().matches("(.*)[a-zA-Z](.*)") || editUser.getPhone().length() < 6)) {
            redirectAttributes.addFlashAttribute("editPhone", "fail");
            errorsFound = true;
        }

        if (!errorsFound && !editUser.getConfirmPassword().isEmpty() && !editUser.isPasswordValid()) {
            redirectAttributes.addFlashAttribute(editPhrase, "invalid");
            errorsFound = true;
        }

        if (!errorsFound && editUser.getJobTitle().isEmpty()) {
            redirectAttributes.addFlashAttribute("editJobTitle", "fail");
            errorsFound = true;
        }

        if (!errorsFound && editUser.getInstitution().isEmpty()) {
            redirectAttributes.addFlashAttribute("editInstitution", "fail");
            errorsFound = true;
        }

        if (!errorsFound && editUser.getCountry().isEmpty()) {
            redirectAttributes.addFlashAttribute("editCountry", "fail");
            errorsFound = true;
        }
        return errorsFound;
    }

    //--------------------User Side Approve Members Page------------

    @RequestMapping("/approve_new_user")
    public String approveNewUser(Model model, HttpSession session) throws Exception {
//    	HashMap<Integer, Team> rv = new HashMap<Integer, Team>();
//    	rv = teamManager.getTeamMapByTeamOwner(getSessionIdOfLoggedInUser(session));
//    	boolean userHasAnyJoinRequest = hasAnyJoinRequest(rv);
//    	model.addAttribute("teamMapOwnedByUser", rv);
//    	model.addAttribute("userHasAnyJoinRequest", userHasAnyJoinRequest);

        List<JoinRequestApproval> rv = new ArrayList<>();
        List<JoinRequestApproval> temp;

        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getUser(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        JSONObject object = new JSONObject(responseBody);
        JSONArray teamIdsJsonArray = object.getJSONArray(TEAMS);

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            Team2 team2 = new Team2();
            JSONObject teamObject = new JSONObject(teamResponseBody);
            JSONArray membersArray = teamObject.getJSONArray(MEMBERS);

            team2.setId(teamObject.getString("id"));
            team2.setName(teamObject.getString("name"));

            boolean isTeamLeader = false;
            temp = new ArrayList<>();

            for (int j = 0; j < membersArray.length(); j++) {
                JSONObject memberObject = membersArray.getJSONObject(j);
                String userId = memberObject.getString(USER_ID);
                String teamMemberType = memberObject.getString(MEMBER_TYPE);
                String teamMemberStatus = memberObject.getString(MEMBER_STATUS);
                String teamJoinedDate = formatZonedDateTime(memberObject.get("joinedDate").toString());

                JoinRequestApproval joinRequestApproval = new JoinRequestApproval();

                if (userId.equals(session.getAttribute("id").toString()) && teamMemberType.equals(MemberType.OWNER.toString())) {
                    isTeamLeader = true;
                }

                if (teamMemberStatus.equals(MemberStatus.PENDING.toString()) && teamMemberType.equals(MemberType.MEMBER.toString())) {
                    User2 myUser = invokeAndExtractUserInfo(userId);
                    joinRequestApproval.setUserId(myUser.getId());
                    joinRequestApproval.setUserEmail(myUser.getEmail());
                    joinRequestApproval.setUserName(myUser.getFirstName() + TAG_SPACE + myUser.getLastName());
                    joinRequestApproval.setApplicationDate(teamJoinedDate);
                    joinRequestApproval.setTeamId(team2.getId());
                    joinRequestApproval.setTeamName(team2.getName());
                    joinRequestApproval.setVerified(myUser.getEmailVerified());
                    temp.add(joinRequestApproval);
                    log.info("Join request: UserId: {}, UserEmail: {}", myUser.getId(), myUser.getEmail());
                }
            }

            if (isTeamLeader && !temp.isEmpty()) {
                rv.addAll(temp);
            }

        }

        model.addAttribute("joinApprovalList", rv);

        return "approve_new_user";
    }

    @RequestMapping("/approve_new_user/accept/{teamId}/{userId}")
    public String userSideAcceptJoinRequest(
            @PathVariable String teamId,
            @PathVariable String userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        log.info("Approve join request: User {}, Team {}, Approver {}",
                userId, teamId, session.getAttribute("id").toString());

        JSONObject mainObject = new JSONObject();
        JSONObject userFields = new JSONObject();
        userFields.put("id", session.getAttribute("id").toString());
        mainObject.put("user", userFields);

        HttpEntity<String> request = createHttpEntityWithBody(mainObject.toString());
        ResponseEntity response;
        try {
            response = restTemplate.exchange(properties.getApproveJoinRequest(teamId, userId), HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio team service: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_APPROVE_NEW_USER;
        }

        String responseBody = response.getBody().toString();
        if (RestUtil.isError(response.getStatusCode())) {
            try {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case EMAIL_NOT_VERIFIED_EXCEPTION:
                        log.warn("Approve join request: User {} email not verified", userId);
                        redirectAttributes.addFlashAttribute(MESSAGE, "User email has not been verified");
                        break;
                    case DETERLAB_OPERATION_FAILED_EXCEPTION:
                        log.warn("Approve join request: User {}, Team {} fail", userId, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE, "Approve join request fail");
                        break;
                    default:
                        log.warn("Server side error: {}", error.getError());
                        redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                        break;
                }
                return REDIRECT_APPROVE_NEW_USER;
            } catch (IOException ioe) {
                log.warn(LOG_IOEXCEPTION, ioe);
                throw new WebServiceRuntimeException(ioe.getMessage());
            }
        }
        // everything looks OK?
        log.info("Join request has been APPROVED, User {}, Team {}", userId, teamId);
        redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Join request has been APPROVED.");
        return REDIRECT_APPROVE_NEW_USER;
    }

    @RequestMapping("/approve_new_user/reject/{teamId}/{userId}")
    public String userSideRejectJoinRequest(
            @PathVariable String teamId,
            @PathVariable String userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        log.info("Reject join request: User {}, Team {}, Approver {}",
                userId, teamId, session.getAttribute("id").toString());

        JSONObject mainObject = new JSONObject();
        JSONObject userFields = new JSONObject();
        userFields.put("id", session.getAttribute("id").toString());
        mainObject.put("user", userFields);

        HttpEntity<String> request = createHttpEntityWithBody(mainObject.toString());
        ResponseEntity response;
        try {
            response = restTemplate.exchange(properties.getRejectJoinRequest(teamId, userId), HttpMethod.DELETE, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio team service: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_APPROVE_NEW_USER;
        }

        String responseBody = response.getBody().toString();
        if (RestUtil.isError(response.getStatusCode())) {
            try {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case DETERLAB_OPERATION_FAILED_EXCEPTION:
                        log.warn("Reject join request: User {}, Team {} fail", userId, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE, "Reject join request fail");
                        break;
                    default:
                        log.warn("Server side error: {}", error.getError());
                        redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                        break;
                }
                return REDIRECT_APPROVE_NEW_USER;
            } catch (IOException ioe) {
                log.warn(LOG_IOEXCEPTION, ioe);
                throw new WebServiceRuntimeException(ioe.getMessage());
            }
        }
        // everything looks OK?
        log.info("Join request has been REJECTED, User {}, Team {}", userId, teamId);
        redirectAttributes.addFlashAttribute(MESSAGE, "Join request has been REJECTED.");
        return REDIRECT_APPROVE_NEW_USER;
    }

    //--------------------------Teams Page--------------------------

    @RequestMapping("/public_teams")
    public String publicTeamsBeforeLogin(Model model) {
        TeamManager2 teamManager2 = new TeamManager2();

        // get public teams
        HttpEntity<String> teamRequest = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamsByVisibility(TeamVisibility.PUBLIC.toString()), HttpMethod.GET, teamRequest, String.class);
        String teamResponseBody = teamResponse.getBody().toString();

        JSONArray teamPublicJsonArray = new JSONArray(teamResponseBody);
        for (int i = 0; i < teamPublicJsonArray.length(); i++) {
            JSONObject teamInfoObject = teamPublicJsonArray.getJSONObject(i);
            Team2 team2 = extractTeamInfo(teamInfoObject.toString());
            teamManager2.addTeamToPublicTeamMap(team2);
        }

        model.addAttribute("publicTeamMap2", teamManager2.getPublicTeamMap());
        return "public_teams";
    }

    @RequestMapping("/teams")
    public String teams(Model model, HttpSession session) {
//        int currentLoggedInUserId = getSessionIdOfLoggedInUser(session);
//        model.addAttribute("infoMsg", teamManager.getInfoMsg());
//        model.addAttribute("currentLoggedInUserId", currentLoggedInUserId);
//        model.addAttribute("teamMap", teamManager.getTeamMap(currentLoggedInUserId));
//        model.addAttribute("publicTeamMap", teamManager.getPublicTeamMap());
//        model.addAttribute("invitedToParticipateMap2", teamManager.getInvitedToParticipateMap2(currentLoggedInUserId));
//        model.addAttribute("joinRequestMap2", teamManager.getJoinRequestTeamMap2(currentLoggedInUserId));

        TeamManager2 teamManager2 = new TeamManager2();

        // stores the list of images created or in progress of creation by teams
        // e.g. teamNameA : "created" : [imageA, imageB], "inProgress" : [imageC, imageD]
        Map<String, Map<String, List<Image>>> imageMap = new HashMap<>();

        // get list of teamids
        String userId = session.getAttribute("id").toString();
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getUser(userId), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        JSONObject object = new JSONObject(responseBody);
        JSONArray teamIdsJsonArray = object.getJSONArray(TEAMS);

        String userEmail = object.getJSONObject(USER_DETAILS).getString(EMAIL);

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            //Tran: check if team is approved for userId
            Team2 joinRequestTeam = extractTeamInfoUserJoinRequest(userId, teamResponseBody);
            if (joinRequestTeam != null) {

                teamManager2.addTeamToUserJoinRequestTeamMap(joinRequestTeam);
            } else {
                Team2 team2 = extractTeamInfo(teamResponseBody);
                teamManager2.addTeamToTeamMap(team2);

                imageMap.put(team2.getName(), invokeAndGetImageList(teamId));  //Tran : only retrieve images of approved teams
            }
        }

        // check if inner image map is empty, have to do it via this manner
        // returns true if the team contains an image list
        boolean isInnerImageMapPresent = imageMap.values().stream().filter(perTeamImageMap -> !perTeamImageMap.isEmpty()).findFirst().isPresent();

        model.addAttribute("userEmail", userEmail);
        model.addAttribute("teamMap2", teamManager2.getTeamMap());
        model.addAttribute("userJoinRequestMap", teamManager2.getUserJoinRequestMap());
        model.addAttribute("isInnerImageMapPresent", isInnerImageMapPresent);
        model.addAttribute("imageMap", imageMap);
        return TEAMS;
    }

    /**
     * Exectues the service-image and returns a Map containing the list of images in two partitions.
     * One partition contains the list of already created images.
     * The other partition contains the list of currently saving in progress images.
     *
     * @param teamId The ncl team id to retrieve the list of images from.
     * @return Returns a Map containing the list of images in two partitions.
     */
    private Map<String, List<Image>> invokeAndGetImageList(String teamId) {
        log.info("Getting list of saved images for team {}", teamId);

        Map<String, List<Image>> resultMap = new HashMap<>();
        List<Image> createdImageList = new ArrayList<>();
        List<Image> inProgressImageList = new ArrayList<>();
        List<Image> failedImageList = new ArrayList<>();
        List<Image> notFoundImageList = new ArrayList<>();

        HttpEntity<String> imageRequest = createHttpEntityHeaderOnly();
        ResponseEntity imageResponse;
        try {
            imageResponse = restTemplate.exchange(properties.getTeamSavedImages(teamId), HttpMethod.GET, imageRequest, String.class);
        } catch (ResourceAccessException e) {
            log.warn("Error connecting to image service: {}", e);
            return resultMap;
        }

        String imageResponseBody = imageResponse.getBody().toString();

        String osImageList = new JSONObject(imageResponseBody).getString(teamId);
        JSONObject osImageObject = new JSONObject(osImageList);

        log.debug("osImageList: {}", osImageList);
        log.debug("osImageObject: {}", osImageObject);

        if (osImageObject == JSONObject.NULL || osImageObject.length() == 0) {
            log.info("List of saved images for team {} is empty.", teamId);
            return resultMap;
        }

        for (int k = 0; k < osImageObject.names().length(); k++) {
            String imageName = osImageObject.names().getString(k);
            String imageStatus = osImageObject.getString(imageName);

            log.info("Image list for team {}: image name {}, status {}", teamId, imageName, imageStatus);

            Image image = new Image();
            image.setImageName(imageName);
            image.setDescription("-");
            image.setTeamId(teamId);

            if ("created".equals(imageStatus)) {
                createdImageList.add(image);
            } else if ("saving".equals(imageStatus)) {
                inProgressImageList.add(image);
            }else if ("failed".equals(imageStatus)) {
                failedImageList.add(image);
            }else if ("notfound".equals(imageStatus)) {
                notFoundImageList.add(image);
            }
        }

        resultMap.put("created", createdImageList);
        resultMap.put("inProgress", inProgressImageList);
        resultMap.put("failed", failedImageList);
        resultMap.put("notfound", notFoundImageList);

        return resultMap;
    }

//    @RequestMapping("/accept_participation/{teamId}")
//    public String acceptParticipationRequest(@PathVariable Integer teamId, Model model, HttpSession session) {
//    	int currentLoggedInUserId = getSessionIdOfLoggedInUser(session);
//        // get user's participation request list
//        // add this user id to the requested list
//        teamManager.acceptParticipationRequest(currentLoggedInUserId, teamId);
//        // remove participation request since accepted
//        teamManager.removeParticipationRequest(currentLoggedInUserId, teamId);
//
//        // must get team name
//        String teamName = teamManager.getTeamNameByTeamId(teamId);
//        teamManager.setInfoMsg("You have just joined Team " + teamName + " !");
//
//        return REDIRECT_TEAMS;
//    }

//    @RequestMapping("/ignore_participation/{teamId}")
//    public String ignoreParticipationRequest(@PathVariable Integer teamId, Model model, HttpSession session) {
//        // get user's participation request list
//        // remove this user id from the requested list
//        String teamName = teamManager.getTeamNameByTeamId(teamId);
//        teamManager.ignoreParticipationRequest2(getSessionIdOfLoggedInUser(session), teamId);
//        teamManager.setInfoMsg("You have just ignored a team request from Team " + teamName + " !");
//
//        return REDIRECT_TEAMS;
//    }

    //    @RequestMapping("/withdraw/{teamId}")
    public String withdrawnJoinRequest(@PathVariable Integer teamId, HttpSession session) {
        // get user team request
        // remove this user id from the user's request list
        String teamName = teamManager.getTeamNameByTeamId(teamId);
        teamManager.removeUserJoinRequest2(getSessionIdOfLoggedInUser(session), teamId);
        teamManager.setInfoMsg("You have withdrawn your join request for Team " + teamName);

        return REDIRECT_TEAMS;
    }

//    @RequestMapping(value="/teams/invite_members/{teamId}", method=RequestMethod.GET)
//    public String inviteMember(@PathVariable Integer teamId, Model model) {
//        model.addAttribute("teamIdVar", teamId);
//        model.addAttribute("teamPageInviteMemberForm", new TeamPageInviteMemberForm());
//        return "team_page_invite_members";
//    }

//    @RequestMapping(value="/teams/invite_members/{teamId}", method=RequestMethod.POST)
//    public String sendInvitation(@PathVariable Integer teamId, @ModelAttribute TeamPageInviteMemberForm teamPageInviteMemberForm,Model model) {
//        int userId = userManager.getUserIdByEmail(teamPageInviteMemberForm.getInviteUserEmail());
//        teamManager.addInvitedToParticipateMap(userId, teamId);
//        return REDIRECT_TEAMS;
//    }

    @RequestMapping(value = "/teams/members_approval/{teamId}", method = RequestMethod.GET)
    public String membersApproval(@PathVariable Integer teamId, Model model) {
        model.addAttribute("team", teamManager.getTeamByTeamId(teamId));
        return "team_page_approve_members";
    }

    @RequestMapping("/teams/members_approval/accept/{teamId}/{userId}")
    public String acceptJoinRequest(@PathVariable Integer teamId, @PathVariable Integer userId) {
        teamManager.acceptJoinRequest(userId, teamId);
        return "redirect:/teams/members_approval/{teamId}";
    }

    @RequestMapping("/teams/members_approval/reject/{teamId}/{userId}")
    public String rejectJoinRequest(@PathVariable Integer teamId, @PathVariable Integer userId) {
        teamManager.rejectJoinRequest(userId, teamId);
        return "redirect:/teams/members_approval/{teamId}";
    }

    @RequestMapping("/teams/delete_image/{teamId}/{imageName}")
    public String deleteImage(
            @PathVariable String teamId,
            @PathVariable String imageName,
            final RedirectAttributes redirectAttributes)
            throws WebServiceRuntimeException {

        String errorMessage = "Error in deleting image {} from team '{}' ";
        String imageMessage = "The image " + "\'" + imageName + "\'";

        log.info("Deleting image {} from team {}", imageName, teamId);
        try {
            HttpEntity<String> request = createHttpEntityHeaderOnly();
            restTemplate.setErrorHandler(new MyResponseErrorHandler());
            ResponseEntity response = restTemplate.exchange(properties.deleteImage(teamId, imageName),
                    HttpMethod.DELETE, request, String.class);
            String responseBody = response.getBody().toString();

            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case INSUFFICIENT_PERMISSION_EXCEPTION:
                        log.warn(errorMessage + ": insufficient permission", imageName, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, "You do not have permission to delete this image. Only " +
                                " team leader or creator of this image can delete the image.");
                        break;
                    case TEAM_NOT_FOUND_EXCEPTION:
                        log.warn(errorMessage + ": team not found", imageName, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, "Team '" + teamId + "' is not found");
                        break;
                    case IMAGE_NOT_FOUND_EXCEPTION:
                        log.warn(errorMessage + ": image does not exist or not found in teams' list of images", imageName, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, imageMessage + " either does not exist or not found in your teams' list of images");
                        break;
                    case DETERLAB_OPERATION_FAILED_EXCEPTION:
                        log.warn(errorMessage + ": operation failed on DeterLab", imageName, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, ERR_SERVER_OVERLOAD);
                        break;
                    case ADAPTER_CONNECTION_EXCEPTION:
                        log.warn(errorMessage + ": adapter connection error", imageName, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, ERR_SERVER_OVERLOAD);
                        break;
                    case ADAPTER_INTERNAL_ERROR_EXCEPTION:
                        log.warn(errorMessage + ": adapter internal error", imageName, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, ERR_SERVER_OVERLOAD);
                        break;
                    default:
                        log.warn(errorMessage + ": {}", imageName, teamId,  exceptionState);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, ERR_SERVER_OVERLOAD);
                        break;
                }
                return REDIRECT_TEAMS;
            } else {
                String sioMessage = new JSONObject(responseBody).getString("msg");

                switch (sioMessage) {
                    case "image still in use":
                        log.warn(errorMessage + ": {}", imageName, teamId, sioMessage);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, imageMessage + " is still in use or busy!");

                        // show experiments list
                        // string experiments is passed from adapter
                        // truncate the square brackets in front and behind
                        if (responseBody.contains(EXPERIMENTS)) {
                            String experiments = new JSONObject(responseBody).getJSONArray(EXPERIMENTS).toString();
                            redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE_LIST, experiments.substring(1, experiments.length()-1));
                        }

                        break;
                    // curl command is ok but there is problem with rm command
                    case "delete image OK from web but there is unknown error when deleting physical image":
                        log.warn(errorMessage + ": {}", imageName, teamId, sioMessage);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_WARNING, imageMessage + " is successfully deleted. " +
                                "However, " + ERR_SERVER_OVERLOAD);
                        break;
                    default:
                        log.info("Deleting image '{}' of team '{}' is successful ", imageName, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_SUCCESS, imageMessage + " is successfully deleted");
                        break;
                }
                return REDIRECT_TEAMS;
            }

        } catch (IOException e) {
            log.warn("Error connecting to sio image service for deleting image: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE_DELETE_IMAGE_FAILURE, ERR_SERVER_OVERLOAD);
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    //--------------------------Team Profile Page--------------------------

    @RequestMapping(value = "/team_profile/{teamId}", method = RequestMethod.GET)
    public String teamProfile(@PathVariable String teamId, Model model, final RedirectAttributes redirectAttributes, HttpSession session) throws IOException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        Team2 team = extractTeamInfo(responseBody);
        model.addAttribute("team", team);
        model.addAttribute(KEY_OWNER, team.getOwner());
        model.addAttribute("membersList", team.getMembersStatusMap().get(MemberStatus.APPROVED));
        session.setAttribute(ORIGINAL_TEAM, team);

        //// check if user belongs to this team or not - show team profile page only if user is owner or a approved team member or  admin //
        //above check applies to only private teams//
        //public teams are visible to all
        String userId = session.getAttribute(webProperties.getSessionUserId()).toString();
        int memberCount = team.getMembersStatusMap().size();
        boolean ismember = false;
        List<User2> membersList = team.getMembersStatusMap().get(MemberStatus.APPROVED);
        for (int i = 0; i < membersList.size(); i++) {
           if (membersList.get(i).getId().equals(userId)) {
                ismember = true;
            }
        }
        List<StatefulExperiment> experimentList = getStatefulExperiments(teamId);

        model.addAttribute("teamExperimentList", experimentList);

        //Starting to get quota
        try {
            response = restTemplate.exchange(properties.getQuotaByTeamId(teamId), HttpMethod.GET, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio team service for display team quota: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_TEAM_PROFILE_TEAM_ID;
        }

        responseBody = response.getBody().toString();

        // handling exceptions from SIO
        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
            switch (exceptionState) {
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn("Get team quota: Team {} not found", teamId);
                    return REDIRECT_INDEX_PAGE;
                default:
                    log.warn("Get team quota : sio or deterlab adapter connection error");
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
            }
        } else {
            log.info("Get team quota info : {}", responseBody);
        }

        TeamQuota teamQuota = extractTeamQuotaInfo(responseBody);
        model.addAttribute("teamQuota", teamQuota);
        session.setAttribute(ORIGINAL_BUDGET, teamQuota.getBudget()); // this is to check if budget changed later
        if (team.getVisibility().equalsIgnoreCase("PRIVATE") && (ismember == true || validateIfAdmin(session))){
           return "team_profile";
        }
        else if (team.getVisibility().equalsIgnoreCase("PUBLIC")) {
           return "team_profile";
        }
        else{
            log.warn("Cannot display team profile:Only team members can see private team's profile");
            redirectAttributes.addFlashAttribute(MESSAGE, "Only team members can see team profile");
            return REDIRECT_INDEX_PAGE;
        }
    }

    @RequestMapping(value = "/team_profile/{teamId}", method = RequestMethod.POST)
    public String editTeamProfile(
            @PathVariable String teamId,
            @ModelAttribute("team") Team2 editTeam,
            final RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException {

        boolean errorsFound = false;

        if (editTeam.getDescription().isEmpty()) {
            errorsFound = true;
            redirectAttributes.addFlashAttribute("editDesc", "fail");
        }

        if (errorsFound) {
            // safer to remove
            session.removeAttribute(ORIGINAL_TEAM);
            return REDIRECT_TEAM_PROFILE + editTeam.getId();
        }

        // can edit team description and team website for now

        JSONObject teamfields = new JSONObject();
        teamfields.put("id", teamId);
        teamfields.put("name", editTeam.getName());
        teamfields.put(DESCRIPTION, editTeam.getDescription());
        teamfields.put(WEBSITE, "http://default.com");
        teamfields.put(ORGANISATION_TYPE, editTeam.getOrganisationType());
        teamfields.put("privacy", "OPEN");
        teamfields.put(STATUS, editTeam.getStatus());
        teamfields.put(MEMBERS, editTeam.getMembersList());

        HttpEntity<String> request = createHttpEntityWithBody(teamfields.toString());
        ResponseEntity response;
        try {
            response = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.PUT, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio team service for edit team profile: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_TEAM_PROFILE + teamId;
        }

        String responseBody = response.getBody().toString();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
            switch (exceptionState) {
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn("Edit team profile: Team {} not found", teamId);
                    return REDIRECT_INDEX_PAGE;
                case FORBIDDEN_EXCEPTION:
                    log.warn("Edit team profile: Profile can only be updated by team owner.");
                    redirectAttributes.addFlashAttribute(MESSAGE, "Profile can only be updated by team owner.");
                    return REDIRECT_TEAM_PROFILE + teamId;
                default:
                    log.warn("Edit team profile: sio or deterlab adapter connection error");
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    return REDIRECT_TEAM_PROFILE + teamId;
            }
        }

        Team2 originalTeam = (Team2) session.getAttribute(ORIGINAL_TEAM);

        if (!originalTeam.getDescription().equals(editTeam.getDescription())) {
            redirectAttributes.addFlashAttribute("editDesc", SUCCESS);
        }

        // safer to remove
        session.removeAttribute(ORIGINAL_TEAM);
        return REDIRECT_TEAM_PROFILE + teamId;
    }

    @RequestMapping(value = "/team_quota/{teamId}", method = RequestMethod.POST)
    public String editTeamQuota(
            @PathVariable String teamId,
            @ModelAttribute("teamQuota") TeamQuota editTeamQuota,
            final RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException {

        final String NUMBER_QUOTA = "#quota";
        JSONObject teamQuotaJSONObject = new JSONObject();
        teamQuotaJSONObject.put(TEAM_ID, teamId);

        // check if budget is negative or exceeding limit
        if (!editTeamQuota.getBudget().equals("")) {
            if (Double.parseDouble(editTeamQuota.getBudget()) < 0) {
                redirectAttributes.addFlashAttribute(EDIT_BUDGET, "negativeError");
                return REDIRECT_TEAM_PROFILE + teamId + NUMBER_QUOTA;
            } else if(Double.parseDouble(editTeamQuota.getBudget()) > 99999999.99) {
                redirectAttributes.addFlashAttribute(EDIT_BUDGET, "exceedingLimit");
                return REDIRECT_TEAM_PROFILE + teamId + NUMBER_QUOTA;
            }
        }

        teamQuotaJSONObject.put(QUOTA, editTeamQuota.getBudget());
        HttpEntity<String> request = createHttpEntityWithBody(teamQuotaJSONObject.toString());
        ResponseEntity response;
        try {
            response = restTemplate.exchange(properties.getQuotaByTeamId(teamId), HttpMethod.PUT, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio team service for display team quota: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_TEAM_PROFILE_TEAM_ID;
        }

        String responseBody = response.getBody().toString();
        // handling exceptions from SIO
        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
            switch (exceptionState) {
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn("Get team quota: Team {} not found", teamId);
                    return REDIRECT_INDEX_PAGE;
                case TEAM_QUOTA_OUT_OF_RANGE_EXCEPTION:
                    log.warn("Get team quota: Budget is out of range");
                    return REDIRECT_TEAM_PROFILE + teamId + NUMBER_QUOTA;
                case FORBIDDEN_EXCEPTION:
                    log.warn("Get team quota: Budget can only be updated by team owner.");
                    redirectAttributes.addFlashAttribute(EDIT_BUDGET, "editDeny");
                    return REDIRECT_TEAM_PROFILE + teamId + NUMBER_QUOTA;
                default:
                    log.warn("Get team quota : sio or deterlab adapter connection error");
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    return REDIRECT_TEAM_PROFILE + teamId + NUMBER_QUOTA;
            }
        }  else {
            log.info("Edit team quota info : {}", responseBody);
        }


        //check if new budget is different in order to display successful message to user
        String originalBudget = (String) session.getAttribute(ORIGINAL_BUDGET);
        if (!originalBudget.equals(editTeamQuota.getBudget())) {
            redirectAttributes.addFlashAttribute(EDIT_BUDGET, SUCCESS);
        }

        // safer to remove
        session.removeAttribute(ORIGINAL_BUDGET);

        return REDIRECT_TEAM_PROFILE + teamId + NUMBER_QUOTA;
    }

    @RequestMapping("/remove_member/{teamId}/{userId}")
    public String removeMember(@PathVariable String teamId, @PathVariable String userId, final RedirectAttributes redirectAttributes) throws IOException {

        JSONObject teamMemberFields = new JSONObject();
        teamMemberFields.put(USER_ID, userId);
        teamMemberFields.put(MEMBER_TYPE, MemberType.MEMBER.name());
        teamMemberFields.put(MEMBER_STATUS, MemberStatus.APPROVED.name());

        HttpEntity<String> request = createHttpEntityWithBody(teamMemberFields.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.removeUserFromTeam(teamId), HttpMethod.DELETE, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio team service for remove user: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_TEAM_PROFILE_TEAM_ID;
        }

        String responseBody = response.getBody().toString();

        User2 user = invokeAndExtractUserInfo(userId);
        String name = user.getFirstName() + TAG_SPACE + user.getLastName();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            switch (exceptionState) {
                case DETERLAB_OPERATION_FAILED_EXCEPTION:
                    // two subcases when fail to remove users from team
                    log.warn("Remove member from team: User {}, Team {} fail - {}", userId, teamId, error.getMessage());

                    if ("user has experiments".equals(error.getMessage())) {
                        /*
                        case 1 - user has experiments
                        display the list of experiments that have to be terminated first

                        since the team profile page has experiments already, we don't have to retrieve them again
                        use the userid to filter out the experiment list at the web pages
                        */
                        redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + " Member " + name + " has experiments.");
                        redirectAttributes.addFlashAttribute(REMOVE_MEMBER_UID, userId);
                        redirectAttributes.addFlashAttribute(REMOVE_MEMBER_NAME, name);
                        break;
                    } else {
                        // case 2 - deterlab operation failure
                        log.warn("Remove member from team: deterlab operation failed");
                        redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + " Member " + name + " cannot be removed.");
                        break;
                    }
                default:
                    log.warn("Server side error for remove members: {}", error.getError());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
            }
        } else {
            log.info("Remove member: {}", response.getBody().toString());
            // add success message
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Member " + name + " has been removed.");
        }

        return REDIRECT_TEAM_PROFILE_TEAM_ID;
    }

    //   @RequestMapping("/team_profile/{teamId}/start_experiment/{expId}")
    //  public String startExperimentFromTeamProfile(@PathVariable Integer teamId, @PathVariable Integer expId, Model model, HttpSession session) {
//         start experiment
//        // ensure experiment is stopped first before starting
//        experimentManager.startExperiment(getSessionIdOfLoggedInUser(session), expId);
//    	return "redirect:/team_profile/{teamId}";
//    }

//    @RequestMapping("/team_profile/{teamId}/stop_experiment/{expId}")
//    public String stopExperimentFromTeamProfile(@PathVariable Integer teamId, @PathVariable Integer expId, Model model, HttpSession session) {
//        // stop experiment
//        // ensure experiment is in ready mode before stopping
//        experimentManager.stopExperiment(getSessionIdOfLoggedInUser(session), expId);
//        return "redirect:/team_profile/{teamId}";
//    }

//    @RequestMapping("/team_profile/{teamId}/remove_experiment/{expId}")
//    public String removeExperimentFromTeamProfile(@PathVariable Integer teamId, @PathVariable Integer expId, Model model, HttpSession session) {
//        // remove experiment
//        // TODO check userid is indeed the experiment owner or team owner
//        // ensure experiment is stopped first
//        if (experimentManager.removeExperiment(getSessionIdOfLoggedInUser(session), expId) == true) {
//            // decrease exp count to be display on Teams page
//            teamManager.decrementExperimentCount(teamId);
//        }
//        model.addAttribute("experimentList", experimentManager.getExperimentListByExperimentOwner(getSessionIdOfLoggedInUser(session)));
//        return "redirect:/team_profile/{teamId}";
//    }

//    @RequestMapping(value="/team_profile/invite_user/{teamId}", method=RequestMethod.GET)
//    public String inviteUserFromTeamProfile(@PathVariable Integer teamId, Model model) {
//        model.addAttribute("teamIdVar", teamId);
//        model.addAttribute("teamPageInviteMemberForm", new TeamPageInviteMemberForm());
//        return "team_profile_invite_members";
//    }

//    @RequestMapping(value="/team_profile/invite_user/{teamId}", method=RequestMethod.POST)
//    public String sendInvitationFromTeamProfile(@PathVariable Integer teamId, @ModelAttribute TeamPageInviteMemberForm teamPageInviteMemberForm, Model model) {
//        int userId = userManager.getUserIdByEmail(teamPageInviteMemberForm.getInviteUserEmail());
//        teamManager.addInvitedToParticipateMap(userId, teamId);
//        return "redirect:/team_profile/{teamId}";
//    }


    //--------------------------Apply for New Team Page--------------------------

    @RequestMapping(value = "/teams/apply_team", method = RequestMethod.GET)
    public String teamPageApplyTeam(Model model) {
        model.addAttribute("teamPageApplyTeamForm", new TeamPageApplyTeamForm());
        if (!model.containsAttribute(KEY_QUERY)) {
            model.addAttribute(KEY_QUERY, new ProjectUsageQuery());
           /* model.addAttribute("newProjects", new ArrayList<ProjectDetails>());
            model.addAttribute("activeProjects", new ArrayList<ProjectDetails>());
            model.addAttribute("inactiveProjects", new ArrayList<ProjectDetails>());
            model.addAttribute("stoppedProjects", new ArrayList<ProjectDetails>());
            model.addAttribute("utilization", new HashMap<String, MonthlyUtilization>());
            model.addAttribute("statsCategory", new HashMap<String, Integer>());
            model.addAttribute("statsAcademic", new HashMap<String, Integer>());*/
        }
        return "team_page_apply_team";
    }

    @RequestMapping(value = "/teams/apply_team", method = RequestMethod.POST)
    public String checkApplyTeamInfo(
            @Valid
            @ModelAttribute TeamPageApplyTeamForm teamPageApplyTeamForm,
            BindingResult bindingResult,
            HttpSession session,
            final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        final String LOG_PREFIX = "Existing user apply for new team: {}";
        if (bindingResult.hasErrors()) {
            log.warn(LOG_PREFIX, "Application form error " + teamPageApplyTeamForm.toString());
            return "team_page_apply_team";
        }

        // log data to ensure data has been parsed
        log.debug(LOG_PREFIX, properties.getRegisterRequestToApplyTeam(session.getAttribute("id").toString()));
        log.info(LOG_PREFIX, teamPageApplyTeamForm.toString());
        JSONObject mainObject = new JSONObject();
        JSONObject teamFields = new JSONObject();
        mainObject.put("team", teamFields);
        teamFields.put("name", teamPageApplyTeamForm.getTeamName());
        teamFields.put(DESCRIPTION, teamPageApplyTeamForm.getTeamDescription());
        teamFields.put(WEBSITE, teamPageApplyTeamForm.getTeamWebsite());
        teamFields.put(ORGANISATION_TYPE, teamPageApplyTeamForm.getTeamOrganizationType());
        teamFields.put(VISIBILITY, teamPageApplyTeamForm.getIsPublic());
        teamFields.put(IS_CLASS, teamPageApplyTeamForm.getIsClass());

        String nclUserId = session.getAttribute("id").toString();
        HttpEntity<String> request = createHttpEntityWithBody(mainObject.toString());
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.getRegisterRequestToApplyTeam(nclUserId), HttpMethod.POST, request, String.class);
            String responseBody = response.getBody().toString();
            if (RestUtil.isError(response.getStatusCode())) {

                // prepare the exception mapping
                EnumMap<ExceptionState, String> exceptionMessageMap = new EnumMap<>(ExceptionState.class);
                exceptionMessageMap.put(USER_ID_NULL_OR_EMPTY_EXCEPTION, "User id is null or empty ");
                exceptionMessageMap.put(TEAM_NAME_NULL_OR_EMPTY_EXCEPTION, "Team name is null or empty ");
                exceptionMessageMap.put(USER_NOT_FOUND_EXCEPTION, "User not found");
                exceptionMessageMap.put(TEAM_NAME_ALREADY_EXISTS_EXCEPTION, "Team name already exists");
                exceptionMessageMap.put(INVALID_TEAM_NAME_EXCEPTION, "Team name contains invalid characters");
                exceptionMessageMap.put(TEAM_MEMBER_ALREADY_EXISTS_EXCEPTION, "Team member already exists");
                exceptionMessageMap.put(ADAPTER_CONNECTION_EXCEPTION, "Connection to adapter failed");
                exceptionMessageMap.put(ADAPTER_INTERNAL_ERROR_EXCEPTION, "Internal server error on adapter");
                exceptionMessageMap.put(DETERLAB_OPERATION_FAILED_EXCEPTION, "Operation failed on DeterLab");

                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                final String errorMessage = exceptionMessageMap.containsKey(exceptionState) ? error.getMessage() : ERR_SERVER_OVERLOAD;
                log.warn(LOG_PREFIX, responseBody);
                redirectAttributes.addFlashAttribute(MESSAGE, errorMessage);
                return "redirect:/teams/apply_team";

            } else {
                // no errors, everything ok
                log.info(LOG_PREFIX, "Application for team " + teamPageApplyTeamForm.getTeamName() + " submitted");
                return "redirect:/teams/team_application_submitted";
            }

        } catch (ResourceAccessException | IOException e) {
            log.error(LOG_PREFIX, e);
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping(value = "/terms_and_conditions", method = RequestMethod.GET)
    public String termsAndConditions() {
        return "terms_and_conditions";
    }

    @RequestMapping(value = "/data_license_agreement", method = RequestMethod.GET)
    public String dataLicenseAgreement() {
        return "data_license_agreement";
    }

    //--------------------------Join Team Page--------------------------

    @RequestMapping(value = "/teams/join_team", method = RequestMethod.GET)
    public String teamPageJoinTeam(Model model) {
        model.addAttribute("teamPageJoinTeamForm", new TeamPageJoinTeamForm());
        return "team_page_join_team";
    }

    @RequestMapping(value = "/teams/join_team", method = RequestMethod.POST)
    public String checkJoinTeamInfo(
            @Valid TeamPageJoinTeamForm teamPageJoinForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        final String LOG_PREFIX = "Existing user join team: {}";

        if (bindingResult.hasErrors()) {
            log.warn(LOG_PREFIX, "Application form error " + teamPageJoinForm.toString());
            return "team_page_join_team";
        }

        JSONObject mainObject = new JSONObject();
        JSONObject teamFields = new JSONObject();
        JSONObject userFields = new JSONObject();

        mainObject.put("team", teamFields);
        mainObject.put("user", userFields);

        userFields.put("id", session.getAttribute("id")); // ncl-id

        teamFields.put("name", teamPageJoinForm.getTeamName());

        log.info(LOG_PREFIX, USER_PREFIX + session.getAttribute("id") + ", team " + teamPageJoinForm.getTeamName());

        HttpEntity<String> request = createHttpEntityWithBody(mainObject.toString());
        ResponseEntity response;

        try {
            restTemplate.setErrorHandler(new MyResponseErrorHandler());
            response = restTemplate.exchange(properties.getJoinRequestExistingUser(), HttpMethod.POST, request, String.class);
            String responseBody = response.getBody().toString();

            if (RestUtil.isError(response.getStatusCode())) {
                // prepare the exception mapping
                EnumMap<ExceptionState, String> exceptionMessageMap = new EnumMap<>(ExceptionState.class);
                exceptionMessageMap.put(USER_NOT_FOUND_EXCEPTION, "User not found");
                exceptionMessageMap.put(USER_ID_NULL_OR_EMPTY_EXCEPTION, "User id is null or empty");
                exceptionMessageMap.put(TEAM_NOT_FOUND_EXCEPTION, "Team name not found");
                exceptionMessageMap.put(TEAM_NAME_NULL_OR_EMPTY_EXCEPTION, "Team name is null or empty");
                exceptionMessageMap.put(USER_ALREADY_IN_TEAM_EXCEPTION, "User already in team");
                exceptionMessageMap.put(TEAM_MEMBER_ALREADY_EXISTS_EXCEPTION, "Team member already exists");
                exceptionMessageMap.put(ADAPTER_CONNECTION_EXCEPTION, "Connection to adapter failed");
                exceptionMessageMap.put(ADAPTER_INTERNAL_ERROR_EXCEPTION, "Internal server error on adapter");
                exceptionMessageMap.put(DETERLAB_OPERATION_FAILED_EXCEPTION, "Operation failed on DeterLab");

                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                final String errorMessage = exceptionMessageMap.containsKey(exceptionState) ? error.getMessage() : ERR_SERVER_OVERLOAD;

                log.warn(LOG_PREFIX, responseBody);
                redirectAttributes.addFlashAttribute(MESSAGE, errorMessage);
                return "redirect:/teams/join_team";

            } else {
                log.info(LOG_PREFIX, "Application for join team " + teamPageJoinForm.getTeamName() + " submitted");
                return "redirect:/teams/join_application_submitted/" + teamPageJoinForm.getTeamName();
            }

        } catch (ResourceAccessException | IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    //--------------------------Experiment Page--------------------------

    @RequestMapping(value = "/experiments", method = RequestMethod.GET)
    public String experiments(Model model, HttpSession session) throws WebServiceRuntimeException {
        List<StatefulExperiment> statefulExperimentList = new ArrayList<>();
        HttpEntity<String> request = getDeterUid(model, session);

        // get list of teamIds
        ResponseEntity userRespEntity = restTemplate.exchange(properties.getUser(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);

        JSONObject object = new JSONObject(userRespEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray(TEAMS);

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();

            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            if (!isMemberJoinRequestPending(session.getAttribute("id").toString(), teamResponseBody)) {
                List<StatefulExperiment> myExpList = getStatefulExperiments(teamId);
                if (!myExpList.isEmpty()) {
                    statefulExperimentList.addAll(myExpList);
                }
            }
        }

        model.addAttribute("experimentList", statefulExperimentList);
        model.addAttribute("internetRequestForm", new InternetRequestForm());
        //ExperimentForm experimentForm= new ExperimentForm();
        model.addAttribute("csrfToken", session.getAttribute("csrfToken").toString());
       // experimentForm.setCsrfToken(session.getAttribute("csrfToken").toString());
        model.addAttribute("experimentForm", new ExperimentForm());

        return EXPERIMENTS;
    }

    private HttpEntity<String> getDeterUid(Model model, HttpSession session) throws WebServiceRuntimeException {
        // get uid on Deter
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getDeterUid(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                log.error("Failed to get Deter uid for user {}: {}", session.getAttribute("id").toString(), error.getError());
                model.addAttribute(DETER_UID, UNKNOWN);
            } else {
                model.addAttribute(DETER_UID, responseBody);
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
        return request;
    }

    @GetMapping(value = "/experiment_profile/{expId}")
    public String experimentProfile(@PathVariable String expId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getStatefulExperiment(expId), HttpMethod.GET, request, String.class);

        StatefulExperiment stateExp = extractStatefulExperiment(response.getBody().toString());

        User2 experimentOwner = invokeAndExtractUserInfo(stateExp.getUserId());

        /*
         * get experiment details
         * returns a json string in the format:
         * {
         *   'ns_file' :
         *              {
         *              'msg' : 'success/fail',
         *              'ns_file' : 'ns_file_contents'
         *              },
         *              'realization_details' :
         *              {
         *              'msg' : 'success/fail',
         *              'realization_details' : 'realization_details_contents'
         *              },
         *              'activity_log'	:
         *              {
         *              'msg' : 'success/fail',
         *              activity_log' : 'activity_log_contents'
         *              }
         *  }
         *  returns a '{}' otherwise if fail
         */
        ResponseEntity expDetailsResponse = restTemplate.exchange(properties.getExperimentDetails(stateExp.getTeamId(), expId), HttpMethod.GET, request, String.class);
        log.debug("experiment profile - experiment details: {}", expDetailsResponse.getBody().toString());

        model.addAttribute("experiment", stateExp);
        model.addAttribute("experimentOwner", experimentOwner.getFirstName() + ' ' + experimentOwner.getLastName());
        model.addAttribute("experimentDetails", new JSONObject(expDetailsResponse.getBody().toString()));
        return "experiment_profile";
    }

    @RequestMapping(value = "/experiments/create", method = RequestMethod.GET)
    public String createExperiment(Model model, HttpSession session) throws WebServiceRuntimeException {
        log.info("Loading create experiment page");
        // a list of teams that the logged in user is in
        List<String> scenarioFileNameList = getScenarioFileNameList();
        List<Team2> userTeamsList = new ArrayList<>();

        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getUser(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        JSONObject object = new JSONObject(responseBody);

        JSONArray teamIdsJsonArray = object.getJSONArray(TEAMS);

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();
            Team2 team2 = extractTeamInfo(teamResponseBody);
            userTeamsList.add(team2);
        }

        model.addAttribute("scenarioFileNameList", scenarioFileNameList);
        model.addAttribute("experimentForm", new ExperimentForm());
        model.addAttribute("userTeamsList", userTeamsList);
        return "experiment_page_create_experiment";
    }

    @RequestMapping(value = "/experiments/create", method = RequestMethod.POST)
    public String validateExperiment(
            @ModelAttribute("experimentForm") ExperimentForm experimentForm,
            BindingResult bindingResult,
            HttpSession session,
            final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        if (bindingResult.hasErrors()) {
            log.info("Create experiment - form has errors");
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                switch (fieldError.getField()) {
                    case MAX_DURATION:
                        redirectAttributes.addFlashAttribute(MESSAGE, MAX_DURATION_ERROR);
                        break;
                    default:
                        redirectAttributes.addFlashAttribute(MESSAGE, "Form not filled up");
                }
            }
            return REDIRECT_CREATE_EXPERIMENT;
        }



        if (!experimentForm.getMaxDuration().toString().matches("\\d+")) {
            redirectAttributes.addFlashAttribute(MESSAGE, MAX_DURATION_ERROR);
            return REDIRECT_CREATE_EXPERIMENT;
        }



        if (experimentForm.getName() == null || experimentForm.getName().isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Experiment Name cannot be empty");
            return REDIRECT_CREATE_EXPERIMENT;
        }

        if(experimentForm.getName()!= null && (!experimentForm.getName().isEmpty()))
        {
            Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
            Matcher matcher = special.matcher(experimentForm.getName());
            boolean b = matcher.find();

            if (b==true) {
                redirectAttributes.addFlashAttribute(MESSAGE, "Experiment Name cannot contain special characters");
                return REDIRECT_CREATE_EXPERIMENT;

            }

        }


        if (experimentForm.getDescription() == null || experimentForm.getDescription().isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Description cannot be empty");
            return REDIRECT_CREATE_EXPERIMENT;
        }


        if(experimentForm.getDescription()!= null && (!experimentForm.getDescription().isEmpty()))
        {

            Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
            Matcher matcher = special.matcher(experimentForm.getDescription());

            boolean b = matcher.find();

            if (b==true) {

                redirectAttributes.addFlashAttribute(MESSAGE, "Description cannot contain special characters");
                return REDIRECT_CREATE_EXPERIMENT;

            }

        }
        experimentForm.setScenarioContents(getScenarioContentsFromFile(experimentForm.getScenarioFileName()));

        JSONObject experimentObject = new JSONObject();
        experimentObject.put(USER_ID, session.getAttribute("id").toString());
        experimentObject.put(TEAM_ID, experimentForm.getTeamId());
        experimentObject.put(TEAM_NAME, experimentForm.getTeamName());
        experimentObject.put("name", experimentForm.getName().replaceAll("\\s+", "")); // truncate whitespaces and non-visible characters like \n
        experimentObject.put(DESCRIPTION, experimentForm.getDescription());
        experimentObject.put("nsFile", "file");
        experimentObject.put("nsFileContent", experimentForm.getNsFileContent());
        experimentObject.put("idleSwap", "240");
        experimentObject.put(MAX_DURATION, experimentForm.getMaxDuration());

        log.info("Calling service to create experiment");
        HttpEntity<String> request = createHttpEntityWithBody(experimentObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getSioExpUrl(), HttpMethod.POST, request, String.class);

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case NS_FILE_PARSE_EXCEPTION:
                        log.warn("Ns file error");
                        redirectAttributes.addFlashAttribute(MESSAGE, "There is an error when parsing the NS File.");
                        break;
                    case EXPERIMENT_NAME_ALREADY_EXISTS_EXCEPTION:
                        log.warn("Exp name already exists");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Experiment name already exists.");
                        break;
                    default:
                        log.warn("Exp service or adapter fail");
                        // possible sio or adapter connection fail
                        redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                        break;
                }
                log.info("Experiment {} created", experimentForm);
                return REDIRECT_CREATE_EXPERIMENT;
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        //
        // TODO Uploaded function for network configuration and optional dataset

//		if (!networkFile.isEmpty()) {
//			try {
//				String networkFileName = getSessionIdOfLoggedInUser(session) + "-networkconfig-" + networkFile.getOriginalFilename();
//				BufferedOutputStream stream = new BufferedOutputStream(
//						new FileOutputStream(new File(App.EXP_CONFIG_DIR + "/" + networkFileName)));
//                FileCopyUtils.copy(networkFile.getInputStream(), stream);
//				stream.close();
//				redirectAttributes.addFlashAttribute(MESSAGE,
//						"You successfully uploaded " + networkFile.getOriginalFilename() + "!");
//				// remember network file name here
//			}
//			catch (Exception e) {
//				redirectAttributes.addFlashAttribute(MESSAGE,
//						"You failed to upload " + networkFile.getOriginalFilename() + " => " + e.getMessage());
//				return REDIRECT_CREATE_EXPERIMENT;
//			}
//		}
//
//		if (!dataFile.isEmpty()) {
//			try {
//				String dataFileName = getSessionIdOfLoggedInUser(session) + "-data-" + dataFile.getOriginalFilename();
//				BufferedOutputStream stream = new BufferedOutputStream(
//						new FileOutputStream(new File(App.EXP_CONFIG_DIR + "/" + dataFileName)));
//                FileCopyUtils.copy(dataFile.getInputStream(), stream);
//				stream.close();
//				redirectAttributes.addFlashAttribute("message2",
//						"You successfully uploaded " + dataFile.getOriginalFilename() + "!");
//				// remember data file name here
//			}
//			catch (Exception e) {
//				redirectAttributes.addFlashAttribute("message2",
//						"You failed to upload " + dataFile.getOriginalFilename() + " => " + e.getMessage());
//			}
//		}
//
//    	// add current experiment to experiment manager
//        experimentManager.addExperiment(getSessionIdOfLoggedInUser(session), experiment);
//        // increase exp count to be display on Teams page
//        teamManager.incrementExperimentCount(experiment.getTeamId());

        return REDIRECT_EXPERIMENTS;
    }

    @RequestMapping(value = "/experiments/save_image/{teamId}/{expId}/{nodeId}", method = RequestMethod.GET)
    public String saveExperimentImage(@PathVariable String teamId, @PathVariable String expId, @PathVariable String nodeId, Model model) {
        Map<String, Map<String, String>> singleNodeInfoMap = new HashMap<>();
        Image saveImageForm = new Image();

        String teamName = invokeAndExtractTeamInfo(teamId).getName();
        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        // experiment may have many nodes
        // extract just the particular node details to display
        for (Map.Entry<String, Map<String, String>> nodesInfo : realization.getNodesInfoMap().entrySet()) {
            String nodeName = nodesInfo.getKey();
            Map<String, String> singleNodeDetailsMap = nodesInfo.getValue();
            if (singleNodeDetailsMap.get(NODE_ID).equals(nodeId)) {
                singleNodeInfoMap.put(nodeName, singleNodeDetailsMap);
                // store the current os of the node into the form also
                // have to pass the the services
                saveImageForm.setCurrentOS(singleNodeDetailsMap.get("os"));
            }
        }

        saveImageForm.setTeamId(teamId);
        saveImageForm.setNodeId(nodeId);

        model.addAttribute(TEAM_NAME, teamName);
        model.addAttribute("singleNodeInfoMap", singleNodeInfoMap);
        model.addAttribute("pathTeamId", teamId);
        model.addAttribute("pathExperimentId", expId);
        model.addAttribute("pathNodeId", nodeId);
        model.addAttribute("experimentName", realization.getExperimentName());
        model.addAttribute("saveImageForm", saveImageForm);
        return "save_experiment_image";
    }

    // bindingResult is required in the method signature to perform the JSR303 validation for Image object
    @RequestMapping(value = "/experiments/save_image/{teamId}/{expId}/{nodeId}", method = RequestMethod.POST)
    public String saveExperimentImage(
            @Valid @ModelAttribute("saveImageForm") Image saveImageForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @PathVariable String teamId,
            @PathVariable String expId,
            @PathVariable String nodeId) throws IOException {

        String REDIRECT_SAVING_IMAGE = "redirect:/experiments/save_image/";

        //checking image name
        String imageName = saveImageForm.getImageName();
        if (imageName.length() < 2) {
            log.warn("Save image form has errors {}", saveImageForm);
            redirectAttributes.addFlashAttribute(MESSAGE, "Image name is required minimum of 2 characters");
            return REDIRECT_SAVING_IMAGE + teamId + "/" + expId + "/" + nodeId;
        } else if (!VALID_IMAGE_NAME.matcher(imageName).matches()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Image name can only contain alphanumeric characters and \"-\"");
            return REDIRECT_SAVING_IMAGE + teamId + "/" + expId + "/" + nodeId;
        }

        log.info("Saving image: team {}, experiment {}, node {}", teamId, expId, nodeId);
        ObjectMapper mapper = new ObjectMapper();
        HttpEntity<String> request = createHttpEntityWithBody(mapper.writeValueAsString(saveImageForm));
        ResponseEntity response;
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        response = restTemplate.exchange(properties.saveImage(), HttpMethod.POST, request, String.class);
        String responseBody = response.getBody().toString();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            log.warn("Save image: error with exception {}", exceptionState);
            String IMAGE_SAVING_ERROR = "Saving image error: {}";
            switch (exceptionState) {
                case DETERLAB_OPERATION_FAILED_EXCEPTION:
                    log.warn(IMAGE_SAVING_ERROR, error.getMessage());
                    redirectAttributes.addFlashAttribute(MESSAGE, error.getMessage() + " Please contact " + CONTACT_EMAIL + " for support.");
                    break;
                case ADAPTER_CONNECTION_EXCEPTION:
                    log.warn(IMAGE_SAVING_ERROR, "cannot connect to adapter");
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
                case ADAPTER_INTERNAL_ERROR_EXCEPTION:
                    log.warn(IMAGE_SAVING_ERROR, "adapter internal server error");
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
                default:
                    log.warn(IMAGE_SAVING_ERROR, error.getMessage());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            }

            return REDIRECT_SAVING_IMAGE + teamId + "/" + expId + "/" + nodeId;
        }

        // everything looks ok
        log.info("Saving image in progress: team {}, experiment {}, node {}, image {}", teamId, expId, nodeId, saveImageForm.getImageName());
        return REDIRECT_EXPERIMENTS;
    }

/*
    private String processSaveImageRequest(@Valid @ModelAttribute("saveImageForm") Image saveImageForm, RedirectAttributes redirectAttributes, @PathVariable String teamId, @PathVariable String expId, @PathVariable String nodeId, ResponseEntity response, String responseBody) throws IOException {
        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            log.warn("Save image exception: {}", exceptionState);

            switch (exceptionState) {
                case DETERLAB_OPERATION_FAILED_EXCEPTION:
                    log.warn("adapter deterlab operation failed exception");
                    redirectAttributes.addFlashAttribute(MESSAGE, error.getMessage());
                    break;
                default:
                    log.warn("Image service or adapter fail");
                    // possible sio or adapter connection fail
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
            }
            return "redirect:/experiments/save_image/" + teamId + "/" + expId + "/" + nodeId;
        } else {
            // everything ok
            log.info("Image service in progress for Team: {}, Exp: {}, Node: {}, Image: {}", teamId, expId, nodeId, saveImageForm.getImageName());
            return REDIRECT_EXPERIMENTS;
        }
    }
*/

//    @RequestMapping("/experiments/configuration/{expId}")
//    public String viewExperimentConfiguration(@PathVariable Integer expId, Model model) {
//    	// get experiment from expid
//    	// retrieve the scenario contents to be displayed
//    	Experiment currExp = experimentManager.getExperimentByExpId(expId);
//    	model.addAttribute("scenarioContents", currExp.getScenarioContents());
//    	return "experiment_scenario_contents";
//    }

    /*@RequestMapping("/remove_experiment/{teamName}/{teamId}/{expId}")
    public String removeExperiment(@PathVariable String teamName, @PathVariable String teamId,
                                   @PathVariable String expId, final RedirectAttributes redirectAttributes,
                                   HttpSession session) throws WebServiceRuntimeException {*/
        @RequestMapping("/remove_experiment/{teamName}/{teamId}/{expId}/{csrfToken}")
        public String removeExperiment(@PathVariable String teamName, @PathVariable String teamId,
                @PathVariable String expId, @PathVariable String csrfToken,final RedirectAttributes redirectAttributes,
        HttpSession session) throws WebServiceRuntimeException {
        // ensure experiment is stopped first
             // fix for Cross site request forgery //
            if(!(csrfToken.equals(session.getAttribute("csrfToken").toString())))
            {
                log.warn("Permission denied to remove experiment: {} for team: {} Invalid Token detected");
                redirectAttributes.addFlashAttribute(MESSAGE, "Invalid Token detected");
                return REDIRECT_UNAUTHOURIZED_ACCESS;
            }
            Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        Team2 team = invokeAndExtractTeamInfo(teamId);

        // check valid authentication to remove experiments
        // either admin, experiment creator or experiment owner
        if (!validateIfAdmin(session) && !realization.getUserId().equals(session.getAttribute("id").toString()) && !team.getOwner().getId().equals(session.getAttribute(webProperties.getSessionUserId()))) {
            log.warn("Permission denied when remove Team:{}, Experiment: {} with User: {}, Role:{}", teamId, expId, session.getAttribute("id"), session.getAttribute(webProperties.getSessionRoles()));
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to remove experiment;" + permissionDeniedMessage);
            return REDIRECT_EXPERIMENTS;
        }

        if (!realization.getState().equals(RealizationState.NOT_RUNNING.toString())) {
            log.warn("Trying to remove Team: {}, Experiment: {} with State: {} that is still in progress?", teamId, expId, realization.getState());
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to remove Exp: " + realization.getExperimentName() + REFRESH + CONTACT_EMAIL);
            return REDIRECT_EXPERIMENTS;
        }

        log.info("Removing experiment: at " + properties.getDeleteExperiment(teamId, expId));
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.getDeleteExperiment(teamId, expId), HttpMethod.DELETE, request, String.class);
        } catch (Exception e) {
            log.warn("Error connecting to experiment service to remove experiment", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_EXPERIMENTS;
        }

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case EXPERIMENT_DELETE_EXCEPTION:
                    case FORBIDDEN_EXCEPTION:
                        log.warn("remove experiment failed for Team: {}, Exp: {}", teamId, expId);
                        redirectAttributes.addFlashAttribute(MESSAGE, error.getMessage());
                        break;
                    case OBJECT_OPTIMISTIC_LOCKING_FAILURE_EXCEPTION:
                        // do nothing
                        log.info("remove experiment database locking failure");
                        break;
                    default:
                        // do nothing
                        break;
                }
                return REDIRECT_EXPERIMENTS;
            } else {
                // everything ok
                log.info("remove experiment success for Team: {}, Exp: {}", teamId, expId);
                redirectAttributes.addFlashAttribute("exp_remove_message", "Team: " + teamName + " has removed Exp: " + realization.getExperimentName());
                return REDIRECT_EXPERIMENTS;
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping("/start_experiment/{teamName}/{expId}/{csrfToken}")
    public String startExperiment(
            @PathVariable String teamName,
            @PathVariable String expId,
            @PathVariable String csrfToken,
            final RedirectAttributes redirectAttributes, Model model, HttpSession session) throws WebServiceRuntimeException {

        // fix for Cross site request forgery //
        if(!(csrfToken.equals(session.getAttribute("csrfToken").toString())))
        {
            log.warn("Permission denied to start experiment: {} for team: {} Invalid Token detected");
            redirectAttributes.addFlashAttribute(MESSAGE, "Invalid Token detected");
            return REDIRECT_UNAUTHOURIZED_ACCESS;
        }
        // ensure experiment is stopped first before starting
        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        if (!checkPermissionRealizeExperiment(realization, session)) {
            log.warn("Permission denied to start experiment: {} for team: {}", realization.getExperimentName(), teamName);
            redirectAttributes.addFlashAttribute(MESSAGE, permissionDeniedMessage);
            return REDIRECT_EXPERIMENTS;
        }

        String teamId = realization.getTeamId();
        String teamStatus = getTeamStatus(teamId);

        if (!teamStatus.equals(TeamStatus.APPROVED.name())) {
            log.warn("Error: trying to realize an experiment {} on team {} with status {}", realization.getExperimentName(), teamId, teamStatus);
            redirectAttributes.addFlashAttribute(MESSAGE, teamName + " is in " + teamStatus + " status and does not have permission to start experiment. Please contact " + CONTACT_EMAIL);
            return REDIRECT_EXPERIMENTS;
        }

        if (!realization.getState().equals(RealizationState.NOT_RUNNING.toString())) {
            log.warn("Trying to start Team: {}, Experiment: {} with State: {} that is not running?", teamName, expId, realization.getState());
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to start Exp: " + realization.getExperimentName() + REFRESH + CONTACT_EMAIL);
            return REDIRECT_EXPERIMENTS;
        }

        //start experiment
        log.info("Starting experiment: at " + properties.getStartExperiment(teamName, expId));
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.getStartExperiment(teamName, expId), HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            log.warn("Error connecting to experiment service to start experiment", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_EXPERIMENTS;
        }

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case EXPERIMENT_START_EXCEPTION:
                    case FORBIDDEN_EXCEPTION:
                        log.warn("start experiment failed for Team: {}, Exp: {}", teamName, expId);
                        redirectAttributes.addFlashAttribute(MESSAGE, error.getMessage());
                        return REDIRECT_EXPERIMENTS;
                    case TEAM_NOT_FOUND_EXCEPTION:
                        log.warn("Check team quota to start experiment: Team {} not found", teamName);
                        return REDIRECT_INDEX_PAGE;
                    case INSUFFICIENT_QUOTA_EXCEPTION:
                        log.warn("Check team quota to start experiment: Team {} do not have sufficient quota", teamName);
                        redirectAttributes.addFlashAttribute(MESSAGE, "There is insufficient quota for you to start this experiment. Please contact your team leader for more details.");
                        return REDIRECT_EXPERIMENTS;
                    case OBJECT_OPTIMISTIC_LOCKING_FAILURE_EXCEPTION:
                        // do nothing
                        log.info("start experiment database locking failure");
                        break;
                    default:
                        // do nothing
                        break;
                }
                log.warn("start experiment some other error occurred exception: {}", exceptionState);
                // possible for it to be error but experiment has started up finish
                // if user clicks on start but reloads the page
//                model.addAttribute(EXPERIMENT_MESSAGE, "Team: " + teamName + " has started Exp: " + realization.getExperimentName());
                return EXPERIMENTS;
            } else {
                // everything ok
                log.info("start experiment success for Team: {}, Exp: {}", teamName, expId);
                redirectAttributes.addFlashAttribute(EXPERIMENT_MESSAGE, getExperimentMessage(realization.getExperimentName(), teamName) + " is starting. This may take up to 10 minutes depending on the scale of your experiment. Please refresh this page later.");
                return REDIRECT_EXPERIMENTS;
            }
        } catch (IOException e) {
            log.warn("start experiment error: {]", e.getMessage());
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping("/stop_experiment/{teamName}/{expId}")
    public String stopExperiment(@PathVariable String teamName, @PathVariable String expId,
                                 Model model, final RedirectAttributes redirectAttributes,
                                 HttpSession session) throws WebServiceRuntimeException {

        // ensure experiment is active first before stopping
        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        if (isNotAdminAndNotInTeam(session, realization)) {
            log.warn("Permission denied to stop experiment: {} for team: {}", realization.getExperimentName(), teamName);
            redirectAttributes.addFlashAttribute(MESSAGE, permissionDeniedMessage);
            return REDIRECT_EXPERIMENTS;
        }

        if (!realization.getState().equals(RealizationState.RUNNING.toString())) {
            log.warn("Trying to stop Team: {}, Experiment: {} with State: {} that is still in progress?", teamName, expId, realization.getState());
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to stop Exp: " + realization.getExperimentName() + REFRESH + CONTACT_EMAIL);
            return REDIRECT_EXPERIMENTS;
        }

        log.info("Stopping experiment: at " + properties.getStopExperiment(teamName, expId));
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response;

        return abc(teamName, expId, redirectAttributes, realization, request);
    }

    /**
     * Invokes the sio to update the experiment. Experiment must be stopped first before modifying. Only experiment creator, team owner and admin can modify experiment.
     * @param teamId team that contains the experiment
     * @param expId exp to be modified
     * @param model insert the form to the html page
     * @param session for pre-modification checks
     * @param redirectAttributes redirect error messages
     * @return experiment modify page
     */


    @RequestMapping("/update_experiment/{teamId}/{expId}/{csrfToken}")
    public String updateExperiment(@PathVariable String teamId, @PathVariable String expId, @PathVariable String csrfToken,Model model, HttpSession session, RedirectAttributes redirectAttributes) {

         // fix for Cross site request forgery //
        if(!(csrfToken.equals(session.getAttribute("csrfToken").toString())))
        {
            log.warn("Permission denied to modify experiment: {} for team: {} Invalid Token detected");
            redirectAttributes.addFlashAttribute(MESSAGE, "Invalid Token detected");
            return REDIRECT_UNAUTHOURIZED_ACCESS;
        }

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getExperiment(expId), HttpMethod.GET, request, String.class);
        Experiment2 editExperiment = extractExperiment(response.getBody().toString());

        Realization realization = invokeAndExtractRealization(editExperiment.getTeamName(), Long.parseLong(expId));

        if (!realization.getState().equals(RealizationState.NOT_RUNNING.toString())) {
            log.warn("Trying to modify Team: {}, Experiment: {} with State: {} that is still in progress?", teamId, expId, realization.getState());
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while attempting to modify Exp: " + realization.getExperimentName() + REFRESH + CONTACT_EMAIL);
            return REDIRECT_EXPERIMENTS;
        }

        Team2 team = invokeAndExtractTeamInfo(teamId);

        // check valid authentication to remove experiments
        // either admin, experiment creator or experiment owner
        if (!validateIfAdmin(session) && !editExperiment.getUserId().equals(session.getAttribute("id").toString()) && !team.getOwner().getId().equals(session.getAttribute(webProperties.getSessionUserId()))) {
            log.warn("Permission denied when updating Team:{}, Experiment: {} with User: {}, Role:{}", teamId, expId, session.getAttribute("id"), session.getAttribute(webProperties.getSessionRoles()));
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to update experiment;" + permissionDeniedMessage);
            return REDIRECT_EXPERIMENTS;
        }

        model.addAttribute("edit_experiment", editExperiment);
        return "experiment_modify";
    }

    @PostMapping("/update_experiment/{teamId}/{expId}")
    public String updateExperimentFormSubmit(@ModelAttribute("edit_experiment") Experiment2 editExperiment, BindingResult bindingResult, @PathVariable String teamId, @PathVariable String expId, RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        // check max duration for errors
        if (bindingResult.hasErrors() || !editExperiment.getMaxDuration().toString().matches("\\d+")) {
            redirectAttributes.addFlashAttribute(MESSAGE, MAX_DURATION_ERROR);
            return REDIRECT_UPDATE_EXPERIMENT + teamId + "/" + expId;
        }

        // get original experiment
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getExperiment(expId), HttpMethod.GET, request, String.class);
        Experiment2 experiment = extractExperiment(response.getBody().toString());

        experiment.setNsFileContent(editExperiment.getNsFileContent());
        experiment.setMaxDuration(editExperiment.getMaxDuration());

        objectMapper.registerModule(new JavaTimeModule());
        String jsonExperiment;
        try {
            jsonExperiment = objectMapper.writeValueAsString(experiment);
        } catch (JsonProcessingException e) {
            log.debug("update experiment convert to json error: {}", experiment);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_UPDATE_EXPERIMENT + teamId + "/" + expId;
        }

        // identical endpoint as delete experiment but different HTTP method
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        request = createHttpEntityWithBody(jsonExperiment);
        ResponseEntity updateExperimentResponse;
        try {
            updateExperimentResponse = restTemplate.exchange(properties.getDeleteExperiment(teamId, expId), HttpMethod.PUT, request, String.class);
        } catch (Exception e) {
            log.warn("Error connecting to experiment service to update experiment", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_EXPERIMENTS;
        }

        String updateExperimentResponseBody = updateExperimentResponse.getBody().toString();

        try {
            if (RestUtil.isError(updateExperimentResponse.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(updateExperimentResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch(exceptionState) {
                    case NS_FILE_PARSE_EXCEPTION:
                    case EXPERIMENT_MODIFY_EXCEPTION:
                        log.warn("update experiment failed for Team: {}, Exp: {}", teamId, expId);
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error in parsing NS File");
                        redirectAttributes.addFlashAttribute("exp_output", error.getMessage());
                        break;
                    case OBJECT_OPTIMISTIC_LOCKING_FAILURE_EXCEPTION:
                        // do nothing
                        log.info("update experiment database locking failure");
                        break;
                    default:
                        // do nothing
                        break;
                }
                return REDIRECT_UPDATE_EXPERIMENT + teamId + "/" + expId;
            } else {
                // everything ok
                log.info("update experiment success for Team:{}, Exp: {}", teamId, expId);
                redirectAttributes.addFlashAttribute(EXPERIMENT_MESSAGE, getExperimentMessage(experiment.getName(), experiment.getTeamName()) + " has been modified. You may proceed to start the experiment.");
                return REDIRECT_EXPERIMENTS;
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping("/get_topology/{teamName}/{expId}")
    @ResponseBody
    public String getTopology(@PathVariable String teamName, @PathVariable String expId) {
        try {
            HttpEntity<String> request = createHttpEntityHeaderOnly();
            ResponseEntity response = restTemplate.exchange(properties.getTopology(teamName, expId), HttpMethod.GET, request, String.class);
            log.info("Retrieve experiment topo success");
            return "data:image/png;base64," + response.getBody();
        } catch (Exception e) {
            log.error("Error getting topology thumbnail", e.getMessage());
            return "";
        }
    }

    @RequestMapping(value = "/request_internet/{teamName}/{teamId}/{expId}", method = RequestMethod.POST)
    public String internetRequest(@PathVariable String teamName,
                                  @PathVariable String teamId,
                                  @PathVariable String expId,
                                  @ModelAttribute InternetRequestForm internetRequestForm,
                                  final RedirectAttributes redirectAttributes
    ) throws WebServiceRuntimeException {

        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        if(!realization.getState().equals(RealizationState.RUNNING.toString())) {
            log.warn("Trying to request internet for the experiment {} from team {} with state {}", expId, teamName,realization.getState());
            redirectAttributes.addFlashAttribute(MESSAGE, "The experiment " + realization.getExperimentName() + " need to be started before you can request for internet access" );
            return REDIRECT_EXPERIMENTS;
        }

        log.info("Requesting internet access at " + properties.requestInternetExperiment(teamId, expId));
        JSONObject requestObject = new JSONObject();
        requestObject.put("reason", internetRequestForm.getReason());

        try {
            HttpEntity<String> request = createHttpEntityWithBody(requestObject.toString());
            restTemplate.setErrorHandler(new MyResponseErrorHandler());
            ResponseEntity response = restTemplate.exchange(properties.requestInternetExperiment(teamId, expId),
                    HttpMethod.POST, request, String.class);
            String responseBody = response.getBody().toString();

            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                log.warn("Error connecting to sio experiment service for sending email: {}", exceptionState);
                redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            } else {
                log.info("Requesting internet access is successful for the experiment {}", expId);
                redirectAttributes.addFlashAttribute(EXPERIMENT_MESSAGE, "Your Internet access request for the experiment \"" + realization.getExperimentName()+ "\" has been successful. You will be notified via email if the request is approved.");
            }

        } catch (IOException e) {
            log.warn("Error connecting to sio exp service for sending email: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            throw new WebServiceRuntimeException(e.getMessage());
        }
        return REDIRECT_EXPERIMENTS;
    }

    @RequestMapping("/web_ssh/access_node/{qualifiedName:.+}")
    public String sshAccessNode(Model model, HttpSession session, @PathVariable String qualifiedName) throws WebServiceRuntimeException {
        getDeterUid(model, session);
        model.addAttribute("qualified", qualifiedName);
        model.addAttribute("cols", ptyProperties.getCols());
        model.addAttribute("rows", ptyProperties.getRows());
        return "webssh";
    }

    @RequestMapping(value = "/web_vnc/access_node/{teamName}/{expId}/{nodeId}", params = {"portNum"})
    public String vncAccessNode(Model model, HttpSession session, RedirectAttributes redirectAttributes,
                                @PathVariable String teamName, @PathVariable Long expId, @PathVariable String nodeId,
                                @NotNull @RequestParam("portNum") Integer portNum) throws WebServiceRuntimeException, NoSuchAlgorithmException {
        Realization realization = invokeAndExtractRealization(teamName, expId);
        if (!checkPermissionRealizeExperiment(realization, session)) {
            log.warn("Permission denied to access experiment {} node for team: {}", realization.getExperimentName(), teamName);
            redirectAttributes.addFlashAttribute(MESSAGE, permissionDeniedMessage);
            return REDIRECT_EXPERIMENTS;
        }
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getStatefulExperiment(expId.toString()), HttpMethod.GET, request, String.class);
        StatefulExperiment statefulExperiment = extractStatefulExperiment(response.getBody().toString());
        getDeterUid(model, session);
        Map attributes = model.asMap();
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(vncProperties.getHttp())
                .queryParam("host", vncProperties.getHost())
                .queryParam("path", qencode(getNodeQualifiedName(statefulExperiment, nodeId) + ":" + portNum, (String) attributes.get(DETER_UID)))
                .build();
        log.info("VNC URI: {}", uriComponents.toString());
        return "redirect:" + uriComponents.toString();
    }

    private String getNodeQualifiedName(StatefulExperiment statefulExperiment, String nodeId) {
        StringBuilder qualifiedName = new StringBuilder();
        statefulExperiment.getNodesInfoMap().forEach((key, value) -> {
            if (value.get(NODE_ID).equals(nodeId)) {
                qualifiedName.append(value.get(QUALIFIED_NAME));
            }
        });
        return qualifiedName.toString();
    }

    // Reference: http://www.baeldung.com/sha-256-hashing-java
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String qencode(String str, String deterUid) throws NoSuchAlgorithmException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date today = Calendar.getInstance().getTime();
        String tstr = sdf.format(today);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(tstr.getBytes());
        digest.update(":".getBytes());
        digest.update(str.getBytes());
        digest.update(":".getBytes());
        digest.update(deterUid.getBytes());
        digest.update(vncProperties.getSalt().getBytes());
        byte[] encodedhash = digest.digest();
        String hash = bytesToHex(encodedhash);
        return java.util.Base64.getEncoder().encodeToString((hash + tstr + ":" + str + ":" + deterUid).getBytes());
    }

    private String abc(@PathVariable String teamName, @PathVariable String expId,
                       RedirectAttributes redirectAttributes, Realization realization,
                       HttpEntity<String> request) throws WebServiceRuntimeException {
        ResponseEntity response;
        try {
            response = restTemplate.exchange(properties.getStopExperiment(teamName, expId), HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            log.warn("Error connecting to experiment service to stop experiment", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_EXPERIMENTS;
        }

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == ExceptionState.FORBIDDEN_EXCEPTION) {
                    log.warn("Permission denied to stop experiment: {} for team: {}", realization.getExperimentName(), teamName);
                    redirectAttributes.addFlashAttribute(MESSAGE, permissionDeniedMessage);
                }
                if (exceptionState == ExceptionState.OBJECT_OPTIMISTIC_LOCKING_FAILURE_EXCEPTION) {
                    log.info("stop experiment database locking failure");
                }
            } else {
                // everything ok
                log.info("stop experiment success for Team: {}, Exp: {}", teamName, expId);
                redirectAttributes.addFlashAttribute(EXPERIMENT_MESSAGE, getExperimentMessage(realization.getExperimentName(), teamName) + " is stopping. Please refresh this page in a few minutes.");
            }
            return REDIRECT_EXPERIMENTS;
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    private boolean isNotAdminAndNotInTeam(HttpSession session, Realization realization) {
        return !validateIfAdmin(session) && !checkPermissionRealizeExperiment(realization, session);
    }

    //-----------------------------------------------------------------------
    //--------------------------Admin Revamp---------------------------------
    //-----------------------------------------------------------------------
    //---------------------------------Admin---------------------------------
    @RequestMapping("/admin")
    public String admin(Model model, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        List<Team2> pendingApprovalTeamsList = new ArrayList<>();

        //------------------------------------
        // get list of teams pending for approval
        //------------------------------------
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioTeamsUrl(), HttpMethod.GET, request, String.class);

        JSONArray jsonArray = new JSONArray(responseEntity.getBody().toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Team2 one = extractTeamInfo(jsonObject.toString());
            if (one.getStatus().equals(TeamStatus.PENDING.name())) {
                pendingApprovalTeamsList.add(one);
            }
        }

        model.addAttribute("pendingApprovalTeamsList", pendingApprovalTeamsList);

        return "admin3";
    }

    @RequestMapping(value = "/admin/gpus", method = RequestMethod.GET)
    public String adminGpuManagement(Model model, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        if (!model.containsAttribute("gpuUserForm")) {
            model.addAttribute("gpuUserForm", new GpuUserForm());
            model.addAttribute("toggleModal", "hide");
        }
        model.addAttribute("domains", gpuProperties.getDomains());
        return "gpu_dashboard";
    }

    @RequestMapping(value = "/admin/gpus", method = RequestMethod.POST)
    public String adminGetGpuUsers(@RequestParam("gpu") Integer gpu,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        redirectAttributes.addFlashAttribute("gpuUsersMap", getGpuUsers(gpu));
        redirectAttributes.addFlashAttribute("selectedGpu", gpu);
        return "redirect:/admin/gpus";
    }

    @RequestMapping(value = "/admin/gpus/{gpu}/users/add", method = RequestMethod.POST)
    public String adminAddGpuUsers(@PathVariable("gpu") Integer gpu,
                                   @Valid @ModelAttribute("gpuUserForm") GpuUserForm gpuUserForm,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        if (bindingResult.hasErrors() || !gpuUserForm.isValid()) {
            log.warn("Gpu user form has errors {}", gpuUserForm.toString());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.gpuUserForm", bindingResult);
            redirectAttributes.addFlashAttribute("gpuUserForm", gpuUserForm);
            redirectAttributes.addFlashAttribute("toggleModal", "show");
        } else {
            GpuProperties.Domain domain = gpuProperties.getDomains().get(gpu);
            String url = "http://" + domain.getHost() + ":" + domain.getPort() + "/users";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", gpuUserForm.getUsername());
            jsonObject.put("fullname", gpuUserForm.getFullname());
            jsonObject.put("password", gpuUserForm.getPassword());

            HttpEntity<String> request = createHttpEntityWithBodyNoAuthHeader(jsonObject.toString());
            ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            String responseBody = response.getBody().toString();

            jsonObject = new JSONObject(responseBody);
            String message = jsonObject.getString(gpuUserForm.getUsername());
            if (message.contains("Failed")) {
                redirectAttributes.addFlashAttribute("message", message);
            } else {
                redirectAttributes.addFlashAttribute("messageSuccess", message);
            }
        }

        redirectAttributes.addFlashAttribute("gpuUsersMap", getGpuUsers(gpu));
        redirectAttributes.addFlashAttribute("selectedGpu", gpu);
        return "redirect:/admin/gpus";
    }

    private Map<String, String> getGpuUsers(@RequestParam("gpu") Integer gpu) throws WebServiceRuntimeException {
        GpuProperties.Domain domain = gpuProperties.getDomains().get(gpu);
        String url = "http://" + domain.getHost() + ":" + domain.getPort() + "/users";
        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseBody, new TypeReference<Map<String, Map<String, String>>>(){});
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping(value = "/admin/gpus/{gpu}/{action}/{userid}", method = RequestMethod.GET)
    public String adminChangeGpuUserStatus(@PathVariable("gpu") Integer gpu,
                                           @PathVariable("action") String action,
                                           @PathVariable("userid") String userid,
                                           RedirectAttributes redirectAttributes,
                                           HttpSession session) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        GpuProperties.Domain domain = gpuProperties.getDomains().get(gpu);
        String url = "http://" + domain.getHost() + ":" + domain.getPort() + "/users/" + userid + "?action=" + action;
        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        JSONObject jsonObject = new JSONObject(responseBody);
        String status = jsonObject.getString(userid);
        redirectAttributes.addFlashAttribute("messageSuccess", "User '" + userid + "' " + status);
        redirectAttributes.addFlashAttribute("gpuUsersMap", getGpuUsers(gpu));
        redirectAttributes.addFlashAttribute("selectedGpu", gpu);
        return "redirect:/admin/gpus";
    }

    @RequestMapping(value = "/admin/gpus/{gpu}/passwd/{userid}", method = RequestMethod.POST)
    public String adminChangeGpuUserPassword(@PathVariable("gpu") Integer gpu,
                                             @PathVariable("userid") String userid,
                                             @RequestParam("newpasswd") String newpasswd,
                                             RedirectAttributes redirectAttributes,
                                             HttpSession session) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        GpuProperties.Domain domain = gpuProperties.getDomains().get(gpu);
        String url = "http://" + domain.getHost() + ":" + domain.getPort() + "/users/" + userid;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", newpasswd);

        HttpEntity<String> request = createHttpEntityWithBodyNoAuthHeader(jsonObject.toString());
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        String responseBody = response.getBody().toString();

        jsonObject = new JSONObject(responseBody);
        String message = jsonObject.getString(userid);
        if (message.contains("Failed")) {
            redirectAttributes.addFlashAttribute("message", message + " '" + userid + "'");
        } else {
            redirectAttributes.addFlashAttribute("messageSuccess", message + " '" + userid + "'");
        }

        redirectAttributes.addFlashAttribute("gpuUsersMap", getGpuUsers(gpu));
        redirectAttributes.addFlashAttribute("selectedGpu", gpu);
        return "redirect:/admin/gpus";
    }

    @RequestMapping(value = "/admin/gpus/{gpu}/remove/{userid}", method = RequestMethod.GET)
    public String adminRemoveGpuUser(@PathVariable("gpu") Integer gpu,
                                     @PathVariable("userid") String userid,
                                     RedirectAttributes redirectAttributes,
                                     HttpSession session) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        GpuProperties.Domain domain = gpuProperties.getDomains().get(gpu);
        String url = "http://" + domain.getHost() + ":" + domain.getPort() + "/users/" + userid;
        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        String responseBody = response.getBody().toString();

        JSONObject jsonObject = new JSONObject(responseBody);
        String message = jsonObject.getString(userid);
        if (message.contains("Failed")) {
            redirectAttributes.addFlashAttribute("message", message + " '" + userid + "'");
        } else {
            redirectAttributes.addFlashAttribute("messageSuccess", message + " '" + userid + "'");
        }

        redirectAttributes.addFlashAttribute("gpuUsersMap", getGpuUsers(gpu));
        redirectAttributes.addFlashAttribute("selectedGpu", gpu);
        return "redirect:/admin/gpus";
    }

    @RequestMapping("/admin/data")
    public String adminDataManagement(Model model, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        //------------------------------------
        // get list of datasets
        //------------------------------------
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getData(), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        List<Dataset> datasetsList = new ArrayList<>();
        JSONArray dataJsonArray = new JSONArray(responseBody);
        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject dataInfoObject = dataJsonArray.getJSONObject(i);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            datasetsList.add(dataset);
        }

        response = restTemplate.exchange(properties.getDownloadStat(), HttpMethod.GET, request, String.class);
        responseBody = response.getBody().toString();

        Map<Integer, Long> dataDownloadStats = new HashMap<>();
        JSONArray statJsonArray1 = new JSONArray(responseBody);
        for (int i = 0; i < statJsonArray1.length(); i++) {
            JSONObject statInfoObject = statJsonArray1.getJSONObject(i);
            dataDownloadStats.put(statInfoObject.getInt(DATA_ID), statInfoObject.getLong(COUNT));
        }

        response = restTemplate.exchange(properties.getPublicDownloadStat(), HttpMethod.GET, request, String.class);
        responseBody = response.getBody().toString();
        JSONArray statJsonArray2 = new JSONArray(responseBody);
        for (int i = 0; i < statJsonArray2.length(); i++) {
            JSONObject statInfoObject = statJsonArray2.getJSONObject(i);
            int key = statInfoObject.getInt(DATA_ID);
            if (dataDownloadStats.containsKey(key)) {
                Long count = dataDownloadStats.get(key) + statInfoObject.getLong(COUNT);
                dataDownloadStats.replace(key, count);
            } else {
                dataDownloadStats.put(statInfoObject.getInt(DATA_ID), statInfoObject.getLong(COUNT));
            }
        }

        model.addAttribute("dataList", datasetsList);
        model.addAttribute("downloadStats", dataDownloadStats);

        return "data_dashboard";
    }

    @RequestMapping("/admin/data/{datasetId}/resources")
    public String adminViewDataResources(@PathVariable String datasetId, Model model, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        //----------------------------------------
        // get list of data resources in a dataset
        //----------------------------------------
        Dataset dataset = invokeAndExtractDataInfo(Long.parseLong(datasetId));
        model.addAttribute("dataset", dataset);

        return "admin_data_resources";
    }

    @RequestMapping(value = "/admin/data/{datasetId}/resources/{resourceId}/update", method = RequestMethod.GET)
    public String adminUpdateResource(@PathVariable String datasetId, @PathVariable String resourceId, Model model, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        Dataset dataset = invokeAndExtractDataInfo(Long.parseLong(datasetId));
        DataResource currentDataResource = new DataResource();

        for (DataResource dataResource : dataset.getDataResources()) {
            if (dataResource.getId() == Long.parseLong(resourceId)) {
                currentDataResource = dataResource;
                break;
            }
        }

        model.addAttribute("did", dataset.getId());
        model.addAttribute("dataresource", currentDataResource);
        session.setAttribute(ORIGINAL_DATARESOURCE, currentDataResource);
        return "admin_data_resources_update";
    }

    // updates the malicious status of a data resource
    @RequestMapping(value = "/admin/data/{datasetId}/resources/{resourceId}/update", method = RequestMethod.POST)
    public String adminUpdateResourceFormSubmit(@PathVariable String datasetId,
                                                @PathVariable String resourceId,
                                                @ModelAttribute DataResource dataResource,
                                                Model model, HttpSession session,
                                                RedirectAttributes redirectAttributes) throws IOException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        DataResource original = (DataResource) session.getAttribute(ORIGINAL_DATARESOURCE);
        Dataset dataset = invokeAndExtractDataInfo(Long.parseLong(datasetId));
        updateDataset(dataset, dataResource);

        // add redirect attributes variable to notify what has been modified
        if (!original.getMaliciousFlag().equalsIgnoreCase(dataResource.getMaliciousFlag())) {
            redirectAttributes.addFlashAttribute("editMaliciousFlag", SUCCESS);
        }

        log.info("Data updated... {}", dataset.getName());
        model.addAttribute("did", dataset.getId());
        model.addAttribute("dataresource", dataResource);
        session.removeAttribute(ORIGINAL_DATARESOURCE);
        return "redirect:/admin/data/" + datasetId + "/resources/" + resourceId + "/update";
    }

    private Dataset updateDataset(Dataset dataset, DataResource dataResource) throws IOException {
        log.info("Data resource updating... {}", dataResource);
        HttpEntity<String> request = createHttpEntityWithBody(objectMapper.writeValueAsString(dataResource));
        ResponseEntity response = restTemplate.exchange(properties.getResource(dataset.getId().toString(), dataResource.getId().toString()), HttpMethod.PUT, request, String.class);

        Dataset updatedDataset = extractDataInfo(response.getBody().toString());
        log.info("Data resource updated... {}", dataResource.getUri());
        return updatedDataset;
    }

    @RequestMapping("/admin/experiments")
    public String adminExperimentsManagement(Model model, HttpSession session) {

        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        //------------------------------------
        // get list of experiments
        //------------------------------------
        HttpEntity<String> expRequest = createHttpEntityHeaderOnly();
        ResponseEntity expResponseEntity = restTemplate.exchange(properties.getSioExpUrl(), HttpMethod.GET, expRequest, String.class);

        //------------------------------------
        // get list of realizations
        //------------------------------------
        HttpEntity<String> realizationRequest = createHttpEntityHeaderOnly();
        ResponseEntity realizationResponseEntity = restTemplate.exchange(properties.getAllRealizations(), HttpMethod.GET, realizationRequest, String.class);

        JSONArray jsonExpArray = new JSONArray(expResponseEntity.getBody().toString());
        JSONArray jsonRealizationArray = new JSONArray(realizationResponseEntity.getBody().toString());
        Map<Experiment2, Realization> experiment2Map = new HashMap<>(); // exp id, experiment
        Map<Long, Realization> realizationMap = new HashMap<>(); // exp id, realization

        for (int k = 0; k < jsonRealizationArray.length(); k++) {
            Realization realization;
            try {
                realization = extractRealization(jsonRealizationArray.getJSONObject(k).toString());
            } catch (JSONException e) {
                log.debug("Admin extract realization {}", e);
                realization = getCleanRealization();
            }
            if (realization.getState().equals(RealizationState.RUNNING.name())) {
                realizationMap.put(realization.getExperimentId(), realization);
            }
        }

        for (int i = 0; i < jsonExpArray.length(); i++) {
            Experiment2 experiment2 = extractExperiment(jsonExpArray.getJSONObject(i).toString());
            if (realizationMap.containsKey(experiment2.getId())) {
                experiment2Map.put(experiment2, realizationMap.get(experiment2.getId()));
            }
        }

        model.addAttribute("runningExpMap", experiment2Map);

        return "experiment_dashboard";
    }

    @RequestMapping("/admin/teams")
    public String adminTeamsManagement(Model model, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        //------------------------------------
        // get list of teams
        //------------------------------------
        TeamManager2 teamManager2 = new TeamManager2();
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioTeamsUrl(), HttpMethod.GET, request, String.class);

        JSONArray jsonArray = new JSONArray(responseEntity.getBody().toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Team2 one = extractTeamInfo(jsonObject.toString());
            teamManager2.addTeamToTeamMap(one);
        }

        model.addAttribute("teamsMap", teamManager2.getTeamMap());

        return "team_dashboard";
    }

    @RequestMapping("/admin/users")
    public String adminUsersManagement(Model model, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        //------------------------------------
        // get list of users
        //------------------------------------
        Map<String, List<String>> userToTeamMap = new HashMap<>(); // userId : list of team names
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response2 = restTemplate.exchange(properties.getSioUsersUrl(), HttpMethod.GET, request, String.class);
        String responseBody2 = response2.getBody().toString();

        JSONArray jsonUserArray = new JSONArray(responseBody2);
        List<User2> usersList = new ArrayList<>();

        for (int i = 0; i < jsonUserArray.length(); i++) {
            JSONObject userObject = jsonUserArray.getJSONObject(i);
            User2 user = extractUserInfo(userObject.toString());
            usersList.add(user);

            // get list of teams' names for each user
            List<String> perUserTeamList = new ArrayList<>();
            if (userObject.get(TEAMS) != null) {
                JSONArray teamJsonArray = userObject.getJSONArray(TEAMS);
                for (int k = 0; k < teamJsonArray.length(); k++) {
                    Team2 team = invokeAndExtractTeamInfo(teamJsonArray.get(k).toString());
                    perUserTeamList.add(team.getName());
                }
                userToTeamMap.put(user.getId(), perUserTeamList);
            }
        }

        model.addAttribute("usersList", usersList);
        model.addAttribute("userToTeamMap", userToTeamMap);

        return "user_dashboard";
    }

    @RequestMapping("/admin/usage")
    public String adminTeamUsage(Model model,
                                 @RequestParam(value = "start", required = false) String start,
                                 @RequestParam(value = "end", required = false) String end,
                                 @RequestParam(value = "organizationType", required = false) String organizationType,
                                 @RequestParam(value = "team", required = false) String team,
                                 final RedirectAttributes redirectAttributes,
                                 HttpSession session) throws IOException, WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime nowDate = ZonedDateTime.now();
        String now = nowDate.format(formatter);
        if (start == null) {
            start = nowDate.with(firstDayOfMonth()).format(formatter);
        }
        if (end == null) {
            end = now;
        }
        if (now.compareTo(start) < 0 || now.compareTo(end) < 0) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Period selected is beyond current date (today).");
            return REDIRECT_TEAM_USAGE;
        }

        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioTeamsUrl(), HttpMethod.GET, request, String.class);
        JSONArray jsonArray = new JSONArray(responseEntity.getBody().toString());

        List<Team2> searchTeams = new ArrayList<>();
        TeamManager2 teamManager2 = new TeamManager2();
        getSearchTeams(organizationType, team, jsonArray, searchTeams, teamManager2);

        if (!searchTeams.isEmpty()) {
            List<String> dates = getDates(start, end, formatter);

            Map<String, List<Long>> teamUsages = new HashMap<>();
            Long totalUsage = 0L;
            for (Team2 team2 : searchTeams) {
                try {
                    List<Long> usages = new ArrayList<>();
                    totalUsage += getTeamUsageStatistics(team2, start, end, request, usages);
                    teamUsages.put(team2.getName(), usages);
                } catch (RestClientException rce) {
                    log.warn("Error connecting to sio analytics service for team usage: {}", rce);
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    return REDIRECT_TEAM_USAGE;
                } catch (StartDateAfterEndDateException sde) {
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_START_DATE_AFTER_END_DATE);
                    return REDIRECT_TEAM_USAGE;
                }
            }
            model.addAttribute("dates", dates);
            model.addAttribute("teamUsages", teamUsages);
            model.addAttribute("totalUsage", totalUsage);
        }

        List<Team2> allTeams = new ArrayList<>(teamManager2.getTeamMap().values());
        allTeams.sort(Comparator.comparing(Team2::getName, String.CASE_INSENSITIVE_ORDER));
        model.addAttribute(ALL_TEAMS, allTeams);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("organizationType", organizationType);
        model.addAttribute("team", team);
        return "usage_statistics";
    }

    private List<String> getDates(String start, String end, DateTimeFormatter formatter) {
        List<String> dates = new ArrayList<>();
        ZonedDateTime currentZonedDateTime = convertToZonedDateTime(start);
        ZonedDateTime endZoneDateTime = convertToZonedDateTime(end);
        while (currentZonedDateTime.isBefore(endZoneDateTime)) {
            String date = currentZonedDateTime.format(formatter);
            dates.add(date);
            currentZonedDateTime = currentZonedDateTime.plusDays(1);
        }
        dates.add(currentZonedDateTime.format(formatter));
        return dates;
    }

    private void getSearchTeams(String organizationType, String team, JSONArray jsonArray, List<Team2> searchTeams, TeamManager2 teamManager2) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Team2 one = extractTeamInfo(jsonObject.toString());
            teamManager2.addTeamToTeamMap(one);
            if (team != null && (team.equals(one.getId()) || (team.equals("All") && (organizationType.equals(one.getOrganisationType()) || organizationType.equals("All"))))) {
                searchTeams.add(one);
            }
        }
    }

    @RequestMapping(value = "/admin/energy", method = RequestMethod.GET)
    public String adminEnergy(Model model,
                              @RequestParam(value = "start", required = false) String start,
                              @RequestParam(value = "end", required = false) String end,
                              final RedirectAttributes redirectAttributes,
                              HttpSession session) throws IOException {

        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime now = ZonedDateTime.now();
        if (start == null) {
            ZonedDateTime startDate = now.with(firstDayOfMonth());
            start = startDate.format(formatter);
        }
        if (end == null) {
            ZonedDateTime endDate = now.with(lastDayOfMonth());
            end = endDate.format(formatter);
        }

        HttpEntity<String> request = createHttpEntityHeaderOnly();

        ResponseEntity responseEntity;
        try {
            responseEntity = restTemplate.exchange(properties.getEnergyStatistics("startDate=" + start, "endDate=" + end), HttpMethod.GET, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio analytics service for energy usage: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_ENERGY_USAGE;
        }

        String responseBody = responseEntity.getBody().toString();
        JSONArray jsonArray = new JSONArray(responseBody);

        // handling exceptions from SIO
        if (RestUtil.isError(responseEntity.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
            switch (exceptionState) {
                case START_DATE_AFTER_END_DATE_EXCEPTION:
                    log.warn("Get energy usage : Start date after end date error");
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_START_DATE_AFTER_END_DATE);
                    return REDIRECT_ENERGY_USAGE;

                default:
                    log.warn("Get energy usage : sio or deterlab adapter connection error");
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    return REDIRECT_ENERGY_USAGE;
            }
        } else {
            log.info("Get energy usage info : {}", responseBody);
        }

        DecimalFormat df2 = new DecimalFormat(".##");

        double sumEnergy = 0.00;
        List<String> listOfDate = new ArrayList<>();
        List<Double> listOfEnergy = new ArrayList<>();
        ZonedDateTime currentZonedDateTime = convertToZonedDateTime(start);
        String currentDate = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            sumEnergy  += jsonArray.getDouble(i);

            // add into listOfDate to display graph
            currentDate = currentZonedDateTime.format(formatter);
            listOfDate.add(currentDate);

            // add into listOfEnergy to display graph
            double energy = Double.valueOf(df2.format(jsonArray.getDouble(i)));
            listOfEnergy.add(energy);

            currentZonedDateTime = convertToZonedDateTime(currentDate).plusDays(1);
        }

        sumEnergy = Double.valueOf(df2.format(sumEnergy));
        model.addAttribute("listOfDate", listOfDate);
        model.addAttribute("listOfEnergy", listOfEnergy);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("energy", sumEnergy);
        return "energy_usage";
    }

    @RequestMapping(value = "/admin/diskspace", method = RequestMethod.GET)
    public String adminDiskSpace(HttpSession session, Model model) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        try {
            ResponseEntity response = restTemplate.exchange(properties.getDiskStatistics(), HttpMethod.GET, request, String.class);
            String responseBody = response.getBody().toString();
            JSONObject jsonObject = new JSONObject(responseBody);
            String[] names = JSONObject.getNames(jsonObject);
            JSONObject diskSpaces = jsonObject.getJSONObject(names[0]);
            JSONArray userSpaces = diskSpaces.getJSONArray("userSpaces");
            JSONArray projSpaces = diskSpaces.getJSONArray("projSpaces");
            List<JSONObject> users = new ArrayList<>();
            List<JSONObject> projects = new ArrayList<>();
            for (int i = 0; i < userSpaces.length(); i++) {
                JSONObject item = userSpaces.getJSONObject(i);
                if (item.getString("directory").equals("/big/users/")) {
                    model.addAttribute("usersTotal", item);
                } else {
                    users.add(item);
                }
            }
            for (int i = 0; i < projSpaces.length(); i++) {
                JSONObject item = projSpaces.getJSONObject(i);
                if (item.getString("directory").equals("/big/proj/")) {
                    model.addAttribute("projectsTotal", item);
                } else {
                    projects.add(item);
                }
            }
            model.addAttribute("users", users);
            model.addAttribute("projects", projects);
            model.addAttribute("timestamp", names[0]);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio analytics service for disk space usage: {}", e);
            model.addAttribute(MESSAGE, "There is a problem in retrieving information.");
        }
        HttpEntity<String> request2 = createHttpEntityHeaderOnly();
        try {
            ResponseEntity response2 = restTemplate.exchange(properties.getSioUsersUrl(), HttpMethod.GET, request2, String.class);
            String responseBody2 = response2.getBody().toString();
            JSONArray jsonUserArray = new JSONArray(responseBody2);
            Map<String, User2> usersMap = new HashMap<>();
            for (int i = 0; i < jsonUserArray.length(); i++) {
                JSONObject userObject = jsonUserArray.getJSONObject(i);
                User2 user = extractUserInfo(userObject.toString());
                usersMap.put(user.getId(), user);
            }
            model.addAttribute("usersMap", usersMap);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio users service for users list: {}", e);
            model.addAttribute(MESSAGE, "There is a problem in retrieving information.");
        }
        HttpEntity<String> request3 = createHttpEntityHeaderOnly();
        try {
            ResponseEntity response3 = restTemplate.exchange(properties.getSioTeamsUrl(), HttpMethod.GET, request3, String.class);
            String responseBody3 = response3.getBody().toString();
            JSONArray jsonTeamArray = new JSONArray(responseBody3);
            Map<String, Team2> teamsMap = new HashMap<>();
            for (int i = 0; i < jsonTeamArray.length(); i++) {
                JSONObject jsonObject = jsonTeamArray.getJSONObject(i);
                Team2 team = extractTeamInfo(jsonObject.toString());
                teamsMap.put(team.getId(), team);
            }
            model.addAttribute("teamsMap", teamsMap);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio teams service for teams list: {}", e);
            model.addAttribute(MESSAGE, "There is a problem in retrieving information.");
        }
        return "diskspace_usage";
    }

    private ProjectDetails getProjectDetails(JSONObject jsonObject) {
        ProjectDetails projectDetails = new ProjectDetails();
        projectDetails.setId(jsonObject.getInt("id"));
        projectDetails.setOrganisationType(jsonObject.getString(ORGANISATION_TYPE));
        projectDetails.setOrganisationName(jsonObject.getString(ORGANISATION_NAME));
        projectDetails.setProjectName(jsonObject.getString(KEY_PROJECT_NAME));
        projectDetails.setOwner(jsonObject.getString(KEY_OWNER));
        try {
            projectDetails.setZonedDateCreated(getZonedDateTime(jsonObject.get(KEY_DATE_CREATED).toString()));
        } catch (IOException e) {
            log.warn("Error getting date created {}", e);
            projectDetails.setDateCreated("");
        }
        projectDetails.setEducation(jsonObject.getBoolean("education"));
        projectDetails.setServiceTool(jsonObject.getBoolean("serviceTool"));
        projectDetails.setSupportedBy(jsonObject.getString("supportedBy"));
        JSONArray usages = jsonObject.getJSONArray("projectUsages");
        for (int i = 0; i < usages.length(); i++) {
            JSONObject usage = usages.getJSONObject(i);
            JSONObject usageId = usage.getJSONObject("id");
            ProjectUsage projectUsage = new ProjectUsage();
            projectUsage.setId(usageId.getInt(KEY_PROJECT_DETAILS_ID));
            projectUsage.setMonth(usageId.getString(KEY_MONTH_YEAR));
            projectUsage.setUsage(usage.getInt(KEY_MONTHLY_USAGE));
            projectUsage.setIncurred(usage.getDouble(KEY_INCURRED));
            projectUsage.setWaived(usage.getDouble(KEY_WAIVED));
            projectDetails.addProjectUsage(projectUsage);
        }
        return projectDetails;
    }

    private List<ProjectDetails> getProjects() {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getMonthly(), HttpMethod.GET, request, String.class);
        JSONArray jsonArray = new JSONArray(response.getBody().toString());
        List<ProjectDetails> projectsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ProjectDetails projectDetails = getProjectDetails(jsonObject);
            projectsList.add(projectDetails);
        }
        return projectsList;
    }

    @GetMapping("/admin/monthly")
    public String adminMonthly(HttpSession session, Model model) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        List<ProjectDetails> projectsList = getProjects();
        model.addAttribute("projectsList", projectsList);

        return "admin_monthly";
    }

    @GetMapping("/user/monthly")
    public String userMonthly(HttpSession session, Model model) {
        String userId = session.getAttribute(webProperties.getSessionUserId()).toString();
        List<ProjectDetails> userProjectsOwnerList = loggedInuserOwnedProjects(userId);
        model.addAttribute("projectsList", userProjectsOwnerList);
        return "user_monthly";
    }

    @GetMapping(value = {"/admin/monthly/contribute", "/admin/monthly/contribute/{id}"})
    public String adminMonthlyContribute(@PathVariable Optional<String> id, HttpSession session, Model model) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        if (id.isPresent()) {
            HttpEntity<String> request = createHttpEntityHeaderOnly();
            ResponseEntity response = restTemplate.exchange(properties.getMonthly() + "/" + id.get(), HttpMethod.GET, request, String.class);
            JSONObject jsonObject = new JSONObject(response.getBody().toString());
            ProjectDetails projectDetails = getProjectDetails(jsonObject);
            model.addAttribute(KEY_PROJECT, projectDetails);
        } else {
            model.addAttribute(KEY_PROJECT, new ProjectDetails());
        }

        return ADMIN_MONTHLY_CONTRIBUTE;
    }

    @PostMapping("/admin/monthly/contribute")
    public String adminMonthlyValidate(@Valid @ModelAttribute("project") ProjectDetails project,
                                       BindingResult binding, HttpSession session, Model model) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        if (binding.hasErrors()) {
            String message = buildErrorMessage(binding);
            model.addAttribute(MESSAGE, message);
            model.addAttribute(KEY_PROJECT, project);
            return ADMIN_MONTHLY_CONTRIBUTE;
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ORGANISATION_TYPE, project.getOrganisationType());
            jsonObject.put(ORGANISATION_NAME, project.getOrganisationName());
            jsonObject.put(KEY_PROJECT_NAME, project.getProjectName());
            jsonObject.put(KEY_OWNER, project.getOwner());
            jsonObject.put(KEY_DATE_CREATED, project.getZonedDateCreated());
            jsonObject.put("education", project.isEducation());
            jsonObject.put("serviceTool", project.isServiceTool());
            jsonObject.put("supportedBy", project.getSupportedBy());
            jsonObject.put("projectUsages", new ArrayList());
            log.debug("JsonObject: {}", jsonObject);

            restTemplate.setErrorHandler(new MyResponseErrorHandler());
            HttpEntity<String> request = createHttpEntityWithBody(jsonObject.toString());
            ResponseEntity response;
            if (project.getId() == null || project.getId() == 0) {
                response = restTemplate.exchange(properties.getMonthly(), HttpMethod.POST, request, String.class);
            } else {
                response = restTemplate.exchange(properties.getMonthly() + "/" + project.getId(), HttpMethod.PUT, request, String.class);
            }
            String responseBody = response.getBody().toString();

            try {
                if (RestUtil.isError(response.getStatusCode())) {
                    MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                    ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                    switch (exceptionState) {
                        case PROJECT_DETAILS_NOT_FOUND_EXCEPTION:
                            log.warn("Project not found for updating");
                            model.addAttribute(MESSAGE, "Error(s):<ul><li>project not found for editing</li></ul>");
                            break;
                        case PROJECT_NAME_ALREADY_EXISTS_EXCEPTION:
                            log.warn("Project name already exists: {}", project.getProjectName());
                            model.addAttribute(MESSAGE, "Error(s):<ul<li>project name already exist</li></ul>");
                            break;
                        case FORBIDDEN_EXCEPTION:
                            log.warn("Saving of project forbidden.");
                            model.addAttribute(MESSAGE, "Error(s):<ul><li>saving project forbidden</li></ul>");
                            break;
                        default:
                            log.warn("Unknown error for validating project.");
                            model.addAttribute(MESSAGE, "Error(s):<ul><li>unknown error for validating project</li></ul>");
                    }
                    model.addAttribute(KEY_PROJECT, project);
                    return ADMIN_MONTHLY_CONTRIBUTE;
                } else {
                    log.info("Project details saved: {}", responseBody);
                }
            } catch (IOException e) {
                log.error("adminMonthlyValidate: {}", e.toString());
                throw new WebServiceRuntimeException(e.getMessage());
            }
        }

        return "redirect:/admin/monthly";
    }

    private String buildErrorMessage(BindingResult binding) {
        StringBuilder message = new StringBuilder();
        message.append(TAG_ERRORS);
        message.append(TAG_UL);
        for (ObjectError objectError : binding.getAllErrors()) {
            FieldError fieldError = (FieldError) objectError;
            message.append(TAG_LI);
            switch (fieldError.getField()) {
                case ORGANISATION_TYPE:
                    message.append("Organisation Type ");
                    message.append(fieldError.getDefaultMessage());
                    break;
                case ORGANISATION_NAME:
                    message.append("Organisation Name ");
                    message.append(fieldError.getDefaultMessage());
                    break;
                case KEY_PROJECT_NAME:
                    message.append("Project Name ");
                    message.append(fieldError.getDefaultMessage());
                    break;
                case KEY_OWNER:
                    message.append("Owner ");
                    message.append(fieldError.getDefaultMessage());
                    break;
                case KEY_DATE_CREATED:
                    message.append("Date Created ");
                    message.append(fieldError.getDefaultMessage());
                    break;
                case "month":
                    message.append("Month ");
                    message.append(fieldError.getDefaultMessage());
                    break;
                default:
                    message.append(fieldError.getField());
                    message.append(TAG_SPACE);
                    message.append(fieldError.getDefaultMessage());
            }
            message.append(TAG_LI_CLOSE);
        }
        message.append(TAG_UL_CLOSE);
        return message.toString();
    }

    @GetMapping("/admin/monthly/remove/{id}")
    public String adminMonthlyRemove(@PathVariable String id, RedirectAttributes attr, HttpSession session) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getMonthly() + "/" + id, HttpMethod.DELETE, request, String.class);
        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                switch (exceptionState) {
                    case PROJECT_DETAILS_NOT_FOUND_EXCEPTION:
                        log.warn("Project not found for deleting");
                        attr.addFlashAttribute(MESSAGE, "Error(s):<ul><li>project not found for deleting</li></ul>");
                        break;
                    case FORBIDDEN_EXCEPTION:
                        log.warn("Removing of project forbidden.");
                        attr.addFlashAttribute(MESSAGE, "Error(s):<ul><li>deleting project forbidden</li></ul>");
                        break;
                    default:
                        log.warn("Unknown error for validating project.");
                        attr.addFlashAttribute(MESSAGE, "Error(s):<ul><li>unknown error for deleting project</li></ul>");
                }
            } else {
                log.info("Project details deleted: {}", responseBody);
            }
        } catch (IOException e) {
            log.error("adminMonthlyRemove: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return "redirect:/admin/monthly";
    }

    @GetMapping("/admin/monthly/{id}/usage")
    public String adminMonthlyUsage(@PathVariable String id, HttpSession session, Model model) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getMonthly() + "/" + id, HttpMethod.GET, request, String.class);
        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        ProjectDetails projectDetails = getProjectDetails(jsonObject);
        model.addAttribute(KEY_PROJECT, projectDetails);

        return "admin_monthly_usage";
    }

    @GetMapping("/user/monthly/{id}/usage")
    public String userMonthlyUsage(@PathVariable String id, HttpSession session, Model model) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getProjectDetails() + "/" + id, HttpMethod.GET, request, String.class);
        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        ProjectDetails projectDetails = getProjectDetails(jsonObject);
        model.addAttribute(KEY_PROJECT, projectDetails);
        return "user_monthly_usage";
    }

    @RequestMapping(value = "/usage/reservation", method = RequestMethod.GET)
    public String adminMonthlyReservation(HttpSession session, Model model) {

        String userId = session.getAttribute(webProperties.getSessionUserId()).toString();
        model.addAttribute("nodeUsageReservationForm", new NodeUsageReservationForm());
        List<ProjectDetails> userProjectsOwnerList = loggedInuserOwnedProjects(userId);
        // admin can make node booking on behalf of user.If logged in user is admin show complete list of projects//
        if (validateIfAdmin(session))
        {
            List<ProjectDetails> projectsList = getProjects();
            model.addAttribute("projectsList", projectsList);
        }
        else  // non admin users can only see their own projects
        {
            model.addAttribute("projectsList", userProjectsOwnerList);
        }

        return "admin_node_usage_reservation";
    }
    private List<ProjectDetails> getProjectsList() {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getProjectDetails(), HttpMethod.GET, request, String.class);
        JSONArray jsonArray = new JSONArray(response.getBody().toString());
        List<ProjectDetails> projectsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ProjectDetails projectDetails = getProjectDetails(jsonObject);
            projectsList.add(projectDetails);
        }
        return projectsList;
    }

    private String findTeamOwner(String json) {
        String returnStr=null;
        Team2 team2 = new Team2();
        JSONObject object = new JSONObject(json);
        JSONArray membersArray = object.getJSONArray(MEMBERS);

        String teamid=object.getString("id");
        String teamOwnerId="";

        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            String userId = memberObject.getString(USER_ID);
            String teamMemberType = memberObject.getString(MEMBER_TYPE);
            if (teamMemberType.equals(MemberType.OWNER.name())) {
                teamOwnerId=userId;
            }

        }
        returnStr=teamid+":"+teamOwnerId;
        return returnStr;
    }

    private List<ProjectDetails> loggedInuserOwnedProjects(String userId)
    {
        List<ProjectDetails> projectsList = getProjectsList();
        List<ProjectDetails> userProjectsOwnerList = new ArrayList();

        //////////////// fetch list of teams for this user//////////////////////////////////
        TeamManager2 teamManager2 = new TeamManager2();
        List<Team2> lstofTeams= new ArrayList();
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getUser(userId), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();
        JSONObject object = new JSONObject(responseBody);
        JSONArray teamIdsJsonArray = object.getJSONArray(TEAMS);

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            Team2 joinRequestTeam = extractTeamInfoUserJoinRequest(userId, teamResponseBody);
            if (joinRequestTeam != null) {
                teamManager2.addTeamToUserJoinRequestTeamMap(joinRequestTeam);

            } else { // only list teams for which this user is approved
                Team2 objteam2 = extractTeamInfo(teamResponseBody);
                String tempstr=findTeamOwner(teamResponseBody);
                String[] ownerId=tempstr.split(":");
                // only add team to list if user owns this team
                if(userId.equals(ownerId[1]))
                {
                    lstofTeams.add(objteam2);
                }
            }
        }
        // only populate teams/project for which the user is owner //
        for(int i=0;i<projectsList.size();i++)
        {
            String projectName=projectsList.get(i).getProjectName();
            for(int j=0;j<lstofTeams.size();j++)
            {
                String teamname=lstofTeams.get(j).getName();
                if(projectName.equals(teamname))
                {
                    userProjectsOwnerList.add(projectsList.get(i));
                }
            }

        }
        return userProjectsOwnerList;
    }

    @RequestMapping(value = "/usage/reservation", method = RequestMethod.POST)  // Fix- make this feature avaible to all users and not just admins
    public String checkApplyNodeReservationInfo(@Valid @ModelAttribute("nodeUsageReservationForm") NodeUsageReservationForm nodeUsageReservationForm,
                                                BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                                HttpSession session, Model model) throws WebServiceRuntimeException {

        final String LOG_PREFIX = "Apply for Node Reservation: {}";
        String userId = session.getAttribute(webProperties.getSessionUserId()).toString();
        if (bindingResult.hasErrors()) {
            log.warn(LOG_PREFIX, "Application form error " + nodeUsageReservationForm.toString());
            StringBuilder message = new StringBuilder();
            message.append(TAG_ERRORS);
            message.append(TAG_UL);
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                message.append(TAG_LI);
                switch (fieldError.getField()) {
                    case "startDate":
                        message.append("Start Date ");
                        message.append(fieldError.getDefaultMessage());
                        break;
                    case "endDate":
                        message.append("End Date ");
                        message.append(fieldError.getDefaultMessage());
                        break;
                    case "noOfNodes":
                        message.append("Number of Nodes ");
                        message.append(fieldError.getDefaultMessage());
                        break;
                    case "projectId":
                        message.append("Project ");
                        message.append(fieldError.getDefaultMessage());
                        break;
                    default:
                        message.append(fieldError.getField());
                        message.append(TAG_SPACE);
                        message.append(fieldError.getDefaultMessage());
                }
                message.append(TAG_LI_CLOSE);
            }
            message.append(TAG_UL_CLOSE);
            model.addAttribute(MESSAGE, message);
            List<ProjectDetails> userProjectsOwnerList = loggedInuserOwnedProjects(userId);
            // admin can make node booking on behalf of user.If logged in user is admin show complete list of projects//
            if (validateIfAdmin(session))
            {
                List<ProjectDetails> projectsList = getProjects();
                model.addAttribute("projectsList", projectsList);
            }
            else  // non admin users can only see their own projects
            {
                model.addAttribute("projectsList", userProjectsOwnerList);
            }
            return "admin_node_usage_reservation";
        }
        log.info(LOG_PREFIX, nodeUsageReservationForm.toString());

        JSONObject nodeReserveFields = new JSONObject();
        nodeReserveFields.put("startDate", nodeUsageReservationForm.getZonedStartDate());
        nodeReserveFields.put("endDate", nodeUsageReservationForm.getZonedEndDate());
        nodeReserveFields.put("numNodes", nodeUsageReservationForm.getNoOfNodes());
        HttpEntity<String> request = createHttpEntityWithBody(nodeReserveFields.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        try {
            ResponseEntity response = restTemplate.exchange(properties.applyNodesReserve(nodeUsageReservationForm.getProjectId()), HttpMethod.POST, request, String.class);
            String responseBody = response.getBody().toString();
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                switch (exceptionState) {
                    case PROJECT_DETAILS_NOT_FOUND_EXCEPTION:
                        log.warn("Project not found for reserving node usage.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>project not found for reserving node usage</li></ul>");
                        break;
                    case NODES_RESERVATION_ALREADY_EXISTS_EXCEPTION:
                        log.warn("Overlapping node usage reservations found for same project.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>overlapping node usage reservations found for same project</li></ul>");
                        break;
                    case FORBIDDEN_EXCEPTION:
                        log.warn("Reserving of node usage forbidden.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>reserving of node usage forbidden</li></ul>");
                        break;
                    default:
                        log.warn("Unknown error for validating node usage reservation.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>unknown error for reserving node usage</li></ul>");
                }
            } else {
                // no errors, everything ok
                log.info(LOG_PREFIX, "Application for"+ nodeUsageReservationForm.getNoOfNodes()+" node reservation from " + nodeUsageReservationForm.getStartDate() + " submitted");
                redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Node Usage Booking done.");
            }
        } catch (Exception e) {
            log.error(LOG_PREFIX, e);
            throw new WebServiceRuntimeException(e.getMessage());
        }
        return "redirect:/usage/reservation";
    }

    @RequestMapping(value = "/edit/usage/reservation", method = RequestMethod.GET)
    public String editNodeUsageReservation(HttpSession session,Model model) {
        model.addAttribute("nodeUsageReservationForm", new NodeUsageReservationForm());
        String userId = session.getAttribute(webProperties.getSessionUserId()).toString();
        model.addAttribute("nodeUsageReservationForm", new NodeUsageReservationForm());
        List<ProjectDetails> userProjectsOwnerList = loggedInuserOwnedProjects(userId);
        // admin can make node booking on behalf of user.If logged in user is admin show complete list of projects//
        if (validateIfAdmin(session))
        {
            List<ProjectDetails> projectsList = getProjects();
            model.addAttribute("projectsList", projectsList);
        }
        else  // non admin users can only see their own projects
        {
            model.addAttribute("projectsList", userProjectsOwnerList);
        }
        return "edit_page_node_usage_reservation";
    }

    @RequestMapping(value = "/edit/usage/reservation", method = RequestMethod.POST)
    public String findNodeUsageReservationInfo(@Valid @ModelAttribute("nodeUsageReservationForm") NodeUsageReservationForm nodeUsageReservationForm,
                                               BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                               HttpSession session, Model model) throws WebServiceRuntimeException {

       final String LOG_PREFIX = "findNodeUsageReservationInfo: {}";
       String userId = session.getAttribute(webProperties.getSessionUserId()).toString();

        JSONObject reqObj = new JSONObject();
        HttpEntity<String> request = createHttpEntityWithBody(reqObj.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        try {
            ResponseEntity response = restTemplate.exchange(properties.getNodesReserveByProject(nodeUsageReservationForm.getProjectId()), HttpMethod.GET, request, String.class);
            String responseBody = response.getBody().toString();
            JSONObject jsonObject = new JSONObject(responseBody);
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                switch (exceptionState) {
                    default:
                        log.warn("Unknown error.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>unknown error : No Reservation Info found</li></ul>");
                }
            } else {
                List<NodeUsageReservationForm> tmplist = new ArrayList<>();
                Iterator iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();

                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    String tmpstr = jsonArray.toString();
                    String[] str = tmpstr.substring(1, tmpstr.length() - 1).split(",");
                    NodeUsageReservationForm obj = new NodeUsageReservationForm();
                    obj.setId(key);
                    obj.setStartDate(str[0].replace("\"", ""));
                    obj.setEndDate(str[1].replace("\"", ""));
                    obj.setNoOfNodes(Integer.parseInt(str[2].replace("\"", "")));
                    obj.setProjectId(nodeUsageReservationForm.getProjectId());
                    tmplist.add(obj);
                }
                List<ProjectDetails> userProjectsOwnerList = loggedInuserOwnedProjects(userId);
                // admin can make node booking on behalf of user.If logged in user is admin show complete list of projects//
                if (validateIfAdmin(session))
                {
                    List<ProjectDetails> projectsList = getProjects();
                    model.addAttribute("projectsList", projectsList);
                }
                else  // non admin users can only see their own projects
                {
                    model.addAttribute("projectsList", userProjectsOwnerList);
                }
                model.addAttribute("mapNodeReservationInfo", tmplist);
            }
        } catch (Exception e) {
            log.error(LOG_PREFIX, e);
            throw new WebServiceRuntimeException(e.getMessage());
        }
        return "edit_page_node_usage_reservation";
    }

    @RequestMapping(value = "/edit/node_reservation/", method = RequestMethod.POST)
    public String editNodeReservation(@Valid @ModelAttribute("nodeUsageReservationForm") NodeUsageReservationForm nodeUsageReservationForm,
                                      BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                      HttpSession session, Model model) throws WebServiceRuntimeException {

        final String LOG_PREFIX = "Edit Node Usage Reservation: {}";
        String userId = session.getAttribute(webProperties.getSessionUserId()).toString();

        if (bindingResult.hasErrors()) {
            log.warn(LOG_PREFIX, "Application form error " + nodeUsageReservationForm.toString());
            StringBuilder message = new StringBuilder();
            message.append(TAG_ERRORS);
            message.append(TAG_UL);
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                message.append(TAG_LI);
                switch (fieldError.getField()) {
                    case "startDate":
                        message.append("Start Date ");
                        message.append(fieldError.getDefaultMessage());
                        break;
                    case "endDate":
                        message.append("End Date ");
                        message.append(fieldError.getDefaultMessage());
                        break;
                    case "noOfNodes":
                        message.append("Number of Nodes ");
                        message.append(fieldError.getDefaultMessage());
                        break;
                    case "projectId":
                        message.append("Project ");
                        message.append(fieldError.getDefaultMessage());
                        break;
                    default:
                        message.append(fieldError.getField());
                        message.append(TAG_SPACE);
                        message.append(fieldError.getDefaultMessage());
                }
                message.append(TAG_LI_CLOSE);
            }
            message.append(TAG_UL_CLOSE);
            model.addAttribute(MESSAGE, message);
            List<ProjectDetails> userProjectsOwnerList = loggedInuserOwnedProjects(userId);
            // admin can make node booking on behalf of user.If logged in user is admin show complete list of projects//
            if (validateIfAdmin(session))
            {
                List<ProjectDetails> projectsList = getProjects();
                model.addAttribute("projectsList", projectsList);
            }
            else  // non admin users can only see their own projects
            {
                model.addAttribute("projectsList", userProjectsOwnerList);
            }
            return "edit_page_node_usage_reservation";
        }

        JSONObject nodeReserveFields = new JSONObject();
        nodeReserveFields.put("startDate", nodeUsageReservationForm.getZonedStartDate());
        nodeReserveFields.put("endDate", nodeUsageReservationForm.getZonedEndDate());
        nodeReserveFields.put("noNodes", nodeUsageReservationForm.getNoOfNodes());
        nodeReserveFields.put("projectId", nodeUsageReservationForm.getProjectId());
        HttpEntity<String> request = createHttpEntityWithBody(nodeReserveFields.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        try {
            ResponseEntity response = restTemplate.exchange(properties.editNodesReserve(nodeUsageReservationForm.getId()), HttpMethod.POST, request, String.class);
            String responseBody = response.getBody().toString();

            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                switch (exceptionState) {
                    case NODES_RESERVATION_NOT_FOUND_EXCEPTION:
                        log.warn("No Reservation found.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>project not found for reserving node usage</li></ul>");
                        break;
                    case NODES_RESERVATION_ALREADY_EXISTS_EXCEPTION:
                        log.warn("Overlapping node usage reservations found for same project.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>overlapping node usage reservations found for same project</li></ul>");
                        break;
                    case FORBIDDEN_EXCEPTION:
                        log.warn("Reserving of node usage forbidden.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>reserving of node usage forbidden</li></ul>");
                        break;
                    default:
                        log.warn("Unknown error for validating node usage reservation.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>unknown error for reserving node usage</li></ul>");
                }
            } else {
                // no errors, everything ok
                log.info(LOG_PREFIX, "Application for" + nodeUsageReservationForm.getNoOfNodes() + " node reservation from " + nodeUsageReservationForm.getStartDate() + " submitted");
                redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Node Usage Booking done.");
            }
        } catch (Exception e) {
            log.error(LOG_PREFIX, e);
            throw new WebServiceRuntimeException(e.getMessage());
        }
        return "redirect:/edit/usage/reservation";
    }

    @RequestMapping(value = "/delete/node_reservation/{id}", method = RequestMethod.GET)
    public String deleteNodeReservation(@PathVariable String id, RedirectAttributes redirectAttributes,
                                        HttpSession session) throws WebServiceRuntimeException {

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.editNodesReserve(id), HttpMethod.DELETE, request, String.class);
        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                switch (exceptionState) {
                    case NODES_RESERVATION_NOT_FOUND_EXCEPTION:
                        log.warn("No Reservation found.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>project not found for reserving node usage</li></ul>");
                        break;
                    case FORBIDDEN_EXCEPTION:
                        log.warn("Deleting of node usage reservation forbidden.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>reserving of node usage forbidden</li></ul>");
                        break;
                    default:
                        log.warn("Unknown error for deleting node usage reservation.");
                        redirectAttributes.addFlashAttribute(MESSAGE, "Error(s):<ul><li>unknown error for reserving node usage</li></ul>");
                }
            } else {
                log.info("Nodes usage reservation deleted: {}", responseBody);
                redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Node Usage Booking deleted.");
            }
        } catch (IOException e) {
            log.error("deleteNodeReservation: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }
        return "redirect:/edit/usage/reservation";
    }

    @GetMapping(value = {"/admin/monthly/{id}/usage/contribute", "/admin/monthly/{id}/usage/contribute/{month}"})
    public String adminMonthlyUsageContribute(@PathVariable String id, @PathVariable Optional<String> month,
                                              HttpSession session, Model model) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        if (month.isPresent()) {
            HttpEntity<String> request = createHttpEntityHeaderOnly();
            ResponseEntity response = restTemplate.exchange(properties.getMonthlyUsage(id) + "/" + month.get(), HttpMethod.GET, request, String.class);
            JSONObject usage = new JSONObject(response.getBody().toString());
            JSONObject usageId = usage.getJSONObject("id");
            ProjectUsage projectUsage = new ProjectUsage();
            projectUsage.setId(usageId.getInt(KEY_PROJECT_DETAILS_ID));
            projectUsage.setMonth(usageId.getString(KEY_MONTH_YEAR));
            projectUsage.setUsage(usage.getInt(KEY_MONTHLY_USAGE));
            projectUsage.setIncurred(usage.getDouble(KEY_INCURRED));
            projectUsage.setWaived(usage.getDouble(KEY_WAIVED));
            model.addAttribute(KEY_USAGE, projectUsage);
        } else {
            model.addAttribute(KEY_USAGE, new ProjectUsage());
        }
        model.addAttribute("pid", id);

        return ADMIN_MONTHLY_USAGE_CONTRIBUTE;
    }

    @PostMapping("/admin/monthly/{pid}/usage/contribute")
    public String adminMonthlyUsageValidate(@Valid @ModelAttribute("usage") ProjectUsage usage, BindingResult binding,
                                            @PathVariable String pid, HttpSession session, Model model) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        if (binding.hasErrors()) {
            String message = buildErrorMessage(binding);
            model.addAttribute(MESSAGE, message);
            model.addAttribute(KEY_USAGE, usage);
            model.addAttribute("pid", pid);
            return ADMIN_MONTHLY_USAGE_CONTRIBUTE;
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_PROJECT_DETAILS_ID, pid);
            jsonObject.put("month", usage.getMonth());
            jsonObject.put(KEY_USAGE, usage.getUsage());
            jsonObject.put(KEY_INCURRED, usage.getIncurred());
            jsonObject.put(KEY_WAIVED, usage.getWaived());
            log.debug("JsonObject: {}", jsonObject);

            restTemplate.setErrorHandler(new MyResponseErrorHandler());
            HttpEntity<String> request = createHttpEntityWithBody(jsonObject.toString());
            ResponseEntity response;
            if (usage.getId() == null || usage.getId() == 0) {
                response = restTemplate.exchange(properties.getMonthlyUsage(pid), HttpMethod.POST, request, String.class);
            } else {
                response = restTemplate.exchange(properties.getMonthlyUsage(pid), HttpMethod.PUT, request, String.class);
            }
            String responseBody = response.getBody().toString();

            try {
                if (RestUtil.isError(response.getStatusCode())) {
                    MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                    ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                    checkProjectUsageExceptionState(usage, model, exceptionState);
                    model.addAttribute(KEY_USAGE, usage);
                    model.addAttribute("pid", pid);
                    return ADMIN_MONTHLY_USAGE_CONTRIBUTE;
                } else {
                    log.info("Project details saved: {}", responseBody);
                }
            } catch (IOException e) {
                log.error("adminMonthlyValidate: {}", e.toString());
                throw new WebServiceRuntimeException(e.getMessage());
            }
        }

        return "redirect:/admin/monthly/" + pid + "/usage";
    }

    private void checkProjectUsageExceptionState(@Valid @ModelAttribute("usage") ProjectUsage usage, Model model, ExceptionState exceptionState) {
        switch (exceptionState) {
            case PROJECT_USAGE_NOT_FOUND_EXCEPTION:
                log.warn("Project usage not found for updating");
                model.addAttribute(MESSAGE, "Error(s):<ul><li>project usage not found for editing</li></ul>");
                break;
            case PROJECT_USAGE_ALREADY_EXISTS_EXCEPTION:
                log.warn("Project usage already exists: {} {}", usage.getId(), usage.getMonth());
                model.addAttribute(MESSAGE, "Error(s):<ul<li>project usage already exist</li></ul>");
                break;
            case FORBIDDEN_EXCEPTION:
                log.warn("Saving of project usage forbidden.");
                model.addAttribute(MESSAGE, "Error(s):<ul><li>saving project forbidden</li></ul>");
                break;
            default:
                log.warn("Unknown error for validating project usage.");
                model.addAttribute(MESSAGE, "Error(s):<ul><li>unknown error for validating project usage</li></ul>");
        }
    }

    @GetMapping("/admin/monthly/{id}/usage/remove/{month}")
    public String adminMonthlyUsageRemove(@PathVariable String id, @PathVariable String month,
                                          RedirectAttributes attr, HttpSession session) throws WebServiceRuntimeException {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getMonthlyUsage(id) + "/" + month, HttpMethod.DELETE, request, String.class);
        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                switch (exceptionState) {
                    case PROJECT_USAGE_NOT_FOUND_EXCEPTION:
                        log.warn("Project usage not found for deleting");
                        attr.addFlashAttribute(MESSAGE, "Error(s):<ul><li>project usage not found for deleting</li></ul>");
                        break;
                    case FORBIDDEN_EXCEPTION:
                        log.warn("Removing of project usage forbidden.");
                        attr.addFlashAttribute(MESSAGE, "Error(s):<ul><li>deleting project forbidden</li></ul>");
                        break;
                    default:
                        log.warn("Unknown error for validating project usage.");
                        attr.addFlashAttribute(MESSAGE, "Error(s):<ul><li>unknown error for deleting project usage</li></ul>");
                }
            } else {
                log.info("Project usage deleted: {}", responseBody);
            }
        } catch (IOException e) {
            log.error("adminMonthlyRemove: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return "redirect:/admin/monthly/" + id + "/usage";
    }

    @GetMapping("/admin/statistics")
    public String adminUsageStatistics(HttpSession session, Model model) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        if (!model.containsAttribute(KEY_QUERY)) {
            model.addAttribute(KEY_QUERY, new ProjectUsageQuery());
            model.addAttribute("newProjects", new ArrayList<ProjectDetails>());
            model.addAttribute("activeProjects", new ArrayList<ProjectDetails>());
            model.addAttribute("inactiveProjects", new ArrayList<ProjectDetails>());
            model.addAttribute("stoppedProjects", new ArrayList<ProjectDetails>());
            model.addAttribute("utilization", new HashMap<String, MonthlyUtilization>());
            model.addAttribute("statsCategory", new HashMap<String, Integer>());
            model.addAttribute("statsAcademic", new HashMap<String, Integer>());
        }

        return "admin_usage_statistics";
    }

    @PostMapping("/admin/statistics")
    public String adminUsageStatisticsQuery(@Valid @ModelAttribute("query") ProjectUsageQuery query, BindingResult result,
                                            RedirectAttributes attributes, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        List<ProjectDetails> newProjects = new ArrayList<>();
        List<ProjectDetails> activeProjects = new ArrayList<>();
        List<ProjectDetails> inactiveProjects = new ArrayList<>();
        List<ProjectDetails> stoppedProjects = new ArrayList<>();
        List<String> months = new ArrayList<>();
        Map<String, MonthlyUtilization> utilizationMap = new HashMap<>();
        Map<String, Integer> statsCategoryMap = new HashMap<>();
        Map<String, Integer> statsAcademicMap = new HashMap<>();
        int totalCategoryUsage = 0;
        int totalAcademicUsage = 0;

        if (result.hasErrors()) {
            StringBuilder message = new StringBuilder();
            message.append(TAG_ERRORS);
            message.append(TAG_UL);
            for (ObjectError objectError : result.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                message.append(TAG_LI);
                message.append(fieldError.getField());
                message.append(TAG_SPACE);
                message.append(fieldError.getDefaultMessage());
                message.append(TAG_LI_CLOSE);
            }
            message.append(TAG_UL_CLOSE);
            attributes.addFlashAttribute(MESSAGE, message.toString());
        } else {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("MMM-yyyy").toFormatter();
            YearMonth m_s = YearMonth.parse(query.getStart(), formatter);
            YearMonth m_e = YearMonth.parse(query.getEnd(), formatter);

            YearMonth counter = m_s;
            while (!counter.isAfter(m_e)) {
                String monthYear = counter.format(formatter);
                utilizationMap.put(monthYear, new MonthlyUtilization(monthYear));
                months.add(monthYear);
                counter = counter.plusMonths(1);
            }
            List<ProjectDetails> projectsList = getProjects();

            for (ProjectDetails project : projectsList) {
                // compute active and inactive projects
                differentiateProjects(newProjects, activeProjects, inactiveProjects, stoppedProjects, m_s, m_e, project);

                // monthly utilisation
                computeMonthlyUtilisation(utilizationMap, formatter, m_s, m_e, project);

                // usage statistics by category
                totalCategoryUsage += getCategoryUsage(statsCategoryMap, m_s, m_e, project);

                // usage statistics by academic institutes
                totalAcademicUsage += getAcademicUsage(statsAcademicMap, m_s, m_e, project);
            }
        }

        attributes.addFlashAttribute(KEY_QUERY, query);
        attributes.addFlashAttribute("newProjects", newProjects);
        attributes.addFlashAttribute("activeProjects", activeProjects);
        attributes.addFlashAttribute("inactiveProjects", inactiveProjects);
        attributes.addFlashAttribute("stoppedProjects", stoppedProjects);
        attributes.addFlashAttribute("months", months);
        attributes.addFlashAttribute("utilization", utilizationMap);
        attributes.addFlashAttribute("statsCategory", statsCategoryMap);
        attributes.addFlashAttribute("totalCategoryUsage", totalCategoryUsage);
        attributes.addFlashAttribute("statsAcademic", statsAcademicMap);
        attributes.addFlashAttribute("totalAcademicUsage", totalAcademicUsage);

        return "redirect:/admin/statistics";
    }

    private void differentiateProjects(List<ProjectDetails> newProjects,
                                       List<ProjectDetails> activeProjects,
                                       List<ProjectDetails> inactiveProjects,
                                       List<ProjectDetails> stoppedProjects,
                                       YearMonth m_s, YearMonth m_e,
                                       ProjectDetails project) {
        YearMonth created = YearMonth.from(project.getZonedDateCreated());
        YearMonth m_e_m1 = m_e.minusMonths(1);
        YearMonth m_e_m2 = m_e.minusMonths(2);
        YearMonth m_active = m_e_m2.isBefore(m_s) ? m_e_m2 : m_s;

        // projects created within the period
        if (!(created.isBefore(m_s) || created.isAfter(m_e))) {
            newProjects.add(project);
        }

        // active projects = projects with resources within the period + projects created
        boolean hasUsage = project.getProjectUsages().stream().anyMatch(p -> p.hasUsageWithinPeriod(m_active, m_e));
        if (hasUsage || !(created.isBefore(m_e_m2) || created.isAfter(m_e))) {
            activeProjects.add(project);
        }

        // inactive projects
        if (!hasUsage && created.isBefore(m_e_m2)) {
            inactiveProjects.add(project);
        }

        // stopped projects
        boolean hasUsagePreviousMonth = project.getProjectUsages().stream().anyMatch(p -> p.hasUsageWithinPeriod(m_e_m1, m_e_m1));
        boolean hasUsageCurrentMonth = project.getProjectUsages().stream().anyMatch(p -> p.hasUsageWithinPeriod(m_e, m_e));
        if (hasUsagePreviousMonth && !hasUsageCurrentMonth) {
            stoppedProjects.add(project);
        }
    }

    private void computeMonthlyUtilisation(Map<String, MonthlyUtilization> utilizationMap, DateTimeFormatter formatter, YearMonth m_s, YearMonth m_e, ProjectDetails project) {
        YearMonth counter = m_s;
        while (!counter.isAfter(m_e)) {
            String monthYear = counter.format(formatter);
            int usageSum = project.getProjectUsages().stream().filter(p -> p.getMonth().equals(monthYear)).mapToInt(ProjectUsage::getUsage).sum();
            utilizationMap.get(monthYear).addNodeHours(usageSum);
            counter = counter.plusMonths(1);
        }
    }

    private int getCategoryUsage(Map<String, Integer> statsCategoryMap, YearMonth m_s, YearMonth m_e, ProjectDetails project) {
        String key = project.getOrganisationType();
        if (key.equals("Academic")) {
            key = project.isEducation() ? "Academia (Education)" : "Academia  (R&D)";
        }
        int totalNodeHours = statsCategoryMap.getOrDefault(key, 0);
        int nodeHours = project.getProjectUsages().stream().filter(p -> p.hasUsageWithinPeriod(m_s, m_e)).mapToInt(ProjectUsage::getUsage).sum();
        statsCategoryMap.put(key, totalNodeHours + nodeHours);
        return nodeHours;
    }

    private int getAcademicUsage(Map<String, Integer> statsAcademicMap, YearMonth m_s, YearMonth m_e, ProjectDetails project) {
        int nodeHours = 0;
        if (project.getOrganisationType().equals("Academic")) {
            int totalNodeHours = statsAcademicMap.getOrDefault(project.getOrganisationName(), 0);
            nodeHours = project.getProjectUsages().stream().filter(p -> p.hasUsageWithinPeriod(m_s, m_e)).mapToInt(ProjectUsage::getUsage).sum();
            statsAcademicMap.put(project.getOrganisationName(), totalNodeHours + nodeHours);
        }
        return nodeHours;
    }

    @GetMapping("/admin/analysis")
    public String adminUsageAnalysis(HttpSession session, Model model) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        if (!model.containsAttribute(KEY_QUERY)) {
            model.addAttribute(KEY_QUERY, new ProjectUsageQuery());
            model.addAttribute("usageAnalysis", new HashMap<String, UsageAnalysis>());
            model.addAttribute("analysisCategory", new HashMap<String, UsageAnalysis>());
            model.addAttribute("analysisAcademic", new HashMap<String, UsageAnalysis>());
        }

        return "admin_usage_analysis";
    }

    @PostMapping("/admin/analysis")
    public String adminUsageAnalysisQuery(@Valid @ModelAttribute("query") ProjectUsageQuery query, BindingResult result,
                                          RedirectAttributes attributes, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        List<String> months = new ArrayList<>();
        Map<String, UsageAnalysis> usageAnalysis = new HashMap<>();
        Map<String, UsageAnalysis> analysisCategory = new HashMap<>();
        Map<String, UsageAnalysis> analysisAcademic = new HashMap<>();

        if (result.hasErrors()) {
            StringBuilder message = new StringBuilder();
            message.append(TAG_ERRORS);
            message.append(TAG_UL);
            for (ObjectError objectError : result.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                message.append(TAG_LI);
                message.append(fieldError.getField());
                message.append(TAG_SPACE);
                message.append(fieldError.getDefaultMessage());
                message.append(TAG_LI_CLOSE);
            }
            message.append(TAG_UL_CLOSE);
            attributes.addFlashAttribute(MESSAGE, message.toString());
        } else {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("MMM-yyyy").toFormatter();
            YearMonth m_s = YearMonth.parse(query.getStart(), formatter);
            YearMonth m_e = YearMonth.parse(query.getEnd(), formatter);

            List<ProjectDetails> projectsList = getProjects();
            YearMonth counter = m_s;
            while (!counter.isAfter(m_e)) {
                String monthYear = counter.format(formatter);
                usageAnalysis.put(monthYear, new UsageAnalysis());
                months.add(monthYear);

                // compute monthly usage costs
                for (ProjectDetails project : projectsList) {
                    double incurredSum = project.getProjectUsages().stream().filter(p -> p.getMonth().equals(monthYear)).mapToDouble(ProjectUsage::getIncurred).sum();
                    double waivedSum = project.getProjectUsages().stream().filter(p -> p.getMonth().equals(monthYear)).mapToDouble(ProjectUsage::getWaived).sum();
                    UsageAnalysis usage = usageAnalysis.get(monthYear);
                    usage.addIncurred(incurredSum);
                    usage.addWaived(waivedSum);
                }

                counter = counter.plusMonths(1);
            }

            // compute usage analysis by category and academic
            for (ProjectDetails project : projectsList) {
                double incurredSum = project.getProjectUsages().stream().filter(p -> p.hasUsageWithinPeriod(m_s, m_e)).mapToDouble(ProjectUsage::getIncurred).sum();
                double waivedSum = project.getProjectUsages().stream().filter(p -> p.hasUsageWithinPeriod(m_s, m_e)).mapToDouble(ProjectUsage::getWaived).sum();
                String key = project.getOrganisationType();
                if (key.equals("Academic")) {
                    UsageAnalysis usageAcademic = analysisAcademic.getOrDefault(key, new UsageAnalysis());
                    usageAcademic.addIncurred(incurredSum);
                    usageAcademic.addWaived(waivedSum);
                    analysisAcademic.putIfAbsent(key, usageAcademic);
                    key = project.isEducation() ? "Academia (Education)" : "Academia  (R&D)";
                }
                UsageAnalysis usageCategory = analysisCategory.getOrDefault(key, new UsageAnalysis());
                usageCategory.addIncurred(incurredSum);
                usageCategory.addWaived(waivedSum);
                analysisCategory.putIfAbsent(key, usageCategory);
            }
        }

        attributes.addFlashAttribute(KEY_QUERY, query);
        attributes.addFlashAttribute("months", months);
        attributes.addFlashAttribute("usageAnalysis", usageAnalysis);
        attributes.addFlashAttribute("analysisCategory", analysisCategory);
        attributes.addFlashAttribute("analysisAcademic", analysisAcademic);

        return "redirect:/admin/analysis";
    }

    /**
     * Allows admins to:
     * view reservations
     * reserve nodes
     * release nodes
     */
    @GetMapping("/admin/nodesReservation")
    public String adminNodesReservation(@ModelAttribute("reservationStatusForm") ReservationStatusForm reservationStatusForm, Model model, HttpSession session) {
        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        List<Team2> allTeams = new ArrayList<>(getTeamMap().values());
        allTeams.sort(Comparator.comparing(Team2::getName, String.CASE_INSENSITIVE_ORDER));
        model.addAttribute(ALL_TEAMS, allTeams);
        model.addAttribute(RESERVATION_STATUS_FORM, reservationStatusForm);

        return "node_reservation";
    }

    @PostMapping("/admin/nodesReservation")
    public String adminNodesReservation(@ModelAttribute("reservationStatusForm") ReservationStatusForm reservationStatusForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        List<Team2> allTeams = new ArrayList<>(getTeamMap().values());
        allTeams.sort(Comparator.comparing(Team2::getName, String.CASE_INSENSITIVE_ORDER));
        model.addAttribute(ALL_TEAMS, allTeams);

        // sanitization
        if (bindingResult.hasErrors()) {
            model.addAttribute(ALL_TEAMS, allTeams);
            model.addAttribute(RESERVATION_STATUS_FORM, reservationStatusForm);
            model.addAttribute(STATUS, NODES_RESERVATION_FAIL);
            model.addAttribute(MESSAGE, "form errors");
            return "node_reservation";
        }

        switch(reservationStatusForm.getAction()) {
            case "release":
                releaseNodes(reservationStatusForm, redirectAttributes);
                break;
            case "reserve":
                reserveNodes(reservationStatusForm, redirectAttributes);
                break;
            case "check":
                checkReservation(reservationStatusForm, redirectAttributes);
                break;
            default:
                // error
                redirectAttributes.addFlashAttribute(STATUS, NODES_RESERVATION_FAIL);
                redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                break;
        }

        redirectAttributes.addFlashAttribute(RESERVATION_STATUS_FORM, reservationStatusForm);
        redirectAttributes.addFlashAttribute(ALL_TEAMS, allTeams);

        return "redirect:/admin/nodesReservation";
    }

    private void checkReservation(ReservationStatusForm reservationStatusForm, RedirectAttributes redirectAttributes) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange((properties.getReservationStatus(reservationStatusForm.getTeamId())), HttpMethod.GET, request, String.class);

        /**
         * @return  a json string in the format:
         *   {
         *       "status" : "ok/fail",
         *       "reserved": ["pc1", "pc4", "pc2"],
         *       "in_use": [["pc4", "ncltest01", "vnctest"], ["pc2", "testbed-ncl", "thales-poc"]]
         *   }
         */
        JSONObject result = new JSONObject(response.getBody().toString());
        String status = result.getString(STATUS);
        Set<String> reservedSet = new HashSet<> (convertJSONArrayToList(result.getJSONArray(RESERVED)));
        JSONArray inUseNodesArray = result.getJSONArray("in_use");
        HashMap<String, String> inUseHashMap = new HashMap<>();
        for (int i=0; i < inUseNodesArray.length(); i++) {
            JSONArray nodeArray = inUseNodesArray.getJSONArray(i);
            inUseHashMap.put(nodeArray.getString(0), nodeArray.getString(1) + "/" + nodeArray.getString(2));

        }

        redirectAttributes.addFlashAttribute("reservedSet", reservedSet);
        redirectAttributes.addFlashAttribute("inUseHashMap", inUseHashMap);
        redirectAttributes.addFlashAttribute(STATUS, status);
    }

    private void releaseNodes(ReservationStatusForm reservationStatusForm, RedirectAttributes redirectAttributes) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();

        int numNodesToRelease = reservationStatusForm.getNumNodes() == null ? -1 : reservationStatusForm.getNumNodes();

        ResponseEntity response = restTemplate.exchange((properties.releaseNodes(reservationStatusForm.getTeamId(),
                numNodesToRelease)), HttpMethod.DELETE, request, String.class);


        JSONObject object = new JSONObject(response.getBody().toString());
        String status = object.getString(STATUS);
        String nodesUpdated = object.getJSONArray("released").toString();

        redirectAttributes.addFlashAttribute(STATUS, status);
        redirectAttributes.addFlashAttribute("nodesUpdated", nodesUpdated);
    }

    private void reserveNodes(ReservationStatusForm reservationStatusForm, RedirectAttributes redirectAttributes) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();

        if (reservationStatusForm.getNumNodes() == null) {
            redirectAttributes.addFlashAttribute(STATUS, "FAIL");
            redirectAttributes.addFlashAttribute(MESSAGE, "Number of nodes not specified");
            return;
        }

        ResponseEntity response = restTemplate.exchange((properties.reserveNodes(reservationStatusForm.getTeamId(),
                reservationStatusForm.getNumNodes(), reservationStatusForm.getMachineType())), HttpMethod.POST, request, String.class);

        JSONObject object = new JSONObject(response.getBody().toString());
        String status = object.getString(STATUS);
        String message = object.getString(MESSAGE);
        String nodesUpdated = object.getJSONArray(RESERVED).toString();

        redirectAttributes.addFlashAttribute(STATUS, status);
        if (!"OK".equals(STATUS)) {
            redirectAttributes.addFlashAttribute(MESSAGE, message);
        }
        redirectAttributes.addFlashAttribute("nodesUpdated", nodesUpdated);
    }

    private List<String> convertJSONArrayToList(JSONArray jsonArray) {
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            resultList.add(jsonArray.getString(i));
        }
        return resultList;
    }

    private HashMap<String, Team2> getTeamMap() {
        TeamManager2 teamManager2 = new TeamManager2();
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioTeamsUrl(), HttpMethod.GET, request, String.class);

        JSONArray jsonArray = new JSONArray(responseEntity.getBody().toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Team2 one = extractTeamInfo(jsonObject.toString());
            teamManager2.addTeamToTeamMap(one);
        }
        return teamManager2.getTeamMap();
    }

    @RequestMapping("/admin/nodesStatus")
    public String adminNodesStatus(Model model, HttpSession session) throws IOException {

        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        // get number of active users and running experiments
        Map<String, String> testbedStatsMap = getTestbedStats();
        testbedStatsMap.put(USER_DASHBOARD_FREE_NODES, "0");
        testbedStatsMap.put(USER_DASHBOARD_TOTAL_NODES, "0");

        Map<String, List<Map<String, String>>> nodesStatus = getNodesStatus();
        Map<String, Map<String, Long>> nodesStatusCount = new HashMap<>();

        List<Map<String, String>> allNodes = new ArrayList<>();
        nodesStatus.forEach((key, value) -> allNodes.addAll(value));
        Map<String, List<Map<String, String>>> allNodesStatus = new HashMap<>();
        allNodesStatus.put("All Servers", allNodes);
        countNodeStatus(testbedStatsMap, allNodesStatus, nodesStatusCount);

        model.addAttribute("nodesStatus", allNodesStatus);
        model.addAttribute("nodesStatusCount", nodesStatusCount);

        model.addAttribute(USER_DASHBOARD_LOGGED_IN_USERS_COUNT, testbedStatsMap.get(USER_DASHBOARD_LOGGED_IN_USERS_COUNT));
        model.addAttribute(USER_DASHBOARD_RUNNING_EXPERIMENTS_COUNT, testbedStatsMap.get(USER_DASHBOARD_RUNNING_EXPERIMENTS_COUNT));
        model.addAttribute(USER_DASHBOARD_FREE_NODES, testbedStatsMap.get(USER_DASHBOARD_FREE_NODES));
        model.addAttribute(USER_DASHBOARD_TOTAL_NODES, testbedStatsMap.get(USER_DASHBOARD_TOTAL_NODES));
        return "node_status";
    }

    /**
     * loop through each of the machine type
     * tabulate the different nodes type
     * count the number of different nodes status, e.g. SYSTEMX = { FREE = 10, IN_USE = 11, ... }
     */
    private void countNodeStatus(Map<String, String> testbedStatsMap, Map<String, List<Map<String, String>>> nodesStatus, Map<String, Map<String, Long>> nodesStatusCount) {
        nodesStatus.entrySet().forEach(machineTypeListEntry -> {
            Map<String, Long> nodesCountMap = new HashMap<>();

            long free = machineTypeListEntry.getValue().stream().filter(stringStringMap -> "free".equalsIgnoreCase(stringStringMap.get(STATUS))).count();
            long inUse = machineTypeListEntry.getValue().stream().filter(stringStringMap -> "in_use".equalsIgnoreCase(stringStringMap.get(STATUS))).count();
            long reserved = machineTypeListEntry.getValue().stream().filter(stringStringMap -> RESERVED.equalsIgnoreCase(stringStringMap.get(STATUS))).count();
            long reload = machineTypeListEntry.getValue().stream().filter(stringStringMap -> "reload".equalsIgnoreCase(stringStringMap.get(STATUS))).count();
            long total = free + inUse + reserved + reload;
            long currentTotal = Long.parseLong(testbedStatsMap.get(USER_DASHBOARD_TOTAL_NODES)) + total;
            long currentFree = Long.parseLong(testbedStatsMap.get(USER_DASHBOARD_FREE_NODES)) + free;

            nodesCountMap.put(NodeType.FREE.name(), free);
            nodesCountMap.put(NodeType.IN_USE.name(), inUse);
            nodesCountMap.put(NodeType.RESERVED.name(), reserved);
            nodesCountMap.put(NodeType.RELOADING.name(), reload);

            nodesStatusCount.put(machineTypeListEntry.getKey(), nodesCountMap);
            testbedStatsMap.put(USER_DASHBOARD_FREE_NODES, Long.toString(currentFree));
            testbedStatsMap.put(USER_DASHBOARD_TOTAL_NODES, Long.toString(currentTotal));
        });
    }

    /**
     * Get simple ZonedDateTime from date string in the format 'YYYY-MM-DD'.
     * @param date  date string to convert
     * @return      ZonedDateTime of
     */
    private ZonedDateTime convertToZonedDateTime(String date) {
        String[] result = date.split("-");
        return ZonedDateTime.of(
                Integer.parseInt(result[0]),
                Integer.parseInt(result[1]),
                Integer.parseInt(result[2]),
                0, 0, 0, 0, ZoneId.of("Asia/Singapore"));
    }

//    @RequestMapping(value="/admin/domains/add", method=RequestMethod.POST)
//    public String addDomain(@Valid Domain domain, BindingResult bindingResult) {
//    	if (bindingResult.hasErrors()) {
//    		return REDIRECT_ADMIN;
//    	} else {
//    		domainManager.addDomains(domain.getDomainName());
//    	}
//    	return REDIRECT_ADMIN;
//    }

//    @RequestMapping("/admin/domains/remove/{domainKey}")
//    public String removeDomain(@PathVariable String domainKey) {
//    	domainManager.removeDomains(domainKey);
//    	return REDIRECT_ADMIN;
//    }

    @RequestMapping("/admin/teams/accept/{teamId}/{teamOwnerId}")
    public String approveTeam(
            @PathVariable String teamId,
            @PathVariable String teamOwnerId,
            final RedirectAttributes redirectAttributes,
            HttpSession session
    ) throws WebServiceRuntimeException {

        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        //FIXME require approver info
        log.info("Approving new team {}, team owner {}", teamId, teamOwnerId);
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(
        properties.getApproveTeam(teamId, teamOwnerId, TeamStatus.APPROVED), HttpMethod.POST, request, String.class);
        String responseBody = response.getBody().toString();
        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error;
            try {
                error = objectMapper.readValue(responseBody, MyErrorResource.class);
            } catch (IOException e) {
                throw new WebServiceRuntimeException(e.getMessage());
            }
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            switch (exceptionState) {
                case TEAM_ID_NULL_OR_EMPTY_EXCEPTION:
                    log.warn("Approve team: TeamId cannot be null or empty: {}",
                            teamId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "TeamId cannot be null or empty");
                    break;
                case USER_ID_NULL_OR_EMPTY_EXCEPTION:
                    log.warn("Approve team: UserId cannot be null or empty: {}",
                            teamOwnerId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "UserId cannot be null or empty");
                    break;
                case EMAIL_NOT_VERIFIED_EXCEPTION:
                    log.warn("Approve team: User {} email not verified", teamOwnerId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "User email has not been verified");
                    break;
                case INVALID_TEAM_STATUS_EXCEPTION:
                    log.warn("Approve team: TeamStatus is invalid");
                    redirectAttributes.addFlashAttribute(MESSAGE, "Team status is invalid");
                    break;
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn("Approve team: Team {} not found", teamId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "Team does not exist");
                    break;
                case DETERLAB_OPERATION_FAILED_EXCEPTION:
                    log.warn("Approve team: Team {} fail", teamId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "Approve team request fail on Deterlab");
                    break;
                default:
                    log.warn("Approve team : sio or deterlab adapter connection error");
                    // possible sio or adapter connection fail
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
            }
            return REDIRECT_ADMIN;
        }

        // http status code is OK, then need to check the response message
        String msg = new JSONObject(responseBody).getString("msg");
        if ("approve project OK".equals(msg)) {
            log.info("Approve team {} OK", teamId);
        } else {
            log.warn("Approve team {} FAIL", teamId);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
        }
        return REDIRECT_ADMIN;
    }

    @RequestMapping("/admin/teams/reject/{teamId}/{teamOwnerId}")
    public String rejectTeam(
            @PathVariable String teamId,
            @PathVariable String teamOwnerId,
            @RequestParam("reason") String reason,
            final RedirectAttributes redirectAttributes,
            HttpSession session
    ) throws WebServiceRuntimeException {

        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        //FIXME require approver info
        log.info("Rejecting new team {}, team owner {}, reason {}", teamId, teamOwnerId, reason);
        HttpEntity<String> request = createHttpEntityWithBody(reason);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(
                properties.getApproveTeam(teamId, teamOwnerId, TeamStatus.REJECTED), HttpMethod.POST, request, String.class);

        String responseBody = response.getBody().toString();
        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error;
            try {
                error = objectMapper.readValue(responseBody, MyErrorResource.class);
            } catch (IOException e) {
                throw new WebServiceRuntimeException(e.getMessage());
            }

            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            switch (exceptionState) {
                case TEAM_ID_NULL_OR_EMPTY_EXCEPTION:
                    log.warn("Reject team: TeamId cannot be null or empty: {}",
                            teamId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "TeamId cannot be null or empty");
                    break;
                case USER_ID_NULL_OR_EMPTY_EXCEPTION:
                    log.warn("Reject team: UserId cannot be null or empty: {}",
                            teamOwnerId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "UserId cannot be null or empty");
                    break;
                case INVALID_TEAM_STATUS_EXCEPTION:
                    log.warn("Reject team: TeamStatus is invalid");
                    redirectAttributes.addFlashAttribute(MESSAGE, "Team status is invalid");
                    break;
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn("Reject team: Team {} not found", teamId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "Team does not exist");
                    break;
                case DETERLAB_OPERATION_FAILED_EXCEPTION:
                    log.warn("Reject team: Team {} fail", teamId);
                    redirectAttributes.addFlashAttribute(MESSAGE, "Reject team request fail on Deterlab");
                    break;
                default:
                    log.warn("Reject team : sio or deterlab adapter connection error");
                    // possible sio or adapter connection fail
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
            }
            return REDIRECT_ADMIN;
        }

        // http status code is OK, then need to check the response message
        String msg = new JSONObject(responseBody).getString("msg");
        if ("reject project OK".equals(msg)) {
            log.info("Reject team {} OK", teamId);
        } else {
            log.warn("Reject team {} FAIL", teamId);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
        }
        return REDIRECT_ADMIN;
    }

    @RequestMapping("/admin/teams/{teamId}")
    public String setupTeamRestriction(
            @PathVariable final String teamId,
            @RequestParam(value = "action", required = true) final String action,
            final RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException {
        final String LOG_MESSAGE = "Updating restriction settings for team {}: {}";

        // check if admin
        if (!validateIfAdmin(session)) {
            log.warn(LOG_MESSAGE, teamId, PERMISSION_DENIED);
            return NO_PERMISSION_PAGE;
        }

        Team2 team = invokeAndExtractTeamInfo(teamId);

        // check if team is approved before restricted
        if ("restrict".equals(action) && team.getStatus().equals(TeamStatus.APPROVED.name())) {
            return restrictTeam(team, redirectAttributes);
        }
        // check if team is restricted before freeing it back to approved
        else if ("free".equals(action) && team.getStatus().equals(TeamStatus.RESTRICTED.name())) {
            return freeTeam(team, redirectAttributes);
        } else {
            log.warn(LOG_MESSAGE, teamId, "Cannot " + action + " team with status " + team.getStatus());
            redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + "Cannot " + action + " team " + team.getName() + " with status " + team.getStatus());
            return "redirect:/admin/teams";
        }
    }

    private String restrictTeam(final Team2 team, RedirectAttributes redirectAttributes) throws IOException {
        log.info("Restricting team {}", team.getId());

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(
                properties.getSioTeamsStatusUrl(team.getId(), TeamStatus.RESTRICTED),
                HttpMethod.PUT, request, String.class);
        String responseBody = response.getBody().toString();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
            String logMessage = "Failed to restrict team {}: {}";
            handleException(team, redirectAttributes, error, exceptionState, logMessage);
            return REDIRECT_ADMIN;
        } else {
            // good
            log.info("Team {} has been restricted", team.getId());
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Team status has been changed to " + TeamStatus.RESTRICTED.name());
            return REDIRECT_ADMIN;
        }
    }

    private String freeTeam(final Team2 team, RedirectAttributes redirectAttributes) throws IOException {
        log.info("Freeing team {}", team.getId());

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(
                properties.getSioTeamsStatusUrl(team.getId(), TeamStatus.APPROVED),
                HttpMethod.PUT, request, String.class);
        String responseBody = response.getBody().toString();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
            String logMessage = "Failed to free team {}: {}";
            handleException(team, redirectAttributes, error, exceptionState, logMessage);
            return REDIRECT_ADMIN;
        } else {
            // good
            log.info("Team {} has been freed", team.getId());
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Team status has been changed to " + TeamStatus.APPROVED.name());
            return REDIRECT_ADMIN;
        }
    }

    private void handleException(Team2 team, RedirectAttributes redirectAttributes, MyErrorResource error, ExceptionState exceptionState, String logMessage) {
        switch (exceptionState) {
            case TEAM_NOT_FOUND_EXCEPTION:
                log.warn(logMessage, team.getId(), TEAM_NOT_FOUND);
                redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + TEAM_NOT_FOUND);
                break;
            case INVALID_STATUS_TRANSITION_EXCEPTION:
            case INVALID_TEAM_STATUS_EXCEPTION:
                log.warn(logMessage, team.getId(), error.getMessage());
                redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + error.getMessage());
                break;
            case FORBIDDEN_EXCEPTION:
                log.warn(logMessage, team.getId(), PERMISSION_DENIED);
                redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + PERMISSION_DENIED);
                break;
            default:
                log.warn(logMessage, team.getId(), exceptionState.getExceptionName());
                redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
        }
    }

    @RequestMapping("/admin/users/{userId}")
    public String freezeUnfreezeUsers(
            @PathVariable final String userId,
            @RequestParam(value = "action", required = true) final String action,
            final RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException {
        User2 user = invokeAndExtractUserInfo(userId);

        // check if admin
        if (!validateIfAdmin(session)) {
            log.warn("Access denied when trying to freeze/unfreeze user {}: must be admin!", userId);
            return NO_PERMISSION_PAGE;
        }

        // check if user status is approved before freeze
        if ("freeze".equals(action) && user.getStatus().equals(UserStatus.APPROVED.toString())) {
            return freezeUser(user, redirectAttributes);
        }
        // check if user status is frozen before unfreeze
        else if ("unfreeze".equals(action) && user.getStatus().equals(UserStatus.FROZEN.toString())) {
            return unfreezeUser(user, redirectAttributes);
        } else {
            log.warn("Error in freeze/unfreeze user {}: failed to {} user with status {}", userId, action, user.getStatus());
            redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + "failed to " + action + USER_STR + user.getEmail() + " with status " + user.getStatus());
            return "redirect:/admin/users";
        }
    }

    private String freezeUser(final User2 user, RedirectAttributes redirectAttributes) throws IOException {
        log.info("Freezing user {}, email {}", user.getId(), user.getEmail());

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(
                properties.getSioUsersStatusUrl(user.getId(), UserStatus.FROZEN.toString()),
                HttpMethod.PUT, request, String.class);
        String responseBody = response.getBody().toString();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            switch (exceptionState) {
                case USER_NOT_FOUND_EXCEPTION:
                    log.warn("Failed to freeze user {}: user not found", user.getId());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + USER_STR + user.getEmail() + NOT_FOUND);
                    break;
                case INVALID_STATUS_TRANSITION_EXCEPTION:
                    log.warn("Failed to freeze user {}: invalid status transition {}", user.getId(), error.getMessage());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + error.getMessage() + " is not allowed.");
                    break;
                case INVALID_USER_STATUS_EXCEPTION:
                    log.warn("Failed to freeze user {}: invalid user status {}", user.getId(), error.getMessage());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + error.getMessage() + " is not a valid status.");
                    break;
                case FORBIDDEN_EXCEPTION:
                    log.warn("Failed to freeze user {}: must be an Admin", user.getId());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + " permission denied.");
                    break;
                default:
                    log.warn("Failed to freeze user {}: {}", user.getId(), exceptionState.getExceptionName());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
            }
            return REDIRECT_ADMIN;
        } else {
            // good
            log.info("User {} has been frozen", user.getId());
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, USER_PREFIX + user.getEmail() + " has been banned.");
            return REDIRECT_ADMIN;
        }
    }

    private String unfreezeUser(final User2 user, RedirectAttributes redirectAttributes) throws IOException {
        log.info("Unfreezing user {}, email {}", user.getId(), user.getEmail());

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(
                properties.getSioUsersStatusUrl(user.getId(), UserStatus.APPROVED.toString()),
                HttpMethod.PUT, request, String.class);
        String responseBody = response.getBody().toString();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            switch (exceptionState) {
                case USER_NOT_FOUND_EXCEPTION:
                    log.warn("Failed to unfreeze user {}: user not found", user.getId());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + USER_STR + user.getEmail() + NOT_FOUND);
                    break;
                case INVALID_STATUS_TRANSITION_EXCEPTION:
                    log.warn("Failed to unfreeze user {}: invalid status transition {}", user.getId(), error.getMessage());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + error.getMessage() + " is not allowed.");
                    break;
                case INVALID_USER_STATUS_EXCEPTION:
                    log.warn("Failed to unfreeze user {}: invalid user status {}", user.getId(), error.getMessage());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + error.getMessage() + " is not a valid status.");
                    break;
                case FORBIDDEN_EXCEPTION:
                    log.warn("Failed to unfreeze user {}: must be an Admin", user.getId());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + " permission denied.");
                    break;
                default:
                    log.warn("Failed to unfreeze user {}: {}", user.getId(), exceptionState.getExceptionName());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
            }
            return REDIRECT_ADMIN;
        } else {
            // good
            log.info("User {} has been unfrozen", user.getId());
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, USER_PREFIX + user.getEmail() + " has been unbanned.");
            return REDIRECT_ADMIN;
        }
    }

    @RequestMapping("/admin/users/{userId}/remove")
    public String removeUser(@PathVariable final String userId, final RedirectAttributes redirectAttributes, HttpSession session) throws IOException {
        // check if admin
        if (!validateIfAdmin(session)) {
            log.warn("Access denied when trying to remove user {}: must be admin!", userId);
            return NO_PERMISSION_PAGE;
        }

        User2 user = invokeAndExtractUserInfo(userId);

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getUser(user.getId()), HttpMethod.DELETE, request, String.class);
        String responseBody = response.getBody().toString();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            switch (exceptionState) {
                case USER_NOT_FOUND_EXCEPTION:
                    log.warn("Failed to remove user {}: user not found", user.getId());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + USER_STR + user.getEmail() + NOT_FOUND);
                    break;
                case USER_IS_NOT_DELETABLE_EXCEPTION:
                    log.warn("Failed to remove user {}: user is not deletable", user.getId());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + USER_STR + user.getEmail() + " is not deletable.");
                    break;
                case CREDENTIALS_NOT_FOUND_EXCEPTION:
                    log.warn("Failed to remove user {}: unable to find credentials", user.getId());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + USER_STR + user.getEmail() + " is not found.");
                    break;
                default:
                    log.warn("Failed to remove user {}: {}", user.getId(), exceptionState.getExceptionName());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    break;
            }
        } else {
            log.info("User {} has been removed", userId);
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, USER_PREFIX + user.getEmail() + " has been removed.");
        }

        return "redirect:/admin/users";
    }

//    @RequestMapping("/admin/experiments/remove/{expId}")
//    public String adminRemoveExp(@PathVariable Integer expId) {
//    	int teamId = experimentManager.getExperimentByExpId(expId).getTeamId();
//        experimentManager.adminRemoveExperiment(expId);
//
//        // decrease exp count to be display on Teams page
//        teamManager.decrementExperimentCount(teamId);
//    	return REDIRECT_ADMIN;
//    }

//    @RequestMapping(value="/admin/node/add", method=RequestMethod.GET)
//    public String adminAddNode(Model model) {
//    	model.addAttribute("node", new Node());
//    	return "admin_add_node";
//    }

//    @RequestMapping(value="/admin/node/add", method=RequestMethod.POST)
//    public String adminAddNode(@ModelAttribute("node") Node node) {
//    	// TODO
//    	// validate fields, eg should be integer
//    	nodeManager.addNode(node);
//    	return REDIRECT_ADMIN;
//    }

    //--------------------------Static pages for teams--------------------------
    @RequestMapping("/teams/team_application_submitted")
    public String teamAppSubmitFromTeamsPage() {
        return "team_page_application_submitted";
    }

    @RequestMapping("/teams/join_application_submitted/{teamName}")
    public String teamAppJoinFromTeamsPage(@PathVariable String teamName, Model model) throws WebServiceRuntimeException {
        log.info("Redirecting to join application submitted page");
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getTeamByName(teamName), HttpMethod.GET, request, String.class);

        String responseBody = response.getBody().toString();
        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case TEAM_NOT_FOUND_EXCEPTION:
                        log.warn("submitted join team request : team name error");
                        break;
                    default:
                        log.warn("submitted join team request : some other failure");
                        // possible sio or adapter connection fail
                        break;
                }
                return "redirect:/teams/join_team";
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        Team2 one = extractTeamInfo(responseBody);
        model.addAttribute("team", one);
        return "team_page_join_application_submitted";
    }

    //--------------------------Static pages for sign up--------------------------

    @RequestMapping("/team_application_submitted")
    public String teamAppSubmit() {
        return "team_application_submitted";
    }

    /**
     * A page to show new users has successfully registered to apply to join an existing team
     * The page contains the team owner information which the users requested to join
     *
     * @param model The model which is passed from signup
     * @return A success page otherwise an error page if the user tries to access this page directly
     */
    @RequestMapping("/join_application_submitted")
    public String joinTeamAppSubmit(Model model) {
        // model attribute should be passed from /signup2
        // team is required to display the team owner details
        if (model.containsAttribute("team")) {
            return "join_team_application_submitted";
        }
        return "error";
    }

    @RequestMapping("/email_not_validated")
    public String emailNotValidated() {
        return "email_not_validated";
    }

    @RequestMapping("/team_application_under_review")
    public String teamAppUnderReview() {
        return "team_application_under_review";
    }

    // model attribute name come from /login
    @RequestMapping("/email_checklist")
    public String emailChecklist(@ModelAttribute("statuschecklist") String status) {
        return "email_checklist";
    }

    @RequestMapping("/join_application_awaiting_approval")
    public String joinTeamAppAwaitingApproval(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute(SIGNUP_MERGED_FORM, new SignUpMergedForm());
        return "join_team_application_awaiting_approval";
    }

    //--------------------------SSH Public Keys------------------------------------------
    @RequestMapping(path = "/show_pub_keys", method = RequestMethod.GET)
    public String showPublicKeys(Model model, HttpSession session) throws WebServiceRuntimeException {
        getDeterUid(model, session);
        SortedMap<String, Map<String, String>> keysMap;

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(
                properties.getPublicKeys(session.getAttribute("id").toString()),
                HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                log.error("Unable to get public keys for user {}", session.getAttribute("id"));
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                throw new RestClientException("[" + error.getError() + "] ");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                keysMap = mapper.readValue(responseBody, new TypeReference<SortedMap<String, Map<String, String>>>() {});
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        model.addAttribute("keys", keysMap);
        return "showpubkeys";
    }

    @RequestMapping(path = "/show_pub_keys", method = RequestMethod.POST)
    public String addPublicKey(@RequestParam("keyFile") MultipartFile keyFile,
                               @RequestParam("keyPass") String keyPass,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) throws WebServiceRuntimeException {
        if (keyFile.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Please select a keyfile to upload");
            redirectAttributes.addFlashAttribute("hasKeyFileError", true);
        } else if (keyPass.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Please enter your password");
            redirectAttributes.addFlashAttribute("hasKeyPassError", true);
        } else {
            try {
                JSONObject keyInfo = new JSONObject();
                keyInfo.put("publicKey", new String(keyFile.getBytes()));
                keyInfo.put(PSWD, keyPass);
                HttpEntity<String> request = createHttpEntityWithBody(keyInfo.toString());
                restTemplate.setErrorHandler(new MyResponseErrorHandler());
                ResponseEntity response = restTemplate.exchange(
                        properties.getPublicKeys(session.getAttribute("id").toString()),
                        HttpMethod.POST, request, String.class
                );
                String responseBody = response.getBody().toString();

                if (RestUtil.isError(response.getStatusCode())) {
                    MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                    ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                    switch (exceptionState) {
                        case VERIFICATION_PASSWORD_NOT_MATCH_EXCEPTION:
                            log.error(error.getMessage());
                            redirectAttributes.addFlashAttribute(MESSAGE, "Invalid password");
                            redirectAttributes.addFlashAttribute("hasKeyPassError", true);
                            break;
                        case INVALID_PUBLIC_KEY_FILE_EXCEPTION:
                            log.error(error.getMessage());
                            redirectAttributes.addFlashAttribute(MESSAGE, "Invalid key file");
                            break;
                        case INVALID_PUBLIC_KEY_FORMAT_EXCEPTION:
                            log.error(error.getMessage());
                            redirectAttributes.addFlashAttribute(MESSAGE, "Invalid key format");
                            break;
                        case FORBIDDEN_EXCEPTION:
                            log.error(error.getMessage());
                            redirectAttributes.addFlashAttribute(MESSAGE, "Adding of public key is forbidden");
                            break;
                        default:
                            log.error("Unknown error when adding public key");
                            redirectAttributes.addFlashAttribute(MESSAGE, "Unknown error when adding public key");
                    }
                }
            } catch (IOException e) {
                throw new WebServiceRuntimeException(e.getMessage());
            }
        }

        return "redirect:/show_pub_keys";
    }

    @RequestMapping(path = "/delete_pub_key/{keyId}", method = RequestMethod.GET)
    public String deletePublicKey(HttpSession session, @PathVariable String keyId) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(
                properties.getPublicKeys(session.getAttribute("id").toString()) + "/" + keyId,
                HttpMethod.DELETE, request, String.class);
        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                log.error("Unable to delete public key {} for user {}", keyId, session.getAttribute("id"));
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                throw new RestClientException("[" + error.getError() + "] ");
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return "redirect:/show_pub_keys";
    }

    //--------------------------Get List of scenarios filenames--------------------------
    private List<String> getScenarioFileNameList() {
        log.info("Retrieving scenario file names");
        // FIXME: hardcode list of filenames for now
        List<String> scenarioFileNameList = new ArrayList<>();
        scenarioFileNameList.add("Scenario 1 - Experiment with a single node");
        scenarioFileNameList.add("Scenario 2 - Experiment with 2 nodes and 10Gb link");
        scenarioFileNameList.add("Scenario 3 - Experiment with 3 nodes in a LAN");
        scenarioFileNameList.add("Scenario 4 - Experiment with 2 nodes and customized link property");
        scenarioFileNameList.add("Scenario 5 - Single SDN switch connected to two nodes");
        scenarioFileNameList.add("Scenario 6 - Tree Topology with configurable SDN switches");
        log.info("Scenario file list: {}", scenarioFileNameList);
        return scenarioFileNameList;
    }

    private String getScenarioContentsFromFile(String scenarioFileName) throws WebServiceRuntimeException {
        // FIXME: switch to better way of referencing scenario descriptions to actual filenames
        String actualScenarioFileName;
        if (scenarioFileName.contains("Scenario 1")) {
            actualScenarioFileName = "basic1.ns";
        } else if (scenarioFileName.contains("Scenario 2")) {
            actualScenarioFileName = "basic2.ns";
        } else if (scenarioFileName.contains("Scenario 3")) {
            actualScenarioFileName = "basic3.ns";
        } else if (scenarioFileName.contains("Scenario 4")) {
            actualScenarioFileName = "basic4.ns";
        } else if (scenarioFileName.contains("Scenario 5")) {
            actualScenarioFileName = "basic5.ns";
        } else if (scenarioFileName.contains("Scenario 6")) {
            actualScenarioFileName = "basic6.ns";
        } else {
            // defaults to basic single node
            actualScenarioFileName = "basic1.ns";
        }

        try {
            log.info("Retrieving scenario files {}", getClass().getClassLoader().getResourceAsStream("scenarios/" + actualScenarioFileName));
            List<String> lines = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("scenarios/" + actualScenarioFileName), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
            }
            //log.info("Experiment ns file contents: {}", sb);
            return sb.toString();
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }


    //---Check if user is a team owner and has any join request waiting for approval----
    private boolean hasAnyJoinRequest(HashMap<Integer, Team> teamMapOwnedByUser) {
        for (Map.Entry<Integer, Team> entry : teamMapOwnedByUser.entrySet()) {
            Team currTeam = entry.getValue();
            if (currTeam.isUserJoinRequestEmpty() == false) {
                // at least one team has join user request
                return true;
            }
        }

        // loop through all teams but never return a single true
        // therefore, user's controlled teams has no join request
        return false;
    }

    //--------------------------MISC--------------------------
    private int getSessionIdOfLoggedInUser(HttpSession session) {
        return Integer.parseInt(session.getAttribute(SESSION_LOGGED_IN_USER_ID).toString());
    }

    private User2 extractUserInfo(String userJson) {
        User2 user2 = new User2();
        if (userJson == null) {
            // return empty user
            return user2;
        }

        JSONObject object = new JSONObject(userJson);
        JSONObject userDetails = object.getJSONObject(USER_DETAILS);
        JSONObject address = userDetails.getJSONObject(ADDRESS);

        user2.setId(object.getString("id"));
        user2.setFirstName(getJSONStr(userDetails.getString(FNAME)));
        user2.setLastName(getJSONStr(userDetails.getString(LNAME)));
        user2.setJobTitle(userDetails.getString(JOB_TITLE));
        user2.setEmail(userDetails.getString(EMAIL));
        user2.setPhone(userDetails.getString(PHONE));
        user2.setAddress1(address.getString(ADDRESS1));
        user2.setAddress2(address.getString(ADDRESS2));
        user2.setCountry(address.getString(COUNTRY));
        user2.setRegion(address.getString(REGION));
        user2.setPostalCode(address.getString(ZIP_CODE));
        user2.setCity(address.getString(CITY));
        user2.setInstitution(userDetails.getString(INSTITUTION));
        user2.setInstitutionAbbreviation(userDetails.getString(INSTITUTION_ABBREVIATION));
        user2.setInstitutionWeb(userDetails.getString(INSTITUTION_WEB));

        user2.setStatus(object.getString(STATUS));
        user2.setEmailVerified(object.getBoolean("emailVerified"));

        // applicationDate is ZonedDateTime
        try {
            user2.setApplicationDate(object.get(APPLICATION_DATE).toString());
        } catch (Exception e) {
            // since applicationDate date is a ZonedDateTime and not String
            // set to '?' at the html page
            log.warn("Error getting user application date {}", e);
        }

        return user2;
    }

    private Team2 extractTeamInfo(String json) {
        Team2 team2 = new Team2();
        JSONObject object = new JSONObject(json);
        JSONArray membersArray = object.getJSONArray(MEMBERS);

        // createdDate is ZonedDateTime
        // processedDate is ZonedDateTime
        try {
            team2.setApplicationDate(object.get(APPLICATION_DATE).toString());
            team2.setProcessedDate(object.get("processedDate").toString());
        } catch (Exception e) {
            log.warn("Error getting team application date and/or processedDate {}", e);

            // created date is a ZonedDateTime
            // since created date and proccessed date is a ZonedDateTime and not String
            // both is set to '?' at the html page if exception
        }

        team2.setIsClass(object.getBoolean(IS_CLASS));
        team2.setId(object.getString("id"));
        team2.setName(object.getString("name"));
        team2.setDescription(object.getString(DESCRIPTION));
        team2.setWebsite(object.getString(WEBSITE));
        team2.setOrganisationType(object.getString(ORGANISATION_TYPE));
        team2.setStatus(object.getString(STATUS));
        team2.setVisibility(object.getString(VISIBILITY));

        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            String userId = memberObject.getString(USER_ID);
            String teamMemberType = memberObject.getString(MEMBER_TYPE);
            String teamMemberStatus = memberObject.getString(MEMBER_STATUS);

            User2 myUser = invokeAndExtractUserInfo(userId);
            if (teamMemberType.equals(MemberType.MEMBER.name())) {

                // add to pending members list for Members Awaiting Approval function
                if (teamMemberStatus.equals(MemberStatus.PENDING.name())) {
                    team2.addPendingMembers(myUser);
                }

            } else if (teamMemberType.equals(MemberType.OWNER.name())) {
                // explicit safer check
                team2.setOwner(myUser);
            }
            team2.addMembersToStatusMap(MemberStatus.valueOf(teamMemberStatus), myUser);
        }
        team2.setMembersCount(team2.getMembersStatusMap().get(MemberStatus.APPROVED).size());
        return team2;
    }

    // use to extract JSON Strings from services
    // in the case where the JSON Strings are null, return "Connection Error"
    private String getJSONStr(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return CONNECTION_ERROR;
        }
        return jsonString;
    }

    /**
     * Checks if user is pending for join request approval from team leader
     * Use for fixing bug for view experiment page where users previously can view the experiments just by issuing a join request
     *
     * @param json        the response body after calling team service
     * @param loginUserId the current logged in user id
     * @return True if the user is anything but APPROVED, false otherwise
     */
    private boolean isMemberJoinRequestPending(String loginUserId, String json) {
        if (json == null) {
            return true;
        }

        JSONObject object = new JSONObject(json);
        JSONArray membersArray = object.getJSONArray(MEMBERS);

        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            String userId = memberObject.getString(USER_ID);
            String teamMemberStatus = memberObject.getString(MEMBER_STATUS);

            if (userId.equals(loginUserId) && !teamMemberStatus.equals(MemberStatus.APPROVED.toString())) {
                return true;
            }
        }
        //log.info("User: {} is viewing experiment page", loginUserId);
        return false;
    }

    private Team2 extractTeamInfoUserJoinRequest(String userId, String json) {
        Team2 team2 = new Team2();
        JSONObject object = new JSONObject(json);
        JSONArray membersArray = object.getJSONArray(MEMBERS);

        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            String uid = memberObject.getString(USER_ID);
            String teamMemberStatus = memberObject.getString(MEMBER_STATUS);
            if (uid.equals(userId) && teamMemberStatus.equals(MemberStatus.PENDING.toString())) {

                team2.setId(object.getString("id"));
                team2.setName(object.getString("name"));
                team2.setDescription(object.getString(DESCRIPTION));
                team2.setWebsite(object.getString(WEBSITE));
                team2.setOrganisationType(object.getString(ORGANISATION_TYPE));
                team2.setStatus(object.getString(STATUS));
                team2.setVisibility(object.getString(VISIBILITY));
                team2.setMembersCount(membersArray.length());

                return team2;
            }
        }

        // no such member in the team found
        return null;
    }

    protected Dataset invokeAndExtractDataInfo(Long dataId) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getDataset(dataId.toString()), HttpMethod.GET, request, String.class);
        return extractDataInfo(response.getBody().toString());
    }

    protected Dataset extractDataInfo(String json) {
        log.debug(json);

        JSONObject object = new JSONObject(json);
        Dataset dataset = new Dataset();

        dataset.setId(object.getInt("id"));
        dataset.setName(object.getString("name"));
        dataset.setDescription(object.getString(DESCRIPTION));
        dataset.setContributorId(object.getString("contributorId"));
        dataset.addVisibility(object.getString(VISIBILITY));
        dataset.addAccessibility(object.getString("accessibility"));
        try {
            dataset.setReleasedDate(getZonedDateTime(object.get("releasedDate").toString()));
        } catch (IOException e) {
            log.warn("Error getting released date {}", e);
            dataset.setReleasedDate(null);
        }
        dataset.setCategoryId(object.getInt("categoryId"));
        dataset.setLicenseId(object.getInt("licenseId"));

        dataset.setContributor(invokeAndExtractUserInfo(dataset.getContributorId()));
        dataset.setCategory(invokeAndExtractCategoryInfo(dataset.getCategoryId()));
        dataset.setLicense(invokeAndExtractLicenseInfo(dataset.getLicenseId()));

        JSONArray resources = object.getJSONArray("resources");
        for (int i = 0; i < resources.length(); i++) {
            JSONObject resource = resources.getJSONObject(i);
            DataResource dataResource = new DataResource();
            dataResource.setId(resource.getLong("id"));
            dataResource.setUri(resource.getString("uri"));
            dataResource.setMalicious(resource.getBoolean("malicious"));
            dataResource.setScanned(resource.getBoolean("scanned"));
            dataset.addResource(dataResource);
        }

        JSONArray approvedUsers = object.getJSONArray("approvedUsers");
        for (int i = 0; i < approvedUsers.length(); i++) {
            dataset.addApprovedUser(approvedUsers.getString(i));
        }

        JSONArray keywords = object.getJSONArray("keywords");
        List<String> keywordList = new ArrayList<>();
        for (int i = 0; i < keywords.length(); i++) {
            keywordList.add(keywords.getString(i));
        }
        dataset.setKeywordList(keywordList);

        return dataset;
    }

    protected DataCategory extractCategoryInfo(String json) {
        log.debug(json);

        DataCategory dataCategory = new DataCategory();
        JSONObject object = new JSONObject(json);

        dataCategory.setId(object.getLong("id"));
        dataCategory.setName(object.getString("name"));
        dataCategory.setDescription(object.getString(DESCRIPTION));

        return dataCategory;
    }

    protected DataLicense extractLicenseInfo(String json) {
        log.debug(json);

        DataLicense dataLicense = new DataLicense();
        JSONObject object = new JSONObject(json);

        dataLicense.setId(object.getLong("id"));
        dataLicense.setName(object.getString("name"));
        dataLicense.setAcronym(object.getString("acronym"));
        dataLicense.setDescription(object.getString(DESCRIPTION));
        dataLicense.setLink(object.getString("link"));

        return dataLicense;
    }

    protected DataCategory invokeAndExtractCategoryInfo(Integer categoryId) {
        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.getCategory(categoryId), HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            log.warn("Data service not available to retrieve Category: {}", categoryId);
            return new DataCategory();
        }

        return extractCategoryInfo(response.getBody().toString());
    }

    protected DataLicense invokeAndExtractLicenseInfo(Integer licenseId) {
        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.getLicense(licenseId), HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            log.warn("Data service not available to retrieve License: {}", licenseId);
            return new DataLicense();
        }
        return extractLicenseInfo(response.getBody().toString());
    }

    protected User2 invokeAndExtractUserInfo(String userId) {
        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.getUser(userId), HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            log.warn("User service not available to retrieve User: {}", userId);
            return new User2();
        }

        return extractUserInfo(response.getBody().toString());
    }

    private Team2 invokeAndExtractTeamInfo(String teamId) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity responseEntity = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, request, String.class);

        return extractTeamInfo(responseEntity.getBody().toString());
    }

    private Experiment2 extractExperiment(String experimentJson) {
        Experiment2 experiment2 = new Experiment2();
        JSONObject object = new JSONObject(experimentJson);

        experiment2.setId(object.getLong(ID));
        experiment2.setUserId(object.getString(USER_ID));
        experiment2.setTeamId(object.getString(TEAM_ID));
        experiment2.setTeamName(object.getString(TEAM_NAME));
        experiment2.setName(object.getString("name"));
        experiment2.setDescription(object.getString(DESCRIPTION));
        experiment2.setNsFile(object.getString("nsFile"));
        experiment2.setNsFileContent(object.getString("nsFileContent"));
        experiment2.setIdleSwap(object.getInt("idleSwap"));
        experiment2.setMaxDuration(object.getInt(MAX_DURATION));

        try {
            experiment2.setCreatedDate(object.get(CREATED_DATE).toString());
        } catch (Exception e) {
            experiment2.setCreatedDate("");
        }

        try {
            experiment2.setLastModifiedDate(object.get(LAST_MODIFIED_DATE).toString());
        } catch (Exception e) {
            experiment2.setLastModifiedDate("");
        }

        return experiment2;
    }

    private Realization invokeAndExtractRealization(String teamName, Long id) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = null;

        try {
            log.info("retrieving the latest exp status: {}", properties.getRealizationByTeam(teamName, id.toString()));
            response = restTemplate.exchange(properties.getRealizationByTeam(teamName, id.toString()), HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            return getCleanRealization();
        }
        String responseBody;

        if (response.getBody() == null) {
            return getCleanRealization();
        } else {
            responseBody = response.getBody().toString();
        }

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                log.warn("error in retrieving realization for team: {}, realization: {}", teamName, id);
                return getCleanRealization();
            } else {
                // will throw JSONException if the format return by sio is not a valid JSOn format
                // will occur if the realization details are still in the old format
                return extractRealization(responseBody);
            }
        } catch (IOException | JSONException e) {
            return getCleanRealization();
        }
    }

    private Realization extractRealization(String json) {
        log.info("extracting realization: {}", json);
        Realization realization = new Realization();
        JSONObject object = new JSONObject(json);

        realization.setExperimentId(object.getLong("experimentId"));
        realization.setExperimentName(object.getString("experimentName"));
        realization.setUserId(object.getString(USER_ID));
        realization.setTeamId(object.getString(TEAM_ID));
        realization.setState(object.getString("state"));

        String exp_report = "";
        Object expDetailsObject = object.get("details");

        log.info("exp detail object: {}", expDetailsObject);

        if (expDetailsObject == JSONObject.NULL || expDetailsObject.toString().isEmpty()) {
            log.info("set details empty");
            realization.setDetails("");
            realization.setNumberOfNodes(0);
        } else {
            log.info("exp report to string: {}", expDetailsObject.toString());
            exp_report = expDetailsObject.toString();
            realization.setDetails(exp_report);

            JSONObject nodesInfoObject = new JSONObject(expDetailsObject.toString());

            for (Object key : nodesInfoObject.keySet()) {
                Map<String, String> nodeDetails = new HashMap<>();
                String nodeName = (String) key;
                JSONObject nodeDetailsJson = new JSONObject(nodesInfoObject.get(nodeName).toString());

                nodeDetails.put("os", getValueFromJSONKey(nodeDetailsJson, "os"));
                nodeDetails.put(QUALIFIED_NAME, getValueFromJSONKey(nodeDetailsJson, QUALIFIED_NAME));

                nodeDetails.put(NODE_ID, getValueFromJSONKey(nodeDetailsJson, NODE_ID));
                realization.addNodeDetails(nodeName, nodeDetails);
            }
            log.info("nodes info object: {}", nodesInfoObject);
            realization.setNumberOfNodes(nodesInfoObject.keySet().size());
        }

        return realization;
    }

    // gets the value that corresponds to a particular key
    // checks if a particular key in the JSONObject exists
    // returns the value if the key exists, otherwise, returns N.A.
    private String getValueFromJSONKey(JSONObject json, String key) {
        if (json.has(key)) {
            return json.get(key).toString();
        }
        return NOT_APPLICABLE;
    }

    /**
     * @param zonedDateTimeJSON JSON string
     * @return a date in the format MMM-d-yyyy
     */
    protected String formatZonedDateTime(String zonedDateTimeJSON) throws Exception {
        ZonedDateTime zonedDateTime = getZonedDateTime(zonedDateTimeJSON);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM-d-yyyy");
        return zonedDateTime.format(format);
    }

    protected ZonedDateTime getZonedDateTime(String zonedDateTimeJSON) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(zonedDateTimeJSON, ZonedDateTime.class);
    }

    /**
     * Creates a HttpEntity with a request body and header but no authorization header
     * To solve the expired jwt token
     *
     * @param jsonString The JSON request converted to string
     * @return A HttpEntity request
     * @see HttpEntity createHttpEntityHeaderOnly() for request with only header
     */
    protected HttpEntity<String> createHttpEntityWithBodyNoAuthHeader(String jsonString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(jsonString, headers);
    }

    /**
     * Creates a HttpEntity that contains only a header and empty body but no authorization header
     * To solve the expired jwt token
     *
     * @return A HttpEntity request
     * @see HttpEntity createHttpEntityWithBody() for request with both body and header
     */
    protected HttpEntity<String> createHttpEntityHeaderOnlyNoAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    /**
     * Creates a HttpEntity with a request body and header
     *
     * @param jsonString The JSON request converted to string
     * @return A HttpEntity request
     * @implNote Authorization header must be set to the JwTToken in the format [Bearer: TOKEN_ID]
     * @see HttpEntity createHttpEntityHeaderOnly() for request with only header
     */
    protected HttpEntity<String> createHttpEntityWithBody(String jsonString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, httpScopedSession.getAttribute(webProperties.getSessionJwtToken()).toString());
        return new HttpEntity<>(jsonString, headers);
    }

    /**
     * Creates a HttpEntity that contains only a header and empty body
     *
     * @return A HttpEntity request
     * @implNote Authorization header must be set to the JwTToken in the format [Bearer: TOKEN_ID]
     * @see HttpEntity createHttpEntityWithBody() for request with both body and header
     */
    protected HttpEntity<String> createHttpEntityHeaderOnly() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, httpScopedSession.getAttribute(webProperties.getSessionJwtToken()).toString());
        return new HttpEntity<>(headers);
    }

    private void setSessionVariables(HttpSession session, String loginEmail, String id, String firstName, String userRoles, String token) {
        User2 user = invokeAndExtractUserInfo(id);
        session.setAttribute(webProperties.getSessionEmail(), loginEmail);
        session.setAttribute(webProperties.getSessionUserId(), id);
        session.setAttribute(webProperties.getSessionUserFirstName(), firstName);
        session.setAttribute(webProperties.getSessionRoles(), userRoles);
        session.setAttribute(webProperties.getSessionJwtToken(), "Bearer " + token);
        log.info("Session variables - sessionLoggedEmail: {}, id: {}, name: {}, roles: {}, token: {}", loginEmail, id, user.getFirstName(), userRoles, "########");
    }

    private void removeSessionVariables(HttpSession session) {
        log.info("removing session variables: email: {}, userid: {}, user first name: {}", session.getAttribute(webProperties.getSessionEmail()), session.getAttribute(webProperties.getSessionUserId()), session.getAttribute(webProperties.getSessionUserFirstName()));
        session.removeAttribute(webProperties.getSessionEmail());
        session.removeAttribute(webProperties.getSessionUserId());
        session.removeAttribute(webProperties.getSessionUserFirstName());
        session.removeAttribute(webProperties.getSessionRoles());
        session.removeAttribute(webProperties.getSessionJwtToken());
        session.invalidate();
    }

    protected boolean validateIfAdmin(HttpSession session) {
        //log.info("User: {} is logged on as: {}", session.getAttribute(webProperties.getSessionEmail()), session.getAttribute(webProperties.getSessionRoles()));
        return session.getAttribute(webProperties.getSessionRoles()).equals(UserType.ADMIN.toString());
    }

    /**
     * Ensure that only users of the team can realize or un-realize experiment
     * A pre-condition is that the users must be approved.
     * Teams must also be approved.
     *
     * @return the main experiment page
     */
    private boolean checkPermissionRealizeExperiment(Realization realization, HttpSession session) {
        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity userRespEntity = restTemplate.exchange(properties.getUser(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);

        JSONObject object = new JSONObject(userRespEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray(TEAMS);

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            if (teamId.equals(realization.getTeamId())) {
                return true;
            }
        }
        return false;
    }

    private String getTeamStatus(String teamId) {
        Team2 team = invokeAndExtractTeamInfo(teamId);
        return team.getStatus();
    }

    private Realization getCleanRealization() {
        Realization realization = new Realization();

        realization.setExperimentId(0L);
        realization.setExperimentName("");
        realization.setUserId("");
        realization.setTeamId("");
        realization.setState(RealizationState.ERROR.toString());
        realization.setDetails("");
        realization.setNumberOfNodes(0);

        return realization;
    }

    /**
     * Computes the number of teams that the user is in and the number of running experiments to populate data for the user dashboard
     *
     * @return a map in the form teams: numberOfTeams, experiments: numberOfExperiments
     */
    private Map<String, Integer> getUserDashboardStats(String userId) {

        int numberOfApprovedTeam = 0;
        int numberOfRunningExperiments = 0;
        Map<String, Integer> userDashboardStats = new HashMap<>();

        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity userRespEntity = restTemplate.exchange(properties.getUser(userId), HttpMethod.GET, request, String.class);

        JSONObject object = new JSONObject(userRespEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray(TEAMS);

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();

            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            if (!isMemberJoinRequestPending(userId, teamResponseBody)) {

                List<StatefulExperiment> statefulExperimentList = getStatefulExperiments(teamId);
                for (int j = 0; j < statefulExperimentList.size(); j++) {
                    if ("RUNNING".equals(statefulExperimentList.get(j).getState())) {
                        numberOfRunningExperiments++;
                    }
                }

                numberOfApprovedTeam ++;
            }
        }

        userDashboardStats.put(USER_DASHBOARD_APPROVED_TEAMS, numberOfApprovedTeam);
        userDashboardStats.put(USER_DASHBOARD_RUNNING_EXPERIMENTS, numberOfRunningExperiments);

        return userDashboardStats;
    }

    private List<StatefulExperiment> getStatefulExperiments(String teamId) {
        log.info("Getting stateful experiments for team {}", teamId);
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity respEntity;
        try {
            respEntity = restTemplate.exchange(properties.getStatefulExperimentsByTeam(teamId), HttpMethod.GET, request, String.class);
        } catch (RestClientException e) {
            log.warn("Connection to sio failed: {}", e);
            return new ArrayList<>();
        }

        String result = respEntity.getBody().toString();
        // log.info(result);
        if (result.isEmpty() || "[]".equals(result)) {
            return new ArrayList<>();
        }

        List<StatefulExperiment> stateExpList = new ArrayList<>();
        JSONArray stateExpsArray = new JSONArray(result);
        for (int i = 0; i < stateExpsArray.length(); i++) {
            stateExpList.add(extractStatefulExperiment(stateExpsArray.getJSONObject(i).toString()));
        }

        return stateExpList;
    }

    /**
     *
     * @param jsonString of the format
     *                  {
     *                   "teamId":"56bde048-c12c-47c7-a66e-170d2f4708d8",
     *                   "teamName":"ncltest01",
     *                   "id":498,
     *                   "name":"modexp",
     *                   "userId":"97a71fc5-91e9-4b64-9c50-73992b2684b5",
     *                   "description":"test modify experiment",
     *                   "createdDate":1507101706.000000000,
     *                   "lastModifiedDate":1507101706.000000000,
     *                   "state":"swapped",
     *                   "nodes":0,
     *                   "minNodes":1,
     *                   "idleHours":0,
     *                   "details":"
     *                      {"n0":
     *                         {"qualifiedName": "n0.modexp.ncltest01.staging.ncl.sg",
     *                          "os": "Ubuntu16.04.3-64-pr",
     *                          "nodeId": "pc6",
     *                          "type": "SystemX"
     *                         },
     *                       "n1":
     *                        { "qualifiedName": "n1.modexp.ncltest01.staging.ncl.sg",
     *                          "os": "Ubuntu16.04.3-64-pr",
     *                          "nodeId": "pc5",
     *                          "type": "SystemX"
     *                        }
     *                      }"
     *                   }
     * @return
     */
    private StatefulExperiment extractStatefulExperiment(String jsonString) {

        JSONObject expJsonObj = new JSONObject(jsonString);
        StatefulExperiment stateExp = new StatefulExperiment();

        stateExp.setTeamId(expJsonObj.getString(TEAM_ID));
        stateExp.setTeamName(expJsonObj.getString(TEAM_NAME));
        stateExp.setId(expJsonObj.getLong(ID));
        stateExp.setName(expJsonObj.getString(NAME));
        stateExp.setUserId(expJsonObj.getString(USER_ID));
        stateExp.setDescription(expJsonObj.getString(DESCRIPTION));
        stateExp.setCreatedDate(expJsonObj.getLong(CREATED_DATE));
        stateExp.setLastModifiedDate(expJsonObj.getLong(LAST_MODIFIED_DATE));
        stateExp.setState(expJsonObj.getString("state"));
        stateExp.setNodes(expJsonObj.getInt("nodes"));
        stateExp.setMaxDuration(expJsonObj.getInt(MAX_DURATION));
        stateExp.setMinNodes(expJsonObj.getInt("minNodes"));
        stateExp.setIdleHours(expJsonObj.getLong("idleHours"));

        String expDetailsString = expJsonObj.getString("details");
        if (null == expDetailsString || expDetailsString.isEmpty()) {
            return stateExp;
        }

        JSONObject details = new JSONObject(expDetailsString);
        if( details == JSONObject.NULL || details.toString().isEmpty()) {
            return stateExp;
        }

        for (Object key : details.keySet()) {
            String nodeName = (String) key;
            JSONObject nodeDetails = details.getJSONObject(nodeName);
            HashMap<String, String> nodeInfoMap = new HashMap<>();
            nodeInfoMap.put(QUALIFIED_NAME, nodeDetails.getString(QUALIFIED_NAME));
            nodeInfoMap.put(OS, nodeDetails.getString(OS));
            nodeInfoMap.put(NODE_ID, nodeDetails.getString(NODE_ID));

            stateExp.addNodeInfo(nodeName, nodeInfoMap);
        }

        return stateExp;
    }

    private SortedMap<String, Map<String, String>> getGlobalImages() throws IOException {
        SortedMap<String, Map<String, String>> globalImagesMap = new TreeMap<>();

        log.info("Retrieving list of global images from: {}", properties.getGlobalImages());
        try {
            HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
            ResponseEntity response = restTemplate.exchange(properties.getGlobalImages(), HttpMethod.GET, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            String json = new JSONObject(response.getBody().toString()).getString("images");
            globalImagesMap = mapper.readValue(json, new TypeReference<SortedMap<String, Map<String, String>>>() {
            });
        } catch (RestClientException e) {
            log.warn("Error connecting to service-image: {}", e);
        }
        return globalImagesMap;
    }

    private int getNodes(NodeType nodeType) {
        String nodesCount;
        log.info("Retrieving number of " + nodeType + " nodes from: {}", properties.getNodes(nodeType));
        try {
            HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
            ResponseEntity response = restTemplate.exchange(properties.getNodes(nodeType), HttpMethod.GET, request, String.class);
            JSONObject object = new JSONObject(response.getBody().toString());
            nodesCount = object.getString(nodeType.name());
        } catch (RestClientException e) {
            log.warn(ERROR_CONNECTING_TO_SERVICE_TELEMETRY, e);
            nodesCount = "0";
        }
        return Integer.parseInt(nodesCount);
    }

    private List<TeamUsageInfo> getTeamsUsageStatisticsForUser(String userId) {

        List<TeamUsageInfo> usageInfoList = new ArrayList<>();

        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity userRespEntity = restTemplate.exchange(properties.getUser(userId), HttpMethod.GET, request, String.class);
        JSONObject object = new JSONObject(userRespEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray(TEAMS);

        // get team info by team id
        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();

            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();
            Team2 team = extractTeamInfo(teamResponseBody);

            if (team.getOwner().getId().equals(userId) && !isMemberJoinRequestPending(userId, teamResponseBody)) {
                TeamUsageInfo usageInfo = new TeamUsageInfo();
                usageInfo.setId(teamId);
                usageInfo.setName(new JSONObject(teamResponseBody).getString("name"));
                usageInfo.setUsage(getUsageStatisticsByTeamId(teamId));
                usageInfoList.add(usageInfo);
            }
        }
        return usageInfoList;
    }

    private Long getTeamUsageStatistics(Team2 team2, String start, String end, HttpEntity<String> request, List<Long> usages)
            throws IOException, StartDateAfterEndDateException, WebServiceRuntimeException {
        Long usage = 0L;
        ResponseEntity responseEntity = restTemplate.exchange(properties.getUsageStat(team2.getId(), "startDate=" + start, "endDate=" + end), HttpMethod.GET, request, String.class);
        String responseBody = responseEntity.getBody().toString();
        JSONArray jsonArray = new JSONArray(responseBody);

        // handling exceptions from SIO
        if (RestUtil.isError(responseEntity.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
            if (exceptionState == START_DATE_AFTER_END_DATE_EXCEPTION) {
                log.warn("Get team usage : Start date after end date error");
                throw new StartDateAfterEndDateException(ERR_START_DATE_AFTER_END_DATE);
            } else {
                log.warn("Get team usage : sio or deterlab adapter connection error");
                throw new WebServiceRuntimeException(ERR_SERVER_OVERLOAD);
            }
        } else {
            log.info("Get team {} usage info : {}", team2.getName(), responseBody);
            for (int i = 0; i < jsonArray.length(); i++) {
                Long mins = jsonArray.getLong(i);
                usages.add(mins);
                usage += mins;
            }
        }
        return usage;
    }

    private String getUsageStatisticsByTeamId(String id) {
        log.info("Getting usage statistics for team {}", id);
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response;
        try {
            response = restTemplate.exchange(properties.getUsageStat(id), HttpMethod.GET, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio get usage statistics {}", e);
            return "?";
        }
        JSONArray jsonArray = new JSONArray(response.getBody().toString());
        Long usage = 0L;
        for (int i = 0; i < jsonArray.length(); i++) {
            usage += jsonArray.getLong(i);
        }
        return String.format("%.2f", usage.doubleValue() / 60);
    }

    private TeamQuota extractTeamQuotaInfo(String responseBody) {
        JSONObject object = new JSONObject(responseBody);
        TeamQuota teamQuota = new TeamQuota();
        Double charges = Double.parseDouble(accountingProperties.getCharges());

        // amountUsed from SIO will never be null => not checking for null value
        String usage = object.getString(KEY_USAGE);                 // getting usage in String
        BigDecimal amountUsed = new BigDecimal(usage);                //  using BigDecimal to handle currency
        amountUsed = amountUsed.multiply(new BigDecimal(charges));   // usage X charges

        //quota passed from SIO can be null , so we have to check for null value
        if (object.has(QUOTA)) {
            Object budgetObject = object.optString(QUOTA, null);
            if (budgetObject == null) {
                teamQuota.setBudget("");                  // there is placeholder here
                teamQuota.setResourcesLeft("Unlimited"); // not placeholder so can pass string over
            } else {
                Double budgetInDouble = object.getDouble(QUOTA);          // retrieve budget from SIO in Double
                BigDecimal budgetInBD = BigDecimal.valueOf(budgetInDouble);     // handling currency using BigDecimal

                // calculate resoucesLeft
                BigDecimal resourceLeftInBD = budgetInBD.subtract(amountUsed);
                resourceLeftInBD = resourceLeftInBD.divide(new BigDecimal(charges), 0, BigDecimal.ROUND_DOWN);
                budgetInBD = budgetInBD.setScale(2, BigDecimal.ROUND_HALF_UP);

                // set budget
                teamQuota.setBudget(budgetInBD.toString());

                //set resroucesLeft
                if (resourceLeftInBD.compareTo(BigDecimal.valueOf(0)) < 0)
                    teamQuota.setResourcesLeft("0");
                else
                    teamQuota.setResourcesLeft(resourceLeftInBD.toString());
            }
        }

        //set teamId and amountUsed
        teamQuota.setTeamId(object.getString(TEAM_ID));
        amountUsed = amountUsed.setScale(2, BigDecimal.ROUND_HALF_UP);
        teamQuota.setAmountUsed(amountUsed.toString());
        return teamQuota;
    }

    /**
     * Invokes the get nodes status in the telemetry service
     * @return a map containing a list of nodes status by their type
     */
    private Map<String, List<Map<String, String>>> getNodesStatus() throws IOException {
        log.info("Getting all nodes' status from: {}", properties.getNodesStatus());

        Map<String, List<Map<String, String>>> output = new HashMap<>();

        try {
            HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
            ResponseEntity response = restTemplate.exchange(properties.getNodesStatus(), HttpMethod.GET, request, String.class);
            JSONObject object = new JSONObject(response.getBody().toString());

            if (object == JSONObject.NULL || object.length() == 0) {
                return output;
            } else {
                // loop through the object as there may be more than one machine type
                for (int i = 0; i < object.names().length(); i++) {
                    // for each machine type, get all the current nodes status
                    String currentMachineType = object.names().getString(i);

                    // converts the JSON Array of the form [ { id : A, status : B, type : C } ] into a proper list of map
                    List<Map<String, String>> nodesList = objectMapper.readValue(object.getJSONArray(currentMachineType).toString(), new TypeReference<List<Map>>(){});
                    output.put(currentMachineType, nodesList);
                }
            }
        } catch (RestClientException e) {
            log.warn(ERROR_CONNECTING_TO_SERVICE_TELEMETRY, e);
            return new HashMap<>();
        }

        log.debug("Finish getting all nodes: {}", output);

        return output;
    }

    private Map<String,String> getTestbedStats() {
        Map<String, String> statsMap = new HashMap<>();

        log.info("Retrieving number of logged in users and running experiments from: {}", properties.getTestbedStats());
        try {
            HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
            ResponseEntity response = restTemplate.exchange(properties.getTestbedStats(), HttpMethod.GET, request, String.class);
            JSONObject object = new JSONObject(response.getBody().toString());
            statsMap.put(USER_DASHBOARD_LOGGED_IN_USERS_COUNT, object.getString("users"));
            statsMap.put(USER_DASHBOARD_RUNNING_EXPERIMENTS_COUNT, object.getString(EXPERIMENTS));

        } catch (RestClientException e) {
            log.warn(ERROR_CONNECTING_TO_SERVICE_TELEMETRY, e);
            statsMap.put(USER_DASHBOARD_LOGGED_IN_USERS_COUNT, "0");
            statsMap.put(USER_DASHBOARD_RUNNING_EXPERIMENTS_COUNT, "0");
        }
        return statsMap;
    }

    /**
     * Refactor experiment messages used for display purposes when starting, stopping and modifying experiment
     * For fixing the SonarQube duplicate errors
     * @param expName experiment name
     * @param teamName team name
     * @return a string in the form "Experiment expName in Team teamName"
     */
    private String getExperimentMessage(String expName, String teamName) {
        return "Experiment " + expName + " in team " + teamName;
    }

    @RequestMapping(value="/add_member/{teamId}", method= RequestMethod.GET)
    public String addMember(@PathVariable String teamId, Model model) {
        model.addAttribute("addMemberForm", new AddMemberForm());
        return "add_member";
    }

    /**
     * For a class project leader to add students to his class using their emails
     *
     * @param teamId
     * @param addMemberForm
     * @param redirectAttributes
     * @return
     * @throws WebServiceRuntimeException
     */
    @RequestMapping(value="/add_member/{teamId}", method= RequestMethod.POST)
    public String addMember(@PathVariable String teamId, @Valid AddMemberForm addMemberForm,
                            final RedirectAttributes redirectAttributes)  throws WebServiceRuntimeException {

        log.info("Adding members to team {}", teamId);

        String[] emails = addMemberForm.getEmails().split("\\r?\\n");

        for (int i = 0; i < emails.length; i++){
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(emails[i]).matches()) {
                redirectAttributes.addFlashAttribute(MESSAGE, EMAIL_ADDRESS_IS_NOT_VALID);
                return REDIRECT_ADD_MEMBER + "/" + teamId;
            }
        }

        HttpEntity<String> request = createHttpEntityWithBody(addMemberForm.getEmails());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity responseEntity;

        try {
            responseEntity = restTemplate.exchange(properties.addStudentsByEmail(teamId), HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio team service for adding members: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return REDIRECT_ADD_MEMBER + "/" + teamId;
        }

        if (RestUtil.isError(responseEntity.getStatusCode())) {
            String responseBody = responseEntity.getBody().toString();
            String logPrefix = "Error in adding members to team " + teamId + ": {}";
            MyErrorResource error;
            String reason;
            try {
                error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                switch (exceptionState) {
                    case TEAM_NOT_FOUND_EXCEPTION:
                        reason = "Team " + teamId + " is not found.";
                        log.warn(logPrefix, reason);
                        redirectAttributes.addFlashAttribute(MESSAGE, reason);
                        break;
                    case DETERLAB_OPERATION_FAILED_EXCEPTION:
                        log.warn(logPrefix, error.getMessage());
                        redirectAttributes.addFlashAttribute(MESSAGE, error.getMessage());
                        break;
                    case USERNAME_ALREADY_EXISTS_EXCEPTION:
                        reason = USER_PREFIX + error.getMessage() + " already exists.";
                        log.warn(logPrefix, reason);
                        redirectAttributes.addFlashAttribute(MESSAGE, reason);
                        break;
                    case USER_ALREADY_IN_TEAM_EXCEPTION:
                    case TEAM_MEMBER_ALREADY_EXISTS_EXCEPTION:
                        reason = USER_PREFIX + error.getMessage() + " is already a member.";
                        log.warn(logPrefix, reason);
                        redirectAttributes.addFlashAttribute(MESSAGE, reason);
                        break;
                    default:
                        log.warn("Error in adding members to team {}: sio or deterlab adapter connection error", teamId);
                        // possible sio or adapter connection fail
                        redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                        break;
                }
                return REDIRECT_ADD_MEMBER + "/" + teamId;
            } catch (IOException e) {
                throw new WebServiceRuntimeException(e.getMessage());
            }
        }

        log.info("Adding members to team {} succeeded", teamId);
        redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "New members have been added successfully!");
        return REDIRECT_ADD_MEMBER + "/" + teamId;
    }


    // when student clicks password reset link in the email
    @GetMapping(path = "/changePasswordStudent", params = {"uid", "key"})
    public String resetPasswordStudent(@NotNull @RequestParam("uid") final String uid,
                                       @NotNull @RequestParam("key") final String key,
                                       Model model) {
        StudentPasswordResetForm studentPasswordResetForm = new StudentPasswordResetForm();
        studentPasswordResetForm.setKey(key);
        studentPasswordResetForm.setUid(uid);

        model.addAttribute("studentPasswordResetForm", studentPasswordResetForm);
        // redirect to the page for user to enter new password
        return STUDENT_RESET_PSWD;
    }

    // send to SIO to process resetting password for new student member
    @PostMapping("/student_reset_password")
    public String processResetPasswordStudent(
            @ModelAttribute("studentPasswordResetForm") StudentPasswordResetForm studentPasswordResetForm
    ) throws WebServiceRuntimeException {

        if (studentPasswordResetForm.getFirstName().isEmpty() || studentPasswordResetForm.getLastName().isEmpty()) {
            studentPasswordResetForm.setErrMsg("First name or last name cannot be empty");
            return STUDENT_RESET_PSWD;
        }

        if (studentPasswordResetForm.getPhone().isEmpty() ||
                studentPasswordResetForm.getPhone().matches("(.*)[a-zA-Z](.*)") ||
                studentPasswordResetForm.getPhone().length() < 6) {
            studentPasswordResetForm.setErrMsg("Phone is invalid");
            return STUDENT_RESET_PSWD;
        }

        if (!studentPasswordResetForm.isPasswordOk()) {
            return STUDENT_RESET_PSWD;
        }

        JSONObject obj = new JSONObject();
        obj.put(FNAME, studentPasswordResetForm.getFirstName());
        obj.put(LNAME, studentPasswordResetForm.getLastName());
        obj.put(PHONE, studentPasswordResetForm.getPhone());
        obj.put(KEY, studentPasswordResetForm.getKey());
        obj.put(PSWD, studentPasswordResetForm.getPassword1());

        String uid = studentPasswordResetForm.getUid();

        HttpEntity<String> request =  createHttpEntityWithBodyNoAuthHeader(obj.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity responseEntity;
        try {
            responseEntity = restTemplate.exchange(properties.changePasswordStudent(uid), HttpMethod.PUT, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio for student password reset! {}", e);
            studentPasswordResetForm.setErrMsg(ERR_SERVER_OVERLOAD);
            return STUDENT_RESET_PSWD;
        }

        String responseBody = responseEntity.getBody().toString();

        if (RestUtil.isError(responseEntity.getStatusCode())) {
            MyErrorResource error;
            String logPrefix = "Error in password reset for student " + uid + ": {}";
            try {
                error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                switch (exceptionState) {
                    case DETERLAB_OPERATION_FAILED_EXCEPTION:
                        log.warn(logPrefix, error.getMessage());
                        studentPasswordResetForm.setErrMsg(error.getMessage());
                        break;

                    case PASSWORD_RESET_REQUEST_TIMEOUT_EXCEPTION:
                        log.warn(logPrefix, "Password reset key timeout");
                        studentPasswordResetForm.setErrMsg("Password reset request timed out. Please request a new reset email.");
                        break;

                    case INVALID_USERNAME_EXCEPTION:
                        log.warn(logPrefix, "Invalid username");
                        studentPasswordResetForm.setErrMsg("You must enter a valid first and last name.");
                        break;

                    case INVALID_PASSWORD_EXCEPTION:
                        log.warn(logPrefix, "Password is invalid");
                        studentPasswordResetForm.setErrMsg("You must supply a valid password.");
                        break;

                    case PASSWORD_RESET_REQUEST_NOT_MATCH_EXCEPTION:
                        log.warn(logPrefix, "Uid and key do not match");
                        studentPasswordResetForm.setErrMsg("Password reset request does not match with the user.");
                        break;

                    case CREDENTIALS_NOT_FOUND_EXCEPTION:
                        log.warn(logPrefix, "Credentials or key not found");
                        studentPasswordResetForm.setErrMsg("Credentials or key is not valid.");
                        break;

                    default:
                        log.warn(logPrefix, "Sio or deterlab adapter connection error");
                        studentPasswordResetForm.setErrMsg(ERR_SERVER_OVERLOAD);
                }

                return STUDENT_RESET_PSWD;
            } catch (IOException e) {
                throw new WebServiceRuntimeException(e.getMessage());
            }
        }

        log.info("Password was reset for student {}", uid);
        studentPasswordResetForm.setSuccessMsg("Password has been reset");
        return "password_reset_success";
    }



    // when class member clicks password reset key link in the email
    @GetMapping(path = "/resetKeyStudent", params = {"uid"})
    public String resetKeyStudent(@NotNull @RequestParam("uid") final String uid) {

        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity responseEntity;
        try {
            responseEntity = restTemplate.exchange(properties.resetKeyStudent(uid), HttpMethod.PUT, request, String.class);
        } catch (RestClientException e) {
            // CredentialsNotFoundException and PasswordResetRequestNotFoundException is not caught here
            log.warn("Error in password key reset: {}", e);
            return "error";
        }

        if (RestUtil.isError(responseEntity.getStatusCode())) {
            log.warn("Error in password key reset: {}", responseEntity.getBody().toString());
            return "student_reset_key_error";
        }

        log.info("Password key was reset for {}", uid);
        return "student_reset_key_success";
    }
}
