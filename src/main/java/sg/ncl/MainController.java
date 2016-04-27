package sg.ncl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import sg.ncl.testbed_interface.LoginForm;
import sg.ncl.testbed_interface.SignUpAccountDetailsForm;
import sg.ncl.testbed_interface.SignUpPersonalDetailsForm;
import sg.ncl.testbed_interface.TeamPageJoinTeamForm;
import sg.ncl.testbed_interface.User;
import sg.ncl.testbed_interface.TeamPageApplyTeamForm;

/**
 * 
 * Spring Controller
 * Direct the views to appropriate locations and invoke the respective REST API
 * @author yeoteye
 * 
 */

@Controller
public class MainController {
    
    private final int ERROR_NO_SUCH_USER_ID = 0;
    private final static Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private final String host = "http://localhost:8080/";
    private final int CURRENT_LOGGED_IN_USER_ID = 200;
    private TeamManager teamManager = TeamManager.getInstance();
    private UserManager userManager = UserManager.getInstance();
    private ExperimentManager experimentManager = new ExperimentManager();
    
    private static SignUpAccountDetailsForm signupAccountDetailsForm = new SignUpAccountDetailsForm();
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String index(Model model) throws Exception {
        model.addAttribute("loginForm", new LoginForm());
        return "index";
    }
    
    @RequestMapping(value="/", method=RequestMethod.POST)
    public String loginSubmit(@ModelAttribute LoginForm loginForm, Model model) throws Exception {
        // following is to test if form fields can be retrieved via user input
        // pretend as though this is a server side validation
        if (userManager.validateLoginDetails(loginForm.getLoginEmail(), loginForm.getLoginPassword()) == false) {
            // case1: invalid login
            loginForm.setErrorMsg("Invalid email/password.");
            return "index";
        } else if (userManager.isEmailVerified(loginForm.getLoginEmail()) == false) {
            // case2: email address not validated
            model.addAttribute("emailAddress", loginForm.getLoginEmail());
            return "email_not_validated";
        } else if (teamManager.checkTeamValidation(userManager.getUserId(loginForm.getLoginEmail())) == false) {
            // case3: team approval under review
            // email address is supposed to be valid here
            return "team_application_under_review";
        } else {
            // all validated
            return "redirect:/dashboard";
        }
    }
    
    @RequestMapping("/passwordreset")
    public String passwordreset(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "passwordreset";
    }
    
    @RequestMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logout() {
        return "redirect:/";
    }
    
    //--------------------------Sign Up Page--------------------------
    
    @RequestMapping(value="/signup", method=RequestMethod.GET)
    public String signup(Model model) {
        // forms has to be added for other views, because the loginForm also exists on those pages
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("signUpAccountDetailsForm", signupAccountDetailsForm);
        return "signup";
    }
    
    @RequestMapping(value="/signup", method=RequestMethod.POST)
    public String validateSignUpForms(@ModelAttribute LoginForm loginForm, @ModelAttribute @Valid SignUpAccountDetailsForm signupAccountDetailsForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup";
        } else if (userManager.getUserId(signupAccountDetailsForm.getEmail()) != ERROR_NO_SUCH_USER_ID) {
            signupAccountDetailsForm.setErrorMsg("Email is already in use");
            return "signup";
        } else if (signupAccountDetailsForm.isPasswordMatch() == false) {
            signupAccountDetailsForm.setErrorMsg("Passwords do not match");
            return "signup";
        } else {
            return "redirect:/signup/personal_details";
        }
    }
    
    @RequestMapping(value="/signup/personal_details", method=RequestMethod.GET)
    public String signupTest(@ModelAttribute SignUpAccountDetailsForm signupAccountDetailsForm, Model model) {
        // forms has to be added for other views, because the loginForm also exists on those pages
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("signUpPersonalDetailsForm", new SignUpPersonalDetailsForm());
        System.out.println(signupAccountDetailsForm.getEmail());
        return "signup_personal";
    }
    
    //--------------------------Account Settings Page--------------------------
    @RequestMapping(value="/account_settings", method=RequestMethod.GET)
    public String accountDetails(Model model) {
        model.addAttribute("currentUser", userManager.getUserById(CURRENT_LOGGED_IN_USER_ID));
        return "account_settings";
    }
    
    @RequestMapping(value="/account_settings", method=RequestMethod.POST)
    public String editAccountDetails(@ModelAttribute User currentUser) {
        System.out.println(currentUser.getEmail());
        return "account_settings";
    }
    
    //--------------------------Teams Page--------------------------
    
    @RequestMapping("/teams")
    public String teams(Model model) {
        model.addAttribute("infoMsg", teamManager.getInfoMsg());
        model.addAttribute("currentLoggedInUserId", CURRENT_LOGGED_IN_USER_ID);
        model.addAttribute("teamMap", teamManager.getTeamMap(CURRENT_LOGGED_IN_USER_ID));
        model.addAttribute("publicTeamMap", teamManager.getPublicTeamMap());
        model.addAttribute("invitedToParticipateMap", teamManager.getInvitedToParticipateMap());
        model.addAttribute("joinRequestMap", teamManager.getJoinRequestTeamMap());
        // REST Client Code
        // final String uri = host + "teams/?";
        // RestTemplate restTemplate = new RestTemplate();
        // TeamsList result = restTemplate.getForObject(uri, TeamsList.class);
        return "teams";
    }
    
    @RequestMapping("/accept_participation/{teamId}")
    public String acceptParticipationRequest(@PathVariable Integer teamId, Model model) {
        // get user's participation request list
        // add this user id to the requested list
        // remove participation request
        teamManager.addParticipationRequest(CURRENT_LOGGED_IN_USER_ID, teamId);
        teamManager.ignoreParticipationRequest(CURRENT_LOGGED_IN_USER_ID, teamId);
        // must get team name
        String teamName = teamManager.getTeamNameByTeamId(teamId);
        teamManager.setInfoMsg("You have just joined Team " + teamName + " !");
        
        // load the other models to the view
        model.addAttribute("infoMsg", teamManager.getInfoMsg());
        model.addAttribute("currentLoggedInUserId", CURRENT_LOGGED_IN_USER_ID);
        model.addAttribute("teamMap", teamManager.getTeamMap(CURRENT_LOGGED_IN_USER_ID));
        model.addAttribute("publicTeamMap", teamManager.getPublicTeamMap());
        model.addAttribute("invitedToParticipateMap", teamManager.getInvitedToParticipateMap());
        model.addAttribute("joinRequestMap", teamManager.getJoinRequestTeamMap());
        return "redirect:/teams";
    }
    
    @RequestMapping("/ignore_participation/{teamId}")
    public String ignoreParticipationRequest(@PathVariable Integer teamId, Model model) {
        // get user's participation request list
        // remove this user id from the requested list
        String teamName = teamManager.getTeamNameByTeamId(teamId);
        teamManager.ignoreParticipationRequest(CURRENT_LOGGED_IN_USER_ID, teamId);
        teamManager.setInfoMsg("You have just ignored a team request from Team " + teamName + " !");
        
        // load the other models to the view
        model.addAttribute("currentLoggedInUserId", CURRENT_LOGGED_IN_USER_ID);
        model.addAttribute("teamMap", teamManager.getTeamMap(CURRENT_LOGGED_IN_USER_ID));
        model.addAttribute("publicTeamMap", teamManager.getPublicTeamMap());
        model.addAttribute("invitedToParticipateMap", teamManager.getInvitedToParticipateMap());
        model.addAttribute("joinRequestMap", teamManager.getJoinRequestTeamMap());
        return "redirect:/teams";
    }
    
    @RequestMapping("/withdrawn/{teamId}")
    public String withdrawnJoinRequest(@PathVariable Integer teamId, Model model) {
        // get user team request
        // remove this user id from the user's request list
        String teamName = teamManager.getTeamNameByTeamId(teamId);
        teamManager.removeUserJoinRequest(CURRENT_LOGGED_IN_USER_ID, teamId);
        teamManager.setInfoMsg("You have withdrawn your join request for Team " + teamName);
        
        // load the other models to the view
        model.addAttribute("infoMsg", teamManager.getInfoMsg());
        model.addAttribute("currentLoggedInUserId", CURRENT_LOGGED_IN_USER_ID);
        model.addAttribute("teamMap", teamManager.getTeamMap(CURRENT_LOGGED_IN_USER_ID));
        model.addAttribute("publicTeamMap", teamManager.getPublicTeamMap());
        model.addAttribute("invitedToParticipateMap", teamManager.getInvitedToParticipateMap());
        model.addAttribute("joinRequestMap", teamManager.getJoinRequestTeamMap());
        return "redirect:/teams";
    }
    
    //--------------------------Team Profile Page--------------------------
    
    @RequestMapping("/team_profile/{teamId}")
    public String teamProfile(@PathVariable Integer teamId, Model model) {
        model.addAttribute("currentLoggedInUserId", CURRENT_LOGGED_IN_USER_ID);
        model.addAttribute("team", teamManager.getTeamByTeamId(teamId));
        model.addAttribute("membersMap", teamManager.getTeamByTeamId(teamId).getMembersMap());
        model.addAttribute("userManager", userManager);
        model.addAttribute("teamExpMap", experimentManager.getTeamExperimentsMap(teamId));
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
    
    //--------------------------Apply for New Team Page--------------------------
    
    @RequestMapping(value="/teams/apply_team", method=RequestMethod.GET)
    public String teamPageApplyTeam(Model model) {
        model.addAttribute("teamPageApplyTeamForm", new TeamPageApplyTeamForm());
        return "team_page_apply_team";
    }
    
    @RequestMapping(value="/teams/apply_team", method=RequestMethod.POST)
    public String checkApplyTeamInfo(@Valid TeamPageApplyTeamForm teamPageApplyTeamForm, BindingResult bindingResult) {
       if (bindingResult.hasErrors()) {
           return "team_page_apply_team";
       }
       // log data to ensure data has been parsed
       LOGGER.log(Level.INFO, "--------Apply for new team info---------");
       LOGGER.log(Level.INFO, teamPageApplyTeamForm.toString());
       return "redirect:/teams/team_application_submitted";
    }
    
    @RequestMapping(value="/teams/team_owner_policy", method=RequestMethod.GET)
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
    public String checkJoinTeamInfo(@Valid TeamPageJoinTeamForm teamPageJoinForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "team_page_join_team";
        }
        // log data to ensure data has been parsed
        LOGGER.log(Level.INFO, "--------Join team---------");
        LOGGER.log(Level.INFO, teamPageJoinForm.toString());
        return "redirect:/teams/join_application_submitted";
    }
    
    //--------------------------Experiment Page--------------------------
    
    @RequestMapping(value="/experiments", method=RequestMethod.GET)
    public String experiments(Model model) {
        model.addAttribute("experimentMap", experimentManager.getExperimentMapByExperimentOwner(CURRENT_LOGGED_IN_USER_ID));
        return "experiments";
    }
    
    @RequestMapping("/remove_experiment/{expId}")
    public String removeExperiment(@PathVariable Integer expId, Model model) {
        // remove experiment
        // TODO check userid is indeed the experiment owner or team owner
        // ensure experiment is stopped first
        experimentManager.removeExperiment(CURRENT_LOGGED_IN_USER_ID, expId);
        model.addAttribute("experimentMap", experimentManager.getExperimentMapByExperimentOwner(CURRENT_LOGGED_IN_USER_ID));
        return "redirect:/experiments";
    }
    
    @RequestMapping("/start_experiment/{expId}")
    public String startExperiment(@PathVariable Integer expId, Model model) {
        // start experiment
        // ensure experiment is stopped first before starting
        return "redirect:/experiments";
    }
    
    //--------------------------Static pages for teams--------------------------
    @RequestMapping("/teams/team_application_submitted")
    public String teamAppSubmitFromTeamsPage() {
        return "team_page_application_submitted";
    }
    
    @RequestMapping("/teams/join_application_submitted")
    public String teamAppJoinFromTeamsPage() {
        return "team_page_join_application_submitted";
    }
    
    //--------------------------Static pages for sign up--------------------------
    
    @RequestMapping("/team_application_submitted")
    public String teamAppSubmit() {
        return "team_application_submitted";
    }
    
    @RequestMapping("/join_application_submitted")
    public String joinTeamAppSubmit() {
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
    public String joinTeamAppAwaitingApproval() {
        return "join_team_application_awaiting_approval";
    }
    
}