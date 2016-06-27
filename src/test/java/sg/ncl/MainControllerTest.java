package sg.ncl;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * Created by Te Ye
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public class MainControllerTest {

//    @Bean
//    RestTemplate restTemplate() {
//        return Mockito.mock(RestTemplate.class);
//    }

    @Inject
    MainController mainController;

    private MockMvc mockMvc;
//    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Autowired
    private RestOperations restOperations;

    @Inject
    private ConnectionProperties properties;

    @Inject
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
//        restTemplate = Mockito.mock(RestTemplate.class);
        mockServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        mockMvc = webAppContextSetup(webApplicationContext).build();

    }

    @Test
    public void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testPostLoginPageInvalidUserPassword() throws Exception {
        mockServer.expect(requestTo(properties.getSioAuthUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        mockMvc.perform(
                post("/login")
                .param("loginEmail", "123456789@nus.edu.sg")
                .param("loginPassword", "123456789")
        )
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    @Test
    public void testGetSignUpPage() throws Exception {
        mockMvc.perform(get("/signup2"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup2"))
                .andExpect(model().attributeExists("loginForm"))
                .andExpect(model().attributeExists("signUpMergedForm"));
    }

    @Test
    public void getUserProfileTest() throws Exception {

        JSONObject predefinedUserJson = createUserJson("1234567890-ABCDEFGHIJKL", "teye", "yeo", "research assistant", "dcsyeoty@nus.edu.sg", "12345678", "national", "nus", "http://nus.edu.sg", "computing drive 12", "", "Singapore", "west", "city singapore", "12345678");
        String predefinedJsonStr = predefinedUserJson.toString();

        // uri must be equal to that defined in MainController
        mockServer.expect(requestTo(properties.getSioUsersUrl() + mainController.getStubUserID()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(predefinedJsonStr, MediaType.APPLICATION_JSON));

        MvcResult result = mockMvc.perform(get("/account_settings"))
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

        JSONObject predefinedUserJson = createUserJson("1234567890-ABCDEFGHIJKL", "teye", "yeo", "research assistant", "dcsyeoty@nus.edu.sg", "12345678", "national", "nus", "http://nus.edu.sg", "computing drive 12", "", "Singapore", "west", "city singapore", "12345678");
        JSONObject predefinedUserDetailsJson = predefinedUserJson.getJSONObject("userDetails");
        String predefinedJsonStr = predefinedUserJson.toString();

        mockServer.expect(requestTo(properties.getSioUsersUrl() + mainController.getStubUserID()))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(predefinedJsonStr, MediaType.APPLICATION_JSON));

        MvcResult result = mockMvc.perform(
                post("/account_settings")
                        .param("email", "apple@nus.edu.sg")
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

    @Test
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
                        .param("teamName", "project")
                        .param("teamDescription", "a simple description")
                        .param("teamWebsite", "http://team.com")
                        .param("organizationType", "academia")
                        .param("isPublic", "PUBLIC")
                        .param("hasAcceptTeamOwnerPolicy", "true"))
                .andExpect(redirectedUrl("/team_application_submitted"))
                .andReturn();
    }

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

    private JSONObject createUserJson(String id, String firstName, String lastName, String jobTitle, String email, String phone, String institution, String institutionAbbrev, String institutionWeb, String address1, String address2, String country, String region, String city, String zipCode) {
        JSONObject object = new JSONObject();
        JSONObject userDetails = new JSONObject();
        JSONObject address = new JSONObject();

        object.put("id", id);
        userDetails.put("firstName", firstName);
        userDetails.put("lastName", lastName);
        userDetails.put("jobTitle", jobTitle);
        userDetails.put("email", email);
        userDetails.put("phone", phone);
        userDetails.put("address", address);
        userDetails.put("institution", institution);
        userDetails.put("institutionAbbreviation", institutionAbbrev);
        userDetails.put("institutionWeb", institutionWeb);

        address.put("address1", address1);
        address.put("address2", address2);
        address.put("country", country);
        address.put("region", region);
        address.put("city", city);
        address.put("zipCode", zipCode);

        object.put("userDetails", userDetails);
        return object;
    }
}