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
import javax.validation.constraints.Null;
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
    private static final String OS_TOKEN = "OS_Token";
    private static final String SESSION_LOGGED_IN_USER_ID = "loggedInUserId";

    private TeamManager teamManager = TeamManager.getInstance();

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
    private static final String USER_DASHBOARD_TOTAL_EXPERIMENTS_COUNTS = "totalNumberOfExperiments";
    private static final String USER_DASHBOARD_FREE_NODES = "freeNodes";
    private static final String USER_DASHBOARD_TOTAL_NODES = "totalNodes";
    private static final String USER_DASHBOARD_GLOBAL_IMAGES = "globalImagesMap";
    private static final String USER_DASHBOARD_LOGGED_IN_USERS_COUNT = "loggedInUsersCount";
    private static final String USER_DASHBOARD_RUNNING_EXPERIMENTS_COUNT = "runningExperimentsCount";

    private static final String DETER_UID = "deterUid";

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");

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
    private static final String REDIRECT_TEAMS = "redirect:/teams";
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
    private static final String PLATFORM = "platform";
    private static final String NSFILE = "nsFile";
    private static final String IDLESWAP = "idleSwap";
    private static final String NSFILECONTENT = "nsFileContent";
    private static final String PROJECT_ID = "projectId";
    private static final String USERS = "/users/";
    private static final String STATE = "state";
    private static final String SUBMITTED = " submitted";


    private static final String START_DATE = "startDate";
    private static final String START_DATE_EQUALS = "startDate=";
    private static final String END_DATE = "endDate";
    private static final String END_DATE_EQUALS = "endDate=";

    private static final String LOG_IOEXCEPTION = "IOException {}";
    private static final String CSRF_TOKEN = "csrfToken";

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
    private static final String PROJECTS_LIST = "projectsList";

    private static final String ADMIN_MONTHLY_USAGE_CONTRIBUTE = "admin_monthly_usage_contribute";
    private static final String ADMIN_MONTHLY_CONTRIBUTE = "admin_monthly_contribute";

    private static final String HTTP = "http://";
    private static final String START = "start";
    private static final String NS_TEXT = "nsText";
    private static final String LOG_TEXT = "logText";
    private static final String PROJECT_USAGE = "projectUsages";


    @ModelAttribute
    public void setResponseHeader(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-Content-Type-Options", "nosniff");
        //response.setHeader("Content-Security-Policy", "script-src 'self' google-analytics.com/ cdn.datatables.net/1.10.12/js/ 'unsafe-inline' 'unsafe-eval'");
        response.setHeader("Strict-Transport-Security", "max-age=16070400; includeSubDomains");
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
    protected NetworkToolProperties networkToolProperties;

    @RequestMapping("/")
    public String index() {

        return "index";
    }

    @RequestMapping("/services_tools")
    public String servicesTools() {
        return "services_tools";
    }

    /***** Start Commemt for New UI Design ****/

    @RequestMapping("/cue")
    public String clusterUserEmulatorSystem(Model model) throws IOException {
        //model.addAttribute(USER_DASHBOARD_GLOBAL_IMAGES, getGlobalImages());
        return "cue";
    }

    @RequestMapping("/ctf")
    public String capturetheflag(Model model) throws IOException {
        //model.addAttribute(USER_DASHBOARD_GLOBAL_IMAGES, getGlobalImages());
        return "ctf";
    }

    @RequestMapping("/people")
    public String people() {
        return "people";
    }

    @RequestMapping("/pricing")
    public String pricing() {
        return "pricing";
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
//        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @RequestMapping("/nclpublications")
    public String publications() {
        return "ncl_publications";
    }


    @RequestMapping("/nclnewsarticles")
    public String newsarticles() {
        return "ncl_newsarticles";
    }

    @RequestMapping("/ncltestbedpublications")
    public String otherPublication() {
        return "ncl_testbedpublications";
    }

    @RequestMapping("/infraserver")
    public String infraServerInformation(Model model) throws IOException {
        return "infraserver";
    }
}

