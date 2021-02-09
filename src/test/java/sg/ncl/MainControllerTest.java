package sg.ncl;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import sg.ncl.domain.NodeType;
import sg.ncl.domain.UserType;
import sg.ncl.testbed_interface.User2;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * @author Te Ye
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebApplication.class)
@WebAppConfiguration
public class MainControllerTest {

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();
    @Rule
    public ExpectedException exception = ExpectedException.none();

//    @Bean
//    RestTemplate restTemplate() {
//        return Mockito.mock(RestTemplate.class);
//    }

    private MockMvc mockMvc;
//    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Inject
    private RestOperations restOperations;

    @Inject
    private ConnectionProperties properties;

    @Inject
    private WebApplicationContext webApplicationContext;

    @Mock
    private WebProperties webProperties;

    @Mock
    private HttpSession session;

    @Before
    public void setUp() throws Exception {
//        restTemplate = Mockito.mock(RestTemplate.class);
        mockServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    //--------------------------------------
    // Test before login HTML pages
    //--------------------------------------
    /*@Test
    public void testIndexPage() throws Exception {
        // ensure page can load <head>, navigation, <body>, <footer>
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("NATIONAL CYBERSECURITY R&amp;D LAB")))
                .andExpect(content().string(containsString("Features")))
                .andExpect(content().string(containsString("Focus on your")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }*/

    @Test
    public void testOverviewPage() throws Exception {
        mockMvc.perform(get("/overview"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("id=\"joinUs\"")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testCommunityPage() throws Exception {
        mockMvc.perform(get("/community"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("id=\"about-us\"")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testEventPage() throws Exception {
        mockMvc.perform(get("/event"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("div id=\"portfolioSlider\"")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testPlanPage() throws Exception {
        mockMvc.perform(get("/plan"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    // page is taken out as of 22/9/2016
    @Ignore
    public void testFuturePlanPage() throws Exception {
        mockMvc.perform(get("/futureplan"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("a href=\"/futureplan/download\"")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testPricingPage() throws Exception {
        mockMvc.perform(get("/pricing"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("Pricing")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testResourcesPage() throws Exception {
        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testResearchPage() throws Exception {
        mockMvc.perform(get("/research"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("Research")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testContactUsPage() throws Exception {
        // calendar page display BEFORE login
        mockMvc.perform(get("/contactus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("navbar-header")))
                .andExpect(content().string(containsString("Contact Us")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("form method=\"post\" action=\"/login\"")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testPostLoginPageInvalidUserPassword() throws Exception {
        mockServer.expect(requestTo(properties.getSioAuthUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest().body("{\"error\":\"sg.ncl.service.authentication.exceptions.InvalidCredentialsException\"}").contentType(MediaType.APPLICATION_JSON));

        ResultActions perform = mockMvc.perform(
                post("/login")
                        .param("loginEmail", "123456789@nus.edu.sg")
                        .param("loginPassword", "123456789")
        );

        perform
                .andExpect(view().name("login"));
        perform
                .andExpect(model().attributeExists("loginForm"));
    }

   /* @Test
    public void testGetSignUpPage() throws Exception {
        mockMvc.perform(get("/signup2"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup2"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("form action=\"/signup2\" method=\"post\" role=\"form\"")))
                .andExpect(content().string(containsString("footer id=\"footer\"")))
                .andExpect(model().attributeExists("signUpMergedForm"));
    }
*/
    @Test
    public void testRedirectNotFoundNotLoggedOn() throws Exception {
        mockMvc.perform(get("/notfound"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testRedirectNotFoundLoggedOn() throws Exception {
        final String id = RandomStringUtils.randomAlphabetic(10);
        mockMvc.perform(get("/notfound").sessionAttr("id", id))
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void testCareerPage() throws Exception {
        mockMvc.perform(get("/career"))
                .andExpect(status().isOk())
                .andExpect(view().name("career"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsUseNodePage() throws Exception {
        mockMvc.perform(get("/tutorials/usenode"))
                .andExpect(status().isOk())
                .andExpect(view().name("usenode"))
                .andExpect(content().string(containsString("How to Access your Experiment Node")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsUseSSHPage() throws Exception {
        mockMvc.perform(get("/tutorials/usessh"))
                .andExpect(status().isOk())
                .andExpect(view().name("usessh"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsUseScpPage() throws Exception {
        mockMvc.perform(get("/tutorials/usescp"))
                .andExpect(status().isOk())
                .andExpect(view().name("usescp"))
                .andExpect(content().string(containsString("Open the SSH terminal on your system go to the directory where the file to be uploaded is located.")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsUseGui() throws Exception {
        mockMvc.perform(get("/tutorials/usegui"))
                .andExpect(status().isOk())
                .andExpect(view().name("usegui"))
                .andExpect(content().string(containsString("Install deskstop and VNC server using the following commands")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsManagerResource() throws Exception {
        mockMvc.perform(get("/tutorials/manageresource"))
                .andExpect(status().isOk())
                .andExpect(view().name("manageresource"))
                .andExpect(content().string(containsString("To set the quota for resources, enter a monetary value for \"Budget\"")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsTestbedInfo() throws Exception {
        mockMvc.perform(get("/tutorials/testbedinfo"))
                .andExpect(status().isOk())
                .andExpect(view().name("testbedinfo"))
                .andExpect(content().string(containsString("View Testbed Info")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsCreateCustom() throws Exception {
        mockMvc.perform(get("/tutorials/createcustom"))
                .andExpect(status().isOk())
                .andExpect(view().name("createcustom"))
                .andExpect(content().string(containsString("How to Save an Image for an Experiment Node")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testErrorOpenStack() throws Exception {
        mockMvc.perform(get("/error_openstack"))
                .andExpect(status().isOk())
                .andExpect(view().name("error_openstack"))
                .andExpect(content().string(containsString("Currently, we are in the process of integrating OpenStack with our systems")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testAccessExperiment() throws Exception {
        mockMvc.perform(get("/accessexperiment"))
                .andExpect(status().isOk())
                .andExpect(view().name("accessexperiment"))
                .andExpect(content().string(containsString("Go to dashboard page and take note of the username under Info!: Use your username: XXX to access the experiments' nodes.")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testResource2() throws Exception {
        mockMvc.perform(get("/resource2"))
                .andExpect(status().isOk())
                .andExpect(view().name("resource2"))
                .andExpect(content().string(containsString("Vulnerability Environments")))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    //--------------------------------------
    // Test after login HTML pages
    //--------------------------------------
    // FIXME: ignore for now as dashboard page invoke two other rest calls
    @Ignore
    @Test
    public void testGetDashboardPage() throws Exception {

        final String id = RandomStringUtils.randomAlphabetic(10);

        mockServer.expect(requestTo(properties.getDeterUid(id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(id, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/dashboard").sessionAttr("id", id))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/calendar1")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("Dashboard")))
                .andExpect(content().string(containsString("footer id=\"footer\"")))
                .andExpect(model().attribute("deterUid", is(id)));
    }

    // FIXME: ignore for now as dashboard page invoke two other rest calls
    @Ignore
    @Test
    public void testGetDashboardPageWithAdmin() throws Exception {
        final String id = RandomStringUtils.randomAlphabetic(10);

        mockServer.expect(requestTo(properties.getDeterUid(id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(id, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/dashboard").sessionAttr("id", id).sessionAttr("roles", UserType.ADMIN.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/calendar1")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("Dashboard")))
                .andExpect(content().string(containsString("footer id=\"footer\"")))
                .andExpect(model().attribute("deterUid", is(id)));
    }

    @Test
    public void getUserProfileTest() throws Exception {

        JSONObject predefinedUserJson = Util.createUserJson();
        String predefinedJsonStr = predefinedUserJson.toString();

        when(webProperties.getSessionJwtToken()).thenReturn("token");

        // uri must be equal to that defined in MainController
        mockServer.expect(requestTo(properties.getSioUsersUrl() + "null"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(predefinedJsonStr, MediaType.APPLICATION_JSON));

        MvcResult result = mockMvc.perform(get("/account_settings").sessionAttr(webProperties.getSessionJwtToken(), "1234"))
                .andExpect(status().isOk())
                .andExpect(view().name("account_settings"))
                .andExpect(model().attribute("editUser", hasProperty("id", is(predefinedUserJson.getString("id")))))
                .andReturn();
    }

    @Test
    public void updateUserProfileTest() throws Exception {
        // TODO to be completed

        // update the phone to test main json
        // update the lastname to test user details json
        // update the address2 to test address json

        JSONObject predefinedUserJson = Util.createUserJson();
        final User2 user2 = Util.getUser();
        final String id = RandomStringUtils.randomAlphabetic(10);
        String predefinedJsonStr = predefinedUserJson.toString();

        when(webProperties.getSessionUserAccount()).thenReturn("originalAccountDetails");
        when(webProperties.getSessionJwtToken()).thenReturn("token");
        when(webProperties.getSessionUserId()).thenReturn("id");
        when(session.getAttribute(webProperties.getSessionUserAccount())).thenReturn(user2);
        when(session.getAttribute(webProperties.getSessionUserId())).thenReturn(id);


        mockServer.expect(requestTo(properties.getSioUsersUrl() + id))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(predefinedJsonStr, MediaType.APPLICATION_JSON));

        MvcResult result = mockMvc.perform(
                post("/account_settings").sessionAttr(webProperties.getSessionUserAccount(), user2).sessionAttr(webProperties.getSessionJwtToken(), "1234").sessionAttr(webProperties.getSessionUserId(), id)
                        .param("email", "apple@nus.edu.sg")
                        .param("password", "password")
                        .param("confirmPassword", "confirmPassword")
                        .param("firstName", "apple")
                        .param("lastName", "edited")
                        .param("phone", "12345678")
                        .param("jobTitle", "research")
                        .param("institution", "national university")
                        .param("institutionAbbreviation", "nus")
                        .param("institutionWeb", "")
                        .param("address1", "address1")
                        .param("address2", "edited")
                        .param("country", "singapore")
                        .param("city", "sg")
                        .param("province", "west")
                        .param("postalCode", "123456"))
                        .andExpect(redirectedUrl("/account_settings"))
                        .andReturn();
    }

    /*@Test
    public void signUpNewUserApplyNewTeam() throws Exception {

        String stubUid = "AAAAAA";
        JSONObject predefinedResultJson = new JSONObject();
        predefinedResultJson.put("msg", "user is created");
        predefinedResultJson.put("uid", stubUid);

        mockServer.expect(requestTo(properties.getSioRegUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(predefinedResultJson.toString(), MediaType.APPLICATION_JSON));

        MvcResult result = mockMvc.perform(
                post("/signup2")
                        .param("email", "apple@nus.edu.sg")
                        .param("password", "appleP@ssword1")
                        .param("confirmPassword", "appleP@ssword1")
                        .param("firstName", "apple")
                        .param("lastName", "orange")
                        .param("phone", "12345678")
                        .param("jobTitle", "research")
                        .param("institution", "national university")
                        .param("institutionAbbreviation", "nus")
                        .param("website", "http://www.nus.edu.sg")
                        .param("address1", "address1")
                        .param("address2", "address2")
                        .param("country", "singapore")
                        .param("city", "sg")
                        .param("province", "west")
                        .param("postalCode", "123456")
                        .param("teamName", "project")
                        .param("joinTeamName", "")
                        .param("teamDescription", "a simple description")
                        .param("teamWebsite", "http://team.com")
                        .param("organizationType", "academia")
                        .param("isPublic", "PUBLIC")
                        .param("hasAcceptTeamOwnerPolicy", "true"))
                .andExpect(redirectedUrl("/team_application_submitted"))
                .andReturn();
    }
*/
    @Test
    @Ignore
    public void signUpNewUserJoinExistingTeam() throws Exception {
        // TODO TBD need a way to mock two rest operations
        String stubUid = "AAAAAA";
        JSONObject predefinedResultJson = new JSONObject();
        predefinedResultJson.put("msg", "user is created");
        predefinedResultJson.put("uid", stubUid);

        JSONObject predefinedTeamJson = new JSONObject();
        predefinedTeamJson.put("id", "123456789");

        MvcResult result = mockMvc.perform(
                post("/signup2")
                        .param("email", "apple@nus.edu.sg")
                        .param("password", "appleP@ssword")
                        .param("confirmPassword", "appleP@ssword")
                        .param("firstName", "apple")
                        .param("lastName", "orange")
                        .param("phone", "12345678")
                        .param("jobTitle", "research")
                        .param("institution", "national university")
                        .param("institutionAbbreviation", "nus")
                        .param("website", "http://www.nus.edu.sg")
                        .param("address1", "address1")
                        .param("address2", "address2")
                        .param("country", "singapore")
                        .param("city", "sg")
                        .param("province", "west")
                        .param("postalCode", "123456")
                        .param("joinTeamName", "project")
                        .param("hasAcceptTeamOwnerPolicy", "true"))
                .andExpect(redirectedUrl("/join_application_submitted"))
                .andReturn();

//        mockServer.expect(requestTo(properties.getTEAMS_URI() + "name/" + "project"))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(withSuccess(predefinedTeamJson.toString(), MediaType.APPLICATION_JSON));
//
//        mockServer.expect(requestTo(properties.getREGISTRATION_URI()))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess(predefinedResultJson.toString(), MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetJoinTeamPageFromTeamPage() throws Exception {
        mockMvc.perform(get("/teams/join_team"))
                .andExpect(status().isOk())
                .andExpect(view().name("team_page_join_team"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("method=\"post\" action=\"/teams/join_team\"")))
                .andExpect(content().string(containsString("footer id=\"footer\"")))
                .andExpect(model().attribute("teamPageJoinTeamForm", hasProperty("teamName")));
    }

    @Test
    public void testGetApplyNewTeamPageFromTeamPage() throws Exception {
        mockMvc.perform(get("/teams/apply_team"))
                .andExpect(status().isOk())
                .andExpect(view().name("team_page_apply_team"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("method=\"post\" action=\"/teams/apply_team\"")))
                .andExpect(content().string(containsString("footer id=\"footer\"")))
                .andExpect(model().attribute("teamPageApplyTeamForm", hasProperty("teamName")));
    }

    @Test
    public void testResetPasswordEnterEmail() throws Exception {

        mockMvc.perform(
                get("/password_reset_email"))
            .andExpect(view().name("password_reset_email"))
            .andExpect(model().attributeExists("passwordResetRequestForm"));
    }

    @Test
    public void testResetPasswordRequestUsernameNotFound() throws Exception {

        mockServer.expect(requestTo(properties.getPasswordResetRequestURI()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ResultActions perform = mockMvc.perform(
                post("/password_reset_request")
                    .param("email", "123456@nus.edu.sg"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("passwordResetRequestForm", hasProperty("errMsg", is("Email not registered. Please use a different email address."))))
                .andExpect(view().name("password_reset_email"));
    }

    @Test
    public void testResetPasswordRequestGood() throws Exception {

        mockServer.expect(requestTo(properties.getPasswordResetRequestURI()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.ACCEPTED));

        mockMvc.perform(
                post("/password_reset_request")
                        .param("email", "123456@nus.edu.sg"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_reset_email_sent"));
    }

    @Test
    public void testResetPasswordNewPassword() throws Exception {

        mockMvc.perform(
                get("/passwordReset?key=12345678"))
                //    .param("key", "12345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_reset_new_password"))
                .andExpect(model().attributeExists("passwordResetForm"));
    }

    @Test
    public void testResetPasswordGood() throws Exception {

        mockServer.expect(requestTo(properties.getPasswordResetURI()))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        mockMvc.perform(
                post("/password_reset")
                        .param("password1", "password1")
                        .param("password2", "password1")
        .sessionAttr("key", "12345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_reset_success"));
    }

    @Test
    public void testResetPasswordRequestTimeout() throws Exception {

        mockServer.expect(requestTo(properties.getPasswordResetURI()))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withBadRequest().body("{\"error\":\"sg.ncl.service.authentication.exceptions.PasswordResetRequestTimeoutException\"}").contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                post("/password_reset")
                        .param("password1", "password1")
                        .param("password2", "password1")
                        .sessionAttr("key", "12345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_reset_new_password"))
        .andExpect(model().attribute("passwordResetForm", hasProperty("errMsg", is("Password reset request timed out. Please request a new reset email."))));
    }


    @Test
    public void testResetPasswordUnknownRequest() throws Exception {

        mockServer.expect(requestTo(properties.getPasswordResetURI()))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).body("{\"error\":\"sg.ncl.service.authentication.exceptions.PasswordResetRequestNotFoundException\"}").contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                post("/password_reset")
                        .param("password1", "password1")
                        .param("password2", "password1")
                        .sessionAttr("key", "12345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_reset_new_password"))
                .andExpect(model().attribute("passwordResetForm", hasProperty("errMsg", is("Invalid password reset request. Please request a new reset email."))));
    }

    @Test
    public void testResetPasswordServerError() throws Exception {

        mockServer.expect(requestTo(properties.getPasswordResetURI()))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withServerError().body("{\"error\":\"sg.ncl.service.authentication.exceptions.AdapterConnectionException\"}").contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                post("/password_reset")
                        .param("password1", "password1")
                        .param("password2", "password1")
                        .param("key", "12345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_reset_new_password"))
                .andExpect(model().attribute("passwordResetForm", hasProperty("errMsg", is("Server-side error. Please contact support@ncl.sg"))));
    }

    @Test
    public void testTutorialsCreateAccountNoLogin() throws Exception {
        mockMvc.perform(get("/tutorials/createaccount"))
                .andExpect(status().isOk())
                .andExpect(view().name("createaccount"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("Fill in all required information here. ALL fields are required.")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsCreateAccountLogin() throws Exception {
        mockMvc.perform(get("/tutorials/createaccount").sessionAttr("id", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("createaccount"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("Fill in all required information here. ALL fields are required.")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsCreateExperimentNoLogin() throws Exception {
        mockMvc.perform(get("/tutorials/createexperiment"))
                .andExpect(status().isOk())
                .andExpect(view().name("createexperiment"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("After logging in, click on “Experiment” on the navigation bar at the top")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsCreateExperimentLogin() throws Exception {
        mockMvc.perform(get("/tutorials/createexperiment").sessionAttr("id", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("createexperiment"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("After logging in, click on “Experiment” on the navigation bar at the top")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsLoadImageNoLogin() throws Exception {
        mockMvc.perform(get("/tutorials/loadimage"))
                .andExpect(status().isOk())
                .andExpect(view().name("loadimage"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("At the Teams page, under the section “Your teams’ saved operating system images”, pick an image that you have saved previously.")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsLoadImageLogin() throws Exception {
        mockMvc.perform(get("/tutorials/loadimage").sessionAttr("id", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("loadimage"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("At the Teams page, under the section “Your teams’ saved operating system images”, pick an image that you have saved previously.")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsSaveImageNoLogin() throws Exception {
        mockMvc.perform(get("/tutorials/saveimage"))
                .andExpect(status().isOk())
                .andExpect(view().name("saveimage"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("Click on “View” under Details to see more details about the started experiment")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsSaveImageLogin() throws Exception {
        mockMvc.perform(get("/tutorials/saveimage").sessionAttr("id", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("saveimage"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("Click on “View” under Details to see more details about the started experiment")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsApplyTeamNoLogin() throws Exception {
        mockMvc.perform(get("/tutorials/applyteam"))
                .andExpect(status().isOk())
                .andExpect(view().name("applyteam"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("Go to team dashboard and click the apply a team button.")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsApplyTeamLogin() throws Exception {
        mockMvc.perform(get("/tutorials/applyteam").sessionAttr("id", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("applyteam"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("Go to team dashboard and click the apply a team button.")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsJoinTeamNoLogin() throws Exception {
        mockMvc.perform(get("/tutorials/jointeam"))
                .andExpect(status().isOk())
                .andExpect(view().name("jointeam"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("Go to team dashboard and click the join a team button.")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsJoinTeamLogin() throws Exception {
        mockMvc.perform(get("/tutorials/jointeam").sessionAttr("id", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("jointeam"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("Go to team dashboard and click the join a team button.")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsNoLogin() throws Exception {
        mockMvc.perform(get("/tutorials"))
                .andExpect(status().isOk())
                .andExpect(view().name("tutorials"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("How to Create an Account")))
                .andExpect(content().string(containsString("How to Apply a Team")))
                .andExpect(content().string(containsString("How to Join a Team")))
                .andExpect(content().string(containsString("How to Create an Experiment")))
                .andExpect(content().string(containsString("How to Use your Experiment Node")))
                .andExpect(content().string(containsString("How to Create Custom Image")))
                .andExpect(content().string(containsString("How to Provision Software Defined Network (SDN) Experiments in NCL Testbed")))
                .andExpect(content().string(containsString("How to Manage Team Resources")))
                .andExpect(content().string(containsString("How to Get Information about NCL Testbed")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTutorialsLogin() throws Exception {
        mockMvc.perform(get("/tutorials").sessionAttr("id", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("tutorials"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("How to Create an Account")))
                .andExpect(content().string(containsString("How to Apply a Team")))
                .andExpect(content().string(containsString("How to Join a Team")))
                .andExpect(content().string(containsString("How to Create an Experiment")))
                .andExpect(content().string(containsString("How to Use your Experiment Node")))
                .andExpect(content().string(containsString("How to Create Custom Image")))
                .andExpect(content().string(containsString("How to Provision Software Defined Network (SDN) Experiments in NCL Testbed")))
                .andExpect(content().string(containsString("How to Manage Team Resources")))
                .andExpect(content().string(containsString("How to Get Information about NCL Testbed")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTestbedInformationNoLogin() throws Exception {
        // mock three REST calls
        // mockServer order is important here, the web service first invokes the get global image follow by get total nodes, get testbed stats

        JSONObject predefinedResultJson = new JSONObject();
        predefinedResultJson.put(NodeType.TOTAL.name(), "99");

        JSONObject predefinedResultJson2 = new JSONObject();
        predefinedResultJson2.put("images", "{\"imageA\": {\"osname\": \"Linux\", \"description\": \"image description\"}}");

        JSONObject predefinedResultJson3 = new JSONObject();
        predefinedResultJson3.put("users", "10");
        predefinedResultJson3.put("experiments", "20");

        mockServer.expect(requestTo(properties.getGlobalImages()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(predefinedResultJson2.toString(), MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(properties.getNodes(NodeType.TOTAL)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(predefinedResultJson.toString(), MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(properties.getTestbedStats()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(predefinedResultJson3.toString(), MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/testbedInformation"))
                .andExpect(status().isOk())
                .andExpect(view().name("testbed_information"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/plan")))
                .andExpect(content().string(containsString("/research")))
                .andExpect(content().string(containsString("/pricing")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/contactus")))
                .andExpect(content().string(containsString("/event")))
                .andExpect(content().string(containsString("/about")))
                .andExpect(content().string(containsString("/signup2")))
                .andExpect(content().string(containsString("/login")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }

    @Test
    public void testTestbedInformationLogin() throws Exception {
        // mock three REST calls
        // mockServer order is important here, the web service first invokes the get global image follow by get total nodes, testbed stats

        JSONObject predefinedResultJson = new JSONObject();
        predefinedResultJson.put(NodeType.TOTAL.name(), "99");

        JSONObject predefinedResultJson2 = new JSONObject();
        predefinedResultJson2.put("images", "{\"imageA\": {\"osname\": \"Linux\", \"description\": \"image description\"}}");

        JSONObject predefinedResultJson3 = new JSONObject();
        predefinedResultJson3.put("users", "10");
        predefinedResultJson3.put("experiments", "20");

        mockServer.expect(requestTo(properties.getGlobalImages()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(predefinedResultJson2.toString(), MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(properties.getNodes(NodeType.TOTAL)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(predefinedResultJson.toString(), MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(properties.getTestbedStats()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(predefinedResultJson3.toString(), MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/testbedInformation").sessionAttr("id", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("testbed_information"))
                .andExpect(content().string(containsString("main.css")))
                .andExpect(content().string(containsString("main.js")))
                .andExpect(content().string(containsString("/teams")))
                .andExpect(content().string(containsString("/experiments")))
                .andExpect(content().string(containsString("/data")))
                .andExpect(content().string(containsString("/admin")))
                .andExpect(content().string(containsString("/admin/experiments")))
                .andExpect(content().string(containsString("/tutorials")))
                .andExpect(content().string(containsString("/resources")))
                .andExpect(content().string(containsString("/testbedInformation")))
                .andExpect(content().string(containsString("/calendar")))
                .andExpect(content().string(containsString("/approve_new_user")))
                .andExpect(content().string(containsString("/account_settings")))
                .andExpect(content().string(containsString("/logout")))
                .andExpect(content().string(containsString("footer id=\"footer\"")));
    }
}
