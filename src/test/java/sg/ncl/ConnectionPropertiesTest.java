package sg.ncl;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import sg.ncl.testbed_interface.TeamStatus;
import sg.ncl.testbed_interface.TeamVisibility;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(properties.getSioAddress()).isNotNull();
        assertThat(properties.getSioAddress()).isInstanceOf(String.class);
    }

    @Test
    public void testSetSioAddress() throws Exception {
        final String one = RandomStringUtils.randomAlphabetic(8);
        properties.setSioAddress(one);
        assertThat(properties.getSioAddress()).isEqualTo(one);
    }

    @Test
    public void testGetSioPort() throws Exception {
        assertThat(properties.getSioAddress()).isNotNull();
        assertThat(properties.getSioAddress()).isInstanceOf(String.class);
    }

    @Test
    public void testSetSioPort() throws Exception {
        final String one = RandomStringUtils.randomAlphabetic(8);
        properties.setSioPort(one);
        assertThat(properties.getSioPort()).isEqualTo(one);
    }

    @Test
    public void testGetAuthEndpoint() throws Exception {
        assertThat(properties.getAuthEndpoint()).isEqualTo("authentications");
    }

    @Test
    public void testGetCredEndpoint() throws Exception {
        assertThat(properties.getCredEndpoint()).isEqualTo("credentials");
    }

    @Test
    public void testGetRegEndpoint() throws Exception {
        assertThat(properties.getRegEndpoint()).isEqualTo("registrations");
    }

    @Test
    public void testGetUserEndpoint() throws Exception {
        assertThat(properties.getUserEndpoint()).isEqualTo("users");
    }

    @Test
    public void testGetTeamEndpoint() throws Exception {
        assertThat(properties.getTeamEndpoint()).isEqualTo("teams");
    }

    @Test
    public void testGetExpEndpoint() throws Exception {
        assertThat(properties.getExpEndpoint()).isEqualTo("experiments");
    }

    @Test
    public void testGetRealEndpoint() throws Exception {
        assertThat(properties.getRealEndpoint()).isEqualTo("realizations");
    }

    @Test
    public void testGetTeamVisibilityEndpoint() throws Exception {
        assertThat(properties.getTeamVisibilityEndpoint()).isEqualTo("?visibility=PUBLIC");
    }

    @Test
    public void testGetSioTeamsUrl() throws Exception {
        assertThat(properties.getSioTeamsUrl()).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTeamEndpoint() + "/");
    }

    @Test
    public void testGetSioCredUrl() throws Exception {
        assertThat(properties.getSioCredUrl()).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getCredEndpoint() + "/");
    }

    @Test
    public void testGetSioExpUrl() throws Exception {
        assertThat(properties.getSioExpUrl()).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getExpEndpoint() + "/");
    }

    @Test
    public void testGetUpdateCredentials() throws Exception {
        String id = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getUpdateCredentials(id)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getCredEndpoint() + "/" + id);
    }

    @Test
    public void testGetRejectJoinRequest() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        String userId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getRejectJoinRequest(teamId, userId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/teams/" + teamId + "/members/" + userId);
    }

    @Test
    public void testGetRegisterRequestToApplyTeam() throws Exception {
        String userId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getRegisterRequestToApplyTeam(userId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/newTeam/" + userId);
    }

    @Test
    public void testGetJoinRequestExistingUser() throws Exception {
        assertThat(properties.getJoinRequestExistingUser()).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/joinApplications");
    }

    @Test
    public void testGetApproveTeam() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        String ownerId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getApproveTeam(teamId, ownerId, TeamStatus.APPROVED)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/teams/" + teamId + "/owner/" + ownerId + "?status=APPROVED");
    }

    @Test
    public void testGetApproveJoinRequest() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        String userId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getApproveJoinRequest(teamId, userId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRegEndpoint() + "/teams/" + teamId + "/members/" + userId);
    }

    @Test
    public void testGetTeamByName() throws Exception {
        String teamName = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getTeamByName(teamName)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTeamEndpoint() + "?name=" + teamName);
    }

    @Test
    public void testGetTeamById() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getTeamById(teamId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTeamEndpoint() + "/" + teamId);
    }

    @Test
    public void testGetTeamsByVisibility() throws Exception {
        assertThat(properties.getTeamsByVisibility(TeamVisibility.PUBLIC.toString())).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTeamEndpoint() + "?visibility=" + TeamVisibility.PUBLIC.toString());
    }

    @Test
    public void testGetUser() throws Exception {
        String userId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getUser(userId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getUserEndpoint() + "/" + userId);
    }

    @Test
    public void testGetExpListByTeamId() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getExpListByTeamId(teamId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getExpEndpoint() + "/teams/" + teamId);
    }

    @Test
    public void testGetRealizationByTeam() throws Exception {
        String teamName = RandomStringUtils.randomAlphanumeric(20);
        String expId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getRealizationByTeam(teamName, expId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRealEndpoint() + "/team/" + teamName + "/experiment/" + expId);
    }

    @Test
    public void testGetDeleteExperiment() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        String expId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getDeleteExperiment(teamId, expId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getExpEndpoint() + "/teams/" + teamId + "/experiments/" + expId);
    }

    @Test
    public void testGetStartExperiment() throws Exception {
        String teamName = RandomStringUtils.randomAlphanumeric(20);
        String expId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getStartExperiment(teamName, expId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRealEndpoint() + "/start/team/" + teamName + "/experiment/" + expId);
    }

    @Test
    public void testGetStopExperiment() throws Exception {
        String teamName = RandomStringUtils.randomAlphanumeric(20);
        String expId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getStopExperiment(teamName, expId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getRealEndpoint() + "/stop/team/" + teamName + "/experiment/" + expId);
    }

    @Test
    public void testGetTelemetryAddress() throws Exception {
        assertThat(properties.getTelemetryAddress()).isNotNull();
        assertThat(properties.getTelemetryAddress()).isInstanceOf(String.class);
    }

    @Test
    public void testSetTelemetryAddress() throws Exception {
        final String one = RandomStringUtils.randomAlphabetic(8);
        properties.setTelemetryAddress(one);
        assertThat(properties.getTelemetryAddress()).isEqualTo(one);
    }

    @Test
    public void testGetTelemetryPort() throws Exception {
        // null because not configured in application.properties
        assertThat(properties.getTelemetryPort()).isNull();
    }

    @Test
    public void testSetTelemetryPort() throws Exception {
        final String one = RandomStringUtils.randomAlphabetic(8);
        properties.setTelemetryPort(one);
        assertThat(properties.getTelemetryPort()).isEqualTo(one);
    }

    @Test
    public void testGetFreeNodes() throws Exception {
        assertThat(properties.getFreeNodes()).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getTelemetryEndpoint());
    }

    @Test
    public void testGetAllImages() throws Exception {
        assertThat(properties.getAllImages()).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getImageEndpoint());
    }

    @Test
    public void testGetTeamImages() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getTeamImages(teamId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getImageEndpoint() + "?teamId=" + teamId);
    }

    @Test
    public void testGetTeamSavedImages() throws Exception {
        String teamId = RandomStringUtils.randomAlphanumeric(20);
        assertThat(properties.getTeamSavedImages(teamId)).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getImageEndpoint() + "/teams/" + teamId);
    }

    @Test
    public void testSaveImage() throws Exception {
        assertThat(properties.saveImage()).isEqualTo("http://" + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getImageEndpoint());
    }

    @Test
    public void testGetPasswordResetRequestURI() {
        assertThat(properties.getPasswordResetRequestURI()).isEqualTo("http://"  + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getCredEndpoint() + "/password/resets");
    }

    @Test
    public void testGetPasswordResetURI() {
        assertThat(properties.getPasswordResetURI()).isEqualTo("http://"  + properties.getSioAddress() + ":" + properties.getSioPort() + "/" + properties.getCredEndpoint() + "/password");
    }
}
