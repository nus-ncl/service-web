package sg.ncl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import javax.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriComponentsBuilder;
import sg.ncl.domain.ExceptionState;
import sg.ncl.domain.UserStatus;
import sg.ncl.domain.UserType;
import sg.ncl.exceptions.EmailAlreadyExistsException;
import sg.ncl.exceptions.InvalidPasswordException;
import sg.ncl.exceptions.UsernameAlreadyExistsException;
import sg.ncl.exceptions.WebServiceRuntimeException;
import sg.ncl.testbed_interface.*;
import sg.ncl.webssh.PtyProperties;
import sg.ncl.webssh.VncProperties;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String CONTACT_EMAIL = "contact_email";
    private static final String CONTACT_US_FORM = "contactUsForm";
    private static final String RECAPTCHA_SECRET_KEY = "secret_key";
    private static final String BookNode_FORM = "BookNodeForm";
    private static final String ERR_SERVER_OVERLOAD = "There is a problem with your request. Please contact " + CONTACT_EMAIL;
    private static final String SIGNUP_PAGE = "register";
    private static final String SIGNUP_FORM = "SignupForm";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String FNAME = "firstName";
    private static final String LNAME = "lastName";
    private static final String JOB_TITLE = "jobTitle";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String INSTITUTION = "institution";
    private static final String COUNTRY = "country";
    private static final String INSTITUTION_ABBREVIATION = "institutionAbbreviation";
    private static final String INSTITUTION_WEB = "institutionWeb";
    private static final String ADDRESS = "address";
    private static final String ADDRESS1 = "address1";
    private static final String ADDRESS2 = "address2";
    private static final String REGION = "region";
    private static final String CITY = "city";
    private static final String ZIP_CODE = "zipCode";
    private static final String USER_DETAILS = "userDetails";
    private static final String APPLICATION_DATE = "applicationDate";
    private static final String ERROR_PREFIX = "Error: ";
    private static final String MESSAGE = "message";
    private static final String REDIRECT_SIGNUP = "redirect:/register";
    private static final String LOGIN_PAGE = "login";
    private static final String ERR_INVALID_CREDENTIALS = "Login failed: Invalid email/password.";
    private static final String LOG_IOEXCEPTION = "IOException {}";
    private static final String CSRF_TOKEN = "csrfToken";
    private static final String STATUS = "status";
    private static final String CONNECTION_ERROR = "Connection Error";
    private static final String MESSAGE_SUCCESS = "messageSuccess";
    private static final String USER_PREFIX = "User ";

    @Autowired
    private MailServices emailService;

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
    protected ConnectionProperties properties;

    @Inject
    protected AccountingProperties accountingProperties;

    @Inject
    protected WebProperties webProperties;

    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    protected PtyProperties ptyProperties;

    @Inject
    protected HttpSession httpScopedSession;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/services_tools")
    public String servicesTools() {
        return "services_tools";
    }

    @RequestMapping("/cue")
    public String clusterUserEmulatorSystem(Model model) throws IOException {
        return "cue";
    }

    @RequestMapping("/ctf")
    public String capturetheflag(Model model) throws IOException {
        return "ctf";
    }

    @RequestMapping("/railway")
    public String railwayEmulatorSystem(Model model) throws IOException {
        return "railway";
    }

    @RequestMapping("/people")
    public String people() {
        return "people";
    }

    @RequestMapping("/pricing")
    public String pricing() {
        return "pricing";
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

    @RequestMapping("/password_reset_email")
    public String passwordRestEmail() {
        return "password_reset_email";
    }

    @RequestMapping("/contactus")
    public String contactus() {
        return "contactus";
    }

    @RequestMapping("/pastevents")
    public String pastevents() {
        return "pastevents";
    }

    @RequestMapping("/recentevents")
    public String resentevents() {
        return "recentevents";
    }

    @RequestMapping("/scholarship")
    public String scholarship() {
        return "scholarship";
    }

    @RequestMapping("/crossedswords")
    public String crossedswordsevent() {
        return "croswordevent";
    }

    @RequestMapping("/cidex")
    public String cidexevent() {
        return "cidexevent";
    }

    @RequestMapping("/lockedshields")
    public String lockedshields() {
        return "lockedShields";
    }

    @RequestMapping("/ijco")
    public String ijco() {
        return "ijco";
    }

    @RequestMapping("/howtoapply")
    public String howtoapply() {
        return "howtoapply";
    }

    @RequestMapping("/scion")
    public String scion() {
        return "scion";
    }

    @PostMapping("/submitRequest")
    public String submitForm(BookNodeForm bookForm, Model model) {
        return "result";
    }

    /*********  start contactus region ********/

    @PostMapping("/sendMessage")
    @ResponseBody
    public String sendContactMessage(@ModelAttribute(CONTACT_US_FORM) ContactUsForm contactForm, Model model)
            throws IOException {

        log.info("Entering to sendMessage");

        String fullName = contactForm.getFullName();
        String email = contactForm.getEmail();
        String message = contactForm.getMessage();
        String recaptchaResponse = contactForm.getG_captcha();
        String responseMessage;

        log.info("value from contact-us-form" + fullName + "," + email + "," + message);
        log.info("value from contact-us-form" + recaptchaResponse);

        // if ( verifyRecaptcha(recaptchaResponse)) {
        if (true) {
            try {
                String auto = "\nThis is auto generate email.\n\n\nRegards,\n" +
                        "NCL Operations";
                String subject = "Contact Us New Message from " + fullName;
                String messageContent = "Incoming New Email via Contact Us \nFull Name: " +
                        fullName + "\nEmail: " + email + "\nMessage: " + message + auto;

                emailService.sendEmail(subject, email, messageContent);
                responseMessage = "Thank you for contacting us. We will get back to you within one working day.";
                log.info("Verify Thank you");

            } catch (MessagingException e) {
                //}catch (Exception ex) {
                responseMessage = "Failed to send the message. Please try again later.";
                log.info("Verify Failed Message");

            }
        } else {
            log.info("reCaPTCHA complete Message");
            responseMessage = "Please complete the reCAPTCHA.";
        }
        //}
        return responseMessage;

    }

    public boolean verifyRecaptcha(String recaptchaResponse) throws IOException {
        log.info("Enter to verify check");
        String url = "https://www.google.com/recaptcha/api/siteverify";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String postParams = "secret=6LdmW_spAAAAAC4y_gzn4LPyZ_4TMIdMI8aAB8C9" + "&response=" + recaptchaResponse;

        try (OutputStream os = con.getOutputStream()) {
            os.write(postParams.getBytes());
            os.flush();
        }

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(con.getInputStream())) {
                String response = scanner.useDelimiter("\\A").next();
                JSONObject jsonResponse = new JSONObject(response);
                return jsonResponse.getBoolean("success");
            }
        } else {
            //return false;
            return true;
        }

    }

/*********  end contactus region ********/

    /*********  start priceList sendEmail region ********/

    @PostMapping("/sendPriceListMessage")
    @ResponseBody
    public String sendPriceListMessage(
            @RequestParam("compute") String compute,
            @RequestParam("gpu") String gpu,
            @RequestParam("name") String fullName,
            @RequestParam("email") String email,
            @RequestParam("startDate") String startdate,
            @RequestParam("endDate") String enddate,
            @RequestParam("message") String message) {

        log.info("Entering to sendPriceListMessage");

        String responseMessage;
        String typeofBook = "";

        if (!compute.equals("")) {
            typeofBook = compute;
        } else {
            typeofBook = gpu;
        }
        // if ( verifyRecaptcha(recaptchaResponse)) {
        if (true) {
            try {
                String auto = "\nThis is auto generate email.\n\n\nRegards,\n" +
                        "NCL Operations";
                String subject = "PriceList - New Message from " + fullName;
                String messageContent = "Incoming New Booking Email via Price List \nFull Name: " +
                        fullName +
                        "\nEmail: " + email +
                        "\nTypeofBook: " + typeofBook +
                        "\nStart Date: " + startdate +
                        "\nEnd Date: " + enddate +
                        "\nMessage: " + message +
                        auto;

                emailService.sendEmail(subject, email, messageContent);
                responseMessage = "Thank you for contacting us. We will get back to you within one working day.";
                log.info("Verify Thank you");

            } catch (MessagingException e) {
                //}catch (Exception ex) {
                responseMessage = "Failed to send the message. Please try again later.";
                log.info("Verify Failed Message");

            }
        } else {
            log.info("reCaPTCHA complete Message");
            responseMessage = "Please complete the reCAPTCHA.";
        }
        //}
        return responseMessage;

    }
/*********  end priceList sendEmail region ********/

    /*********  start signup region ********/

    @RequestMapping(path = "/checkUsername", params = {"username"})
    @ResponseBody
    public String checkUsername(
            @NotNull @RequestParam("username") final String username) throws WebServiceRuntimeException {
        log.info("entering check UserName function");

        String responseMsg;
        Pattern spclCharacter = Pattern.compile("^[a-zA-Z0-9-]*$");
        Matcher hasSpclCharacter = spclCharacter.matcher(username);
        log.info("username = {}", username);

        if (username.isEmpty())
            responseMsg = "Please enter a user name";
        else if (!hasSpclCharacter.find())
            responseMsg = "User name cannot have special characters";
        else {
            String uidResponse = callUserNameValidate(username);

            if (uidResponse.equals("username not found")) {
                responseMsg = "Username " + username + " is available";
            } else if (uidResponse.equals("username occupied")) {
                responseMsg = "This username " + username + " is taken. Please select other username";
            } else {
                responseMsg = "Connection not available right now , please try again later";
                log.warn("Registration connection fail");
                throw new WebServiceRuntimeException(ERR_SERVER_OVERLOAD);
            }
        }
        return responseMsg;
    }

    /*********  call API - check UserName ********/
    public String callUserNameValidate(String username) {

        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity<String> response = restTemplate.exchange(properties.getRegUidAvailaibleUrl(username), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody();
        JSONObject jsonObject = new JSONObject(responseBody);
        String uidResponse = jsonObject.getString("message");
        return uidResponse;
    }

    @GetMapping(value = "/register")
    public String signUp(Model model, HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if (inputFlashMap != null) {
            String message = (String) inputFlashMap.get("message");
            SignupForm signUpForm = (SignupForm) inputFlashMap.get("signUpForm");

            if (message != null) {
                model.addAttribute("message", message);
            }
            if (signUpForm != null) {
                model.addAttribute("signUpForm", signUpForm);
            } else {
                model.addAttribute("signUpForm", new SignupForm());
            }
        } else {
            model.addAttribute("signUpForm", new SignupForm());
        }

        return "register";
    }


    @PostMapping(value = "/register")
    public String registerAccount(@Valid @ModelAttribute("signUpForm") SignupForm signUpForm,
                                  BindingResult bindingResult, final RedirectAttributes redirectAttributes, HttpSession session)
            throws WebServiceRuntimeException {

        log.info("entering to validateDetials");

        if (bindingResult.hasErrors()) {

            log.info("bindResult of signUPForm data", signUpForm.getUsername() + "");
            redirectAttributes.addFlashAttribute(MESSAGE, "Please provide ");

            return "register"; // return to the form with error messages
        }

        log.info("parameter of signUPForm data", signUpForm.getUsername() + "");


        JSONObject registerObject = new JSONObject();
        JSONObject credentialsFields = new JSONObject();

        credentialsFields.put(USERNAME, signUpForm.getEmail().trim());
        credentialsFields.put(PASSWORD, signUpForm.getPassword());

        // create the user JSON
        JSONObject userFields = new JSONObject();
        JSONObject userDetails = new JSONObject();
        JSONObject addressDetails = new JSONObject();

        // create the user JSON
        userDetails.put(FNAME, signUpForm.getFirstName().trim());
        userDetails.put(LNAME, signUpForm.getLastName().trim());
        userDetails.put(JOB_TITLE, signUpForm.getJobTitle().trim());
        userDetails.put(EMAIL, signUpForm.getEmail().trim());
        userDetails.put(PHONE, signUpForm.getPhone().trim());
        userDetails.put(INSTITUTION, signUpForm.getInstitution().trim());
        userDetails.put(INSTITUTION_ABBREVIATION, signUpForm.getInstitutionAbbreviation().trim());
        userDetails.put(INSTITUTION_WEB, signUpForm.getInstitutionWeb().trim());
        userDetails.put(ADDRESS, addressDetails);
        addressDetails.put(ADDRESS1, signUpForm.getAddress1().trim());
        addressDetails.put(ADDRESS2, signUpForm.getAddress2().trim());
        addressDetails.put(COUNTRY, signUpForm.getCountry().trim());
        addressDetails.put(REGION, signUpForm.getRegion().trim());
        addressDetails.put(CITY, signUpForm.getCity().trim());
        addressDetails.put(ZIP_CODE, signUpForm.getZipCode().trim());
        userFields.put(USER_DETAILS, userDetails);
        userFields.put(APPLICATION_DATE, ZonedDateTime.now());

        // add all to main json
        registerObject.put("credentials", credentialsFields);
        registerObject.put("user", userFields);
        registerObject.put("uid", signUpForm.getUsername());

        try {

            log.info("parameter json parameter : {}", registerObject.toString());

            registerUser(registerObject);
            session.setAttribute(webProperties.getSessionUserUid(), signUpForm.getUsername());
            return "redirect:/register_submitted";

        } catch (UsernameAlreadyExistsException |
                EmailAlreadyExistsException |
                InvalidPasswordException |
                WebServiceRuntimeException e) {
            log.info(" error Message", e.getMessage());

            redirectAttributes.addFlashAttribute(MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute(SIGNUP_FORM, signUpForm);
            return "redirect:/register";
        }
    }


    @RequestMapping("/register_submitted")
    public String registerSubmitted() {
        return "register_submitted";
    }

    /**
     * Use when registering new accounts
     *
     * @param mainObject A JSONObject that contains user's credentials, personal details and team application details
     */
    private void registerUser(JSONObject mainObject) throws
            UsernameAlreadyExistsException,
            EmailAlreadyExistsException,
            InvalidPasswordException,
            WebServiceRuntimeException {

        log.info(" entering to registerUser function : {}", mainObject.toString());

        HttpEntity<String> request = createHttpEntityWithBodyNoAuthHeader(mainObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity<String> response = restTemplate.exchange(properties.getSioRegUrl(), HttpMethod.POST, request, String.class);

        String responseBody = response.getBody();

        log.warn("response body data {}", response.getBody());

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);

                log.warn("Register user exception error: {}", error.getError());

                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());
                String email = mainObject.getJSONObject("user").getJSONObject(USER_DETAILS).getString(EMAIL);

                switch (exceptionState) {
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
                        log.warn("Registration connection fail");
                        // possible sio connection fail
                        throw new WebServiceRuntimeException(ERR_SERVER_OVERLOAD);
                }
            } else {
                // do nothing
                log.info("Not an error for status code: {}", response.getStatusCode());
            }
        } catch (IOException e) {
            throw new WebServiceRuntimeException(e.getMessage());
        }
    }


    @RequestMapping(value = "/emailVerification", params = {"id", "email", "key"})
    public String verifyEmail(@NotNull @RequestParam("id") final String id,
                              @NotNull @RequestParam("email") final String emailBase64,
                              @NotNull @RequestParam("key") final String key, Model model ) {

        LoginForm form = new LoginForm();
        form.setType("create");
        form.setKey(key);
        form.setUuid(id);
        form.setEmail(emailBase64);

        model.addAttribute("loginForm", form);
        return LOGIN_PAGE;

    }

    @RequestMapping("/email_validation_success")
    public String emailVerifySucccess() {
        return "email_validation_success";
    }

    @RequestMapping("/email_validation_failed")
    public String emailVerifyfail(Model model) {
        return "email_validation_failed";
    }

    /** end register region ***/

    /** Login region ***/

    @GetMapping(value = "/logout")
    public String logout(HttpSession session) {
        removeSessionVariables(session);
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return LOGIN_PAGE;
    }

    @PostMapping(value = "/login")
    public String loginSubmit(
            @Valid
            @ModelAttribute("loginForm") LoginForm loginForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session, final RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {

        if(!loginForm.getKey().isEmpty() && !loginForm.getLoginPassword().isEmpty() && !loginForm.getType().isEmpty())
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectNode keyObject = objectMapper.createObjectNode();
            keyObject.put("key", loginForm.getKey());
            keyObject.put("password", loginForm.getLoginPassword());
            keyObject.put("type", loginForm.getType());

            HttpEntity<String> request = new HttpEntity<>(keyObject.toString(), headers);
            restTemplate.setErrorHandler(new MyResponseErrorHandler());

            UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getSioRegUrl() + "/users/" + loginForm.getUuid() + "/emails/" + loginForm.getEmail());
            ResponseEntity<String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.PUT, request, String.class);
            log.info("verfiy response value : {}",response.getBody());

            String responseBody = response.getBody();
            JSONObject jsonObject = new JSONObject(responseBody);

            if (RestUtil.isError(response.getStatusCode())) {

                log.error("Activation of user {} failed.", loginForm.getUuid());
                loginForm.setKey(null);
                loginForm.setType(null);
                if(jsonObject.getString("message").equals("Invalid Password"))
                    model.addAttribute("errorMsg", jsonObject.getString("message") + ". Please enter correct password");
                else if(jsonObject.getString("message").contains("has already been verified"))
                    model.addAttribute("errorMsg", jsonObject.getString("message") + " Please proceed to Login.");
                else
                    model.addAttribute("errorMsg", jsonObject.getString("message"));
                return "email_validation_failed";
            } else {

                if(jsonObject.getBoolean("status") == false) {
                    log.error("Activation of user {} failed.", loginForm.getUuid());
                    loginForm.setKey(null);
                    loginForm.setType(null);
                    model.addAttribute("errorMsg", jsonObject.getString("message"));
                    return "email_validation_failed";
                }
                else if(jsonObject.getBoolean("status") == true)
                {
                    log.info("Activation of user {} completed.", loginForm.getUuid());
                    loginForm.setKey(null);
                    loginForm.setType(null);
                    return "email_validation_success";
                }
            }
        }

        if (bindingResult.hasErrors()) {
            loginForm.setErrorMsg(ERR_INVALID_CREDENTIALS);
            return LOGIN_PAGE;
        }

        // check only admin allow to login
        //validate input login data
        String inputEmail = loginForm.getLoginEmail();
        String inputPwd = loginForm.getLoginPassword();
        if (inputEmail.trim().isEmpty() || inputPwd.trim().isEmpty()) {
            loginForm.setErrorMsg("Email or Password cannot be empty!");
            return LOGIN_PAGE;
        }
        //convert Base64
        String plainCreds = inputEmail + ":" + inputPwd;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        ResponseEntity<String> response;
        // Login API
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Basic " + base64Creds);
        HttpEntity<String> request = new HttpEntity<>(headers);
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        try {
            response = restTemplate.exchange(properties.getSioAuthUrl(), HttpMethod.POST, request, String.class);
            log.info("Login Api response = {}", response);

        } catch (RestClientException e) {
            log.warn("Error connecting to authentication service: {}", e);
            loginForm.setErrorMsg(ERR_SERVER_OVERLOAD);
            return LOGIN_PAGE;
        }

        String jwtTokenString = response.getBody();

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
        String osToken = tokenObject.getString("os_token");
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
        String csrfToken = generateCSRFToken();
        session.setAttribute(CSRF_TOKEN, csrfToken);
        loginForm.setErrorMsg(CSRF_TOKEN);

        log.info("role {}" , role);
        if(role.equals("ADMIN")){
            // call admin_dashboard
            setSessionVariables(session, loginForm.getLoginEmail(), id, role, token, osToken);
            return "redirect:/dashboard";
        }
        else{

            loginForm.setErrorMsg("Apologies, the dashboard is currently not accessible to Normal Users.");
            return LOGIN_PAGE;
        }

    }


    private String generateCSRFToken() {
        SecureRandom entropy = new SecureRandom();

        byte secureBytes[] = new byte[20];
        entropy.nextBytes(secureBytes);
        String token = Base64.encodeBase64String(secureBytes);
        token = token.replace("\\/", "@");
        return token;
    }


    protected User2 invokeAndExtractUserInfo(String userId) {
        HttpEntity<String> request = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity <String> response;

        try {
            UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getUser(userId));
            response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            log.warn("User service not available to retrieve User: {}", userId);
            return new User2();
        }

        return extractUserInfo(response.getBody());
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

    private String getJSONStr(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return CONNECTION_ERROR;
        }
        return jsonString;
    }

    private void setSessionVariables(HttpSession session, String loginEmail, String id, String userRoles, String token, String osToken) {
        User2 user = invokeAndExtractUserInfo(id);
        session.setAttribute(webProperties.getSessionEmail(), loginEmail);
        session.setAttribute(webProperties.getSessionUserId(), id);
        //session.setAttribute(webProperties.getSessionUserFirstName(), firstName);
        session.setAttribute(webProperties.getSessionRoles(), userRoles);
        session.setAttribute(webProperties.getSessionJwtToken(), "Bearer " + token);
        session.setAttribute(webProperties.getSessionOsToken(), osToken);
        log.info("Session variables - sessionLoggedEmail: {}, id: {}, name: {}, roles: {}, token: {}, os_token: {}",
                loginEmail, id, user.getFirstName(), userRoles, "########", "______");
    }

    private void removeSessionVariables(HttpSession session) {
        log.info("removing session variables: email: {}, userid: {}, user first name: {}", session.getAttribute(webProperties.getSessionEmail()), session.getAttribute(webProperties.getSessionUserId()), session.getAttribute(webProperties.getSessionUserFirstName()), session.getAttribute(webProperties.getSessionUserUid()));
        session.removeAttribute(webProperties.getSessionEmail());
        session.removeAttribute(webProperties.getSessionUserId());
        session.removeAttribute(webProperties.getSessionUserFirstName());
        session.removeAttribute(webProperties.getSessionRoles());
        session.removeAttribute(webProperties.getSessionJwtToken());
        session.removeAttribute(webProperties.getSessionOsToken());
        session.removeAttribute(webProperties.getSessionUserUid());
        session.invalidate();
    }

    /**
     *  Admin Dashboard Section - Start
     */
    @RequestMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) throws WebServiceRuntimeException {

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        String callingurl = properties.getRegUid(session.getAttribute(webProperties.getSessionUserId()).toString());

        log.info("status url {}" , callingurl);

        ResponseEntity<String> response = restTemplate.exchange(properties.getRegUid(session.getAttribute(webProperties.getSessionUserId()).toString()),
                HttpMethod.GET, request, String.class);

        String responseBody = response.getBody();
        JSONObject jsonObject = new JSONObject(responseBody);

        //log.info("get response data {}", responseBody);

        String uidMessage = jsonObject.getString("message");

        String uid = jsonObject.getString("uid");

        if(!uid.isEmpty()){
            model.addAttribute("userUid", uid);
        }

        // pending user list ,
        List<User2> pendingUserList = getUserListsByStatus(UserStatus.PENDING,session);
        model.addAttribute("pendUserLists", pendingUserList);

        // approved user list
        List<User2> approvedUserList = getUserListsByStatus(UserStatus.APPROVED,session);
        model.addAttribute("approvedUserLists", approvedUserList);

        return "/dashboard";
    }

    public List<User2> getUserListsByStatus(UserStatus status,HttpSession session){

        List<User2> usersList = new ArrayList<>();

        if (!validateIfAdmin(session)) {
            return usersList;
        }

        HttpEntity<String> request = createHttpEntityHeaderOnly();

        ResponseEntity <String> responseUserList = restTemplate.exchange(properties.getSioUsersUrl()+"/status/"+status,
                HttpMethod.GET, request, String.class);


        String jsonUserList = responseUserList.getBody();
        JSONArray jsonUserArray = new JSONArray(jsonUserList);
       // log.info("user list by status {}" , jsonUserList.toString());


        for (int i = 0; i < jsonUserArray.length(); i++) {
            JSONObject userObject = jsonUserArray.getJSONObject(i);
            User2 user = extractUserInfo(userObject.toString());
            usersList.add(user);
        }

        return usersList;
    }

    protected boolean validateIfAdmin(HttpSession session) {
        return session.getAttribute(webProperties.getSessionRoles()).equals(UserType.ADMIN.toString());
    }


    @RequestMapping("/admin/users")
    public String usersAccountByAdmin(
            @RequestParam("userId") String userId,
            @RequestParam(value = "action", required = true) final String action,
            final RedirectAttributes redirectAttributes,
            HttpSession session) throws IOException {
        log.info("Entering to /admin/users");
        User2 user = invokeAndExtractUserInfo(userId);

        // check if admin
        if (!validateIfAdmin(session)) {
            log.warn("Access denied when trying to reject/approve user {}: must be admin!", userId);

            //return NO_PERMISSION_PAGE; // return erro message
            redirectAttributes.addFlashAttribute(MESSAGE, "Access denied when trying to reject/approve user {}: must be admin!");
            // return "redirect:/admin/users";
            return "redirect:/dashboard";
            //responseMessage = "Access denied when trying to reject/approve user {}: must be admin!";
            //return responseMessage;
        }

        if ("reject".equals(action) && user.getStatus().equals(UserStatus.PENDING.toString())) {
            return RejectUserByAdmin(user, redirectAttributes);
        }

        else if ("approve".equals(action) && user.getStatus().equals(UserStatus.PENDING.toString())) {
            //return approveUserByAdmin(user, redirectAttributes);
            return "redirect:/dashboard";
        }
        //Delete user
        else if ("delete".equals(action)) {
            //return deleteUserByAdmin(user, redirectAttributes);
            return "redirect:/dashboard";
        }

        else {

            log.warn("Error in reject/approve user {}: failed to {} user with status {}", userId, action, user.getStatus());
            redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + "failed to " + action + " user " + user.getEmail() + " with status " + user.getStatus());

           // return "redirect:/admin/users";
            return "redirect:/dashboard";
        }
    }

    private String RejectUserByAdmin(final User2 user, RedirectAttributes redirectAttributes) throws IOException {
        log.info("Reject user {}, email {}", user.getId(), user.getEmail());

        HttpEntity<String> request = createHttpEntityWithOsToken();
        String requestUrl = properties.rejectUserAccountByAdmin(user.getId());
        log.info( "Reject requestUrl: {}", requestUrl );
        ResponseEntity <String> response = restTemplate.exchange(properties.rejectUserAccountByAdmin(user.getId()),
                    HttpMethod.DELETE, request, String.class);

            String responseBody = response.getBody();

            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(responseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case USER_NOT_FOUND_EXCEPTION:
                        log.warn("Failed to delete user {}: user not found", user.getId());
                        redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + " user " + user.getEmail() + " not found.");
                        break;
                    case USER_IS_NOT_DELETABLE_EXCEPTION:
                        log.warn("Failed to delete user {}: User account Failed to delete by admin. {}", user.getId(), error.getMessage());
                        redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + error.getMessage());
                        break;
                    case FORBIDDEN_EXCEPTION:
                        log.warn("Failed to delete user {}: must be an Admin", user.getId());
                        redirectAttributes.addFlashAttribute(MESSAGE, ERROR_PREFIX + " permission denied.");
                        break;
                    default:
                        log.warn("Failed to delete user {}: {}", user.getId(), exceptionState.getExceptionName());
                        redirectAttributes.addFlashAttribute(MESSAGE, ERR_SERVER_OVERLOAD);
                        break;
                }

                //return "redirect:/admin/users";
                return "redirect:/dashboard";

            } else {
                // good
                log.info("User {} has been deleted", user.getId());
                redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS, USER_PREFIX + user.getEmail() + " has been deleted.");
                //return "redirect:/admin/users";
                return "redirect:/dashboard";
            }
        }
    /** Admin Dashboard Section - End */


    protected HttpEntity<String> createHttpEntityHeaderOnly() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, httpScopedSession.getAttribute(webProperties.getSessionJwtToken()).toString());
        return new HttpEntity<>(headers);
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

    protected HttpEntity<String> createHttpEntityWithOsToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, httpScopedSession.getAttribute(webProperties.getSessionJwtToken()).toString());
        headers.set(OS_TOKEN, httpScopedSession.getAttribute(webProperties.getSessionOsToken()).toString());
        return new HttpEntity<>(headers);
    }

}



