package sg.ncl;

import org.json.JSONObject;
import org.junit.Before;
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

    private final String USERS_URI = "http://localhost:80/users/";
    private final String USER_ID = "eec32c55-507e-4c30-b850-a4111b565c8f";
    private String AUTHORIZATION_HEADER = "Basic dXNlcjpwYXNzd29yZA==";

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
        // have to run test after api-interface is up
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

        JSONObject predefinedUserJson = createUserJson("1234567890-ABCDEFGHIJKL", "teye", "yeo", "dcsyeoty@nus.edu.sg", "12345678", "computing drive 12", "", "Singapore", "west", "12345678");
        String predefinedJsonStr = predefinedUserJson.toString();

        // uri must be equal to that defined in MainController

        mockServer.expect(requestTo("http://localhost:80/users/3c1cee22-f10c-47e4-8122-31851cbe85f6"))
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

        JSONObject predefinedUserJson = createUserJson("1234567890-ABCDEFGHIJKL", "teye", "yeo", "dcsyeoty@nus.edu.sg", "12345678", "computing drive 12", "", "Singapore", "west", "12345678");
        String predefinedJsonStr = predefinedUserJson.toString();

        mockServer.expect(requestTo("http://localhost:80/users/3c1cee22-f10c-47e4-8122-31851cbe85f6"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(predefinedJsonStr, MediaType.APPLICATION_JSON));

        mockMvc.perform(
                post("/account_settings")
                        .param("phone", "98887666")
                        .param("lastName", "yang")
                        .param("address2", "expected address 2 is filled up"))
                        .andExpect(view().name("redirect:/account_settings"));
    }

    private JSONObject createUserJson(String id, String firstName, String lastName, String email, String phone, String address1, String address2, String country, String region, String zipCode) {
        JSONObject object = new JSONObject();
        JSONObject userDetails = new JSONObject();
        JSONObject address = new JSONObject();

        object.put("id", id);
        userDetails.put("firstName", firstName);
        userDetails.put("lastName", lastName);
        userDetails.put("email", email);
        userDetails.put("phone", phone);
        userDetails.put("address", address);

        address.put("address1", address1);
        address.put("address2", address2);
        address.put("country", country);
        address.put("region", region);
        address.put("zipCode", zipCode);

        object.put("userDetails", userDetails);
        return object;
    }
}