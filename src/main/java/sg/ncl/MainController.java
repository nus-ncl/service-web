package sg.ncl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sg.ncl.webssh.PtyProperties;
import sg.ncl.webssh.VncProperties;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

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

    private static final String CONTACT_EMAIL = "support@ncl.sg";

    @ModelAttribute
    public void setResponseHeader(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-Content-Type-Options", "nosniff");
        //response.setHeader("Content-Security-Policy", "script-src 'self' google-analytics.com/ cdn.datatables.net/1.10.12/js/ 'unsafe-inline' 'unsafe-eval'");
        response.setHeader("Strict-Transport-Security", "max-age=16070400; includeSubDomains");
    }

    @Autowired
    protected RestTemplate restTemplate;

//    @Inject
//    protected ObjectMapper objectMapper;

    @Inject
    protected ConnectionProperties properties;

//    @Inject
//    protected WebProperties webProperties;

    @Inject
    protected AccountingProperties accountingProperties;

//    @Inject
//    protected HttpSession httpScopedSession;

    @Inject
    protected PtyProperties ptyProperties;

//    @Inject
//    protected VncProperties vncProperties;

//    @Inject
//    protected NetworkToolProperties networkToolProperties;

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

    @GetMapping(value = "/login")
    public String login(Model model) {
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

    @RequestMapping("/password_reset_email")
    public String passwordRestEmail() {
        return "password_reset_email";
    }

    @RequestMapping("/signup")
    public String registeraccount() {
        return "register";
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
        return "lockedshields";
    }

    @RequestMapping("/ijco")
    public String ijco() {
        return "ijco";
    }
}

