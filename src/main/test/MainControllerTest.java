import sg.ncl.MainController;
import sg.ncl.testbed_interface.LoginForm;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


public class MainControllerTest {
	
	@Inject
	MainController mainController;
	
	private MockMvc mockMvc;

	@Inject
	private WebApplicationContext webApplicationContext;
	
	@Before
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

	@Test
	public void testGetLoginPage() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("login"));
	}
	
	@Test
	public void testPostLoginPage() throws Exception {
		mockMvc.perform(post("/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("login"))
			.andExpect(model().attributeExists("loginForm"));
	}
	
	@Test
	public void testLoginSuccessful() throws Exception {
		LoginForm loginForm = Mockito.mock(LoginForm.class);
		Mockito.when(loginForm.getLoginEmail()).thenReturn("johndoe@nus.edu.sg");
		Mockito.when(loginForm.getLoginPassword()).thenReturn("password");
		mockMvc.perform(post("/login"))
		.andExpect(status().isOk())
		.andExpect(view().name("redirect:/dashboard"));
	}
	
	@Test
	public void testGetSignUpPage() throws Exception {
		mockMvc.perform(get("/signup2"))
		.andExpect(status().isOk())
		.andExpect(view().name("signup2"));
	}
	
	@Test
	public void testPostSignUpPage() throws Exception {
		mockMvc.perform(post("/signup2"))
		.andExpect(status().isOk())
		.andExpect(view().name("signup2"))
		.andExpect(model().attributeExists("loginForm"))
		.andExpect(model().attributeExists("signUpMergedForm"));
	}
}