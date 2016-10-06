package sg.ncl;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import sg.ncl.testbed_interface.TeamStatus;
import sg.ncl.testbed_interface.TeamVisibility;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Te Ye
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApp.class)
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
        assertThat(properties.getAuthEndpoint(), is(equalTo("authentications")));
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

    @Test
    public void testGetSioTeamsUrl() throws Exception {
        assertThat(properties.getSioTeamsUrl(), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTeamEndpoint() + "/")));
    }

    @Test
    public void testGetSioCredUrl() throws Exception {
        assertThat(properties.getSioCredUrl(), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getCredEndpoint() + "/")));
    }

    @Test
    public void testGetSioExpUrl() throws Exception {
        assertThat(properties.getSioExpUrl(), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getExpEndpoint() + "/")));
    }

    @Test
    public void testGetUpdateCredentials() throws Exception {
        String id = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getUpdateCredentials(id), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getCredEndpoint() + "/" + id)));
    }

    @Test
    public void testGetRejectJoinRequest() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        String userId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getRejectJoinRequest(teamId, userId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/teams/" + teamId + "/members/" + userId)));
    }

    @Test
    public void testGetRegisterRequestToApplyTeam() throws Exception {
        String userId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getRegisterRequestToApplyTeam(userId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/newTeam/" + userId)));
    }

    @Test
    public void testGetJoinRequestExistingUser() throws Exception {
        assertThat(properties.getJoinRequestExistingUser(), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/joinApplications")));
    }

    @Test
    public void testGetApproveTeam() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        String ownerId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getApproveTeam(teamId, ownerId, TeamStatus.APPROVED), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/teams/" + teamId + "/owner/" + ownerId + "?status=APPROVED")));
    }

    @Test
    public void testGetApproveJoinRequest() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        String userId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getApproveJoinRequest(teamId, userId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/teams/" + teamId + "/members/" + userId)));
    }

    @Test
    public void testGetTeamByName() throws Exception {
        String teamName = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getTeamByName(teamName), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTeamEndpoint() + "?name=" + teamName)));
    }

    @Test
    public void testGetTeamById() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getTeamById(teamId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTeamEndpoint() + "/" + teamId)));
    }

    @Test
    public void testGetTeamsByVisibility() throws Exception {
        assertThat(properties.getTeamsByVisibility(TeamVisibility.PUBLIC.toString()), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTeamEndpoint() + "?visibility=" + TeamVisibility.PUBLIC.toString())));
    }

    @Test
    public void testGetUser() throws Exception {
        String userId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getUser(userId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getUserEndpoint() + "/" + userId)));
    }

    @Test
    public void testGetExpListByTeamId() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getExpListByTeamId(teamId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getExpEndpoint() + "/teams/" + teamId)));
    }

    @Test
    public void testGetRealizationByTeam() throws Exception {
        String teamName = RandomStringUtils.randomAlphanumeric(20);
        String expId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getRealizationByTeam(teamName, expId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRealEndpoint() + "/team/" + teamName + "/experiment/" + expId)));
    }

    @Test
    public void testGetDeleteExperiment() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        String expId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getDeleteExperiment(teamId, expId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getExpEndpoint() + "/teams/" + teamId + "/experiments/" + expId)));
    }

    @Test
    public void testGetStartExperiment() throws Exception {
        String teamName = RandomStringUtils.randomAlphanumeric(20);
        String expId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getStartExperiment(teamName, expId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRealEndpoint() + "/start/team/" + teamName + "/experiment/" + expId)));
    }

    @Test
    public void testGetStopExperiment() throws Exception {
        String teamName = RandomStringUtils.randomAlphanumeric(20);
        String expId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getStopExperiment(teamName, expId), is(equalTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRealEndpoint() + "/stop/team/" + teamName + "/experiment/" + expId)));
    }

}
