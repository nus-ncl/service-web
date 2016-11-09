package sg.ncl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dcsyeoty on 08-Nov-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebPropertiesTest.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "ncl.web.service.shared.sessionRoles=abcde",
        "ncl.web.service.shared.sessionEmail=abcde@ncl.sg",
        "ncl.web.service.shared.sessionUserId=123456",
        "ncl.web.service.shared.sessionUserFirstName=hello",
        "ncl.web.service.shared.sessionJwtToken=token"
})
public class WebPropertiesTest {

    @Configuration
    @EnableConfigurationProperties(WebProperties.class)
    static class TestConfig {
    }

    @Inject
    private WebProperties properties;

    @Test
    public void testSessionRoles() throws Exception {
        assertThat(properties.getSessionRoles()).isEqualTo("abcde");
    }

    @Test
    public void testSessionEmail() throws Exception {
        assertThat(properties.getSessionEmail()).isEqualTo("abcde@ncl.sg");
    }

    @Test
    public void testSessionUserId() throws Exception {
        assertThat(properties.getSessionUserId()).isEqualTo("123456");
    }

    @Test
    public void testSessionUserFirstName() throws Exception {
        assertThat(properties.getSessionUserFirstName()).isEqualTo("hello");
    }

    @Test
    public void testSessionJwtToken() throws Exception {
        assertThat(properties.getSessionJwtToken()).isEqualTo("token");
    }
}
