package sg.ncl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import sg.ncl.domain.NodeType;
import sg.ncl.testbed_interface.TeamStatus;

/**
 * @authors: Te Ye, Tran Ly Vu
 */
@ConfigurationProperties(prefix = ConnectionProperties.PREFIX)
@Getter
@Setter
public class ConnectionProperties {

    private static final String HTTP_MODE = "http://";
    public static final String PREFIX = "ncl.web.service";
    public static final String EXPERIMENTS = "/experiments/";
    private static final String RESOURCES = "resources";

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
    private String analyticsEndpoint;

    // service-telemetry
    private String telemetryAddress;
    private String telemetryPort;
    private String telemetryEndpoint;


    public String getSioUsersUrl() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + userEndpoint + "/";
    }

    public String getSioUsersStatusUrl(final String id, final String status) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + userEndpoint + "/" + id + "/status/" + status;
    }

    public String getSioTeamsUrl() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/";
    }

    public String getSioTeamsStatusUrl(final String id, final TeamStatus status) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/" + id + "/status/" + status;
    }

    public String getSioAuthUrl() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + authEndpoint;
    }

    public String getSioCredUrl() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + credEndpoint + "/";
    }

    public String getSioDataUrl() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/";
    }

    public String getSioRegUrl() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + regEndpoint;
    }

    public String getSioExpUrl() {return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint;
    }

    //-------------------------------------
    // CREDENTIALS
    //-------------------------------------
    public String getUpdateCredentials(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + credEndpoint + "/" + id;
    }

    //-------------------------------------
    // REGISTRATIONS
    //-------------------------------------

    public String getDeterUid(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + regEndpoint + "/user/" + id;
    }

    public String getRejectJoinRequest(String teamId, String userId) {
        // same but REST API is DELETE
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + regEndpoint + "/teams/" + teamId + "/members/" + userId;
    }

    // for existing users
    public String getRegisterRequestToApplyTeam(String nclUserId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + regEndpoint + "/newTeam/" + nclUserId;
    }

    // for existing users
    public String getJoinRequestExistingUser() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + regEndpoint + "/joinApplications";
    }

    public String getApproveTeam(String teamId, String ownerId, TeamStatus teamStatus) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + regEndpoint + "/teams/" + teamId + "/owner/" + ownerId + "?status=" + teamStatus;
    }

    public String getApproveJoinRequest(String teamId, String userId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + regEndpoint + "/teams/" + teamId + "/members/" + userId;
    }


    //-------------------------------------
    // TEAMS
    //-------------------------------------

    public String getTeamByName(String name) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "?name=" + name;
    }

    public String getTeamById(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/" + id;
    }

    public String getTeamsByVisibility(String visibility) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "?visibility=" + visibility;
    }

    public String getQuotaByTeamId(String teamId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/" + teamId + "/quota" ;
    }

    //-------------------------------------
    // USERS
    //-------------------------------------

    public String getUser(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + userEndpoint + "/" + id;
    }

    public String removeUserFromTeam(String teamId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/" + teamId + "/members";
    }

    //-------------------------------------
    // EXPERIMENTS
    //-------------------------------------
    public String getExperiment(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint + "/" + id;
    }

    public String getStatefulExperiment(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint + "/" + id + "/status";
    }

    public String getExperimentDetails(String teamId, String expId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint + "/teams/" + teamId + "/" +  expEndpoint + "/" + expId + "/experimentDetails";
    }

    public String getExpListByTeamId(String teamId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint + "/teams/" + teamId;
    }

    public String getStatefulExperimentsByTeam(String teamId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + teamEndpoint + "/" + teamId + "/" + expEndpoint;
    }

    public String getRealizationByTeam(String teamName, String expId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + realEndpoint + "/team/" + teamName + "/experiment/" + expId;
    }

    public String getDeleteExperiment(String teamId, String expId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint + "/teams/" + teamId + EXPERIMENTS + expId;
    }

    public String getStartExperiment(String teamName, String expId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + realEndpoint + "/start/team/" + teamName + "/experiment/" + expId;
    }

    public String getStopExperiment(String teamName, String expId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + realEndpoint + "/stop/team/" + teamName + "/experiment/" + expId;
    }

    public String requestInternetExperiment (String teamId, String expId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint + "/teams/" + teamId + EXPERIMENTS + expId + "/internet";
    }

    public String getTopology(String teamName, String expId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + expEndpoint + "/teams/" + teamName + EXPERIMENTS + expId + "/topology";
    }

    public String getAllRealizations() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + realEndpoint;
    }

    //-------------------------------------
    // DATA
    //-------------------------------------

    public String requestDataset(String dataId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/requests";
    }

    public String getRequest(String dataId, String requestId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/requests/" + requestId;
    }

    public String getPublicDataUsers() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/public/users";
    }

    public String getPublicDataUser(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/public/users/" + id;
    }

    public String getPublicData() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "?visibility=PUBLIC";
    }

    public String getPublicDataset(String dataId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "?visibility=PUBLIC";
    }

    public String downloadPublicOpenResource(String dataId, String resourceId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/" + RESOURCES + "/" + resourceId + "/download?visibility=PUBLIC";
    }

    public String getData() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint;
    }

    public String getDataset(String dataId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId;
    }

    public String searchDatasets(String keywords) {
        StringBuilder params = new StringBuilder();
        String[] keywordArray = keywords.split("\\W+");
        for (int i = 0; i < keywordArray.length; i++) {
            if (i == 0) {
                params.append("?keyword=").append(keywordArray[i]);
            } else {
                params.append("&keyword=").append(keywordArray[i]);
            }
        }
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/search" + params.toString();
    }

    public String getResource(String dataId, String resourceId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/" + RESOURCES + "/" + resourceId;
    }

    public String downloadResource(String dataId, String resourceId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/" + RESOURCES + "/" + resourceId + "/download";
    }

    public String getCategories() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/categories";
    }

    public String getCategory(Integer categoryId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/categories/" + categoryId;
    }

    public String getLicenses() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/licenses";
    }

    public String getLicense(Integer licenseId) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/licenses/" + licenseId;
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

    public String getNodes(NodeType nodeType) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + telemetryEndpoint + "/nodes/counts?type=" + nodeType;
    }

    public String getNodesStatus() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + telemetryEndpoint + "/nodes/status";
    }

    // retrieve the number of logged in users and running experiments
    public String getTestbedStats() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + telemetryEndpoint + "/testbed/stats";
    }

    //-------------------------------------
    // IMAGES
    //-------------------------------------

    public String getAllImages() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + imageEndpoint;
    }

    public String getGlobalImages() {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + imageEndpoint + "/global";
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

    public String deleteImage(String teamId, String imageName) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + imageEndpoint + "/" + imageName + "?teamId=" + teamId ;
    }

    //-------------------------------------
    // USAGE STATISTICS
    //-------------------------------------
    public String getUsageStatisticsByTeamId(String id) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" +realEndpoint + "/teams/" + id + "/usage";
    }

    //-------------------------------------
    // ENERGY STATISTICS
    //-------------------------------------

    public String getEnergyStatistics(String... paramString) {
        StringBuilder bld = new StringBuilder();
        for (int i = 0; i < paramString.length; i++) {
            if (i == 0) {
                bld.append("?");
                bld.append(paramString[i]);
            } else {
                bld.append("&");
                bld.append(paramString[i]);
            }
        }
        String params = bld.toString();
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" +analyticsEndpoint + "/energy" + params;
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
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/chunks/" + chunkNumber + "/files/" + identifier;
    }

    public String sendUploadChunk(String dataId, Integer chunkNumber) {
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + dataEndpoint + "/" + dataId + "/chunks/" + chunkNumber;
    }

    //-------------------------------------
    // ANALYTICS (DATA DOWNLOADS)
    //-------------------------------------
    public String getDownloadStat(String... paramString) {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramString.length; i++) {
            if (i == 0) {
                params.append("?").append(paramString[i]);
            } else {
                params.append("&").append(paramString[i]);
            }
        }
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + analyticsEndpoint + "/datasets/downloads" + params.toString();
    }

    public String getPublicDownloadStat(String... paramString) {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramString.length; i++) {
            if (i == 0) {
                params.append("?").append(paramString[i]);
            } else {
                params.append("&").append(paramString[i]);
            }
        }
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + analyticsEndpoint + "/datasets/downloads/public" + params.toString();
    }

    public String getUsageStat(String teamId, String... paramString) {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramString.length; i++) {
            if (i == 0) {
                params.append("?").append(paramString[i]);
            } else {
                params.append("&").append(paramString[i]);
            }
        }
        return HTTP_MODE + sioAddress + ":" + sioPort + "/" + analyticsEndpoint + "/usage/teams/" + teamId + params.toString();
    }
}
