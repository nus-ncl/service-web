import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import sg.ncl.MainController;
import sg.ncl.testbed_interface.LoginForm;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

@RunWith(JMockit.class)
public class MainControllerTest {
	
	@Injectable
	MainController mainController;
	
	private MockMvc mockMvc;
	
	@Mocked
	View mockView;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(mainController).setSingleView(mockView).build();
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
		/*
		LoginForm loginForm = Mockito.mock(LoginForm.class);
		Mockito.when(loginForm.getLoginEmail()).thenReturn("johndoe@nus.edu.sg");
		Mockito.when(loginForm.getLoginPassword()).thenReturn("password");
		mockMvc.perform(post("/login"))
		.andExpect(status().isOk())
		.andExpect(view().name("dashboard"));
		*/
	}
}