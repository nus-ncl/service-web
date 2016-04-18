package sg.ncl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
    
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
    
    @RequestMapping("/teams")
    public String teams() {
        return "teams";
    }
    
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