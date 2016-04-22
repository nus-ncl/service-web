package sg.ncl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import sg.ncl.testbed_interface.LoginForm;
import sg.ncl.testbed_interface.TeamPageJoinTeamForm;
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
    
    private final static Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private final String host = "http://localhost:8080/";
    private final int CURRENT_LOGGED_IN_USER_ID = 200;
    private TeamManager teamManager = TeamManager.getInstance();
    private UserManager userManager = UserManager.getInstance();
    private ExperimentManager experimentManager = new ExperimentManager();
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String index(Model model) throws Exception {
        model.addAttribute("loginForm", new LoginForm());
        return "index";
    }
    
    @RequestMapping(value="/", method=RequestMethod.POST)
    public String loginSubmit(@ModelAttribute LoginForm loginForm, Model model) throws Exception {
        model.addAttribute("loginForm", loginForm);
        // following is to test if form fields can be retrieved via user input
        // pretend as though this is a server side validation
        // case1: invalid login
        /**
        if (userManager.validateLoginDetails(loginForm.getEmail(), loginForm.getPassword()) == false) {
            loginForm.setErrorMsg("Invalid email/password.");
            return "index";
        } else if (userManager.isEmailVerified(loginForm.getEmail()) == false) {
            return "email_not_validated";
        } else if (teamManager.checkTeamValidation(userManager.getUserId(loginForm.getEmail()))) {
            return "team_application_under_review";
        } else {
            return "redirect:/dashboard";
        }
        */
        if (userManager.validateLoginDetails(loginForm.getEmail(), loginForm.getPassword()) == false) {
            loginForm.setErrorMsg("Invalid email/password.");
            return "index";
        } else if (userManager.isEmailVerified(loginForm.getEmail()) == false) {
            model.addAttribute("emailAddress", loginForm.getEmail());
            return "email_not_validated";
        } else {
            return "redirect:/dashboard";
        }
        // add three other cases
        // team not validated
        // email validated, team not validated
        // email validated, team
    }
    
    @RequestMapping("/signup")
    public String signup(Model model) {
        // forms has to be added for other views, because the loginForm also exists on those pages
        model.addAttribute("loginForm", new LoginForm());
        return "signup";
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
    
    //--------------------------Teams Page--------------------------
    
    @RequestMapping("/teams")
    public String teams(Model model) {
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
    
    //--------------------------Apply for New Team Page--------------------------
    
    @RequestMapping(value="/apply_team", method=RequestMethod.GET)
    public String teamPageApplyTeam(Model model) {
        model.addAttribute("teamPageApplyTeamForm", new TeamPageApplyTeamForm());
        return "team_page_apply_team";
    }
    
    @RequestMapping(value="/apply_team", method=RequestMethod.POST)
    public String checkApplyTeamInfo(@Valid TeamPageApplyTeamForm teamPageApplyTeamForm, BindingResult bindingResult) {
       if (bindingResult.hasErrors()) {
           return "team_page_apply_team";
       }
       // log data to ensure data has been parsed
       LOGGER.log(Level.INFO, "--------Apply for new team info---------");
       LOGGER.log(Level.INFO, teamPageApplyTeamForm.toString());
       return "redirect:/team_application_submitted";
    }
    
    @RequestMapping(value="/team_owner_policy", method=RequestMethod.GET)
    public String teamOwnerPolicy() {
        return "team_owner_policy";
    }
    
    //--------------------------Join Team Page--------------------------
    
    @RequestMapping(value="/join_team",  method=RequestMethod.GET)
    public String teamPageJoinTeam(Model model) {
        model.addAttribute("teamPageJoinTeamForm", new TeamPageJoinTeamForm());
        return "team_page_join_team";
    }
    
    @RequestMapping(value="/join_team", method=RequestMethod.POST)
    public String checkJoinTeamInfo(@Valid TeamPageJoinTeamForm teamPageJoinForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "team_page_join_team";
        }
        // log data to ensure data has been parsed
        LOGGER.log(Level.INFO, "--------Join team---------");
        LOGGER.log(Level.INFO, teamPageJoinForm.toString());
        return "redirect:/join_application_submitted";
    }
    
    //--------------------------Experiment Page--------------------------
    
    @RequestMapping(value="/experiments", method=RequestMethod.GET)
    public String experiments(Model model) {
        model.addAttribute("experimentMap", experimentManager.getExperimentMap());
        return "experiments";
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