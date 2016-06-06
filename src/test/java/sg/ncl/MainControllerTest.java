package sg.ncl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

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

    @Bean
    RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
    @Inject
    MainController mainController;

    private MockMvc mockMvc;
    private RestTemplate restTemplate = new RestTemplate();
    private MockRestServiceServer mockServer;

    private final String USERS_URI = "http://localhost:80/users/";
    private final String USER_ID = "eec32c55-507e-4c30-b850-a4111b565c8f";
    private String AUTHORIZATION_HEADER = "Basic dXNlcjpwYXNzd29yZA==";

    @Inject
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        restTemplate = Mockito.mock(RestTemplate.class);
        mockServer = MockRestServiceServer.createServer(restTemplate);
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
    public void updateUserProfileTest() throws Exception {

        mockServer.expect(requestTo("http://localhost:80/users/sadsadsadsad"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{aaaaaa}", MediaType.APPLICATION_JSON));

        String item = restTemplate.getForObject("http://localhost:80/users/{id}", String.class, "sadsadsadsad");

        MvcResult result = mockMvc.perform(get("/account_settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("account_settings"))
                .andReturn();

//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Authorization", AUTHORIZATION_HEADER);
//        HttpEntity<String> request = new HttpEntity<String>("parameters", responseHeaders);
//        ResponseEntity<String> responseEntity = new ResponseEntity<String>("{}", HttpStatus.OK);
//
//        when(restTemplate.exchange("http://localhost:80/users/eec32c55-507e-4c30-b850-a4111b565c8f", HttpMethod.GET, request, String.class)).thenReturn(responseEntity);

//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Authorization", AUTHORIZATION_HEADER);
//        HttpEntity<String> request = new HttpEntity<String>("parameters", responseHeaders);
//        ResponseEntity<String> responseEntity = new ResponseEntity<String>("{}", HttpStatus.OK);
//
//        when(restTemplate.exchange("/account_settings", HttpMethod.GET, request, String.class)).thenReturn(responseEntity);
        System.out.println("AAAAAAAAAAAAAAAAAAAA" + item);
        mockServer.verify();
    }
}