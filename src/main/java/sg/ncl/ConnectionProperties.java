package sg.ncl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import sg.ncl.testbed_interface.TeamStatus;

/**
 * Created by Te Ye
 */
@ConfigurationProperties(prefix = ConnectionProperties.PREFIX)
public class ConnectionProperties {

    public static final String PREFIX = "ncl.web.service";

    private String sioAddress;
    private String sioPort;
    private String authEndpoint;
    private String credEndpoint;
    private String regEndpoint;
    private String userEndpoint;
    private String teamEndpoint;
    private String teamVisibilityEndpoint;
    private String expEndpoint;
    private String approveJoinRequest;
    private String realEndpoint;

    public String getSioAddress() {
        return sioAddress;
    }

    public void setSioAddress(String sioAddress) {
        this.sioAddress = sioAddress;
    }

    public String getSioPort() {
        return sioPort;
    }

    public void setSioPort(String sioPort) {
        this.sioPort = sioPort;
    }

    public String getAuthEndpoint() {
        return authEndpoint;
    }

    public void setAuthEndpoint(String authEndpoint) {
        this.authEndpoint = authEndpoint;
    }

    public String getCredEndpoint() {
        return credEndpoint;
    }

    public void setCredEndpoint(String credEndpoint) {
        this.credEndpoint = credEndpoint;
    }

    public String getRegEndpoint() {
        return regEndpoint;
    }

    public void setRegEndpoint(String regEndpoint) {
        this.regEndpoint = regEndpoint;
    }

    public String getUserEndpoint() {
        return userEndpoint;
    }

    public void setUserEndpoint(String userEndpoint) {
        this.userEndpoint = userEndpoint;
    }

    public String getTeamEndpoint() {
        return teamEndpoint;
    }

    public void setTeamEndpoint(String teamEndpoint) {
        this.teamEndpoint = teamEndpoint;
    }

    public String getExpEndpoint() {
        return expEndpoint;
    }

    public void setExpEndpoint(String expEndpoint) {
        this.expEndpoint = expEndpoint;
    }

    public String getRealEndpoint() {
        return realEndpoint;
    }

    public void setRealEndpoint(String realEndpoint) {
        this.realEndpoint = realEndpoint;
    }

    public String getSioUsersUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + userEndpoint + "/";
    }

    public String getSioTeamsUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/";
    }

    public String getSioAuthUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + authEndpoint;
    }

    public String getSioCredUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + credEndpoint + "/";
    }

    public String getSioRegUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + regEndpoint;
    }

    public String getTeamVisibilityEndpoint() {
        return teamVisibilityEndpoint;
    }

    public void setTeamVisibilityEndpoint(String teamVisibilityEndpoint) {
        this.teamVisibilityEndpoint = teamVisibilityEndpoint;
    }

    public String getSioExpUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + expEndpoint + "/";
    }

    public void setApproveJoinRequest(String approveJoinRequest) {
        this.approveJoinRequest = approveJoinRequest;
    }

    //-------------------------------------
    // CREDENTIALS
    //-------------------------------------
    public String getUpdateCredentials(String id) {
        return "http://" + sioAddress + ":" + sioPort + "/" + credEndpoint + "/" + id;
    }

    //-------------------------------------
    // REGISTRATIONS
    //-------------------------------------

    public String getDeterUid(String id) {
        return "http://" + sioAddress + ":" + sioPort + "/" + regEndpoint + "/user/" + id;
    }

    public String getRejectJoinRequest(String teamId, String userId) {
        // same but REST API is DELETE
        return "http://" + sioAddress + ":" + sioPort + "/" + regEndpoint + "/teams/" + teamId + "/members/" + userId;
    }

    // for existing users
    public String getRegisterRequestToApplyTeam(String nclUserId) {
        return "http://" + sioAddress + ":" + sioPort + "/" + regEndpoint + "/newTeam/" + nclUserId;
    }

    // for existing users
    public String getJoinRequestExistingUser() {
        return "http://" + sioAddress + ":" + sioPort + "/" + regEndpoint + "/joinApplications";
    }

    public String getApproveTeam(String teamId, String ownerId, TeamStatus teamStatus) {
        return "http://" + sioAddress + ":" + sioPort + "/" + regEndpoint + "/teams/" + teamId + "/owner/" + ownerId + "?status=" + teamStatus;
    }

    public String getApproveJoinRequest(String teamId, String userId) {
        return "http://" + sioAddress + ":" + sioPort + "/" + regEndpoint + "/teams/" + teamId + "/members/" + userId;
    }

    //-------------------------------------
    // TEAMS
    //-------------------------------------

    public String getTeamByName(String name) {
        return "http://" + sioAddress + ":" + sioPort + "/" + teamEndpoint + "?name=" + name;
    }

    public String getTeamById(String id) {
        return "http://" + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/" + id;
    }

    public String getTeamsByVisibility(String visibility) {
        return "http://" + sioAddress + ":" + sioPort + "/" + teamEndpoint + "?visibility=" + visibility;
    }

    //-------------------------------------
    // USERS
    //-------------------------------------

    public String getUser(String id) {
        return "http://" + sioAddress + ":" + sioPort + "/" + userEndpoint + "/" + id;
    }

    //-------------------------------------
    // EXPERIMENTS
    //-------------------------------------

    public String getExpListByTeamId(String teamId) {
        return "http://" + sioAddress + ":" + sioPort + "/" + expEndpoint + "/teams/" + teamId;
    }

    public String getRealizationByTeam(String teamName, String expId) {
        return "http://" + sioAddress + ":" + sioPort + "/" + realEndpoint + "/team/" + teamName + "/experiment/" + expId;
    }

    public String getDeleteExperiment(String teamId, String expId) {
        return "http://" +  sioAddress + ":" + sioPort + "/" + expEndpoint + "/teams/" + teamId + "/experiments/" + expId;
    }

    public String getStartExperiment(String teamName, String expId) {
        return "http://" + sioAddress + ":" + sioPort + "/" + realEndpoint + "/start/team/" + teamName + "/experiment/" + expId;
    }

    public String getStopExperiment(String teamName, String expId) {
        return "http://" +  sioAddress + ":" + sioPort + "/" + realEndpoint + "/stop/team/" + teamName + "/experiment/" + expId;
    }
}

