package sg.ncl;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import sg.ncl.domain.*;
import sg.ncl.exceptions.*;
import sg.ncl.testbed_interface.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

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

    // error messages
    private static final String ERR_SERVER_OVERLOAD = "There is a problem with your request. Please contact " + CONTACT_EMAIL;
    private static final String CONNECTION_ERROR = "Connection Error";
    private final String permissionDeniedMessage = "Permission denied. If the error persists, please contact " + CONTACT_EMAIL;

    // for user dashboard hashmap key values
    private static final String USER_DASHBOARD_TEAMS = "teams";
    private static final String USER_DASHBOARD_RUNNING_EXPERIMENTS = "runningExperiments";
    private static final String USER_DASHBOARD_FREE_NODES = "freeNodes";
    private static final String USER_DASHBOARD_TOTAL_NODES = "totalNodes";
    private static final String USER_DASHBOARD_GLOBAL_IMAGES = "globalImagesMap";

    private static final String DETER_UID = "deterUid";

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");

    private static final String FORGET_PSWD_PAGE = "password_reset_email";
    private static final String FORGET_PSWD_NEW_PSWD_PAGE = "password_reset_new_password";
    private static final String NO_PERMISSION_PAGE = "nopermission";

    private static final String TEAM_NAME = "teamName";
    private static final String NODE_ID = "nodeId";
    private static final String PERMISSION_DENIED = "Permission denied";
    private static final String TEAM_NOT_FOUND = "Team not found";
    private static final String NOTES = "notes";

    // remove members from team profile; to display the list of experiments created by user
    private static final String REMOVE_MEMBER_UID = "removeMemberUid";
    private static final String REMOVE_MEMBER_NAME = "removeMemberName";

    private static final String MEMBER_TYPE = "memberType";

    @Autowired
    protected RestTemplate restTemplate;

    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    protected ConnectionProperties properties;

    @Inject
    protected WebProperties webProperties;

    @Inject
    protected HttpSession httpScopedSession;

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

//    @RequestMapping("/futureplan")
//    public String futureplan() {
//        return "futureplan";
//    }

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

    @RequestMapping("/calendar")
    public String calendar() {
        return "calendar";
    }


    @RequestMapping("/tools")
    public String tools() {
        return "tools";
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

//    @RequestMapping("/admin2")
//    public String admin2() {
//        return "admin2";
//    }

    @RequestMapping("/tutorials")
    public String tutorials() {
        return "tutorials";
    }

    @RequestMapping("/maintainance")
    public String maintainance() {
        return "maintainance";
    }

    @RequestMapping("/testbedInformation")
    public String testbedInformation(Model model) throws IOException {
        model.addAttribute(USER_DASHBOARD_GLOBAL_IMAGES, getGlobalImages());
        model.addAttribute(USER_DASHBOARD_TOTAL_NODES, getNodes(NodeType.TOTAL));
        return "testbedInformation";
    }


//    @RequestMapping(value="/futureplan/download", method=RequestMethod.GET)
//    public void futureplanDownload(HttpServletResponse response) throws FuturePlanDownloadException, IOException {
//        InputStream stream = null;
//        response.setContentType("application/pdf");
//        try {
//            stream = getClass().getClassLoader().getResourceAsStream("downloads/future_plan.pdf");
//            response.setContentType("application/force-download");
//            response.setHeader("Content-Disposition", "attachment; filename=future_plan.pdf");
//            IOUtils.copy(stream, response.getOutputStream());
//            response.flushBuffer();
//        } catch (Exception ex) {
//            logger.info("Error writing file to output stream.");
//            throw new FuturePlanDownloadException("IOError writing file to output stream");
//        } finally {
//            if (stream != null) {
//                stream.close();
//            }
//        }
//    }

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
        if (session.getAttribute("id") != null && !session.getAttribute("id").toString().isEmpty()) {
            // user is already logged on and has encountered an error
            // redirect to dashboard
            return "redirect:/dashboard";
        } else {
            // user have not logged on before
            // redirect to home page
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @RequestMapping(value = "/emailVerification", params = {"id", "email", "key"})
    public String verifyEmail(
            @NotNull @RequestParam("id") final String id,
            @NotNull @RequestParam("email") final String emailBase64,
            @NotNull @RequestParam("key") final String key
    ) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode keyObject = objectMapper.createObjectNode();
        keyObject.put("key", key);

        HttpEntity<String> request = new HttpEntity<>(keyObject.toString(), headers);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        final String link = properties.getSioUsersUrl() + id + "/emails/" + emailBase64;
        log.info("Activation link: {}, verification key {}", link, key);
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
            loginForm.setErrorMsg("Login failed: Invalid email/password.");
            return "login";
        }

        String inputEmail = loginForm.getLoginEmail();
        String inputPwd = loginForm.getLoginPassword();
        if (inputEmail.trim().isEmpty() || inputPwd.trim().isEmpty()) {
            loginForm.setErrorMsg("Email or Password cannot be empty!");
            return "login";
        }

        String plainCreds = inputEmail + ":" + inputPwd;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        ResponseEntity response;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Creds);

        HttpEntity<String> request = new HttpEntity<>(headers);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        try {
            response = restTemplate.exchange(properties.getSioAuthUrl(), HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio authentication service: {}", e);
            loginForm.setErrorMsg(ERR_SERVER_OVERLOAD);
            return "login";
        }

        String jwtTokenString = response.getBody().toString();
        log.info("token string {}", jwtTokenString);
        if (jwtTokenString == null || jwtTokenString.isEmpty()) {
            log.warn("login failed for {}: unknown response code", loginForm.getLoginEmail());
            loginForm.setErrorMsg("Login failed: Invalid email/password.");
            return "login";
        }
        if (RestUtil.isError(response.getStatusCode())) {
            try {
                MyErrorResource error = objectMapper.readValue(jwtTokenString, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == ExceptionState.CREDENTIALS_NOT_FOUND_EXCEPTION) {
                    log.warn("login failed for {}: credentials not found", loginForm.getLoginEmail());
                    loginForm.setErrorMsg("Login failed: Account does not exist. Please register.");
                    return "login";
                }
                log.warn("login failed for {}: {}", loginForm.getLoginEmail(), error.getError());
                loginForm.setErrorMsg("Login failed: Invalid email/password.");
                return "login";
            } catch (IOException ioe) {
                log.warn("IOException {}", ioe);
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
            loginForm.setErrorMsg("Login failed: Invalid email/password.");
            return "login";
        }

        // now check user status to decide what to show to the user
        User2 user = invokeAndExtractUserInfo(id);

        try {
            String userStatus = user.getStatus();
            boolean emailVerified = user.getEmailVerified();

            if (UserStatus.FROZEN.toString().equals(userStatus)) {
                log.warn("User {} login failed: account has been frozen", id);
                loginForm.setErrorMsg("Login Failed: Account Frozen. Please contact " + CONTACT_EMAIL);
                return "login";
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
                return "login";
            }
        } catch (Exception e) {
            log.warn("Error parsing json object for user: {}", e.getMessage());
            loginForm.setErrorMsg(ERR_SERVER_OVERLOAD);
            return "login";
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
        return "dashboard";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        removeSessionVariables(session);
        return "redirect:/";
    }

    //--------------------------Sign Up Page--------------------------

    @RequestMapping(value = "/signup2", method = RequestMethod.GET)
    public String signup2(Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            log.debug((String) inputFlashMap.get(MESSAGE));
            model.addAttribute("signUpMergedForm", (SignUpMergedForm) inputFlashMap.get("signUpMergedForm"));
        } else {
            log.debug("InputFlashMap is null");
            model.addAttribute("signUpMergedForm", new SignUpMergedForm());
        }
        return "signup2";
    }

    @RequestMapping(value = "/signup2", method = RequestMethod.POST)
    public String validateDetails(
            @Valid
            @ModelAttribute("signUpMergedForm") SignUpMergedForm signUpMergedForm,
            BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        if (bindingResult.hasErrors() || signUpMergedForm.getIsValid() == false) {
            log.warn("Register form has errors {}", signUpMergedForm.toString());
            return "signup2";
        }

        if (!signUpMergedForm.getHasAcceptTeamOwnerPolicy()) {
            signUpMergedForm.setErrorTeamOwnerPolicy("Please accept the team owner policy");
            log.warn("Policy not accepted");
            return "signup2";
        }

        // get form fields
        // craft the registration json
        JSONObject mainObject = new JSONObject();
        JSONObject credentialsFields = new JSONObject();
        credentialsFields.put("username", signUpMergedForm.getEmail().trim());
        credentialsFields.put("password", signUpMergedForm.getPassword());

        // create the user JSON
        JSONObject userFields = new JSONObject();
        JSONObject userDetails = new JSONObject();
        JSONObject addressDetails = new JSONObject();

        userDetails.put("firstName", signUpMergedForm.getFirstName().trim());
        userDetails.put("lastName", signUpMergedForm.getLastName().trim());
        userDetails.put("jobTitle", signUpMergedForm.getJobTitle().trim());
        userDetails.put("email", signUpMergedForm.getEmail().trim());
        userDetails.put("phone", signUpMergedForm.getPhone().trim());
        userDetails.put("institution", signUpMergedForm.getInstitution().trim());
        userDetails.put("institutionAbbreviation", signUpMergedForm.getInstitutionAbbreviation().trim());
        userDetails.put("institutionWeb", signUpMergedForm.getWebsite().trim());
        userDetails.put("address", addressDetails);

        addressDetails.put("address1", signUpMergedForm.getAddress1().trim());
        addressDetails.put("address2", signUpMergedForm.getAddress2().trim());
        addressDetails.put("country", signUpMergedForm.getCountry().trim());
        addressDetails.put("region", signUpMergedForm.getProvince().trim());
        addressDetails.put("city", signUpMergedForm.getCity().trim());
        addressDetails.put("zipCode", signUpMergedForm.getPostalCode().trim());

        userFields.put("userDetails", userDetails);
        userFields.put("applicationDate", ZonedDateTime.now());

        JSONObject teamFields = new JSONObject();

        // add all to main json
        mainObject.put("credentials", credentialsFields);
        mainObject.put("user", userFields);
        mainObject.put("team", teamFields);
        mainObject.put(NOTES, signUpMergedForm.getJoinTeamReason());

        // check if user chose create new team or join existing team by checking team name
        String createNewTeamName = signUpMergedForm.getTeamName().trim();
        String joinNewTeamName = signUpMergedForm.getJoinTeamName().trim();


        if (createNewTeamName != null && !createNewTeamName.isEmpty()) {
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
                return "signup2";
            } else {

                teamFields.put("name", signUpMergedForm.getTeamName().trim());
                teamFields.put("description", signUpMergedForm.getTeamDescription().trim());
                teamFields.put("website", signUpMergedForm.getTeamWebsite().trim());
                teamFields.put("organisationType", signUpMergedForm.getTeamOrganizationType());
                teamFields.put("visibility", signUpMergedForm.getIsPublic());
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
                    redirectAttributes.addFlashAttribute("signUpMergedForm", signUpMergedForm);
                    return "redirect:/signup2";
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                    redirectAttributes.addFlashAttribute("signUpMergedForm", signUpMergedForm);
                    return "redirect:/signup2";
                }

                log.info("Signup new team success");
                return "redirect:/team_application_submitted";
            }

        } else if (joinNewTeamName != null && !joinNewTeamName.isEmpty()) {

            log.info("Signup join team name {}", joinNewTeamName);
            // get the team JSON from team name
            Team2 joinTeamInfo;

            try {
                joinTeamInfo = getTeamIdByName(signUpMergedForm.getJoinTeamName().trim());
            } catch (TeamNotFoundException | AdapterConnectionException e) {
                redirectAttributes.addFlashAttribute(MESSAGE, e.getMessage());
                redirectAttributes.addFlashAttribute("signUpMergedForm", signUpMergedForm);
                return "redirect:/signup2";
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
                redirectAttributes.addFlashAttribute("signUpMergedForm", signUpMergedForm);
                return "redirect:/signup2";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                redirectAttributes.addFlashAttribute("signUpMergedForm", signUpMergedForm);
                return "redirect:/signup2";
            }

            log.info("Signup join team success");
            log.info("jointeam info: {}", joinTeamInfo);
            redirectAttributes.addFlashAttribute("team", joinTeamInfo);
            return "redirect:/join_application_submitted";
        } else {
            log.warn("Signup unreachable statement");
            // logic error not suppose to reach here
            // possible if user fill up create new team but without the team name
            redirectAttributes.addFlashAttribute("signupError", "There is a problem when submitting your form. Please re-enter and submit the details again.");
            redirectAttributes.addFlashAttribute("signUpMergedForm", signUpMergedForm);
            return "redirect:/signup2";
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

                final String errorPrefix = "Error: ";

                switch (exceptionState) {
                    case DETERLAB_OPERATION_FAILED_EXCEPTION:
                        log.warn("Register new user failed on DeterLab: {}", error.getMessage());
                        throw new DeterLabOperationFailedException(errorPrefix + (error.getMessage().contains("unknown error") ? ERR_SERVER_OVERLOAD : error.getMessage()));
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
                    {
                        String email = mainObject.getJSONObject("user").getJSONObject("userDetails").getString("email");
                        log.warn("Register new users : email already exists: {}", email);
                        throw new UsernameAlreadyExistsException(errorPrefix + email + " already in use.");
                    }
                    case EMAIL_ALREADY_EXISTS_EXCEPTION:
                        // throw from adapter deterlab
                    {
                        String email = mainObject.getJSONObject("user").getJSONObject("userDetails").getString("email");
                        log.warn("Register new users : email already exists: {}", email);
                        throw new EmailAlreadyExistsException(errorPrefix + email + " already in use.");
                    }
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

        boolean errorsFound = false;
        String editPhrase = "editPhrase";

        // check fields first
        if (errorsFound == false && editUser.getFirstName().isEmpty()) {
            redirectAttributes.addFlashAttribute("editFirstName", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getLastName().isEmpty()) {
            redirectAttributes.addFlashAttribute("editLastName", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getPhone().isEmpty()) {
            redirectAttributes.addFlashAttribute("editPhone", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && (editUser.getPhone().matches("(.*)[a-zA-Z](.*)") || editUser.getPhone().length() < 6)) {
            // previously already check if phone is empty
            // now check phone must contain only digits
            redirectAttributes.addFlashAttribute("editPhone", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && !editUser.getConfirmPassword().isEmpty() && !editUser.isPasswordValid()) {
            redirectAttributes.addFlashAttribute(editPhrase, "invalid");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getJobTitle().isEmpty()) {
            redirectAttributes.addFlashAttribute("editJobTitle", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getInstitution().isEmpty()) {
            redirectAttributes.addFlashAttribute("editInstitution", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getCountry().isEmpty()) {
            redirectAttributes.addFlashAttribute("editCountry", "fail");
            errorsFound = true;
        }

        if (errorsFound) {
            session.removeAttribute(webProperties.getSessionUserAccount());
            return "redirect:/account_settings";
        } else {
            // used to compare original and edited User2 objects
            User2 originalUser = (User2) session.getAttribute(webProperties.getSessionUserAccount());

            JSONObject userObject = new JSONObject();
            JSONObject userDetails = new JSONObject();
            JSONObject address = new JSONObject();

            userDetails.put("firstName", editUser.getFirstName());
            userDetails.put("lastName", editUser.getLastName());
            userDetails.put("email", editUser.getEmail());
            userDetails.put("phone", editUser.getPhone());
            userDetails.put("jobTitle", editUser.getJobTitle());
            userDetails.put("address", address);
            userDetails.put("institution", editUser.getInstitution());
            userDetails.put("institutionAbbreviation", originalUser.getInstitutionAbbreviation());
            userDetails.put("institutionWeb", originalUser.getInstitutionWeb());

            address.put("address1", originalUser.getAddress1());
            address.put("address2", originalUser.getAddress2());
            address.put("country", editUser.getCountry());
            address.put("city", originalUser.getCity());
            address.put("region", originalUser.getRegion());
            address.put("zipCode", originalUser.getPostalCode());

            userObject.put("userDetails", userDetails);

            String userId_uri = properties.getSioUsersUrl() + session.getAttribute(webProperties.getSessionUserId());

            HttpEntity<String> request = createHttpEntityWithBody(userObject.toString());
            restTemplate.exchange(userId_uri, HttpMethod.PUT, request, String.class);

            if (!originalUser.getFirstName().equals(editUser.getFirstName())) {
                redirectAttributes.addFlashAttribute("editFirstName", "success");
            }
            if (!originalUser.getLastName().equals(editUser.getLastName())) {
                redirectAttributes.addFlashAttribute("editLastName", "success");
            }
            if (!originalUser.getPhone().equals(editUser.getPhone())) {
                redirectAttributes.addFlashAttribute("editPhone", "success");
            }
            if (!originalUser.getJobTitle().equals(editUser.getJobTitle())) {
                redirectAttributes.addFlashAttribute("editJobTitle", "success");
            }
            if (!originalUser.getInstitution().equals(editUser.getInstitution())) {
                redirectAttributes.addFlashAttribute("editInstitution", "success");
            }
            if (!originalUser.getCountry().equals(editUser.getCountry())) {
                redirectAttributes.addFlashAttribute("editCountry", "success");
            }

            // credential service change password
            if (editUser.isPasswordMatch()) {
                JSONObject credObject = new JSONObject();
                credObject.put("password", editUser.getPassword());

                HttpEntity<String> credRequest = createHttpEntityWithBody(credObject.toString());
                restTemplate.setErrorHandler(new MyResponseErrorHandler());
                ResponseEntity response = restTemplate.exchange(properties.getUpdateCredentials(session.getAttribute("id").toString()), HttpMethod.PUT, credRequest, String.class);
                String responseBody = response.getBody().toString();

                try {
                    if (RestUtil.isError(response.getStatusCode())) {
                        MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                        redirectAttributes.addFlashAttribute(editPhrase, "fail");
                    } else {
                        redirectAttributes.addFlashAttribute(editPhrase, "success");
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
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            Team2 team2 = new Team2();
            JSONObject teamObject = new JSONObject(teamResponseBody);
            JSONArray membersArray = teamObject.getJSONArray("members");

            team2.setId(teamObject.getString("id"));
            team2.setName(teamObject.getString("name"));

            boolean isTeamLeader = false;
            temp = new ArrayList<>();

            for (int j = 0; j < membersArray.length(); j++) {
                JSONObject memberObject = membersArray.getJSONObject(j);
                String userId = memberObject.getString("userId");
                String teamMemberType = memberObject.getString(MEMBER_TYPE);
                String teamMemberStatus = memberObject.getString("memberStatus");
                String teamJoinedDate = formatZonedDateTime(memberObject.get("joinedDate").toString());

                JoinRequestApproval joinRequestApproval = new JoinRequestApproval();

                if (userId.equals(session.getAttribute("id").toString()) && teamMemberType.equals(MemberType.OWNER.toString())) {
                    isTeamLeader = true;
                }

                if (teamMemberStatus.equals(MemberStatus.PENDING.toString()) && teamMemberType.equals(MemberType.MEMBER.toString())) {
                    User2 myUser = invokeAndExtractUserInfo(userId);
                    joinRequestApproval.setUserId(myUser.getId());
                    joinRequestApproval.setUserEmail(myUser.getEmail());
                    joinRequestApproval.setUserName(myUser.getFirstName() + " " + myUser.getLastName());
                    joinRequestApproval.setApplicationDate(teamJoinedDate);
                    joinRequestApproval.setTeamId(team2.getId());
                    joinRequestApproval.setTeamName(team2.getName());

                    temp.add(joinRequestApproval);
                    log.info("Join request: UserId: {}, UserEmail: {}", myUser.getId(), myUser.getEmail());
                }
            }

            if (isTeamLeader) {
                if (!temp.isEmpty()) {
                    rv.addAll(temp);
                }
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
            return "redirect:/approve_new_user";
        }

        String responseBody = response.getBody().toString();
        if (RestUtil.isError(response.getStatusCode())) {
            try {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case DETERLAB_OPERATION_FAILED_EXCEPTION:
                        log.warn("Approve join request: User {}, Team {} fail", userId, teamId);
                        redirectAttributes.addFlashAttribute(MESSAGE, "Approve join request fail");
                        break;
                    default:
                        log.warn("Server side error: {}", error.getError());
                        redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                        break;
                }
                return "redirect:/approve_new_user";
            } catch (IOException ioe) {
                log.warn("IOException {}", ioe);
                throw new WebServiceRuntimeException(ioe.getMessage());
            }
        }
        // everything looks OK?
        log.info("Join request has been APPROVED, User {}, Team {}", userId, teamId);
        redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Join request has been APPROVED.");
        return "redirect:/approve_new_user";
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
            return "redirect:/approve_new_user";
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
                return "redirect:/approve_new_user";
            } catch (IOException ioe) {
                log.warn("IOException {}", ioe);
                throw new WebServiceRuntimeException(ioe.getMessage());
            }
        }
        // everything looks OK?
        log.info("Join request has been REJECTED, User {}, Team {}", userId, teamId);
        redirectAttributes.addFlashAttribute(MESSAGE, "Join request has been REJECTED.");
        return "redirect:/approve_new_user";
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
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getUser(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        JSONObject object = new JSONObject(responseBody);
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

        String userEmail = object.getJSONObject("userDetails").getString("email");

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            Team2 team2 = extractTeamInfo(teamResponseBody);
            teamManager2.addTeamToTeamMap(team2);

            Team2 joinRequestTeam = extractTeamInfoUserJoinRequest(session.getAttribute("id").toString(), teamResponseBody);
            if (joinRequestTeam != null) {
                teamManager2.addTeamToUserJoinRequestTeamMap(joinRequestTeam);
            }

            imageMap.put(team2.getName(), invokeAndGetImageList(teamId));
        }

        // check if inner image map is empty, have to do it via this manner
        // returns true if the team contains an image list
        boolean isInnerImageMapPresent = imageMap.values().stream().filter(perTeamImageMap -> !perTeamImageMap.isEmpty()).findFirst().isPresent();

        model.addAttribute("userEmail", userEmail);
        model.addAttribute("teamMap2", teamManager2.getTeamMap());
        model.addAttribute("userJoinRequestMap", teamManager2.getUserJoinRequestMap());
        model.addAttribute("isInnerImageMapPresent", isInnerImageMapPresent);
        model.addAttribute("imageMap", imageMap);
        return "teams";
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

        HttpEntity<String> imageRequest = createHttpEntityHeaderOnly();
        ResponseEntity imageResponse;
        try {
            imageResponse = restTemplate.exchange(properties.getTeamSavedImages(teamId), HttpMethod.GET, imageRequest, String.class);
        } catch (ResourceAccessException e) {
            log.warn("Error connecting to image service: {}", e);
            return new HashMap<>();
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
            } else if ("notfound".equals(imageStatus)) {
                inProgressImageList.add(image);
            }
        }

        resultMap.put("created", createdImageList);
        resultMap.put("inProgress", inProgressImageList);

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
//        return "redirect:/teams";
//    }

//    @RequestMapping("/ignore_participation/{teamId}")
//    public String ignoreParticipationRequest(@PathVariable Integer teamId, Model model, HttpSession session) {
//        // get user's participation request list
//        // remove this user id from the requested list
//        String teamName = teamManager.getTeamNameByTeamId(teamId);
//        teamManager.ignoreParticipationRequest2(getSessionIdOfLoggedInUser(session), teamId);
//        teamManager.setInfoMsg("You have just ignored a team request from Team " + teamName + " !");
//
//        return "redirect:/teams";
//    }

//    @RequestMapping("/withdraw/{teamId}")
    public String withdrawnJoinRequest(@PathVariable Integer teamId, HttpSession session) {
        // get user team request
        // remove this user id from the user's request list
        String teamName = teamManager.getTeamNameByTeamId(teamId);
        teamManager.removeUserJoinRequest2(getSessionIdOfLoggedInUser(session), teamId);
        teamManager.setInfoMsg("You have withdrawn your join request for Team " + teamName);

        return "redirect:/teams";
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
//        return "redirect:/teams";
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

    //--------------------------Team Profile Page--------------------------

    @RequestMapping(value = "/team_profile/{teamId}", method = RequestMethod.GET)
    public String teamProfile(@PathVariable String teamId, Model model, HttpSession session) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();

        Team2 team = extractTeamInfo(responseBody);
        model.addAttribute("team", team);
        model.addAttribute("owner", team.getOwner());
        model.addAttribute("membersList", team.getMembersStatusMap().get(MemberStatus.APPROVED));
        session.setAttribute("originalTeam", team);

        request = createHttpEntityHeaderOnly();
        response = restTemplate.exchange(properties.getExpListByTeamId(teamId), HttpMethod.GET, request, String.class);
        JSONArray experimentsArray = new JSONArray(response.getBody().toString());

        List<Experiment2> experimentList = new ArrayList<>();
        Map<Long, Realization> realizationMap = new HashMap<>();

        for (int k = 0; k < experimentsArray.length(); k++) {
            Experiment2 experiment2 = extractExperiment(experimentsArray.getJSONObject(k).toString());
            Realization realization = invokeAndExtractRealization(experiment2.getTeamName(), experiment2.getId());
            realizationMap.put(experiment2.getId(), realization);
            experimentList.add(experiment2);
        }

        model.addAttribute("teamExperimentList", experimentList);
        model.addAttribute("teamRealizationMap", realizationMap);

        return "team_profile";
    }

    @RequestMapping(value = "/team_profile/{teamId}", method = RequestMethod.POST)
    public String editTeamProfile(
            @PathVariable String teamId,
            @ModelAttribute("team") Team2 editTeam,
            final RedirectAttributes redirectAttributes,
            HttpSession session) {

        boolean errorsFound = false;

        if (editTeam.getDescription().isEmpty()) {
            errorsFound = true;
            redirectAttributes.addFlashAttribute("editDesc", "fail");
        }

        if (errorsFound) {
            // safer to remove
            session.removeAttribute("originalTeam");
            return "redirect:/team_profile/" + editTeam.getId();
        }

        // can edit team description and team website for now

        JSONObject teamfields = new JSONObject();
        teamfields.put("id", teamId);
        teamfields.put("name", editTeam.getName());
        teamfields.put("description", editTeam.getDescription());
        teamfields.put("website", "http://default.com");
        teamfields.put("organisationType", editTeam.getOrganisationType());
        teamfields.put("privacy", "OPEN");
        teamfields.put("status", editTeam.getStatus());
        teamfields.put("members", editTeam.getMembersList());

        HttpEntity<String> request = createHttpEntityWithBody(teamfields.toString());
        ResponseEntity response = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.PUT, request, String.class);

        Team2 originalTeam = (Team2) session.getAttribute("originalTeam");

        if (!originalTeam.getDescription().equals(editTeam.getDescription())) {
            redirectAttributes.addFlashAttribute("editDesc", "success");
        }

        // safer to remove
        session.removeAttribute("originalTeam");
        return "redirect:/team_profile/" + teamId;
    }

    @RequestMapping("/remove_member/{teamId}/{userId}")
    public String removeMember(@PathVariable String teamId, @PathVariable String userId, final RedirectAttributes redirectAttributes) throws IOException {

        JSONObject teamMemberFields = new JSONObject();
        teamMemberFields.put("userId", userId);
        teamMemberFields.put(MEMBER_TYPE, MemberType.MEMBER.name());
        teamMemberFields.put("memberStatus", MemberStatus.APPROVED.name());

        HttpEntity<String> request = createHttpEntityWithBody(teamMemberFields.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.removeUserFromTeam(teamId), HttpMethod.DELETE, request, String.class);
        } catch (RestClientException e) {
            log.warn("Error connecting to sio team service for remove user: {}", e);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return "redirect:/team_profile/{teamId}";
        }

        String responseBody = response.getBody().toString();

        User2 user = invokeAndExtractUserInfo(userId);
        String name = user.getFirstName() + " " + user.getLastName();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            switch (exceptionState) {
                case DETERLAB_OPERATION_FAILED_EXCEPTION:
                    // two subcases when fail to remove users from team
                    log.warn("Remove member from team: User {}, Team {} fail - {}", userId, teamId, error.getMessage());

                    if ("user has experiments".equals(error.getMessage())) {
                        // case 1 - user has experiments
                        // display the list of experiments that have to be terminated first

                        // since the team profile page has experiments already, we don't have to retrieve them again
                        // use the userid to filter out the experiment list at the web pages
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

        return "redirect:/team_profile/{teamId}";
    }

//    @RequestMapping("/team_profile/{teamId}/start_experiment/{expId}")
//    public String startExperimentFromTeamProfile(@PathVariable Integer teamId, @PathVariable Integer expId, Model model, HttpSession session) {
//        // start experiment
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
        return "team_page_apply_team";
    }

    @RequestMapping(value = "/teams/apply_team", method = RequestMethod.POST)
    public String checkApplyTeamInfo(
            @Valid TeamPageApplyTeamForm teamPageApplyTeamForm,
            BindingResult bindingResult,
            HttpSession session,
            final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        final String logPrefix = "Existing user apply for new team: {}";

        if (bindingResult.hasErrors()) {
            log.warn(logPrefix, "Application form error " + teamPageApplyTeamForm.toString());
            return "team_page_apply_team";
        }
        // log data to ensure data has been parsed
        log.debug(logPrefix, properties.getRegisterRequestToApplyTeam(session.getAttribute("id").toString()));
        log.info(logPrefix, teamPageApplyTeamForm.toString());

        JSONObject mainObject = new JSONObject();
        JSONObject teamFields = new JSONObject();

        mainObject.put(NOTES, teamPageApplyTeamForm.getJoinTeamReason());
        mainObject.put("team", teamFields);

        teamFields.put("name", teamPageApplyTeamForm.getTeamName());
        teamFields.put("description", teamPageApplyTeamForm.getTeamDescription());
        teamFields.put("website", teamPageApplyTeamForm.getTeamWebsite());
        teamFields.put("organisationType", teamPageApplyTeamForm.getTeamOrganizationType());
        teamFields.put("visibility", teamPageApplyTeamForm.getIsPublic());



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

                log.warn(logPrefix, responseBody);
                redirectAttributes.addFlashAttribute("message", errorMessage);
                return "redirect:/teams/apply_team";

            } else {
                // no errors, everything ok
                log.info (logPrefix, "Application for team " + teamPageApplyTeamForm.getTeamName() + " submitted");
                return "redirect:/teams/team_application_submitted";
            }

        } catch (ResourceAccessException | IOException e) {
            log.error(logPrefix, e);
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping(value = "/acceptable_usage_policy", method = RequestMethod.GET)
    public String teamOwnerPolicy() {
        return "acceptable_usage_policy";
    }

    @RequestMapping(value = "/terms_and_conditions", method = RequestMethod.GET)
    public String termsAndConditions() {
        return "terms_and_conditions";
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

        final String logPrefix = "Existing user join team: {}";

        if (bindingResult.hasErrors()) {
            log.warn(logPrefix, "Application form error " + teamPageJoinForm.toString());
            return "team_page_join_team";
        }

        JSONObject mainObject = new JSONObject();
        JSONObject teamFields = new JSONObject();
        JSONObject userFields = new JSONObject();

        mainObject.put("team", teamFields);
        mainObject.put("user", userFields);
        mainObject.put(NOTES, teamPageJoinForm.getJoinTeamReason());

        userFields.put("id", session.getAttribute("id")); // ncl-id

        teamFields.put("name", teamPageJoinForm.getTeamName());

        log.info(logPrefix, "User " + session.getAttribute("id") + ", team " + teamPageJoinForm.getTeamName());

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

                log.warn(logPrefix, responseBody);
                redirectAttributes.addFlashAttribute("message", errorMessage);
                return "redirect:/teams/join_team";

            } else {
                log.info(logPrefix, "Application for join team " + teamPageJoinForm.getTeamName()+ " submitted");
                return "redirect:/teams/join_application_submitted/" + teamPageJoinForm.getTeamName();
            }

        } catch (ResourceAccessException | IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    //--------------------------Experiment Page--------------------------

    @RequestMapping(value = "/experiments", method = RequestMethod.GET)
    public String experiments(Model model, HttpSession session) throws WebServiceRuntimeException {
//        long start = System.currentTimeMillis();
        List<Experiment2> experimentList = new ArrayList<>();
        Map<Long, Realization> realizationMap = new HashMap<>();
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getDeterUid(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                log.error("No user to get experiment: {}", session.getAttribute("id"));
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                log.info("experiment error: {} - {} - {} - user token:{}", error.getError(), error.getMessage(), error.getLocalizedMessage(), httpScopedSession.getAttribute(webProperties.getSessionJwtToken()));
                model.addAttribute(DETER_UID, CONNECTION_ERROR);
            } else {
                log.info("Show the deter user id: {}", responseBody);
                model.addAttribute(DETER_UID, responseBody);
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        // get list of teamids
        ResponseEntity userRespEntity = restTemplate.exchange(properties.getUser(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);

        JSONObject object = new JSONObject(userRespEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();

            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            if (!isMemberJoinRequestPending(session.getAttribute("id").toString(), teamResponseBody)) {
                // get experiments lists of the teams
                HttpEntity<String> expRequest = createHttpEntityHeaderOnly();
                ResponseEntity expRespEntity = restTemplate.exchange(properties.getExpListByTeamId(teamId), HttpMethod.GET, expRequest, String.class);

                JSONArray experimentsArray = new JSONArray(expRespEntity.getBody().toString());

                for (int k = 0; k < experimentsArray.length(); k++) {
                    Experiment2 experiment2 = extractExperiment(experimentsArray.getJSONObject(k).toString());
                    Realization realization = invokeAndExtractRealization(experiment2.getTeamName(), experiment2.getId());
                    realizationMap.put(experiment2.getId(), realization);
                    experimentList.add(experiment2);
                }
            }
        }

        model.addAttribute("experimentList", experimentList);
        model.addAttribute("realizationMap", realizationMap);
//        System.out.println("Elapsed time to get experiment page:" + (System.currentTimeMillis() - start));
        return "experiments";
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

        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

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
            HttpSession session,
            BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        if (bindingResult.hasErrors()) {
            log.info("Create experiment - form has errors");
            return "redirect:/experiments/create";
        }

        if (experimentForm.getName() == null || experimentForm.getName().isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Experiment Name cannot be empty");
            return "redirect:/experiments/create";
        }

        if (experimentForm.getDescription() == null || experimentForm.getDescription().isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "Description cannot be empty");
            return "redirect:/experiments/create";
        }

        experimentForm.setScenarioContents(getScenarioContentsFromFile(experimentForm.getScenarioFileName()));

        JSONObject experimentObject = new JSONObject();
        experimentObject.put("userId", session.getAttribute("id").toString());
        experimentObject.put("teamId", experimentForm.getTeamId());
        experimentObject.put(TEAM_NAME, experimentForm.getTeamName());
        experimentObject.put("name", experimentForm.getName().replaceAll("\\s+", "")); // truncate whitespaces and non-visible characters like \n
        experimentObject.put("description", experimentForm.getDescription());
        experimentObject.put("nsFile", "file");
        experimentObject.put("nsFileContent", experimentForm.getNsFileContent());
        experimentObject.put("idleSwap", "240");
        experimentObject.put("maxDuration", "960");

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
                return "redirect:/experiments/create";
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
//				return "redirect:/experiments/create";
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

        return "redirect:/experiments";
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

        model.addAttribute("teamName", teamName);
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

        if (saveImageForm.getImageName().length() < 2) {
            log.warn("Save image form has errors {}", saveImageForm);
            redirectAttributes.addFlashAttribute("message", "Image name too short, minimum 2 characters");
            return "redirect:/experiments/save_image/" + teamId + "/" + expId + "/" + nodeId;
        }

        log.info("Saving image: team {}, experiment {}, node {}", teamId, expId, nodeId);

        ObjectMapper mapper = new ObjectMapper();
        HttpEntity<String> request = createHttpEntityWithBody(mapper.writeValueAsString(saveImageForm));

        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.saveImage(), HttpMethod.POST, request, String.class);
        String responseBody = response.getBody().toString();

        if (RestUtil.isError(response.getStatusCode())) {
            MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
            ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

            log.warn("Save image: error with exception {}", exceptionState);

            switch (exceptionState) {
                case DETERLAB_OPERATION_FAILED_EXCEPTION:
                    log.warn("Save image: error, operation failed on DeterLab");
                    redirectAttributes.addFlashAttribute("message", error.getMessage());
                    break;
                case ADAPTER_CONNECTION_EXCEPTION:
                    log.warn("Save image: error, cannot connect to adapter");
                    redirectAttributes.addFlashAttribute("message", "connection to adapter failed");
                    break;
                case ADAPTER_INTERNAL_ERROR_EXCEPTION:
                    log.warn("Save image: error, adapter internal server error");
                    redirectAttributes.addFlashAttribute("message", "internal error was found on the adapter");
                    break;
                default:
                    log.warn("Save image: other error");
                    redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
            }

            return "redirect:/experiments/save_image/" + teamId + "/" + expId + "/" + nodeId;
        }

        // everything looks ok
        log.info("Save image in progress: team {}, experiment {}, node {}, image {}", teamId, expId, nodeId, saveImageForm.getImageName());
        return "redirect:/experiments";
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
                    redirectAttributes.addFlashAttribute("message", error.getMessage());
                    break;
                default:
                    log.warn("Image service or adapter fail");
                    // possible sio or adapter connection fail
                    redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
                    break;
            }
            return "redirect:/experiments/save_image/" + teamId + "/" + expId + "/" + nodeId;
        } else {
            // everything ok
            log.info("Image service in progress for Team: {}, Exp: {}, Node: {}, Image: {}", teamId, expId, nodeId, saveImageForm.getImageName());
            return "redirect:/experiments";
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

    @RequestMapping("/remove_experiment/{teamName}/{teamId}/{expId}")
    public String removeExperiment(@PathVariable String teamName, @PathVariable String teamId, @PathVariable String expId, final RedirectAttributes redirectAttributes, HttpSession session) throws WebServiceRuntimeException {
        // ensure experiment is stopped first
        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        Team2 team = invokeAndExtractTeamInfo(teamId);

        // check valid authentication to remove experiments
        // either admin, experiment creator or experiment owner
        if (!validateIfAdmin(session) && !realization.getUserId().equals(session.getAttribute("id").toString()) && !team.getOwner().getId().equals(session.getAttribute(webProperties.getSessionUserId()))) {
            log.warn("Permission denied when remove Team:{}, Experiment: {} with User: {}, Role:{}", teamId, expId, session.getAttribute("id"), session.getAttribute(webProperties.getSessionRoles()));
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to remove experiment;" + permissionDeniedMessage);
            return "redirect:/experiments";
        }

        if (!realization.getState().equals(RealizationState.NOT_RUNNING.toString())) {
            log.warn("Trying to remove Team: {}, Experiment: {} with State: {} that is still in progress?", teamId, expId, realization.getState());
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to remove Exp: " + realization.getExperimentName() + ". Please refresh the page again. If the error persists, please contact " + CONTACT_EMAIL);
            return "redirect:/experiments";
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
            return "redirect:/experiments";
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
                return "redirect:/experiments";
            } else {
                // everything ok
                log.info("remove experiment success for Team: {}, Exp: {}", teamId, expId);
                redirectAttributes.addFlashAttribute("exp_remove_message", "Team: " + teamName + " has removed Exp: " + realization.getExperimentName());
                return "redirect:/experiments";
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping("/start_experiment/{teamName}/{expId}")
    public String startExperiment(
            @PathVariable String teamName,
            @PathVariable String expId,
            final RedirectAttributes redirectAttributes, Model model, HttpSession session) throws WebServiceRuntimeException {

        // ensure experiment is stopped first before starting
        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));


        if (!checkPermissionRealizeExperiment(realization, session)) {
            log.warn("Permission denied to start experiment: {} for team: {}", realization.getExperimentName(), teamName);
            redirectAttributes.addFlashAttribute(MESSAGE, permissionDeniedMessage);
            return "redirect:/experiments";
        }

        String teamStatus = getTeamStatus(realization.getTeamId());

        if (!teamStatus.equals(TeamStatus.APPROVED.name())) {
            log.warn("Error: trying to realize an experiment {} on team {} with status {}", realization.getExperimentName(), realization.getTeamId(), teamStatus);
            redirectAttributes.addFlashAttribute(MESSAGE, teamName + " is in " + teamStatus + " status and does not have permission to start experiment. Please contact " + CONTACT_EMAIL);
            return "redirect:/experiments";
        }

        if (!realization.getState().equals(RealizationState.NOT_RUNNING.toString())) {
            log.warn("Trying to start Team: {}, Experiment: {} with State: {} that is not running?", teamName, expId, realization.getState());
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to start Exp: " + realization.getExperimentName() + ". Please refresh the page again. If the error persists, please contact " + CONTACT_EMAIL);
            return "redirect:/experiments";
        }

        log.info("Starting experiment: at " + properties.getStartExperiment(teamName, expId));
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response;

        try {
            response = restTemplate.exchange(properties.getStartExperiment(teamName, expId), HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            log.warn("Error connecting to experiment service to start experiment", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return "redirect:/experiments";
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
                        return "redirect:/experiments";
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
                return "experiments";
            } else {
                // everything ok
                log.info("start experiment success for Team: {}, Exp: {}", teamName, expId);
                redirectAttributes.addFlashAttribute(EXPERIMENT_MESSAGE, "Experiment " + realization.getExperimentName() + " in team " + teamName + " is starting. This may take up to 10 minutes depending on the scale of your experiment. Please refresh this page later.");
                return "redirect:/experiments";
            }
        } catch (IOException e) {
            log.warn("start experiment error: {]", e.getMessage());
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping("/stop_experiment/{teamName}/{expId}")
    public String stopExperiment(@PathVariable String teamName, @PathVariable String expId, Model model, final RedirectAttributes redirectAttributes, HttpSession session) throws WebServiceRuntimeException {

        // ensure experiment is active first before stopping
        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        if (isNotAdminAndNotInTeam(session, realization)) {
            log.warn("Permission denied to stop experiment: {} for team: {}", realization.getExperimentName(), teamName);
            redirectAttributes.addFlashAttribute(MESSAGE, permissionDeniedMessage);
            return "redirect:/experiments";
        }

        if (!realization.getState().equals(RealizationState.RUNNING.toString())) {
            log.warn("Trying to stop Team: {}, Experiment: {} with State: {} that is still in progress?", teamName, expId, realization.getState());
            redirectAttributes.addFlashAttribute(MESSAGE, "An error occurred while trying to stop Exp: " + realization.getExperimentName() + ". Please refresh the page again. If the error persists, please contact " + CONTACT_EMAIL);
            return "redirect:/experiments";
        }

        log.info("Stopping experiment: at " + properties.getStopExperiment(teamName, expId));
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response;

        return abc(teamName, expId, redirectAttributes, realization, request);
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

    private String abc(@PathVariable String teamName, @PathVariable String expId, RedirectAttributes redirectAttributes, Realization realization, HttpEntity<String> request) throws WebServiceRuntimeException {
        ResponseEntity response;
        try {
            response = restTemplate.exchange(properties.getStopExperiment(teamName, expId), HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            log.warn("Error connecting to experiment service to stop experiment", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
            return "redirect:/experiments";
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
                redirectAttributes.addFlashAttribute(EXPERIMENT_MESSAGE, "Experiment " + realization.getExperimentName() + " in team " + teamName + " is stopping. Please refresh this page in a few minutes.");
            }
            return "redirect:/experiments";
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

        TeamManager2 teamManager2 = new TeamManager2();

        Map<String, List<String>> userToTeamMap = new HashMap<>(); // userId : list of team names
        List<Team2> pendingApprovalTeamsList = new ArrayList<>();

        //------------------------------------
        // get list of teams
        // get list of teams pending for approval
        //------------------------------------
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioTeamsUrl(), HttpMethod.GET, request, String.class);

        JSONArray jsonArray = new JSONArray(responseEntity.getBody().toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Team2 one = extractTeamInfo(jsonObject.toString());
            teamManager2.addTeamToTeamMap(one);
            if (one.getStatus().equals(TeamStatus.PENDING.name())) {
                pendingApprovalTeamsList.add(one);
            }
        }

        //------------------------------------
        // get list of users
        //------------------------------------
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
            if (userObject.get("teams") != null) {
                JSONArray teamJsonArray = userObject.getJSONArray("teams");
                for (int k = 0; k < teamJsonArray.length(); k++) {
                    Team2 team = invokeAndExtractTeamInfo(teamJsonArray.get(k).toString());
                    perUserTeamList.add(team.getName());
                }
                userToTeamMap.put(user.getId(), perUserTeamList);
            }
        }

        model.addAttribute("teamsMap", teamManager2.getTeamMap());
        model.addAttribute("pendingApprovalTeamsList", pendingApprovalTeamsList);
        model.addAttribute("usersList", usersList);
        model.addAttribute("userToTeamMap", userToTeamMap);

        return "admin2";
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

        ResponseEntity response4 = restTemplate.exchange(properties.getDownloadStat(), HttpMethod.GET, request, String.class);
        String responseBody4 = response4.getBody().toString();

        Map<Integer, Long> dataDownloadStats = new HashMap<>();
        JSONArray statJsonArray = new JSONArray(responseBody4);
        for (int i = 0; i < statJsonArray.length(); i++) {
            JSONObject statInfoObject = statJsonArray.getJSONObject(i);
            dataDownloadStats.put(statInfoObject.getInt("dataId"), statInfoObject.getLong("count"));
        }

        model.addAttribute("dataList", datasetsList);
        model.addAttribute("downloadStats", dataDownloadStats);

        return "data_dashboard";
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

    @RequestMapping("/admin/usage")
    public String adminTeamUsage(Model model,
                                 @RequestParam(value = "team", required = false) String team,
                                 @RequestParam(value = "start", required = false) String start,
                                 @RequestParam(value = "end", required = false) String end,
                                 HttpSession session) {
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

        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioTeamsUrl(), HttpMethod.GET, request, String.class);
        JSONArray jsonArray = new JSONArray(responseEntity.getBody().toString());

        TeamManager2 teamManager2 = new TeamManager2();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Team2 one = extractTeamInfo(jsonObject.toString());
            teamManager2.addTeamToTeamMap(one);
        }

        if (team != null) {
            responseEntity = restTemplate.exchange(properties.getUsageStat(team, "startDate=" + start, "endDate=" + end), HttpMethod.GET, request, String.class);
            String usage = responseEntity.getBody().toString();
            model.addAttribute("usage", usage);
        }

        model.addAttribute("teamsMap", teamManager2.getTeamMap());
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("team", team);
        return "usage_statistics";
    }

//    @RequestMapping(value="/admin/domains/add", method=RequestMethod.POST)
//    public String addDomain(@Valid Domain domain, BindingResult bindingResult) {
//    	if (bindingResult.hasErrors()) {
//    		return "redirect:/admin";
//    	} else {
//    		domainManager.addDomains(domain.getDomainName());
//    	}
//    	return "redirect:/admin";
//    }

//    @RequestMapping("/admin/domains/remove/{domainKey}")
//    public String removeDomain(@PathVariable String domainKey) {
//    	domainManager.removeDomains(domainKey);
//    	return "redirect:/admin";
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
            return "redirect:/admin";
        }

        // http status code is OK, then need to check the response message
        String msg = new JSONObject(responseBody).getString("msg");
        if ("approve project OK".equals(msg)) {
            log.info("Approve team {} OK", teamId);
        } else {
            log.warn("Approve team {} FAIL", teamId);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
        }
        return "redirect:/admin";
    }

    @RequestMapping("/admin/teams/reject/{teamId}/{teamOwnerId}")
    public String rejectTeam(
            @PathVariable String teamId,
            @PathVariable String teamOwnerId,
            final RedirectAttributes redirectAttributes,
            HttpSession session
    ) throws WebServiceRuntimeException {

        if (!validateIfAdmin(session)) {
            return NO_PERMISSION_PAGE;
        }

        //FIXME require approver info
        log.info("Rejecting new team {}, team owner {}", teamId, teamOwnerId);
        HttpEntity<String> request = createHttpEntityHeaderOnly();
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
            return "redirect:/admin";
        }

        // http status code is OK, then need to check the response message
        String msg = new JSONObject(responseBody).getString("msg");
        if ("reject project OK".equals(msg)) {
            log.info("Reject team {} OK", teamId);
        } else {
            log.warn("Reject team {} FAIL", teamId);
            redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
        }
        return "redirect:/admin";
    }

    @RequestMapping("/admin/teams/{teamId}")
    public String setupTeamRestriction(
            @PathVariable final String teamId,
            @RequestParam(value = "action", required=true) final String action,
            final RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException
    {
        final String logMessage = "Updating restriction settings for team {}: {}";

        // check if admin
        if (!validateIfAdmin(session)) {
            log.warn(logMessage, teamId, PERMISSION_DENIED);
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
            log.warn(logMessage, teamId, "Cannot " + action + " team with status " + team.getStatus());
            redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + "Cannot " + action + " team " + team.getName() + " with status " + team.getStatus());
            return "redirect:/admin";
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
            switch (exceptionState) {
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn(logMessage, team.getId(), TEAM_NOT_FOUND);
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + TEAM_NOT_FOUND);
                    break;
                case INVALID_STATUS_TRANSITION_EXCEPTION:
                    log.warn(logMessage, team.getId(), error.getMessage());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + error.getMessage());
                    break;
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
            return "redirect:/admin";
        } else {
            // good
            log.info("Team {} has been restricted", team.getId());
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Team " + team.getName() + " status has been changed to " + TeamStatus.RESTRICTED.name());
            return "redirect:/admin";
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
            switch (exceptionState) {
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn(logMessage, team.getId(), TEAM_NOT_FOUND);
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + TEAM_NOT_FOUND);
                    break;
                case INVALID_STATUS_TRANSITION_EXCEPTION:
                    log.warn(logMessage, team.getId(), error.getMessage());
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + error.getMessage());
                    break;
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
            return "redirect:/admin";
        } else {
            // good
            log.info("Team {} has been freed", team.getId());
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "Team " + team.getName() + " status has been changed to " + TeamStatus.APPROVED.name());
            return "redirect:/admin";
        }
    }

    @RequestMapping("/admin/users/{userId}")
    public String freezeUnfreezeUsers(
            @PathVariable final String userId,
            @RequestParam(value = "action", required = true) final String action,
            final RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException
    {
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
            redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + "failed to " + action + " user " + user.getEmail() + " with status " + user.getStatus());
            return "redirect:/admin";
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
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + " user " + user.getEmail() + " not found.");
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
            return "redirect:/admin";
        } else {
            // good
            log.info("User {} has been frozen", user.getId());
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "User " + user.getEmail() + " has been banned.");
            return "redirect:/admin";
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
                    redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + " user " + user.getEmail() + " not found.");
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
            return "redirect:/admin";
        } else {
            // good
            log.info("User {} has been unfrozen", user.getId());
            redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, "User " + user.getEmail() + " has been unbanned.");
            return "redirect:/admin";
        }
    }



//    @RequestMapping("/admin/experiments/remove/{expId}")
//    public String adminRemoveExp(@PathVariable Integer expId) {
//    	int teamId = experimentManager.getExperimentByExpId(expId).getTeamId();
//        experimentManager.adminRemoveExperiment(expId);
//
//        // decrease exp count to be display on Teams page
//        teamManager.decrementExperimentCount(teamId);
//    	return "redirect:/admin";
//    }

//    @RequestMapping(value="/admin/data/contribute", method=RequestMethod.GET)
//    public String adminContributeDataset(Model model) {
//    	model.addAttribute("dataset", new Dataset());
//
//    	File rootFolder = new File(App.ROOT);
//    	List<String> fileNames = Arrays.stream(rootFolder.listFiles())
//    			.map(f -> f.getError())
//    			.collect(Collectors.toList());
//
//    		model.addAttribute("files",
//    			Arrays.stream(rootFolder.listFiles())
//    					.sorted(Comparator.comparingLong(f -> -1 * f.lastModified()))
//    					.map(f -> f.getError())
//    					.collect(Collectors.toList())
//    		);
//
//    	return "admin_contribute_data";
//    }

//    @RequestMapping(value="/admin/data/contribute", method=RequestMethod.POST)
//    public String validateAdminContributeDataset(@ModelAttribute("dataset") Dataset dataset, HttpSession session, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
//        BufferedOutputStream stream = null;
//        FileOutputStream fileOutputStream = null;
//        // TODO
//    	// validation
//    	// get file from user upload to server
//		if (!file.isEmpty()) {
//			try {
//				String fileName = getSessionIdOfLoggedInUser(session) + "-" + file.getOriginalFilename();
//                fileOutputStream = new FileOutputStream(new File(App.ROOT + "/" + fileName));
//				stream = new BufferedOutputStream(fileOutputStream);
//                FileCopyUtils.copy(file.getInputStream(), stream);
//				redirectAttributes.addFlashAttribute(MESSAGE,
//						"You successfully uploaded " + file.getOriginalFilename() + "!");
//				datasetManager.addDataset(getSessionIdOfLoggedInUser(session), dataset, file.getOriginalFilename());
//			}
//			catch (Exception e) {
//				redirectAttributes.addFlashAttribute(MESSAGE,
//						"You failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
//			} finally {
//                if (stream != null) {
//                    stream.close();
//                }
//                if (fileOutputStream != null) {
//                    fileOutputStream.close();
//                }
//            }
//        }
//		else {
//			redirectAttributes.addFlashAttribute(MESSAGE,
//					"You failed to upload " + file.getOriginalFilename() + " because the file was empty");
//		}
//    	return "redirect:/admin";
//    }

//    @RequestMapping("/admin/data/remove/{datasetId}")
//    public String adminRemoveDataset(@PathVariable Integer datasetId) {
//    	datasetManager.removeDataset(datasetId);
//    	return "redirect:/admin";
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
//    	return "redirect:/admin";
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
        model.addAttribute("signUpMergedForm", new SignUpMergedForm());
        return "join_team_application_awaiting_approval";
    }

    //--------------------------Get List of scenarios filenames--------------------------
    private List<String> getScenarioFileNameList() throws WebServiceRuntimeException {
        log.info("Retrieving scenario file names");
//        List<String> scenarioFileNameList = null;
//        try {
//            scenarioFileNameList = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("scenarios"), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            throw new WebServiceRuntimeException(e.getMessage());
//        }
//        File folder = null;
//        try {
//            folder = new ClassPathResource("scenarios").getFile();
//        } catch (IOException e) {
//            throw new WebServiceRuntimeException(e.getMessage());
//        }
//        List<String> scenarioFileNameList = new ArrayList<>();
//		File[] files = folder.listFiles();
//		for (File file : files) {
//			if (file.isFile()) {
//				scenarioFileNameList.add(file.getError());
//			}
//		}
        // FIXME: hardcode list of filenames for now
        List<String> scenarioFileNameList = new ArrayList<>();
        scenarioFileNameList.add("Scenario 1 - Experiment with a single node");
        scenarioFileNameList.add("Scenario 2 - Experiment with 2 nodes and 10Gb link");
        scenarioFileNameList.add("Scenario 3 - Experiment with 3 nodes in a LAN");
        scenarioFileNameList.add("Scenario 4 - Experiment with 2 nodes and customized link property");
//        scenarioFileNameList.add("Scenario 4 - Two nodes linked with a 10Gbps SDN switch");
//        scenarioFileNameList.add("Scenario 5 - Three nodes with Blockchain capabilities");
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
            log.info("Experiment ns file contents: {}", sb);
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
        JSONObject userDetails = object.getJSONObject("userDetails");
        JSONObject address = userDetails.getJSONObject("address");

        user2.setId(object.getString("id"));
        user2.setFirstName(getJSONStr(userDetails.getString("firstName")));
        user2.setLastName(getJSONStr(userDetails.getString("lastName")));
        user2.setJobTitle(userDetails.getString("jobTitle"));
        user2.setEmail(userDetails.getString("email"));
        user2.setPhone(userDetails.getString("phone"));
        user2.setAddress1(address.getString("address1"));
        user2.setAddress2(address.getString("address2"));
        user2.setCountry(address.getString("country"));
        user2.setRegion(address.getString("region"));
        user2.setPostalCode(address.getString("zipCode"));
        user2.setCity(address.getString("city"));
        user2.setInstitution(userDetails.getString("institution"));
        user2.setInstitutionAbbreviation(userDetails.getString("institutionAbbreviation"));
        user2.setInstitutionWeb(userDetails.getString("institutionWeb"));

        user2.setStatus(object.getString("status"));
        user2.setEmailVerified(object.getBoolean("emailVerified"));

        return user2;
    }

    private Team2 extractTeamInfo(String json) {
        Team2 team2 = new Team2();
        JSONObject object = new JSONObject(json);
        JSONArray membersArray = object.getJSONArray("members");

        try {
            team2.setCreatedDate(formatZonedDateTime(object.get("applicationDate").toString()));
        } catch (Exception e) {
            log.warn("Error getting team application date {}", e);
            team2.setCreatedDate(UNKNOWN);
        }
        team2.setId(object.getString("id"));
        team2.setName(object.getString("name"));
        team2.setDescription(object.getString("description"));
        team2.setWebsite(object.getString("website"));
        team2.setOrganisationType(object.getString("organisationType"));
        team2.setStatus(object.getString("status"));
        team2.setVisibility(object.getString("visibility"));

        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            String userId = memberObject.getString("userId");
            String teamMemberType = memberObject.getString(MEMBER_TYPE);
            String teamMemberStatus = memberObject.getString("memberStatus");

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
        JSONArray membersArray = object.getJSONArray("members");

        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            String userId = memberObject.getString("userId");
            String teamMemberStatus = memberObject.getString("memberStatus");

            if (userId.equals(loginUserId) && !teamMemberStatus.equals(MemberStatus.APPROVED.toString())) {
                return true;
            }
        }
        log.info("User: {} is viewing experiment page", loginUserId);
        return false;
    }

    private Team2 extractTeamInfoUserJoinRequest(String userId, String json) {
        Team2 team2 = new Team2();
        JSONObject object = new JSONObject(json);
        JSONArray membersArray = object.getJSONArray("members");

        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            String uid = memberObject.getString("userId");
            String teamMemberStatus = memberObject.getString("memberStatus");
            if (uid.equals(userId) && teamMemberStatus.equals(MemberStatus.PENDING.toString())) {

                team2.setId(object.getString("id"));
                team2.setName(object.getString("name"));
                team2.setDescription(object.getString("description"));
                team2.setWebsite(object.getString("website"));
                team2.setOrganisationType(object.getString("organisationType"));
                team2.setStatus(object.getString("status"));
                team2.setVisibility(object.getString("visibility"));
                team2.setMembersCount(membersArray.length());

                return team2;
            }
        }

        // no such member in the team found
        return null;
    }

    protected Dataset extractDataInfo(String json) {
        log.debug(json);

        JSONObject object = new JSONObject(json);
        Dataset dataset = new Dataset();

        dataset.setId(object.getInt("id"));
        dataset.setName(object.getString("name"));
        dataset.setDescription(object.getString("description"));
        dataset.setContributorId(object.getString("contributorId"));
        dataset.addVisibility(object.getString("visibility"));
        dataset.addAccessibility(object.getString("accessibility"));
        try {
            dataset.setReleasedDate(getZonedDateTime(object.get("releasedDate").toString()));
        } catch (IOException e) {
            log.warn("Error getting released date {}", e);
            dataset.setReleasedDate(null);
        }

        dataset.setContributor(invokeAndExtractUserInfo(dataset.getContributorId()));

        JSONArray resources = object.getJSONArray("resources");
        for (int i = 0; i < resources.length(); i++) {
            JSONObject resource = resources.getJSONObject(i);
            DataResource dataResource = new DataResource();
            dataResource.setId(resource.getLong("id"));
            dataResource.setUri(resource.getString("uri"));
            dataset.addResource(dataResource);
        }

        JSONArray approvedUsers = object.getJSONArray("approvedUsers");
        for (int i =0; i < approvedUsers.length(); i++) {
            dataset.addApprovedUser(approvedUsers.getString(0));
        }

        return dataset;
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

        experiment2.setId(object.getLong("id"));
        experiment2.setUserId(object.getString("userId"));
        experiment2.setTeamId(object.getString("teamId"));
        experiment2.setTeamName(object.getString(TEAM_NAME));
        experiment2.setName(object.getString("name"));
        experiment2.setDescription(object.getString("description"));
        experiment2.setNsFile(object.getString("nsFile"));
        experiment2.setNsFileContent(object.getString("nsFileContent"));
        experiment2.setIdleSwap(object.getInt("idleSwap"));
        experiment2.setMaxDuration(object.getInt("maxDuration"));

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
        realization.setUserId(object.getString("userId"));
        realization.setTeamId(object.getString("teamId"));
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
                nodeDetails.put("os", nodeDetailsJson.getString("os"));
                nodeDetails.put("qualifiedName", nodeDetailsJson.getString("qualifiedName"));
                nodeDetails.put(NODE_ID, nodeDetailsJson.getString(NODE_ID));
                realization.addNodeDetails(nodeName, nodeDetails);
            }
            log.info("nodes info object: {}", nodesInfoObject);
            realization.setNumberOfNodes(nodesInfoObject.keySet().size());
        }

        return realization;
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
        headers.set("Authorization", httpScopedSession.getAttribute(webProperties.getSessionJwtToken()).toString());
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
        headers.set("Authorization", httpScopedSession.getAttribute(webProperties.getSessionJwtToken()).toString());
        return new HttpEntity<>(headers);
    }

    private void setSessionVariables(HttpSession session, String loginEmail, String id, String firstName, String userRoles, String token) {
        User2 user = invokeAndExtractUserInfo(id);
        session.setAttribute(webProperties.getSessionEmail(), loginEmail);
        session.setAttribute(webProperties.getSessionUserId(), id);
        session.setAttribute(webProperties.getSessionUserFirstName(), firstName);
        session.setAttribute(webProperties.getSessionRoles(), userRoles);
        session.setAttribute(webProperties.getSessionJwtToken(), "Bearer " + token);
        log.info("Session variables - sessionLoggedEmail: {}, id: {}, name: {}, roles: {}, token: {}", loginEmail, id, user.getFirstName(), userRoles, token);
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
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

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
        int numberOfRunningExperiments = 0;
        Map<String, Integer> userDashboardStats = new HashMap<>();

        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity userRespEntity = restTemplate.exchange(properties.getUser(userId), HttpMethod.GET, request, String.class);

        JSONObject object = new JSONObject(userRespEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();

            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            if (!isMemberJoinRequestPending(userId, teamResponseBody)) {
                // get experiments lists of the teams
                HttpEntity<String> expRequest = createHttpEntityHeaderOnly();
                ResponseEntity expRespEntity = restTemplate.exchange(properties.getExpListByTeamId(teamId), HttpMethod.GET, expRequest, String.class);

                JSONArray experimentsArray = new JSONArray(expRespEntity.getBody().toString());

                numberOfRunningExperiments = getNumberOfRunningExperiments(numberOfRunningExperiments, experimentsArray);
            }
        }

        userDashboardStats.put(USER_DASHBOARD_TEAMS, teamIdsJsonArray.length());
        userDashboardStats.put(USER_DASHBOARD_RUNNING_EXPERIMENTS, numberOfRunningExperiments);
        userDashboardStats.put(USER_DASHBOARD_FREE_NODES, getNodes(NodeType.FREE));
        return userDashboardStats;
    }

    private int getNumberOfRunningExperiments(int numberOfRunningExperiments, JSONArray experimentsArray) {
        for (int k = 0; k < experimentsArray.length(); k++) {
            Experiment2 experiment2 = extractExperiment(experimentsArray.getJSONObject(k).toString());
            Realization realization = invokeAndExtractRealization(experiment2.getTeamName(), experiment2.getId());
            if (realization.getState().equals(RealizationState.RUNNING.toString())) {
                numberOfRunningExperiments++;
            }
        }
        return numberOfRunningExperiments;
    }

    private SortedMap<String, Map<String, String>> getGlobalImages() throws IOException {
        SortedMap<String, Map<String, String>> globalImagesMap = new TreeMap<>();

        log.info("Retrieving list of global images from: {}", properties.getGlobalImages());

        try {
            HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
            ResponseEntity response = restTemplate.exchange(properties.getGlobalImages(), HttpMethod.GET, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            String json = new JSONObject(response.getBody().toString()).getString("images");
            globalImagesMap = mapper.readValue(json, new TypeReference<SortedMap<String, Map<String, String>>>(){});
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
            log.warn("Error connecting to service-telemetry: {}", e);
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
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

        // get team info by team id
        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();

            HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
            ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamById(teamId), HttpMethod.GET, teamRequest, String.class);
            String teamResponseBody = teamResponse.getBody().toString();

            if (!isMemberJoinRequestPending(userId, teamResponseBody)) {
                TeamUsageInfo usageInfo = new TeamUsageInfo();
                usageInfo.setId(teamId);
                usageInfo.setName(new JSONObject(teamResponseBody).getString("name"));
                usageInfo.setUsage(getUsageStatisticsByTeamId(teamId));
                usageInfoList.add(usageInfo);
            }
        }
        return usageInfoList;
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
        return response.getBody().toString();
    }
}