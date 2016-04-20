package sg.ncl;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import sg.ncl.testbed_interface.TeamPageJoinTeamForm;

@Controller
public class MainController {
    
    private final String host = "http://localhost:8080/";
    private TeamManager teamManager = new TeamManager();
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String index(Model model) throws Exception {
        model.addAttribute("loginForm", new LoginForm());
        return "index";
    }
    
    @RequestMapping(value="/", method=RequestMethod.POST)
    public String loginSubmit(@ModelAttribute LoginForm loginForm, Model model) throws Exception {
        model.addAttribute("loginForm", loginForm);
        return "redirect:/dashboard";
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
    
    /**
     * Teams Page
     */

    @RequestMapping("/teams")
    public String teams(Model model) {
        model.addAttribute("teamList", teamManager.getTeamList());
        model.addAttribute("publicTeamList", teamManager.getPublicTeamList());
        model.addAttribute("invitedToParticipateList", teamManager.getInvitedParticipateList());
        model.addAttribute("joinRequestList", teamManager.getJoinRequestList());
        // REST Client Code
        // final String uri = host + "teams/?";
        // RestTemplate restTemplate = new RestTemplate();
        // TeamsList result = restTemplate.getForObject(uri, TeamsList.class);
        return "teams";
    }
    
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
        return "redirect:/join_application_submitted";
    }
    
    /**
     * Static Sign Up Status Page
     */
    
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