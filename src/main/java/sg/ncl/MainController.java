package sg.ncl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sg.ncl.domain.*;
import sg.ncl.exceptions.*;
import sg.ncl.testbed_interface.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Spring Controller
 * Direct the views to appropriate locations and invoke the respective REST API
 *
 * @author Cassie, Desmond, Te Ye
 */
@Controller
@Slf4j
public class MainController {

    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String APPLICATION_FORCE_DOWNLOAD = "application/force-download";
    private final String SESSION_LOGGED_IN_USER_ID = "loggedInUserId";

    private TeamManager teamManager = TeamManager.getInstance();
//    private UserManager userManager = UserManager.getInstance();
//    private ExperimentManager experimentManager = ExperimentManager.getInstance();
//    private DomainManager domainManager = DomainManager.getInstance();
//    private DatasetManager datasetManager = DatasetManager.getInstance();
//    private NodeManager nodeManager = NodeManager.getInstance();

    private String session_roles = "roles";

    // to know which form fields have been changed
    private User2 originalUser = null;

    private String AUTHORIZATION_HEADER = null;

    private static final String CONTACT_EMAIL = "support@ncl.sg";

    // error messages
    private static final String ERR_SERVER_OVERLOAD = "There is a problem with your request. Please contact " + CONTACT_EMAIL;

    private final String permissionDeniedMessage = "Permission denied. If the error persists, please contact " + CONTACT_EMAIL;

    // for user dashboard hashmap key values
    private static final String USER_DASHBOARD_TEAMS = "teams";
    private static final String USER_DASHBOARD_RUNNING_EXPERIMENTS = "runningExperiments";
    private static final String USER_DASHBOARD_FREE_NODES = "freeNodes";

    @Autowired
    private RestTemplate restTemplate;

    @Inject
    private ObjectMapper objectMapper;

    @Autowired
    private ConnectionProperties properties;

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

    @RequestMapping("/resources1")
    public String resources1() {
        return "resources1";
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

    @RequestMapping("/calendar1")
    public String calendar1() {
        return "calendar1";
    }

    @RequestMapping("/tools")
    public String tools() {
        return "tools";
    }

    @RequestMapping("/createaccount")
    public String createAccount() {
        return "createaccount";
    }

    @RequestMapping("/createexperiment")
    public String createExperimentTutorial() {
        return "createexperiment";
    }

    @RequestMapping("/applyteam")
    public String applyteam() {
        return "applyteam";
    }

    @RequestMapping("/jointeam")
    public String jointeam() {
        return "jointeam";
    }

//    @RequestMapping("/resource2")
//    public String resource2() {
//        return "resource2";
//    }
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




//    @RequestMapping("/dataresource")
//    public String dataresource() {
//        return "dataresource";
//    }

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

    @RequestMapping(value = "/SubscriptionAgreement/download", method = RequestMethod.GET)
    public void subscriptionAgreementDownload(HttpServletResponse response) throws MasterSubscriptionAgreementDownloadException, IOException {
        InputStream stream = null;
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        try {
            stream = getClass().getClassLoader().getResourceAsStream("downloads/SubscriptionAgreement.pdf");
            response.setContentType(APPLICATION_FORCE_DOWNLOAD);
            response.setHeader(CONTENT_DISPOSITION, "attachment; filename=SubscriptionAgreement.pdf");
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            log.info("Error for subscription download." + ex.getMessage());
            throw new MasterSubscriptionAgreementDownloadException("Error for subscription download.");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    @RequestMapping(value = "/UsagePolicy/download", method = RequestMethod.GET)
    public void usagePolicyDownload(HttpServletResponse response) throws UsagePolicyDownloadException, IOException {
        InputStream stream = null;
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        try {
            stream = getClass().getClassLoader().getResourceAsStream("downloads/UsagePolicy.pdf");
            response.setContentType(APPLICATION_FORCE_DOWNLOAD);
            response.setHeader(CONTENT_DISPOSITION, "attachment; filename=UsagePolicy.pdf");
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            log.info("Error for usage policy download." + ex.getMessage());
            throw new UsagePolicyDownloadException("Error for usage policy download.");
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
    public String verifyEmail(@RequestParam final String id, @RequestParam final String email, @RequestParam final String key) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);

        ObjectNode keyObject = objectMapper.createObjectNode();
        keyObject.put("key", key);

        HttpEntity<String> request = new HttpEntity<>(keyObject.toString(), headers);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        // convert email to base64 code as email contains "."
        String emailBase64 = new String(Base64.encodeBase64(email.getBytes()));
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
            HttpSession session) throws WebServiceRuntimeException {

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


        AUTHORIZATION_HEADER = "Bearer " + token;

        // now check user status to decide what to show to the user
        User2 user = invokeAndExtractUserInfo(id);

        try {
            String userStatus = user.getStatus();
            boolean emailVerified = user.getEmailVerified();
            if (!emailVerified || (UserStatus.CREATED.toString()).equals(userStatus)) {
                log.info("User {} not validated, redirected to email verification page", id);
                return "redirect:/email_not_validated";
            } else if ((UserStatus.PENDING.toString()).equals(userStatus)) {
                log.info("User {} not approved, redirected to application pending page", id);
                return "redirect:/team_application_under_review";
            } else if ((UserStatus.APPROVED.toString()).equals(userStatus)) {
                // set session variables
                setSessionVariables(session, loginForm.getLoginEmail(), id, user.getFirstName(), role);
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

    @RequestMapping("/passwordreset")
    public String passwordreset(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "passwordreset";
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getDeterUid(session.getAttribute("id").toString()), HttpMethod.GET, request, String.class);

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                log.error("No such user: {}", session.getAttribute("id"));
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                model.addAttribute("deterUid", "Connection Error");
            } else {
                log.info("Show the deter user id: {}", responseBody);
                model.addAttribute("deterUid", responseBody);
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        // retrieve user dashboard stats
        Map<String, Integer> userDashboardMap = getUserDashboardStats(session.getAttribute("id").toString());
        model.addAttribute("userDashboardMap", userDashboardMap);
        return "dashboard";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        removeSessionVariables(session);
        return "redirect:/";
    }

    //--------------------------Sign Up Page--------------------------

    @RequestMapping(value = "/signup2", method = RequestMethod.GET)
    public String signup2(Model model) {
        model.addAttribute("signUpMergedForm", new SignUpMergedForm());
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
            return "/signup2";
        }

        if (!signUpMergedForm.getHasAcceptTeamOwnerPolicy()) {
            signUpMergedForm.setErrorTeamOwnerPolicy("Please accept the team owner policy");
            log.warn("Policy not accepted");
            return "/signup2";
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
                return "/signup2";
            } else {

                teamFields.put("name", signUpMergedForm.getTeamName().trim());
                teamFields.put("description", signUpMergedForm.getTeamDescription().trim());
                teamFields.put("website", signUpMergedForm.getTeamWebsite().trim());
                teamFields.put("organisationType", signUpMergedForm.getTeamOrganizationType());
                teamFields.put("visibility", signUpMergedForm.getIsPublic());
                mainObject.put("isJoinTeam", false);

                try {
                    registerUserToDeter(mainObject);
                } catch (TeamNotFoundException | ApplyNewProjectException | RegisterTeamNameDuplicateException | UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
                    redirectAttributes.addFlashAttribute("message", e.getMessage());
                    return "redirect:/signup2";
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
                    return "redirect:/signup2";
                }

                log.info("Signup new team success");
                return "redirect:/team_application_submitted";
            }

        } else if (joinNewTeamName != null && !joinNewTeamName.isEmpty()) {
            log.info("Signup join team name {}", joinNewTeamName);
            // get the team JSON from team name
            String teamIdToJoin = "";

            try {
                teamIdToJoin = getTeamIdByName(signUpMergedForm.getJoinTeamName().trim());
            } catch (TeamNotFoundException | AdapterConnectionException e) {
                redirectAttributes.addFlashAttribute("message", e.getMessage());
                return "redirect:/signup2";
            }

            teamFields.put("id", teamIdToJoin);

            // set the flag to indicate to controller that it is joining an existing team
            mainObject.put("isJoinTeam", true);

            try {
                registerUserToDeter(mainObject);
            } catch (TeamNotFoundException | AdapterConnectionException | ApplyNewProjectException | RegisterTeamNameDuplicateException | UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
                redirectAttributes.addFlashAttribute("message", e.getMessage());
                return "redirect:/signup2";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
                return "redirect:/signup2";
            }

            log.info("Signup join team success");
            return "redirect:/join_application_submitted/" + signUpMergedForm.getJoinTeamName().trim();

        } else {
            log.warn("Signup unreachable statement");
            // logic error not suppose to reach here
            // possible if user fill up create new team but without the team name
            redirectAttributes.addFlashAttribute("signupError", "There is a problem when submitting your form. Please re-enter and submit the details again.");
            return "redirect:/signup2";
        }
    }

    /**
     * Use when registering new accounts
     *
     * @param mainObject A JSONObject that contains user's credentials, personal details and team application details
     */
    private void registerUserToDeter(JSONObject mainObject) throws WebServiceRuntimeException, TeamNotFoundException, AdapterConnectionException, ApplyNewProjectException, RegisterTeamNameDuplicateException, UsernameAlreadyExistsException, EmailAlreadyExistsException {
        HttpEntity<String> request = createHttpEntityWithBody(mainObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getSioRegUrl(), HttpMethod.POST, request, String.class);

        String responseBody = response.getBody().toString();

        log.info("Register user to deter response: {}", responseBody);

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);

                log.warn("Register user exception error: {}", error.getError());

                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case JOIN_PROJECT_EXCEPTION:
                        log.warn("Register new users join team request : team name error");
                        throw new TeamNotFoundException("Team name does not exists");
                    case APPLY_NEW_PROJECT_EXCEPTION:
                        log.warn("Register new users new team request : team name error");
                        throw new ApplyNewProjectException();
                    case REGISTER_TEAM_NAME_DUPLICATE_EXCEPTION:
                        log.warn("Register new users new team request : team name duplicate");
                        throw new RegisterTeamNameDuplicateException();
                    case USERNAME_ALREADY_EXISTS_EXCEPTION:
                        // throw from user service
                    {
                        String email = mainObject.getJSONObject("user").getJSONObject("userDetails").getString("email");
                        log.warn("Register new users : email already exists: {}", email);
                        throw new UsernameAlreadyExistsException("Error: " + email + " already in use.");
                    }
                    case EMAIL_ALREADY_EXISTS_EXCEPTION:
                        // throw from adapter deterlab
                    {
                        String email = mainObject.getJSONObject("user").getJSONObject("userDetails").getString("email");
                        log.warn("Register new users : email already exists: {}", email);
                        throw new EmailAlreadyExistsException("Error: " + email + " already in use.");
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
    private String getTeamIdByName(String teamName) throws WebServiceRuntimeException, TeamNotFoundException, AdapterConnectionException {
        // FIXME check if team name exists
        // FIXME check for general exception?
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getTeamByName(teamName), HttpMethod.GET, request, String.class);

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == ExceptionState.TEAM_NOT_FOUND_EXCEPTION) {
                    log.warn("Get team by name : team name error");
                    throw new TeamNotFoundException("Team name " + teamName + "does not exists");
                } else {
                    log.warn("Team service or adapter connection fail");
                    // possible sio or adapter connection fail
                    throw new AdapterConnectionException(ERR_SERVER_OVERLOAD);
                }

            } else {
                JSONObject object = new JSONObject(responseBody);
                return object.getString("id");
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
                log.error("No such user: {}", session.getAttribute("id"));
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                throw new RestClientException("[" + error.getError() + "] ");
            } else {
                User2 user2 = extractUserInfo(responseBody);
                originalUser = user2;
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
        // Need to be this way to "edit" details
        // If not, the form details will overwrite existing user's details

        boolean errorsFound = false;

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

        if (errorsFound == false && editUser.getJobTitle().isEmpty()) {
            redirectAttributes.addFlashAttribute("editJobTitle", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getInstitution().isEmpty()) {
            redirectAttributes.addFlashAttribute("editInstitution", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getInstitutionAbbreviation().isEmpty()) {
            redirectAttributes.addFlashAttribute("editInstitutionAbbrev", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getInstitutionWeb().isEmpty()) {
            redirectAttributes.addFlashAttribute("editInstitutionWeb", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getAddress1().isEmpty()) {
            redirectAttributes.addFlashAttribute("editAddress1", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getCountry().isEmpty()) {
            redirectAttributes.addFlashAttribute("editCountry", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getCity().isEmpty()) {
            redirectAttributes.addFlashAttribute("editCity", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getRegion().isEmpty()) {
            redirectAttributes.addFlashAttribute("editProvince", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && editUser.getPostalCode().isEmpty()) {
            redirectAttributes.addFlashAttribute("editPostalCode", "fail");
            errorsFound = true;
        }

        if (errorsFound == false && (editUser.getPostalCode().matches("(.*)[a-zA-Z](.*)") || editUser.getPostalCode().length() < 6)) {
            // previously already check if postal code is empty
            // now check postal code must contain only digits
            redirectAttributes.addFlashAttribute("editPostalCode", "fail");
            errorsFound = true;
        }

        if (errorsFound) {
            originalUser = null;
            return "redirect:/account_settings";
        } else {
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
            userDetails.put("institutionAbbreviation", editUser.getInstitutionAbbreviation());
            userDetails.put("institutionWeb", editUser.getInstitutionWeb());

            address.put("address1", editUser.getAddress1());
            address.put("address2", editUser.getAddress2());
            address.put("country", editUser.getCountry());
            address.put("city", editUser.getCity());
            address.put("region", editUser.getRegion());
            address.put("zipCode", editUser.getPostalCode());

            userObject.put("userDetails", userDetails);

            String userId_uri = properties.getSioUsersUrl() + session.getAttribute("id");

            HttpEntity<String> request = createHttpEntityWithBody(userObject.toString());
            ResponseEntity resp = restTemplate.exchange(userId_uri, HttpMethod.PUT, request, String.class);

            if (originalUser != null) {
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
                if (!originalUser.getInstitutionAbbreviation().equals(editUser.getInstitutionAbbreviation())) {
                    redirectAttributes.addFlashAttribute("editInstitutionAbbrev", "success");
                }
                if (!originalUser.getInstitutionWeb().equals(editUser.getInstitutionWeb())) {
                    redirectAttributes.addFlashAttribute("editInstitutionWeb", "success");
                }
                if (!originalUser.getAddress1().equals(editUser.getAddress1())) {
                    redirectAttributes.addFlashAttribute("editAddress1", "success");
                }
                if (!originalUser.getAddress2().equals(editUser.getAddress2())) {
                    redirectAttributes.addFlashAttribute("editAddress2", "success");
                }
                if (!originalUser.getCountry().equals(editUser.getCountry())) {
                    redirectAttributes.addFlashAttribute("editCountry", "success");
                }
                if (!originalUser.getCity().equals(editUser.getCity())) {
                    redirectAttributes.addFlashAttribute("editCity", "success");
                }
                if (!originalUser.getRegion().equals(editUser.getRegion())) {
                    redirectAttributes.addFlashAttribute("editProvince", "success");
                }
                if (!originalUser.getPostalCode().equals(editUser.getPostalCode())) {
                    redirectAttributes.addFlashAttribute("editPostalCode", "success");
                }
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
                        redirectAttributes.addFlashAttribute("editPassword", "fail");
                    } else {
                        redirectAttributes.addFlashAttribute("editPassword", "success");
                    }
                } catch (IOException e) {
                    throw new WebServiceRuntimeException(e.getMessage());
                } finally {
                    originalUser = null;
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
                String teamMemberType = memberObject.getString("memberType");
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
            redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
            return "redirect:/approve_new_user";
        }

        String responseBody = response.getBody().toString();
        if (RestUtil.isError(response.getStatusCode())) {
            try {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                log.warn("Server side error: {}", error.getError());
                redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
                return "redirect:/approve_new_user";
            } catch (IOException ioe) {
                log.warn("IOException {}", ioe);
                throw new WebServiceRuntimeException(ioe.getMessage());
            }
        }
        // everything looks OK?
        String msg = new JSONObject(responseBody).getString("msg");
        if (!"process join request OK".equals(msg)) {
            log.warn("Cannot process join request: {}", msg);
            redirectAttributes.addFlashAttribute("message", "Cannot process join request: " + msg);
            return "redirect:/approve_new_user";
        }
        log.info("Join request has been APPROVED, User {}, Team {}", userId, teamId);
        redirectAttributes.addFlashAttribute("message", "Join request has been APPROVED.");
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
            redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
            return "redirect:/approve_new_user";
        }

        String responseBody = response.getBody().toString();
        if (RestUtil.isError(response.getStatusCode())) {
            try {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                log.warn("Server side error: {}", error.getError());
                redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
                return "redirect:/approve_new_user";
            } catch (IOException ioe) {
                log.warn("IOException {}", ioe);
                throw new WebServiceRuntimeException(ioe.getMessage());
            }
        }
        // everything looks OK?
        String msg = new JSONObject(responseBody).getString("msg");
        if (!"process join request OK".equals(msg)) {
            log.warn("Cannot process join request: {}", msg);
            redirectAttributes.addFlashAttribute("message", "Cannot process join request: " + msg);
            return "redirect:/approve_new_user";
        }
        log.info("Join request has been REJECTED, User {}, Team {}", userId, teamId);
        redirectAttributes.addFlashAttribute("message", "Join request has been REJECTED.");
        return "redirect:/approve_new_user";
    }

    //--------------------------Teams Page--------------------------

    @RequestMapping("/public_teams")
    public String publicTeamsBeforeLogin(Model model) {
        TeamManager2 teamManager2 = new TeamManager2();

        // get public teams
        HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
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

            Map<String, String> savedImageMap = new HashMap<>();
            HttpEntity<String> imageRequest = createHttpEntityWithBody(team2.toString());
            ResponseEntity imageResponse = restTemplate.exchange(properties.getSavedImages(), HttpMethod.GET, teamRequest, String.class);
            String imageResponseBody = imageResponse.getBody().toString();

            System.out.println(imageResponseBody);
        }

        // get public teams
//        HttpEntity<String> teamRequest = createHttpEntityHeaderOnly();
//        ResponseEntity teamResponse = restTemplate.exchange(properties.getTeamsByVisibility(TeamVisibility.PUBLIC.toString()), HttpMethod.GET, teamRequest, String.class);
//        String teamResponseBody = teamResponse.getBody().toString();
//
//        JSONArray teamPublicJsonArray = new JSONArray(teamResponseBody);
//        for (int i = 0; i < teamPublicJsonArray.length(); i++) {
//            JSONObject teamInfoObject = teamPublicJsonArray.getJSONObject(i);
//            Team2 team2 = extractTeamInfo(teamInfoObject.toString());
//            teamManager2.addTeamToPublicTeamMap(team2);
//        }

        model.addAttribute("userEmail", userEmail);
        model.addAttribute("teamMap2", teamManager2.getTeamMap());
//        model.addAttribute("publicTeamMap2", teamManager2.getPublicTeamMap());
        model.addAttribute("userJoinRequestMap", teamManager2.getUserJoinRequestMap());
        return "teams";
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

    @RequestMapping("/withdraw/{teamId}")
    public String withdrawnJoinRequest(@PathVariable Integer teamId, Model model, HttpSession session) {
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

//        model.addAttribute("currentLoggedInUserId", getSessionIdOfLoggedInUser(session));
        model.addAttribute("team", team);
        model.addAttribute("owner", team.getOwner());
        model.addAttribute("membersList", team.getMembersList());
        session.setAttribute("originalTeam", team);
//        model.addAttribute("team", teamManager.getTeamByTeamId(teamId));
//        model.addAttribute("membersMap", teamManager.getTeamByTeamId(teamId).getMembersMap());
//        model.addAttribute("userManager", userManager);
//        model.addAttribute("teamExpMap", experimentManager.getTeamExperimentsMap(teamId));
        // model add attribute team is correct
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

        if (editTeam.getWebsite().isEmpty()) {
            errorsFound = true;
            redirectAttributes.addFlashAttribute("editWebsite", "fail");
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
        teamfields.put("website", editTeam.getWebsite());
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
        if (!originalTeam.getWebsite().equals(editTeam.getWebsite())) {
            redirectAttributes.addFlashAttribute("editWebsite", "success");
        }

        // safer to remove
        session.removeAttribute("originalTeam");
        return "redirect:/team_profile/" + teamId;
    }

    @RequestMapping("/remove_member/{teamId}/{userId}")
    public String removeMember(@PathVariable Integer teamId, @PathVariable Integer userId, Model model) {
        // TODO check if user is indeed in the team
        // TODO what happens to active experiments of the user?
        // remove member from the team
        // reduce the team count
        teamManager.removeMembers(userId, teamId);
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

        if (bindingResult.hasErrors()) {
            log.warn("Existing users apply for new team, form has errors {}", teamPageApplyTeamForm.toString());
            // return "redirect:/teams/apply_team";
            return "team_page_apply_team";
        }
        // log data to ensure data has been parsed
        log.info("Apply for new team info at : " + properties.getRegisterRequestToApplyTeam(session.getAttribute("id").toString()));
        log.info("Team application form: " + teamPageApplyTeamForm.toString());

        JSONObject mainObject = new JSONObject();
        JSONObject teamFields = new JSONObject();
        mainObject.put("team", teamFields);
        teamFields.put("name", teamPageApplyTeamForm.getTeamName());
        teamFields.put("description", teamPageApplyTeamForm.getTeamDescription());
        teamFields.put("website", teamPageApplyTeamForm.getTeamWebsite());
        teamFields.put("organisationType", teamPageApplyTeamForm.getTeamOrganizationType());
        teamFields.put("visibility", teamPageApplyTeamForm.getIsPublic());

        HttpEntity<String> request = createHttpEntityWithBody(mainObject.toString());
        ResponseEntity response = restTemplate.exchange(properties.getRegisterRequestToApplyTeam(session.getAttribute("id").toString()), HttpMethod.POST, request, String.class);

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case APPLY_NEW_PROJECT_EXCEPTION:
                        log.info("Apply new team fail at adapter deterlab");
                        redirectAttributes.addFlashAttribute("message", error.getMessage());
                        break;
                    case REGISTER_TEAM_NAME_DUPLICATE_EXCEPTION:
                        log.info("Apply new team fail: team name already exists", teamPageApplyTeamForm.getTeamName());
                        redirectAttributes.addFlashAttribute("message", "Team name already exists.");
                        break;
                    default:
                        log.info("Apply new team fail: registration service or adapter fail");
                        // possible sio or adapter connection fail
                        redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
                        break;
                }
                return "redirect:/teams/apply_team";

            } else {
                // no errors, everything ok
                log.info("Completed invoking the apply team request service for Team: {}", teamPageApplyTeamForm.getTeamName());
                return "redirect:/teams/team_application_submitted";
            }
        } catch (IOException e) {
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

        if (bindingResult.hasErrors()) {
            log.info("join team request form for team page has errors");
            return "team_page_join_team";
        }
        // log data to ensure data has been parsed
        log.info("--------Join team---------");

        JSONObject mainObject = new JSONObject();
        JSONObject teamFields = new JSONObject();
        JSONObject userFields = new JSONObject();
        mainObject.put("team", teamFields);
        mainObject.put("user", userFields);

        userFields.put("id", session.getAttribute("id")); // ncl-id
        teamFields.put("name", teamPageJoinForm.getTeamName());

        log.info("Calling the registration service to do join team request");
        HttpEntity<String> request = createHttpEntityWithBody(mainObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getJoinRequestExistingUser(), HttpMethod.POST, request, String.class);

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == ExceptionState.TEAM_NOT_FOUND_EXCEPTION) {
                    log.warn("join team request : team name error");
                    redirectAttributes.addFlashAttribute("message", "Team name does not exists.");
                } else {
                    log.warn("join team request : some other failure");
                    // possible sio or adapter connection fail
                    redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
                }
                return "redirect:/teams/join_team";
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        log.info("Completed invoking the join team request service for Team: {}", teamPageJoinForm.getTeamName());
        return "redirect:/teams/join_application_submitted/" + teamPageJoinForm.getTeamName();
    }

    //--------------------------Experiment Page--------------------------

    @RequestMapping(value = "/experiments", method = RequestMethod.GET)
    public String experiments(Model model, HttpSession session) {
//        long start = System.currentTimeMillis();
        List<Experiment2> experimentList = new ArrayList<>();
        Map<Long, Realization> realizationMap = new HashMap<>();

        // get list of teamids
        HttpEntity<String> request = createHttpEntityHeaderOnly();
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
            redirectAttributes.addFlashAttribute("message", "Experiment Name cannot be empty");
            return "redirect:/experiments/create";
        }

        if (experimentForm.getDescription() == null || experimentForm.getDescription().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Description cannot be empty");
            return "redirect:/experiments/create";
        }

        experimentForm.setScenarioContents(getScenarioContentsFromFile(experimentForm.getScenarioFileName()));

        JSONObject experimentObject = new JSONObject();
        experimentObject.put("userId", session.getAttribute("id").toString());
        experimentObject.put("teamId", experimentForm.getTeamId());
        experimentObject.put("teamName", experimentForm.getTeamName());
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
                        redirectAttributes.addFlashAttribute("message", "There is an error when parsing the NS File.");
                        break;
                    case EXP_NAME_ALREADY_EXISTS_EXCEPTION:
                    case EXPERIMENT_NAME_IN_USE_EXCEPTION:
                        log.warn("Exp name already exists");
                        redirectAttributes.addFlashAttribute("message", "Experiment name already exists.");
                        break;
                    default:
                        log.warn("Exp service or adapter fail");
                        // possible sio or adapter connection fail
                        redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
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
//				redirectAttributes.addFlashAttribute("message",
//						"You successfully uploaded " + networkFile.getOriginalFilename() + "!");
//				// remember network file name here
//			}
//			catch (Exception e) {
//				redirectAttributes.addFlashAttribute("message",
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
        // TODO check userid is indeed the experiment owner or team owner
        // ensure experiment is stopped first
        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        // check valid authentication to remove experiments
        if (!validateIfAdmin(session) && !realization.getUserId().equals(session.getAttribute("id").toString())) {
            log.warn("Permission denied when remove Team:{}, Experiment: {} with User: {}, Role:{}", teamId, expId, session.getAttribute("id"), session.getAttribute(session_roles));
            redirectAttributes.addFlashAttribute("message", "An error occurred while trying to remove experiment;" + permissionDeniedMessage);
            return "redirect:/experiments";
        }

        if (!realization.getState().equals(RealizationState.NOT_RUNNING.toString())) {
            log.warn("Trying to remove Team: {}, Experiment: {} with State: {} that is still in progress?", teamId, expId, realization.getState());
            redirectAttributes.addFlashAttribute("message", "An error occurred while trying to remove Exp: " + realization.getExperimentName() + ". Please refresh the page again. If the error persists, please contact " + CONTACT_EMAIL);
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
            redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
            return "redirect:/experiments";
        }

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case EXP_DELETE_EXCEPTION:
                    case FORBIDDEN_EXCEPTION:
                        log.warn("remove experiment failed for Team: {}, Exp: {}", teamId, expId);
                        redirectAttributes.addFlashAttribute("message", error.getMessage());
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
            redirectAttributes.addFlashAttribute("message", permissionDeniedMessage);
            return "redirect:/experiments";
        }

        if (!realization.getState().equals(RealizationState.NOT_RUNNING.toString())) {
            log.warn("Trying to start Team: {}, Experiment: {} with State: {} that is not running?", teamName, expId, realization.getState());
            redirectAttributes.addFlashAttribute("message", "An error occurred while trying to start Exp: " + realization.getExperimentName() + ". Please refresh the page again. If the error persists, please contact " + CONTACT_EMAIL);
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
            redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
            return "redirect:/experiments";
        }

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case EXP_START_EXCEPTION:
                    case FORBIDDEN_EXCEPTION:
                        log.warn("start experiment failed for Team: {}, Exp: {}", teamName, expId);
                        redirectAttributes.addFlashAttribute("message", error.getMessage());
                        return "redirect:/experiments";
                    default:
                        // do nothing
                        break;
                }
                // possible for it to be error but experiment has started up finish
                // if user clicks on start but reloads the page
//                model.addAttribute("exp_message", "Team: " + teamName + " has started Exp: " + realization.getExperimentName());
                return "/experiments";
            } else {
                // everything ok
                log.info("start experiment success for Team: {}, Exp: {}", teamName, expId);
                redirectAttributes.addFlashAttribute("exp_message", "Team: " + teamName + " has started Exp: " + realization.getExperimentName());
                return "redirect:/experiments";
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    @RequestMapping("/stop_experiment/{teamName}/{expId}")
    public String stopExperiment(@PathVariable String teamName, @PathVariable String expId, Model model, final RedirectAttributes redirectAttributes, HttpSession session) throws WebServiceRuntimeException {

        // ensure experiment is active first before stopping
        Realization realization = invokeAndExtractRealization(teamName, Long.parseLong(expId));

        if (isNotAdminAndNotInTeam(session, realization)) {
            log.warn("Permission denied to stop experiment: {} for team: {}", realization.getExperimentName(), teamName);
            redirectAttributes.addFlashAttribute("message", permissionDeniedMessage);
            return "redirect:/experiments";
        }

        if (!realization.getState().equals(RealizationState.RUNNING.toString())) {
            log.warn("Trying to stop Team: {}, Experiment: {} with State: {} that is still in progress?", teamName, expId, realization.getState());
            redirectAttributes.addFlashAttribute("message", "An error occurred while trying to stop Exp: " + realization.getExperimentName() + ". Please refresh the page again. If the error persists, please contact " + CONTACT_EMAIL);
            return "redirect:/experiments";
        }

        log.info("Stopping experiment: at " + properties.getStopExperiment(teamName, expId));
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response;

        return abc(teamName, expId, redirectAttributes, realization, request);
    }

    private String abc(@PathVariable String teamName, @PathVariable String expId, RedirectAttributes redirectAttributes, Realization realization, HttpEntity<String> request) throws WebServiceRuntimeException {
        ResponseEntity response;
        try {
            response = restTemplate.exchange(properties.getStopExperiment(teamName, expId), HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            log.warn("Error connecting to experiment service to stop experiment", e.getMessage());
            redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
            return "redirect:/experiments";
        }

        String responseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == ExceptionState.FORBIDDEN_EXCEPTION) {
                    log.warn("Permission denied to stop experiment: {} for team: {}", realization.getExperimentName(), teamName);
                    redirectAttributes.addFlashAttribute("message", permissionDeniedMessage);
                }
            } else {
                // everything ok
                log.info("stop experiment success for Team: {}, Exp: {}", teamName, expId);
                redirectAttributes.addFlashAttribute("exp_message", "Team: " + teamName + " has stopped Exp: " + realization.getExperimentName());
            }
            return "redirect:/experiments";
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }

    private boolean isNotAdminAndNotInTeam(HttpSession session, Realization realization) {
        return !validateIfAdmin(session) && !checkPermissionRealizeExperiment(realization, session);
    }

    //---------------------------------Dataset Page--------------------------

    @RequestMapping("/data")
    public String data(Model model, HttpSession session) throws Exception {
        DatasetManager datasetManager = new DatasetManager();

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getData(), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody().toString();

        JSONArray dataJsonArray = new JSONArray(dataResponseBody);
        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject dataInfoObject = dataJsonArray.getJSONObject(i);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            datasetManager.addDataset(dataset);
        }

    	model.addAttribute("allDataMap", datasetManager.getDatasetMap());
    	return "data";
    }

//    @RequestMapping(value="/data/contribute", method=RequestMethod.GET)
//    public String contributeData(Model model) {
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
//    	return "contribute_data";
//    }

//    @RequestMapping(value="/data/contribute", method=RequestMethod.POST)
//    public String validateContributeData(@ModelAttribute("dataset") Dataset dataset, HttpSession session, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
//    	// TODO
//    	// validation
//    	// get file from user upload to server
//        BufferedOutputStream stream = null;
//        FileOutputStream fileOutputStream = null;
//
//		if (!file.isEmpty()) {
//			try {
//				String fileName = getSessionIdOfLoggedInUser(session) + "-" + file.getOriginalFilename();
//                fileOutputStream = new FileOutputStream(new File(App.ROOT + "/" + fileName));
//				stream = new BufferedOutputStream(fileOutputStream);
//                FileCopyUtils.copy(file.getInputStream(), stream);
//				stream.close();
//				redirectAttributes.addFlashAttribute("message",
//						"You successfully uploaded " + file.getOriginalFilename() + "!");
//				datasetManager.addDataset(getSessionIdOfLoggedInUser(session), dataset, file.getOriginalFilename());
//			}
//			catch (Exception e) {
//				redirectAttributes.addFlashAttribute("message",
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
//			redirectAttributes.addFlashAttribute("message",
//					"You failed to upload " + file.getOriginalFilename() + " because the file was empty");
//		}
//    	return "redirect:/data";
//    }

//    @RequestMapping(value="/data/edit/{datasetId}", method=RequestMethod.GET)
//    public String datasetInfo(@PathVariable Integer datasetId, Model model) {
//    	Dataset dataset = datasetManager.getDataset(datasetId);
//    	model.addAttribute("editDataset", dataset);
//    	return "edit_data";
//    }

//    @RequestMapping(value="/data/edit/{datasetId}", method=RequestMethod.POST)
//    public String editDatasetInfo(@PathVariable Integer datasetId, @ModelAttribute("editDataset") Dataset dataset, final RedirectAttributes redirectAttributes) {
//    	Dataset origDataset = datasetManager.getDataset(datasetId);
//
//    	String editedDatasetName = dataset.getDatasetName();
//    	String editedDatasetDesc = dataset.getDatasetDescription();
//    	String editedDatasetLicense = dataset.getLicense();
//    	String editedDatasetPublic = dataset.getIsPublic();
//    	boolean editedDatasetIsRequiredAuthorization = dataset.getRequireAuthorization();
//
//    	System.out.println(origDataset.getDatasetId());
//    	System.out.println(dataset.getDatasetId());
//
//    	if (origDataset.updateName(editedDatasetName) == true) {
//    		redirectAttributes.addFlashAttribute("editName", "success");
//    	}
//
//    	if (origDataset.updateDescription(editedDatasetDesc) == true) {
//    		redirectAttributes.addFlashAttribute("editDesc", "success");
//    	}
//
//    	if (origDataset.updateLicense(editedDatasetLicense) == true) {
//    		redirectAttributes.addFlashAttribute("editLicense", "success");
//    	}
//
//    	if (origDataset.updatePublic(editedDatasetPublic) == true) {
//    		redirectAttributes.addFlashAttribute("editPublic", "success");
//    	}
//
//    	if (origDataset.updateAuthorization(editedDatasetIsRequiredAuthorization) == true) {
//    		redirectAttributes.addFlashAttribute("editIsRequiredAuthorization", "success");
//    	}
//
//    	datasetManager.updateDatasetDetails(origDataset);
//
//    	return "redirect:/data/edit/{datasetId}";
//    }

//    @RequestMapping("/data/remove_dataset/{datasetId}")
//    public String removeDataset(@PathVariable Integer datasetId) {
//    	datasetManager.removeDataset(datasetId);
//    	return "redirect:/data";
//    }

    @RequestMapping("/data/public")
    public String getPublicDatasets(Model model) throws Exception {
        DatasetManager datasetManager = new DatasetManager();

        HttpEntity<String> dataRequest = createHttpEntityHeaderOnly();
        ResponseEntity dataResponse = restTemplate.exchange(properties.getPublicData(), HttpMethod.GET, dataRequest, String.class);
        String dataResponseBody = dataResponse.getBody().toString();

        JSONArray dataPublicJsonArray = new JSONArray(dataResponseBody);
        for (int i = 0; i < dataPublicJsonArray.length(); i++) {
            JSONObject dataInfoObject = dataPublicJsonArray.getJSONObject(i);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            datasetManager.addDataset(dataset);
        }

    	model.addAttribute("publicDataMap", datasetManager.getDatasetMap());
    	return "data_public";
    }

//    @RequestMapping("/data/public/request_access/{dataOwnerId}")
//    public String requestAccessForDataset(@PathVariable Integer dataOwnerId, Model model) {
//    	// TODO
//    	// send reuqest to team owner
//    	// show feedback to users
//    	User rv = userManager.getUserById(dataOwnerId);
//    	model.addAttribute("ownerName", rv.getError());
//    	model.addAttribute("ownerEmail", rv.getEmail());
//    	return "data_request_access";
//    }

    //-----------------------------------------------------------------------
    //--------------------------Admin Revamp---------------------------------
    //-----------------------------------------------------------------------
    //---------------------------------Admin---------------------------------
    @RequestMapping("/admin")
    public String admin(Model model, HttpSession session) {

        if (!validateIfAdmin(session)) {
            return "nopermission";
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
            if (one.getStatus().equals(TeamStatus.PENDING.toString())) {
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
            return "nopermission";
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
                case ID_NULL_OR_EMPTY_EXCEPTION:
                    log.warn("Approve team: TeamId or UserId cannot be null or empty. TeamId: {}, UserId: {}",
                            teamId, teamOwnerId);
                    redirectAttributes.addFlashAttribute("message", "TeamId or UserId cannot be null or empty");
                    break;
                case INVALID_TEAM_STATUS_EXCEPTION:
                    log.warn("Approve team: TeamStatus is invalid");
                    redirectAttributes.addFlashAttribute("message", "Team status is invalid");
                    break;
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn("Approve team: Team {} not found", teamId);
                    redirectAttributes.addFlashAttribute("message", "Team does not exist");
                    break;
                default:
                    log.warn("Approve team : sio or deterlab adapter connection error");
                    // possible sio or adapter connection fail
                    redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
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
            redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
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
            return "nopermission";
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
                case ID_NULL_OR_EMPTY_EXCEPTION:
                    log.warn("Reject team: TeamId or UserId cannot be null or empty. TeamId: {}, UserId: {}",
                            teamId, teamOwnerId);
                    redirectAttributes.addFlashAttribute("message", "TeamId or UserId cannot be null or empty");
                    break;
                case INVALID_TEAM_STATUS_EXCEPTION:
                    log.warn("Reject team: TeamStatus is invalid");
                    redirectAttributes.addFlashAttribute("message", "Team status is invalid");
                    break;
                case TEAM_NOT_FOUND_EXCEPTION:
                    log.warn("Reject team: Team {} not found", teamId);
                    redirectAttributes.addFlashAttribute("message", "Team does not exist");
                    break;
                default:
                    log.warn("Reject team : sio or deterlab adapter connection error");
                    // possible sio or adapter connection fail
                    redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
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
            redirectAttributes.addFlashAttribute("message", ERR_SERVER_OVERLOAD);
        }
        return "redirect:/admin";
    }

//    @RequestMapping("/admin/users/ban/{userId}")
//    public String banUser(@PathVariable Integer userId) {
//    	// TODO
//    	// perform ban action here
//    	// need to cleanly remove user info from teams, user. etc
//    	return "redirect:/admin";
//    }

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
//				redirectAttributes.addFlashAttribute("message",
//						"You successfully uploaded " + file.getOriginalFilename() + "!");
//				datasetManager.addDataset(getSessionIdOfLoggedInUser(session), dataset, file.getOriginalFilename());
//			}
//			catch (Exception e) {
//				redirectAttributes.addFlashAttribute("message",
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
//			redirectAttributes.addFlashAttribute("message",
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
        log.info("Join application submitted");
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
    public String teamAppSubmit(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("signUpMergedForm", new SignUpMergedForm());
        return "team_application_submitted";
    }

    @RequestMapping("/join_application_submitted/{teamName}")
    public String joinTeamAppSubmit(@PathVariable String teamName, Model model) throws WebServiceRuntimeException {
        log.info("Register new user join application submitted");
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
                        log.warn("Register new user join application request : team name error");
                        break;
                    default:
                        log.warn("Register new user join application request : some other failure");
                        // possible sio or adapter connection fail
                        break;
                }
                return "redirect:/signup2";
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }

        Team2 one = extractTeamInfo(responseBody);
        model.addAttribute("team", one);
        return "join_team_application_submitted";
    }

    @RequestMapping("/email_not_validated")
    public String emailNotValidated() {
        return "email_not_validated";
    }

    @RequestMapping("/team_application_under_review")
    public String teamAppUnderReview() {
        return "team_application_under_review";
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
        scenarioFileNameList.add("Scenario 1 - A single node");
        scenarioFileNameList.add("Scenario 2 - Two nodes linked with a 10Gbps link");
        scenarioFileNameList.add("Scenario 3 - Three nodes in a star topology");
//        scenarioFileNameList.add("Scenario 4 - Two nodes linked with a 10Gbps SDN switch");
//        scenarioFileNameList.add("Scenario 5 - Three nodes with Blockchain capabilities");
        log.info("Scenario file list: {}", scenarioFileNameList);
        return scenarioFileNameList;
    }

    private String getScenarioContentsFromFile(String scenarioFileName) throws WebServiceRuntimeException {
        // FIXME: switch to better way of referencing scenario descriptions to actual filenames
        String actualScenarioFileName;
        if (scenarioFileName.contains("Scenario 1")) {
            actualScenarioFileName = "basic.ns";
        } else if (scenarioFileName.contains("Scenario 2")) {
            actualScenarioFileName = "basic2.ns";
        } else if (scenarioFileName.contains("Scenario 3")) {
            actualScenarioFileName = "basic3.ns";
        } else {
            // defaults to basic single node
            actualScenarioFileName = "basic.ns";
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
            String teamMemberType = memberObject.getString("memberType");
            String teamMemberStatus = memberObject.getString("memberStatus");

            User2 myUser = invokeAndExtractUserInfo(userId);
            if (teamMemberType.equals(MemberType.MEMBER.toString())) {
                team2.addMembers(myUser);

                // add to pending members list for Members Awaiting Approval function
                if (teamMemberStatus.equals(MemberStatus.PENDING.toString())) {
                    team2.addPendingMembers(myUser);
                }

            } else if (teamMemberType.equals(MemberType.OWNER.toString())) {
                // explicit safer check
                team2.setOwner(myUser);
            }
        }
        team2.setMembersCount(membersArray.length());
        return team2;
    }

    private Dataset extractDataInfo(String json) throws Exception {
        log.debug(json);

        JSONObject object = new JSONObject(json);
        Dataset dataset = new Dataset();

        dataset.setId(object.getInt("id"));
        dataset.setName(object.getString("name"));
        dataset.setDescription(object.getString("description"));
        dataset.setContributorId(object.getString("contributorId"));
        dataset.setVisibility(object.getString("visibility"));
        dataset.setAccessibility(object.getString("accessibility"));
        dataset.setReleaseDate(formatZonedDateTime(object.get("releaseDate").toString()));

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

    // use to extract JSON Strings from services
    // in the case where the JSON Strings are null, return "Connection Error"
    private String getJSONStr(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return "Connection Error";
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

    private User2 invokeAndExtractUserInfo(String userId) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
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
        experiment2.setTeamName(object.getString("teamName"));
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
        ResponseEntity response;

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
                return extractRealization(responseBody);
            }
        } catch (Exception e) {
            return getCleanRealization();
        }
    }

    private Realization extractRealization(String json) {
        Realization realization = new Realization();
        JSONObject object = new JSONObject(json);

        realization.setExperimentId(object.getLong("experimentId"));
        realization.setExperimentName(object.getString("experimentName"));
        realization.setUserId(object.getString("userId"));
        realization.setTeamId(object.getString("teamId"));
        realization.setState(object.getString("state"));

        String exp_report = "";

        if (object.get("details") == null) {
            realization.setDetails("");
        } else {
            exp_report = object.get("details").toString().replaceAll("@", "\\\r\\\n");
            realization.setDetails(exp_report);
        }

        return realization;
    }

    /**
     * @param zonedDateTimeJSON JSON string
     * @return a date in the format MMM-d-yyyy
     */
    private String formatZonedDateTime(String zonedDateTimeJSON) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ZonedDateTime zonedDateTime = mapper.readValue(zonedDateTimeJSON, ZonedDateTime.class);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM-d-yyyy");
        return zonedDateTime.format(format);
    }

    /**
     * Creates a HttpEntity with a request body and header
     *
     * @param jsonString The JSON request converted to string
     * @return A HttpEntity request
     * @implNote Authorization header must be set to the JwTToken in the format [Bearer: TOKEN_ID]
     * @see HttpEntity createHttpEntityHeaderOnly() for request with only header
     */
    private HttpEntity<String> createHttpEntityWithBody(String jsonString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);
        return new HttpEntity<>(jsonString, headers);
    }

    /**
     * Creates a HttpEntity that contains only a header and empty body
     *
     * @return A HttpEntity request
     * @implNote Authorization header must be set to the JwTToken in the format [Bearer: TOKEN_ID]
     * @see HttpEntity createHttpEntityWithBody() for request with both body and header
     */
    private HttpEntity<String> createHttpEntityHeaderOnly() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);
        return new HttpEntity<>(headers);
    }

    private void setSessionVariables(HttpSession session, String loginEmail, String id, String firstName, String userRoles) {
        User2 user = invokeAndExtractUserInfo(id);
        session.setAttribute("sessionLoggedEmail", loginEmail);
        session.setAttribute("id", id);
        session.setAttribute("name", firstName);
        session.setAttribute(session_roles, userRoles);
        log.info("Session variables - sessionLoggedEmail: {}, id: {}, name: {}, roles: {}", loginEmail, id, user.getFirstName(), userRoles);
    }

    private void removeSessionVariables(HttpSession session) {
        session.removeAttribute("sessionLoggedEmail");
        session.removeAttribute("id");
        session.removeAttribute("name");
        session.removeAttribute(session_roles);
        AUTHORIZATION_HEADER = null;
    }

    private boolean validateIfAdmin(HttpSession session) {
        log.info("User: {} is logged on as: {}", session.getAttribute("sessionLoggedEmail"), session.getAttribute(session_roles));
        return session.getAttribute(session_roles).equals(UserType.ADMIN.toString());
    }

    /**
     * Ensure that only users of the team can realize or un-realize experiment
     * A pre-condition is that the users must be approved.
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

    private Realization getCleanRealization() {
        Realization realization = new Realization();

        realization.setExperimentId(0L);
        realization.setExperimentName("");
        realization.setUserId("");
        realization.setTeamId("");
        realization.setState(RealizationState.ERROR.toString());
        realization.setDetails("");

        return realization;
    }

    /**
     * Computes the number of teams that the user is in and the number of running experiments to populate data for the user dashboard
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
        userDashboardStats.put(USER_DASHBOARD_FREE_NODES, getNumberOfFreeNodes());
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

    private int getNumberOfFreeNodes() {
        String freeNodes;
        log.info("Retrieving number of free nodes from: {}", properties.getFreeNodes());
        try {
            String jsonResult = restTemplate.getForObject(properties.getFreeNodes(), String.class);
            JSONObject object = new JSONObject(jsonResult);
            freeNodes = object.getString("numberOfFreeNodes");
        } catch (RestClientException e) {
            log.warn("Error connecting to service-telemetry: {}", e);
            freeNodes = "0";
        }
        return Integer.parseInt(freeNodes);
    }
}