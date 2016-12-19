package sg.ncl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import sg.ncl.testbed_interface.TeamStatus;

/**
 * Created by Te Ye
 */
@ConfigurationProperties(prefix = ConnectionProperties.PREFIX)
public class ConnectionProperties {

    private static final String HTTP_MODE = "http://";
    public static final String PREFIX = "ncl.web.service";

    private String sioAddress;
    private String sioPort;
    private String authEndpoint;
    private String credEndpoint;
    private String dataEndpoint;
    private String regEndpoint;
    private String userEndpoint;
    private String teamEndpoint;
    private String teamVisibilityEndpoint;
    private String expEndpoint;
    private String approveJoinRequest;
    private String realEndpoint;
    private String imageEndpoint;

    // service-telemetry
    private String telemetryAddress;
    private String telemetryPort;
    private String telemetryEndpoint;

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

    public String getDataEndpoint() {
        return dataEndpoint;
    }

    public void setDataEndpoint(String dataEndpoint) {
        this.dataEndpoint = dataEndpoint;
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

    public String getImageEndpoint() {
        return imageEndpoint;
    }

    public void setImageEndpoint(String imageEndpoint) {
        this.imageEndpoint = imageEndpoint;
    }

    public String getSioUsersUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + userEndpoint + "/";
    }

    public String getSioUsersStatusUrl(final String id, final String status) {
        return "http://" + sioAddress + ":" + sioPort + "/" + userEndpoint + "/" + id + "/status/" + status;
    }

    public String getSioTeamsUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/";
    }

    public String getSioTeamsStatusUrl(final String id, final TeamStatus status) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/" + id + "/status/" + status;
    }

    public String getSioAuthUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + authEndpoint;
    }

    public String getSioCredUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + credEndpoint + "/";
    }

    public String getSioDataUrl() {
        return "http://" + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/";
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

    public String getTopology(String teamName, String expId) {
        return "http://" +  sioAddress + ":" + sioPort + "/" + expEndpoint + "/teams/" + teamName + "/experiments/" + expId + "/topology";
    }

    public String getAllExperiment() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint + "/experiments";
    }

    //-------------------------------------
    // DATA
    //-------------------------------------

    public String getPublicData() {
        return "http://" +  sioAddress + ":" + sioPort + "/" + dataEndpoint + "?visibility=PUBLIC";
    }

    public String getData() {
        return "http://" +  sioAddress + ":" + sioPort + "/" + dataEndpoint;
    }

    public String getDataset(String dataId) {
        return "http://" +  sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId;
    }

    public String getResource(String dataId, String resourceId) {
        return "http://" +  sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/resources/" + resourceId;
    }

    public String downloadResource(String dataId, String resourceId) {
        return "http://" +  sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/resources/" + resourceId + "/download";
    }

    //-------------------------------------
    // TELEMETRY
    //-------------------------------------

    public String getTelemetryAddress() {
        return telemetryAddress;
    }

    public void setTelemetryAddress(String telemetryAddress) {
        this.telemetryAddress = telemetryAddress;
    }

    public String getTelemetryPort() {
        return telemetryPort;
    }

    public void setTelemetryPort(String telemetryPort) {
        this.telemetryPort = telemetryPort;
    }

    public String getTelemetryEndpoint() {
        return telemetryEndpoint;
    }

    public void setTelemetryEndpoint(String telemetryEndpoint) {
        this.telemetryEndpoint = telemetryEndpoint;
    }

    public String getFreeNodes() {
        return "http://" + telemetryAddress + ":" + telemetryPort + "/" + telemetryEndpoint;
    }
    
    //-------------------------------------
    // IMAGES
    //-------------------------------------

    public String getAllImages() {
        return "http://" + sioAddress + ":" + sioPort + "/" + imageEndpoint;
    }

    public String getTeamImages(String teamId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + imageEndpoint + "?teamId=" + teamId;
    }

    public String getTeamSavedImages(String teamId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + imageEndpoint + "/teams/" + teamId;
    }

    public String saveImage() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + imageEndpoint;
    }

    //-------------------------------------
    // USAGE STATISTICS
    //-------------------------------------
    public String getUsageStatisticsByTeamId(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" +realEndpoint + "/teams/" + id + "/usage";
    }

    //-------------------------------------
    // PASSWORD RESET
    //-------------------------------------
    public String getPasswordResetRequestURI() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" +credEndpoint + "/password/resets";
    }

    public String getPasswordResetURI() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" +credEndpoint + "/password";
    }

    //-------------------------------------
    // DATA RESOURCE UPLOAD
    //-------------------------------------
    public String checkUploadChunk(String dataId, Integer chunkNumber, String identifier) {
        return "http://" + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/chunks/" + chunkNumber + "/files/" + identifier;
    }

    public String sendUploadChunk(String dataId, Integer chunkNumber) {
        return "http://" + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/chunks/" + chunkNumber;
    }
}
