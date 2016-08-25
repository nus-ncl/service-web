package sg.ncl;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Te Ye
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApp.class)
@WebAppConfiguration
public class ConnectionPropertiesTest {

    @Inject
    private ConnectionProperties properties;

    @Test
    public void testGetSioAddress() throws Exception {
        assertThat(properties.getSioAddress(), is(notNullValue()));
        assertThat(properties.getSioAddress(), is(any(String.class)));
    }

    @Test
    public void testSetSioAddress() throws Exception {
        final String one = RandomStringUtils.randomAlphabetic(8);
        properties.setSioAddress(one);
        assertThat(properties.getSioAddress(), is(equalTo(one)));
    }

    @Test
    public void testGetSioPort() throws Exception {
        assertThat(properties.getSioAddress(), is(notNullValue()));
        assertThat(properties.getSioAddress(), is(any(String.class)));
    }

    @Test
    public void testSetSioPort() throws Exception {
        final String one = RandomStringUtils.randomAlphabetic(8);
        properties.setSioPort(one);
        assertThat(properties.getSioPort(), is(equalTo(one)));
    }

    @Test
    public void testGetAuthEndpoint() throws Exception {
        assertThat(properties.getAuthEndpoint(), is(equalTo("authentication")));
    }

    @Test
    public void testGetCredEndpoint() throws Exception {
        assertThat(properties.getCredEndpoint(), is(equalTo("credentials")));
    }

    @Test
    public void testGetRegEndpoint() throws Exception {
        assertThat(properties.getRegEndpoint(), is(equalTo("registrations")));
    }

    @Test
    public void testGetUserEndpoint() throws Exception {
        assertThat(properties.getUserEndpoint(), is(equalTo("users")));
    }

    @Test
    public void testGetTeamEndpoint() throws Exception {
        assertThat(properties.getTeamEndpoint(), is(equalTo("teams")));
    }

    @Test
    public void testGetExpEndpoint() throws Exception {
        assertThat(properties.getExpEndpoint(), is(equalTo("experiments")));
    }

    @Test
    public void testGetRealEndpoint() throws Exception {
        assertThat(properties.getRealEndpoint(), is(equalTo("realizations")));
    }

    @Test
    public void testGetTeamVisibilityEndpoint() throws Exception {
        assertThat(properties.getTeamVisibilityEndpoint(), is(equalTo("?visibility=PUBLIC")));
    }

}
