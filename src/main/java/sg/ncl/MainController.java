package sg.ncl;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sg.ncl.rest_client.RestClient;
import sg.ncl.testbed_interface.*;

/**
 * 
 * Spring Controller
 * Direct the views to appropriate locations and invoke the respective REST API
 * @author Cassie, Desmond, Te Ye
 * 
 */
@Controller
public class MainController {
    
	private final String SESSION_LOGGED_IN_USER_ID = "loggedInUserId";
    private final int ERROR_NO_SUCH_USER_ID = 0;
    private final static Logger logger = Logger.getLogger(MainController.class.getName());
    private int CURRENT_LOGGED_IN_USER_ID = ERROR_NO_SUCH_USER_ID;
    private boolean IS_USER_ADMIN = false;
    private TeamManager teamManager = TeamManager.getInstance();
    private UserManager userManager = UserManager.getInstance();
    private ExperimentManager experimentManager = ExperimentManager.getInstance();
    private DomainManager domainManager = DomainManager.getInstance();
    private DatasetManager datasetManager = DatasetManager.getInstance();
    private NodeManager nodeManager = NodeManager.getInstance();

    private String memberTypeOwner = "OWNER";
    private String memberTypeMember = "MEMBER";

    // to know which form fields have been changed
    private User2 originalUser = null;

    private TeamManager2 teamManager2 = TeamManager2.getInstance();
    
    private String SCENARIOS_DIR_PATH = "src/main/resources/scenarios";

    private final String USER_ID = "2535dccd-b7c1-4610-bd9b-4ed231f48f07";
    private final String TEAM_ID = "40d02a00-c47c-492a-abf4-b3c6670a345e";

    private String AUTHORIZATION_HEADER = "Basic dXNlcjpwYXNzd29yZA==";

    private final RestClient restClient = new RestClient();

    @Autowired
    private RestTemplate restTemplate;

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

    @RequestMapping("/futureplan")
    public String futureplan() {
        return "futureplan";
    }

    @RequestMapping("/pricing")
    public String pricing() {
        return "pricing";
    }

    @RequestMapping("/resources")
    public String resources() {
        return "resources";
    }

    @RequestMapping(value="/futureplan/download", method=RequestMethod.GET)
    public void futureplanDownload(HttpServletResponse response) {
        response.setContentType("application/pdf");
        try {
            File fileToDownload = new File("src/main/resources/downloads/future_plan.pdf");
            InputStream is = new FileInputStream(fileToDownload);
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment; filename=future_plan.pdf");
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            is.close();
        } catch (IOException ex) {
            logger.info("Error writing file to output stream.");
            throw new RuntimeException("IOError writing file to output stream");
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
    
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login(Model model) {
    	model.addAttribute("loginForm", new LoginForm());
    	return "login";
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginSubmit(@ModelAttribute("loginForm") LoginForm loginForm, Model model, HttpSession session) {
        String inputEmail = loginForm.getLoginEmail();
        String inputPwd = loginForm.getLoginPassword();

        String plainCreds = inputEmail + ":" + inputPwd;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        String jwtTokenString;
        String id = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + base64Creds);

            HttpEntity<String> request = new HttpEntity<String>("parameters", headers);
            ResponseEntity responseEntity = restTemplate.exchange(properties.getSioAuthUrl(), HttpMethod.POST, request, String.class);

            // TODO call the proper validation functions
            jwtTokenString = responseEntity.getBody().toString();
        } catch (Exception e) {
            // case1: invalid login
            loginForm.setErrorMsg("Invalid email/password.");
            return "login";
        }

        if (jwtTokenString != null || !jwtTokenString.isEmpty()) {
            JSONObject tokenObject = new JSONObject(jwtTokenString);
            String token = tokenObject.getString("token");
            id = tokenObject.getString("id");

//            System.out.println(token);
            AUTHORIZATION_HEADER = "Bearer " + token;

            // needed for legacy codes to work
            CURRENT_LOGGED_IN_USER_ID = userManager.getUserIdByEmail(loginForm.getLoginEmail());
//            IS_USER_ADMIN = userManager.isUserAdmin(CURRENT_LOGGED_IN_USER_ID);
            session.setAttribute("isUserAdmin", IS_USER_ADMIN);
            session.setAttribute(SESSION_LOGGED_IN_USER_ID, CURRENT_LOGGED_IN_USER_ID);

            // FIXME supposed to set some session ID such as the user id returned by the token
            session.setAttribute("sessionLoggedEmail", loginForm.getLoginEmail());
            session.setAttribute("id", id);

            return "redirect:/dashboard";
        } else {
            // case1: invalid login
            loginForm.setErrorMsg("Invalid email/password.");
            return "login";
        }

        /*
    	String inputEmail = loginForm.getLoginEmail();
    	int userId = userManager.getUserIdByEmail(inputEmail);
    	
        if (userManager.validateLoginDetails(loginForm.getLoginEmail(), loginForm.getLoginPassword()) == false) 
        {
            // case1: invalid login
            loginForm.setErrorMsg("Invalid email/password.");
            return "login";
        } 
        else if (userManager.isEmailVerified(loginForm.getLoginEmail()) == false) 
        {
            // case2: email address not validated
            model.addAttribute("emailAddress", loginForm.getLoginEmail());
            return "redirect:/email_not_validated";
        } 
        else if (teamManager.getApprovedTeams(userId) == 0 && teamManager.getJoinRequestTeamMap2(userId) != null) 
        {
        	// case3 
        	// user is not a team owner nor a team member
        	// user has request to join a team but has not been approved by the team owner
        	return "redirect:/join_application_awaiting_approval";
        } 
        else if (teamManager.getApprovedTeams(userId) == 0 && teamManager.getUnApprovedTeams(userId) > 0)
        {
            // case4: since it goes through case3, user must be applying for a team
        	// team approval under review
            // email address is supposed to be valid here
            return "redirect:/team_application_under_review";
        } 
        else 
        {
            // all validated
        	// user may have no team at this point due to rejected team application or join request
        	// must allow user to login so that user can apply again
            // set login
            CURRENT_LOGGED_IN_USER_ID = userManager.getUserIdByEmail(loginForm.getLoginEmail());
            IS_USER_ADMIN = userManager.isUserAdmin(CURRENT_LOGGED_IN_USER_ID);
            session.setAttribute("isUserAdmin", IS_USER_ADMIN);
            session.setAttribute(SESSION_LOGGED_IN_USER_ID, CURRENT_LOGGED_IN_USER_ID);
            return "redirect:/dashboard";
        }
        */

    }
    
    @RequestMapping("/passwordreset")
    public String passwordreset(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "passwordreset";
    }
    
    @RequestMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard";
    }
    
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logout(HttpSession session) {
        CURRENT_LOGGED_IN_USER_ID = ERROR_NO_SUCH_USER_ID;
        session.removeAttribute("isUserAdmin");
        session.removeAttribute(SESSION_LOGGED_IN_USER_ID);
        session.removeAttribute("sessionLoggedEmail");
        session.removeAttribute("id");
        return "redirect:/";
    }
    
    //--------------------------Sign Up Page--------------------------
    
    @RequestMapping(value="/signup2", method=RequestMethod.GET)
    public String signup2(Model model) {
    	// TODO get each model data and put into relevant ones
    	model.addAttribute("loginForm", new LoginForm());
    	model.addAttribute("signUpMergedForm", new SignUpMergedForm());
    	return "signup2";
    }
    
    @RequestMapping(value="/signup2", method=RequestMethod.POST)
    public String validateDetails(@ModelAttribute("loginForm") LoginForm loginForm, @ModelAttribute("signUpMergedForm") SignUpMergedForm signUpMergedForm) {
    	// TODO get each model data and put into relevant ones

        // get form fields
        // craft the registration json
        JSONObject mainObject = new JSONObject();
        JSONObject credentialsFields = new JSONObject();
        credentialsFields.put("username", signUpMergedForm.getEmail());
        credentialsFields.put("password", signUpMergedForm.getPassword());

        // create the user JSON
        JSONObject userFields = new JSONObject();
        JSONObject userDetails = new JSONObject();
        JSONObject addressDetails = new JSONObject();

        userDetails.put("firstName", signUpMergedForm.getFirstName());
        userDetails.put("lastName", signUpMergedForm.getLastName());
        userDetails.put("jobTitle", signUpMergedForm.getJobTitle());
        userDetails.put("email", signUpMergedForm.getEmail());
        userDetails.put("phone", signUpMergedForm.getPhone());
        userDetails.put("institution", signUpMergedForm.getInstitution());
        userDetails.put("institutionAbbreviation", signUpMergedForm.getInstitutionAbbreviation());
        userDetails.put("institutionWeb", signUpMergedForm.getWebsite());
        userDetails.put("address", addressDetails);

        addressDetails.put("address1", signUpMergedForm.getAddress1());
        addressDetails.put("address2", signUpMergedForm.getAddress2());
        addressDetails.put("country", signUpMergedForm.getCountry());
        addressDetails.put("region", signUpMergedForm.getProvince());
        addressDetails.put("city", signUpMergedForm.getCity());
        addressDetails.put("zipCode", signUpMergedForm.getPostalCode());

        userFields.put("userDetails", userDetails);
        userFields.put("applicationDate", ZonedDateTime.now());

        JSONObject teamFields = new JSONObject();

        // add all to main json
        mainObject.put("credentials", credentialsFields);
        mainObject.put("user", userFields);
        mainObject.put("team", teamFields);
    	
    	// check if user chose create new team or join existing team by checking team name
    	String createNewTeamName = signUpMergedForm.getTeamName();
    	String joinNewTeamName = signUpMergedForm.getJoinTeamName();
    	
    	// System.out.println("New team name: " + createNewTeamName);
    	// System.out.println("Join existing team name: " + joinNewTeamName);
    	
    	if (createNewTeamName != null && !createNewTeamName.isEmpty()) {

            // FIXME need to check if team exists?
            teamFields.put("name", signUpMergedForm.getTeamName());
            teamFields.put("description", signUpMergedForm.getTeamDescription());
            teamFields.put("website", signUpMergedForm.getTeamWebsite());
            teamFields.put("organisationType", signUpMergedForm.getTeamOrganizationType());
            teamFields.put("visibility", signUpMergedForm.getIsPublic());
            mainObject.put("isJoinTeam", false);
            registerUserToDeter(mainObject);
        	return "redirect:/team_application_submitted";
        	
    	} else if (joinNewTeamName != null) {

            // get the team JSON from team name
            // FIXME need to check if team exists?
            String teamIdToJoin = getTeamIdByName(signUpMergedForm.getJoinTeamName());
            teamFields.put("id", teamIdToJoin);

            // set the flag to indicate to controller that it is joining an existing team
            mainObject.put("isJoinTeam", true);

            registerUserToDeter(mainObject);

            return "redirect:/join_application_submitted";

    	} else {
    		// logic error not suppose to reach here
    		return "redirect:/signup2";
    	}
    }

    private void registerUserToDeter(JSONObject mainObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>(mainObject.toString(), headers);
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioRegUrl(), HttpMethod.POST, request, String.class);
    }

    private String getTeamIdByName(String teamName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>("parameters", headers);
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioTeamsUrl() + "?name=" + teamName, HttpMethod.GET, request, String.class);
        String resultJSON = responseEntity.getBody().toString();
        JSONObject object = new JSONObject(resultJSON);
        return object.getString("id");
    }

    //--------------------------Account Settings Page--------------------------
    @RequestMapping(value="/account_settings", method=RequestMethod.GET)
    public String accountDetails(Model model, HttpSession session) throws IOException {
    	// TODO id should be some session variable?

    	String userId_uri = properties.getSioUsersUrl() + session.getAttribute("id");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>("parameters", headers);
        ResponseEntity responseEntity = restTemplate.exchange(userId_uri, HttpMethod.GET, request, String.class);

        User2 user2 = extractUserInfo(responseEntity.getBody().toString());
        originalUser = user2;
        model.addAttribute("editUser", user2);
        return "account_settings";
    }
    
    @RequestMapping(value="/account_settings", method=RequestMethod.POST)
    public String editAccountDetails(@ModelAttribute("editUser") User2 editUser, final RedirectAttributes redirectAttributes, HttpSession session) {
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

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", AUTHORIZATION_HEADER);

            HttpEntity<String> request = new HttpEntity<String>(userObject.toString(), headers);
            ResponseEntity responseEntity = restTemplate.exchange(userId_uri, HttpMethod.PUT, request, String.class);

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

                HttpHeaders credHeaders = new HttpHeaders();
                credHeaders.setContentType(MediaType.APPLICATION_JSON);
                credHeaders.set("Authorization", AUTHORIZATION_HEADER);

                HttpEntity<String> credRequest = new HttpEntity<String>(credObject.toString(), headers);
                restTemplate.exchange(properties.getSioCredUrl() + session.getAttribute("id"), HttpMethod.PUT, credRequest, String.class);
                redirectAttributes.addFlashAttribute("editPassword", "success");
            }
        }
        /*
    	User originalUser = userManager.getUserById(getSessionIdOfLoggedInUser(session));
    	
    	String editedName = editUser.getName();
    	String editedPassword = editUser.getPassword();
    	String editedConfirmPassword = editUser.getConfirmPassword();
    	String editedJobTitle = editUser.getJobTitle();
    	String editedInstitution = editUser.getInstitution();
    	String editedInstitutionAbbreviation = editUser.getInstitutionAbbreviation();
    	String editedWebsite = editUser.getWebsite();
    	String editedAddress1 = editUser.getAddress1();
    	String editedAddress2 = editUser.getAddress2();
    	String editedCountry = editUser.getCountry();
    	String editedCity = editUser.getCity();
    	String editedProvince = editUser.getProvince();
    	String editedPostalCode = editUser.getPostalCode();
    	
    	if (originalUser.updateName(editedName) == true) {
        	redirectAttributes.addFlashAttribute("editName", "success");
    	}
    	
    	if (editedPassword.equals(editedConfirmPassword) == false) {
    		redirectAttributes.addFlashAttribute("editPasswordMismatch", "unsuccess");
    	} else if (originalUser.updatePassword(editedPassword) == true) {
    		redirectAttributes.addFlashAttribute("editPassword", "success");
    	} else {
    		redirectAttributes.addFlashAttribute("editPassword", "unsuccess");
    	}
    	
    	if (originalUser.updateJobTitle(editedJobTitle) == true) {
    		redirectAttributes.addFlashAttribute("editJobTitle", "success");
    	}
    	
    	if (originalUser.updateInstitution(editedInstitution) == true) {
    		redirectAttributes.addFlashAttribute("editInstitution", "success");
    	}
    	
    	if (originalUser.updateInstitutionAbbreviation(editedInstitutionAbbreviation) == true) {
    		redirectAttributes.addFlashAttribute("editInstitutionAbbreviation", "success");
    	}
    	
    	if (originalUser.updateWebsite(editedWebsite) == true) {
    		redirectAttributes.addFlashAttribute("editWebsite", "success");
    	}
    	
    	if (originalUser.updateAddress1(editedAddress1) == true) {
    		redirectAttributes.addFlashAttribute("editAddress1", "success");
    	}
    	
    	if (originalUser.updateAddress2(editedAddress2) == true) {
    		redirectAttributes.addFlashAttribute("editAddress2", "success");
    	}
    	
    	if (originalUser.updateCountry(editedCountry) == true) {
    		redirectAttributes.addFlashAttribute("editCountry", "success");
    	}
    	
    	if (originalUser.updateCity(editedCity) == true) {
    		redirectAttributes.addFlashAttribute("editCity", "success");
    	}
    	
    	if (originalUser.updateProvince(editedProvince) == true) {
    		redirectAttributes.addFlashAttribute("editProvince", "success");
    	}
    	
    	if (originalUser.updatePostalCode(editedPostalCode) == true) {
    		redirectAttributes.addFlashAttribute("editPostalCode", "success");
    	}
    	
    	userManager.updateUserDetails(originalUser);
        return "redirect:/account_settings";
        */
        originalUser = null;
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
        ResponseEntity responseEntity = restClient.sendGetRequest(properties.getSioUsersUrl() + "/" + session.getAttribute("id"));

        JSONObject object = new JSONObject(responseEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            ResponseEntity teamResponseEntity = restClient.sendGetRequest(properties.getSioTeamsUrl() + "/" + teamId);

            Team2 team2 = new Team2();
            JSONObject teamObject = new JSONObject(teamResponseEntity.getBody().toString());
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

                if (userId.equals(session.getAttribute("id").toString()) && teamMemberType.equals(memberTypeOwner)) {
                    isTeamLeader = true;
                }

                if (teamMemberStatus.equals("PENDING") && teamMemberType.equals(memberTypeMember)) {
                    User2 myUser = invokeAndExtractUserInfo(userId);
                    joinRequestApproval.setUserId(myUser.getId());
                    joinRequestApproval.setUserEmail(myUser.getEmail());
                    joinRequestApproval.setUserName(myUser.getFirstName() + " " + myUser.getLastName());
                    joinRequestApproval.setApplicationDate(teamJoinedDate);
                    joinRequestApproval.setTeamId(team2.getId());
                    joinRequestApproval.setTeamName(team2.getName());

                    temp.add(joinRequestApproval);
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
    public String userSideAcceptJoinRequest(@PathVariable String teamId, @PathVariable String userId, HttpSession session) {

        JSONObject mainObject = new JSONObject();
        JSONObject userFields = new JSONObject();

        userFields.put("id", session.getAttribute("id").toString());
        mainObject.put("user", userFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>(mainObject.toString(), headers);
        ResponseEntity responseEntity = restTemplate.exchange(properties.getApproveJoinRequest(teamId, userId), HttpMethod.POST, request, String.class);

        return "redirect:/approve_new_user";
    }
    
    @RequestMapping("/approve_new_user/reject/{teamId}/{userId}")
    public String userSideRejectJoinRequest(@PathVariable Integer teamId, @PathVariable Integer userId) {
        teamManager.rejectJoinRequest(userId, teamId);
        return "redirect:/approve_new_user";
    }
    
    //--------------------------Teams Page--------------------------
    
    @RequestMapping("/teams")
    public String teams(Model model, HttpSession session) {
//        int currentLoggedInUserId = getSessionIdOfLoggedInUser(session);
//        model.addAttribute("infoMsg", teamManager.getInfoMsg());
//        model.addAttribute("currentLoggedInUserId", currentLoggedInUserId);
//        model.addAttribute("teamMap", teamManager.getTeamMap(currentLoggedInUserId));
//        model.addAttribute("publicTeamMap", teamManager.getPublicTeamMap());
//        model.addAttribute("invitedToParticipateMap2", teamManager.getInvitedToParticipateMap2(currentLoggedInUserId));
//        model.addAttribute("joinRequestMap2", teamManager.getJoinRequestTeamMap2(currentLoggedInUserId));

        // get list of teamids
        ResponseEntity responseEntity = restClient.sendGetRequest(properties.getSioUsersUrl() + "/" + session.getAttribute("id"));

        JSONObject object = new JSONObject(responseEntity.getBody().toString());
        System.out.println(responseEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

        String userEmail = object.getJSONObject("userDetails").getString("email");

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            ResponseEntity teamResponseEntity = restClient.sendGetRequest(properties.getSioTeamsUrl() + "/" + teamId);
            Team2 team2 = extractTeamInfo(teamResponseEntity.getBody().toString());
            teamManager2.addTeamToTeamMap(team2);

            Team2 joinRequestTeam = extractTeamInfoUserJoinRequest(session.getAttribute("id").toString(), teamResponseEntity.getBody().toString());
            if (joinRequestTeam != null) {
                teamManager2.addTeamToUserJoinRequestTeamMap(joinRequestTeam);
            }
//            System.out.println(teamResponseEntity.getBody().toString());
        }

        // get public teams
        ResponseEntity teamPublicResponseEntity = restClient.sendGetRequest(properties.getSioTeamsUrl() + properties.getTeamVisibilityEndpoint());

        JSONArray teamPublicJsonArray = new JSONArray(teamPublicResponseEntity.getBody().toString());
        for (int i = 0; i < teamPublicJsonArray.length(); i++) {
            JSONObject teamInfoObject = teamPublicJsonArray.getJSONObject(i);
            Team2 team2 = extractTeamInfo(teamInfoObject.toString());
            teamManager2.addTeamToPublicTeamMap(team2);
        }

        model.addAttribute("userEmail", userEmail);
        model.addAttribute("teamMap2", teamManager2.getTeamMap());
        model.addAttribute("publicTeamMap2", teamManager2.getPublicTeamMap());
        model.addAttribute("userJoinRequestMap", teamManager2.getUserJoinRequestMap());
        return "teams";
    }
    
    @RequestMapping("/accept_participation/{teamId}")
    public String acceptParticipationRequest(@PathVariable Integer teamId, Model model, HttpSession session) {
    	int currentLoggedInUserId = getSessionIdOfLoggedInUser(session);
        // get user's participation request list
        // add this user id to the requested list
        teamManager.acceptParticipationRequest(currentLoggedInUserId, teamId);
        // remove participation request since accepted
        teamManager.removeParticipationRequest(currentLoggedInUserId, teamId);
        
        // must get team name
        String teamName = teamManager.getTeamNameByTeamId(teamId);
        teamManager.setInfoMsg("You have just joined Team " + teamName + " !");
        
        return "redirect:/teams";
    }
    
    @RequestMapping("/ignore_participation/{teamId}")
    public String ignoreParticipationRequest(@PathVariable Integer teamId, Model model, HttpSession session) {
        // get user's participation request list
        // remove this user id from the requested list
        String teamName = teamManager.getTeamNameByTeamId(teamId);
        teamManager.ignoreParticipationRequest2(getSessionIdOfLoggedInUser(session), teamId);
        teamManager.setInfoMsg("You have just ignored a team request from Team " + teamName + " !");
        
        return "redirect:/teams";
    }
    
    @RequestMapping("/withdraw/{teamId}")
    public String withdrawnJoinRequest(@PathVariable Integer teamId, Model model, HttpSession session) {
        // get user team request
        // remove this user id from the user's request list
        String teamName = teamManager.getTeamNameByTeamId(teamId);
        teamManager.removeUserJoinRequest2(getSessionIdOfLoggedInUser(session), teamId);
        teamManager.setInfoMsg("You have withdrawn your join request for Team " + teamName);
        
        return "redirect:/teams";
    }
    
    @RequestMapping(value="/teams/invite_members/{teamId}", method=RequestMethod.GET)
    public String inviteMember(@PathVariable Integer teamId, Model model) {
        model.addAttribute("teamIdVar", teamId);
        model.addAttribute("teamPageInviteMemberForm", new TeamPageInviteMemberForm());
        return "team_page_invite_members";
    }
    
    @RequestMapping(value="/teams/invite_members/{teamId}", method=RequestMethod.POST)
    public String sendInvitation(@PathVariable Integer teamId, @ModelAttribute TeamPageInviteMemberForm teamPageInviteMemberForm,Model model) {
        int userId = userManager.getUserIdByEmail(teamPageInviteMemberForm.getInviteUserEmail());
        teamManager.addInvitedToParticipateMap(userId, teamId);
        return "redirect:/teams";
    }
    
    @RequestMapping(value="/teams/members_approval/{teamId}", method=RequestMethod.GET)
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
    
    @RequestMapping("/team_profile/{teamId}")
    public String teamProfile(@PathVariable String teamId, Model model, HttpSession session) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>("parameters", headers);
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioTeamsUrl() + teamId, HttpMethod.GET, request, String.class);

        Team2 team = extractTeamInfo(responseEntity.getBody().toString());

//        model.addAttribute("currentLoggedInUserId", getSessionIdOfLoggedInUser(session));
        model.addAttribute("team", team);
        model.addAttribute("owner", team.getOwner());
        model.addAttribute("membersList", team.getMembersList());

//        model.addAttribute("team", teamManager.getTeamByTeamId(teamId));
//        model.addAttribute("membersMap", teamManager.getTeamByTeamId(teamId).getMembersMap());
//        model.addAttribute("userManager", userManager);
//        model.addAttribute("teamExpMap", experimentManager.getTeamExperimentsMap(teamId));
        // model add attribute team is correct
        return "team_profile";
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
    
    @RequestMapping("/team_profile/{teamId}/start_experiment/{expId}")
    public String startExperimentFromTeamProfile(@PathVariable Integer teamId, @PathVariable Integer expId, Model model, HttpSession session) {
        // start experiment
        // ensure experiment is stopped first before starting
        experimentManager.startExperiment(getSessionIdOfLoggedInUser(session), expId);
    	return "redirect:/team_profile/{teamId}";
    }
    
    @RequestMapping("/team_profile/{teamId}/stop_experiment/{expId}")
    public String stopExperimentFromTeamProfile(@PathVariable Integer teamId, @PathVariable Integer expId, Model model, HttpSession session) {
        // stop experiment
        // ensure experiment is in ready mode before stopping
        experimentManager.stopExperiment(getSessionIdOfLoggedInUser(session), expId);
        return "redirect:/team_profile/{teamId}";
    }
    
    @RequestMapping("/team_profile/{teamId}/remove_experiment/{expId}")
    public String removeExperimentFromTeamProfile(@PathVariable Integer teamId, @PathVariable Integer expId, Model model, HttpSession session) {
        // remove experiment
        // TODO check userid is indeed the experiment owner or team owner
        // ensure experiment is stopped first
        if (experimentManager.removeExperiment(getSessionIdOfLoggedInUser(session), expId) == true) {
            // decrease exp count to be display on Teams page
            teamManager.decrementExperimentCount(teamId);
        }
        model.addAttribute("experimentList", experimentManager.getExperimentListByExperimentOwner(getSessionIdOfLoggedInUser(session)));
        return "redirect:/team_profile/{teamId}";
    }
    
    @RequestMapping(value="/team_profile/invite_user/{teamId}", method=RequestMethod.GET)
    public String inviteUserFromTeamProfile(@PathVariable Integer teamId, Model model) {
        model.addAttribute("teamIdVar", teamId);
        model.addAttribute("teamPageInviteMemberForm", new TeamPageInviteMemberForm());
        return "team_profile_invite_members";
    }
    
    @RequestMapping(value="/team_profile/invite_user/{teamId}", method=RequestMethod.POST)
    public String sendInvitationFromTeamProfile(@PathVariable Integer teamId, @ModelAttribute TeamPageInviteMemberForm teamPageInviteMemberForm, Model model) {
        int userId = userManager.getUserIdByEmail(teamPageInviteMemberForm.getInviteUserEmail());
        teamManager.addInvitedToParticipateMap(userId, teamId);
        return "redirect:/team_profile/{teamId}";
    }
    
    //--------------------------Apply for New Team Page--------------------------
    
    @RequestMapping(value="/teams/apply_team", method=RequestMethod.GET)
    public String teamPageApplyTeam(Model model) {
        model.addAttribute("teamPageApplyTeamForm", new TeamPageApplyTeamForm());
        return "team_page_apply_team";
    }
    
    @RequestMapping(value="/teams/apply_team", method=RequestMethod.POST)
    public String checkApplyTeamInfo(@Valid TeamPageApplyTeamForm teamPageApplyTeamForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
           logger.warning("Existing users apply for new team, page has errors");
           // return "redirect:/teams/apply_team";
           return "team_page_apply_team";
        }
        // log data to ensure data has been parsed
        logger.info("Apply for new team info at : " + properties.getRegisterRequestToApplyTeam(session.getAttribute("id").toString()));
        logger.info("Team application form: " + teamPageApplyTeamForm.toString());

        JSONObject mainObject = new JSONObject();
        JSONObject teamFields = new JSONObject();
        mainObject.put("team", teamFields);
        teamFields.put("name", teamPageApplyTeamForm.getTeamName());
        teamFields.put("description", teamPageApplyTeamForm.getTeamDescription());
        teamFields.put("website", teamPageApplyTeamForm.getTeamWebsite());
        teamFields.put("organisationType", teamPageApplyTeamForm.getTeamOrganizationType());
        teamFields.put("visibility", teamPageApplyTeamForm.getIsPublic());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>(mainObject.toString(), headers);
        ResponseEntity responseEntity = restTemplate.exchange(properties.getRegisterRequestToApplyTeam(session.getAttribute("id").toString()), HttpMethod.POST, request, String.class);

        return "redirect:/teams/team_application_submitted";
    }
    
    @RequestMapping(value="/team_owner_policy", method=RequestMethod.GET)
    public String teamOwnerPolicy() {
        return "team_owner_policy";
    }
    
    //--------------------------Join Team Page--------------------------
    
    @RequestMapping(value="/teams/join_team",  method=RequestMethod.GET)
    public String teamPageJoinTeam(Model model) {
        model.addAttribute("teamPageJoinTeamForm", new TeamPageJoinTeamForm());
        return "team_page_join_team";
    }
    
    @RequestMapping(value="/teams/join_team", method=RequestMethod.POST)
    public String checkJoinTeamInfo(@Valid TeamPageJoinTeamForm teamPageJoinForm, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "team_page_join_team";
        }
        // log data to ensure data has been parsed
        logger.log(Level.INFO, "--------Join team---------");

        JSONObject mainObject = new JSONObject();
        JSONObject teamFields = new JSONObject();
        JSONObject userFields = new JSONObject();
        mainObject.put("team", teamFields);
        mainObject.put("user", userFields);

        userFields.put("id", session.getAttribute("id")); // ncl-id
        teamFields.put("name", teamPageJoinForm.getTeamName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>(mainObject.toString(), headers);
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioRegUrl() + "/joinApplications", HttpMethod.POST, request, String.class);
        
        // perform join team request here
        // add to user join team list
        // ensure user is not already in the team or have submitted the application
        // add to team join request map also for members approval function
        User currentUser = userManager.getUserById(getSessionIdOfLoggedInUser(session));
        int teamId = teamManager.getTeamIdByTeamName(teamPageJoinForm.getTeamName());
        teamManager.addJoinRequestTeamMap2(getSessionIdOfLoggedInUser(session), teamId, currentUser);
        return "redirect:/teams/join_application_submitted/" + teamId;
    }
    
    //--------------------------Experiment Page--------------------------
    
    @RequestMapping(value="/experiments", method=RequestMethod.GET)
    public String experiments(Model model, HttpSession session) {

        List<Experiment2> experimentList = new ArrayList<>();
        ResponseEntity responseEntity = restClient.sendGetRequest(properties.getSioExpUrl() + "/users/" + session.getAttribute("id").toString());

        JSONArray experimentsArray = new JSONArray(responseEntity.getBody().toString());

        for (int i = 0; i < experimentsArray.length(); i++) {
            Experiment2 experiment2 = extractExperiment(experimentsArray.getJSONObject(i).toString());
            experimentList.add(experiment2);
        }

        model.addAttribute("experimentList", experimentList);
        return "experiments";
    }
    
    @RequestMapping(value="/experiments/create", method=RequestMethod.GET)
    public String createExperiment(Model model, HttpSession session) {
//    	List<String> scenarioFileNameList = getScenarioFileNameList();
//        model.addAttribute("experiment", new Experiment2());
//        model.addAttribute("scenarioFileNameList", scenarioFileNameList);
//        model.addAttribute("teamMap", teamManager.getTeamMap(getSessionIdOfLoggedInUser(session)));

        // a list of teams that the logged in user is in
        List<Team2> userTeamsList = new ArrayList<>();

        // get list of teamids
        ResponseEntity responseEntity = restClient.sendGetRequest(properties.getSioUsersUrl() + "/" + session.getAttribute("id"));

        JSONObject object = new JSONObject(responseEntity.getBody().toString());
        System.out.println(responseEntity.getBody().toString());
        JSONArray teamIdsJsonArray = object.getJSONArray("teams");

        for (int i = 0; i < teamIdsJsonArray.length(); i++) {
            String teamId = teamIdsJsonArray.get(i).toString();
            ResponseEntity teamResponseEntity = restClient.sendGetRequest(properties.getSioTeamsUrl() + "/" + teamId);
            Team2 team2 = extractTeamInfo(teamResponseEntity.getBody().toString());
            userTeamsList.add(team2);
        }

        model.addAttribute("experimentForm", new ExperimentPageCreateExperimentForm());
        model.addAttribute("userTeamsList", userTeamsList);
        return "experiment_page_create_experiment";
    }
    
    @RequestMapping(value="/experiments/create", method=RequestMethod.POST)
    public String validateExperiment(@ModelAttribute("experimentPageCreateExperimentForm") ExperimentPageCreateExperimentForm experimentPageCreateExperimentForm, HttpSession session) {

        JSONObject experimentObject = new JSONObject();
        experimentObject.put("userId", session.getAttribute("id").toString());
        experimentObject.put("teamId", experimentPageCreateExperimentForm.getTeamId());
        experimentObject.put("teamName", experimentPageCreateExperimentForm.getTeamName());
        experimentObject.put("name", experimentPageCreateExperimentForm.getName());
        experimentObject.put("description", experimentPageCreateExperimentForm.getDescription());
        experimentObject.put("nsFile", "file");
        experimentObject.put("nsFileContent", experimentPageCreateExperimentForm.getNsFileContent());
        experimentObject.put("idleSwap", "240");
        experimentObject.put("maxDuration", "960");

        System.out.println(experimentObject.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(experimentObject.toString(), headers);
        ResponseEntity responseEntity = restTemplate.exchange(properties.getSioExpUrl(), HttpMethod.POST, request, String.class);

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
    
    @RequestMapping("/experiments/configuration/{expId}")
    public String viewExperimentConfiguration(@PathVariable Integer expId, Model model) {
    	// get experiment from expid
    	// retrieve the scenario contents to be displayed
    	Experiment currExp = experimentManager.getExperimentByExpId(expId);
    	model.addAttribute("scenarioContents", currExp.getScenarioContents());
    	return "experiment_scenario_contents";
    }
    
    @RequestMapping("/remove_experiment/{expId}")
    public String removeExperiment(@PathVariable Integer expId, Model model, HttpSession session) {
        // remove experiment
        // TODO check userid is indeed the experiment owner or team owner
        // ensure experiment is stopped first
    	int teamId = experimentManager.getExperimentByExpId(expId).getTeamId();
        experimentManager.removeExperiment(getSessionIdOfLoggedInUser(session), expId);
        model.addAttribute("experimentList", experimentManager.getExperimentListByExperimentOwner(getSessionIdOfLoggedInUser(session)));
        
        // decrease exp count to be display on Teams page
        teamManager.decrementExperimentCount(teamId);
        return "redirect:/experiments";
    }
    
    @RequestMapping("/start_experiment/{expId}")
    public String startExperiment(@PathVariable Integer expId, Model model, HttpSession session) {
        // start experiment
        // ensure experiment is stopped first before starting
        experimentManager.startExperiment(getSessionIdOfLoggedInUser(session), expId);
        model.addAttribute("experimentList", experimentManager.getExperimentListByExperimentOwner(getSessionIdOfLoggedInUser(session)));
        return "redirect:/experiments";
    }
    
    @RequestMapping("/stop_experiment/{expId}")
    public String stopExperiment(@PathVariable Integer expId, Model model, HttpSession session) {
        // stop experiment
        // ensure experiment is in ready mode before stopping
        experimentManager.stopExperiment(getSessionIdOfLoggedInUser(session), expId);
        model.addAttribute("experimentList", experimentManager.getExperimentListByExperimentOwner(getSessionIdOfLoggedInUser(session)));
        return "redirect:/experiments";
    }
    
    //---------------------------------Dataset Page--------------------------
    
    @RequestMapping("/data")
    public String data(Model model, HttpSession session) {
    	model.addAttribute("datasetOwnedByUserList", datasetManager.getDatasetContributedByUser(getSessionIdOfLoggedInUser(session)));
    	model.addAttribute("datasetAccessibleByUserList", datasetManager.getDatasetAccessibleByuser(getSessionIdOfLoggedInUser(session)));
    	model.addAttribute("userManager", userManager);
    	return "data";
    }
    
    @RequestMapping(value="/data/contribute", method=RequestMethod.GET)
    public String contributeData(Model model) {
    	model.addAttribute("dataset", new Dataset());
    	
    	File rootFolder = new File(App.ROOT);
    	List<String> fileNames = Arrays.stream(rootFolder.listFiles())
    			.map(f -> f.getName())
    			.collect(Collectors.toList());

    		model.addAttribute("files",
    			Arrays.stream(rootFolder.listFiles())
    					.sorted(Comparator.comparingLong(f -> -1 * f.lastModified()))
    					.map(f -> f.getName())
    					.collect(Collectors.toList())
    		);
    	
    	return "contribute_data";
    }
    
    @RequestMapping(value="/data/contribute", method=RequestMethod.POST)
    public String validateContributeData(@ModelAttribute("dataset") Dataset dataset, HttpSession session, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    	// TODO
    	// validation
    	// get file from user upload to server
		if (!file.isEmpty()) {
			try {
				String fileName = getSessionIdOfLoggedInUser(session) + "-" + file.getOriginalFilename();
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(App.ROOT + "/" + fileName)));
                FileCopyUtils.copy(file.getInputStream(), stream);
				stream.close();
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + file.getOriginalFilename() + "!");
				datasetManager.addDataset(getSessionIdOfLoggedInUser(session), dataset, file.getOriginalFilename());
			}
			catch (Exception e) {
				redirectAttributes.addFlashAttribute("message",
						"You failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
			}
		}
		else {
			redirectAttributes.addFlashAttribute("message",
					"You failed to upload " + file.getOriginalFilename() + " because the file was empty");
		}
    	return "redirect:/data";
    }
    
    @RequestMapping(value="/data/edit/{datasetId}", method=RequestMethod.GET)
    public String datasetInfo(@PathVariable Integer datasetId, Model model) {
    	Dataset dataset = datasetManager.getDataset(datasetId);
    	model.addAttribute("editDataset", dataset);
    	return "edit_data";
    }
    
    @RequestMapping(value="/data/edit/{datasetId}", method=RequestMethod.POST)
    public String editDatasetInfo(@PathVariable Integer datasetId, @ModelAttribute("editDataset") Dataset dataset, final RedirectAttributes redirectAttributes) {
    	Dataset origDataset = datasetManager.getDataset(datasetId);
    	
    	String editedDatasetName = dataset.getDatasetName();
    	String editedDatasetDesc = dataset.getDatasetDescription();
    	String editedDatasetLicense = dataset.getLicense();
    	String editedDatasetPublic = dataset.getIsPublic();
    	boolean editedDatasetIsRequiredAuthorization = dataset.getRequireAuthorization();
    	
    	System.out.println(origDataset.getDatasetId());
    	System.out.println(dataset.getDatasetId());
    	
    	if (origDataset.updateName(editedDatasetName) == true) {
    		redirectAttributes.addFlashAttribute("editName", "success");
    	}
    	
    	if (origDataset.updateDescription(editedDatasetDesc) == true) {
    		redirectAttributes.addFlashAttribute("editDesc", "success");
    	}
    	
    	if (origDataset.updateLicense(editedDatasetLicense) == true) {
    		redirectAttributes.addFlashAttribute("editLicense", "success");
    	}
    	
    	if (origDataset.updatePublic(editedDatasetPublic) == true) {
    		redirectAttributes.addFlashAttribute("editPublic", "success");
    	}
    	
    	if (origDataset.updateAuthorization(editedDatasetIsRequiredAuthorization) == true) {
    		redirectAttributes.addFlashAttribute("editIsRequiredAuthorization", "success");
    	}
    	
    	datasetManager.updateDatasetDetails(origDataset);
    	
    	return "redirect:/data/edit/{datasetId}";
    }
    
    @RequestMapping("/data/remove_dataset/{datasetId}")
    public String removeDataset(@PathVariable Integer datasetId) {
    	datasetManager.removeDataset(datasetId);
    	return "redirect:/data";
    }
    
    @RequestMapping("/data/public")
    public String openDataset(Model model) {
    	model.addAttribute("publicDataMap", datasetManager.getDatasetMap());
    	model.addAttribute("userManager", userManager);
    	return "data_public";
    }
    
    @RequestMapping("/data/public/request_access/{dataOwnerId}")
    public String requestAccessForDataset(@PathVariable Integer dataOwnerId, Model model) {
    	// TODO
    	// send reuqest to team owner
    	// show feedback to users
    	User rv = userManager.getUserById(dataOwnerId);
    	model.addAttribute("ownerName", rv.getName());
    	model.addAttribute("ownerEmail", rv.getEmail());
    	return "data_request_access";
    }
    
    //---------------------------------Admin---------------------------------
    @RequestMapping("/admin")
    public String admin(Model model) {
    	model.addAttribute("domain", new Domain());
    	model.addAttribute("domainTable", domainManager.getDomainTable());
    	model.addAttribute("usersMap", userManager.getUserMap());
    	model.addAttribute("teamsMap", teamManager.getTeamMap());
    	model.addAttribute("teamManager", teamManager);
    	model.addAttribute("teamsPendingApprovalMap", teamManager.getTeamsPendingApproval());
    	model.addAttribute("experimentMap", experimentManager.getExperimentMap2());
    	
    	model.addAttribute("totalTeamCount", teamManager.getTotalTeamsCount());
    	model.addAttribute("totalExpCount", experimentManager.getTotalExpCount());
    	model.addAttribute("totalMemberCount", teamManager.getTotalMembersCount());
    	model.addAttribute("totalMemberAwaitingApprovalCount", teamManager.getTotalMembersAwaitingApproval());
    	
    	model.addAttribute("datasetMap", datasetManager.getDatasetMap());
    	model.addAttribute("userManager", userManager);
    	
    	model.addAttribute("nodeMap", nodeManager.getNodeMap());
    	
    	return "admin";
    }
    
    @RequestMapping(value="/admin/domains/add", method=RequestMethod.POST)
    public String addDomain(@Valid Domain domain, BindingResult bindingResult) {
    	if (bindingResult.hasErrors()) {
    		return "redirect:/admin";
    	} else {
    		domainManager.addDomains(domain.getDomainName());
    	}
    	return "redirect:/admin";
    }
    
    @RequestMapping("/admin/domains/remove/{domainKey}")
    public String removeDomain(@PathVariable String domainKey) {
    	domainManager.removeDomains(domainKey);
    	return "redirect:/admin";
    }
    
    @RequestMapping("/admin/teams/accept/{teamId}")
    public String approveTeam(@PathVariable Integer teamId) {
    	// set the approved flag to true
    	teamManager.approveTeamApplication(teamId);
    	return "redirect:/admin";
    }
    
    @RequestMapping("/admin/teams/reject/{teamId}")
    public String rejectTeam(@PathVariable Integer teamId) {
    	// need to cleanly remove the team application
    	teamManager.rejectTeamApplication(teamId);
    	return "redirect:/admin";
    }
    
    @RequestMapping("/admin/users/ban/{userId}")
    public String banUser(@PathVariable Integer userId) {
    	// TODO
    	// perform ban action here
    	// need to cleanly remove user info from teams, user. etc
    	return "redirect:/admin";
    }
    
    @RequestMapping("/admin/experiments/remove/{expId}")
    public String adminRemoveExp(@PathVariable Integer expId) {
    	int teamId = experimentManager.getExperimentByExpId(expId).getTeamId();
        experimentManager.adminRemoveExperiment(expId);
        
        // decrease exp count to be display on Teams page
        teamManager.decrementExperimentCount(teamId);
    	return "redirect:/admin";
    }
    
    @RequestMapping(value="/admin/data/contribute", method=RequestMethod.GET)
    public String adminContributeDataset(Model model) {
    	model.addAttribute("dataset", new Dataset());
    	
    	File rootFolder = new File(App.ROOT);
    	List<String> fileNames = Arrays.stream(rootFolder.listFiles())
    			.map(f -> f.getName())
    			.collect(Collectors.toList());

    		model.addAttribute("files",
    			Arrays.stream(rootFolder.listFiles())
    					.sorted(Comparator.comparingLong(f -> -1 * f.lastModified()))
    					.map(f -> f.getName())
    					.collect(Collectors.toList())
    		);
    		
    	return "admin_contribute_data";
    }
    
    @RequestMapping(value="/admin/data/contribute", method=RequestMethod.POST)
    public String validateAdminContributeDataset(@ModelAttribute("dataset") Dataset dataset, HttpSession session, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    	// TODO
    	// validation
    	// get file from user upload to server
		if (!file.isEmpty()) {
			try {
				String fileName = getSessionIdOfLoggedInUser(session) + "-" + file.getOriginalFilename();
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(App.ROOT + "/" + fileName)));
                FileCopyUtils.copy(file.getInputStream(), stream);
				stream.close();
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + file.getOriginalFilename() + "!");
				datasetManager.addDataset(getSessionIdOfLoggedInUser(session), dataset, file.getOriginalFilename());
			}
			catch (Exception e) {
				redirectAttributes.addFlashAttribute("message",
						"You failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
			}
		}
		else {
			redirectAttributes.addFlashAttribute("message",
					"You failed to upload " + file.getOriginalFilename() + " because the file was empty");
		}
    	return "redirect:/admin";
    }
    
    @RequestMapping("/admin/data/remove/{datasetId}")
    public String adminRemoveDataset(@PathVariable Integer datasetId) {
    	datasetManager.removeDataset(datasetId);
    	return "redirect:/admin";
    }
    
    @RequestMapping(value="/admin/node/add", method=RequestMethod.GET)
    public String adminAddNode(Model model) {
    	model.addAttribute("node", new Node());
    	return "admin_add_node";
    }
    
    @RequestMapping(value="/admin/node/add", method=RequestMethod.POST)
    public String adminAddNode(@ModelAttribute("node") Node node) {
    	// TODO
    	// validate fields, eg should be integer
    	nodeManager.addNode(node);
    	return "redirect:/admin";
    }
    
    //--------------------------Static pages for teams--------------------------
    @RequestMapping("/teams/team_application_submitted")
    public String teamAppSubmitFromTeamsPage() {
        return "team_page_application_submitted";
    }
    
    @RequestMapping("/teams/join_application_submitted/{teamId}")
    public String teamAppJoinFromTeamsPage(@PathVariable Integer teamId, Model model) {
        int teamOwnerId = teamManager.getTeamByTeamId(teamId).getTeamOwnerId();
        model.addAttribute("teamOwner", userManager.getUserById(teamOwnerId));
        return "team_page_join_application_submitted";
    }
    
    //--------------------------Static pages for sign up--------------------------
    
    @RequestMapping("/team_application_submitted")
    public String teamAppSubmit(Model model) {
    	model.addAttribute("loginForm", new LoginForm());
    	model.addAttribute("signUpMergedForm", new SignUpMergedForm());
        return "team_application_submitted";
    }
    
    @RequestMapping("/join_application_submitted")
    public String joinTeamAppSubmit(Model model) {
    	model.addAttribute("loginForm", new LoginForm());
    	model.addAttribute("signUpMergedForm", new SignUpMergedForm());
        return "join_team_application_submitted";
    }
    
    @RequestMapping("/email_not_validated")
    public String emailNotValidated(Model model) {
    	model.addAttribute("loginForm", new LoginForm());
    	model.addAttribute("signUpMergedForm", new SignUpMergedForm());
        return "email_not_validated";
    }
    
    @RequestMapping("/team_application_under_review")
    public String teamAppUnderReview(Model model) {
    	model.addAttribute("loginForm", new LoginForm());
    	model.addAttribute("signUpMergedForm", new SignUpMergedForm());
        return "team_application_under_review";
    }
    
    @RequestMapping("/join_application_awaiting_approval")
    public String joinTeamAppAwaitingApproval(Model model) {
    	model.addAttribute("loginForm", new LoginForm());
    	model.addAttribute("signUpMergedForm", new SignUpMergedForm());
        return "join_team_application_awaiting_approval";
    }
    
    //--------------------------Get List of scenarios filenames--------------------------
    private List<String> getScenarioFileNameList() {
		List<String> scenarioFileNameList = new ArrayList<String>();
		File[] files = new File(SCENARIOS_DIR_PATH).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				scenarioFileNameList.add(file.getName());
			}
		}
		return scenarioFileNameList;
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
    public int getSessionIdOfLoggedInUser(HttpSession session) {
    	return Integer.parseInt(session.getAttribute(SESSION_LOGGED_IN_USER_ID).toString());
    }

    public User2 extractUserInfo(String userJson) {
        User2 user2 = new User2();
        JSONObject object = new JSONObject(userJson);
        JSONObject userDetails = object.getJSONObject("userDetails");
        JSONObject address = userDetails.getJSONObject("address");

        user2.setId(object.getString("id"));
        user2.setFirstName(userDetails.getString("firstName"));
        user2.setLastName(userDetails.getString("lastName"));
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

        return user2;
    }

    public Team2 extractTeamInfo(String json) {
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
            if (teamMemberType.equals(memberTypeMember)) {
                team2.addMembers(myUser);

                // add to pending members list for Members Awaiting Approval function
                if (teamMemberStatus.equals("PENDING")) {
                    team2.addPendingMembers(myUser);
                }

            } else if (teamMemberType.equals(memberTypeOwner)) {
                // explicit safer check
                team2.setOwner(myUser);
            }
        }
        team2.setMembersCount(membersArray.length());
        return team2;
    }

    public Team2 extractTeamInfoUserJoinRequest(String userId, String json) {
        Team2 team2 = new Team2();
        JSONObject object = new JSONObject(json);
        JSONArray membersArray = object.getJSONArray("members");

        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            String uid = memberObject.getString("userId");
            String teamMemberStatus = memberObject.getString("memberStatus");
            if (uid.equals(userId) && teamMemberStatus.equals("PENDING")) {

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

    public User2 invokeAndExtractUserInfo(String userId) {
        String userId_uri = properties.getSioUsersUrl() + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>("parameters", headers);
        ResponseEntity responseEntity = restTemplate.exchange(userId_uri, HttpMethod.GET, request, String.class);

        User2 user2 = extractUserInfo(responseEntity.getBody().toString());
        return user2;
    }

    public String getStubUserID() {
        return USER_ID;
    }

    public Experiment2 extractExperiment(String experimentJson) {
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

    /**
     *
     * @param zonedDateTimeJSON JSON string
     * @return
     */
    private String formatZonedDateTime(String zonedDateTimeJSON) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ZonedDateTime zonedDateTime = mapper.readValue(zonedDateTimeJSON, ZonedDateTime.class);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM-d-yyyy");
        return zonedDateTime.format(format);
    }
}