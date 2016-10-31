package sg.ncl.testbed_interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Desmond
 */
public class Realization {

    private Long id;
    private Long experimentId;
    private String experimentName;
    private String userId;
    private String teamId;
    private Integer numberOfNodes;
    private String state;
    private String details;
    private Long idleMinutes;
    private Long runningMinutes;
    private List<String> detailsList;

    private Map<String, Map<String, String>> nodesInfoMap;

    public Realization() {
        details = "";
        detailsList = new ArrayList<>();
        nodesInfoMap = new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Long experimentId) {
        this.experimentId = experimentId;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Integer getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(Integer numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getIdleMinutes() {
        return idleMinutes;
    }

    public void setIdleMinutes(Long idleMinutes) {
        this.idleMinutes = idleMinutes;
    }

    public Long getRunningMinutes() {
        return runningMinutes;
    }

    public void setRunningMinutes(Long runningMinutes) {
        this.runningMinutes = runningMinutes;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<String> getDetailsList() {
        return detailsList;
    }

    public void setDetailsList(List<String> detailsList) {
        this.detailsList = detailsList;
    }

    public Map<String, Map<String, String>> getNodesInfoMap() {
        return nodesInfoMap;
    }

    public void setNodesInfoMap(Map<String, Map<String, String>> nodesInfoMap) {
        this.nodesInfoMap = nodesInfoMap;
    }

    public void addNodeDetails(String nodeName, Map<String, String> nodeDetails) {
        nodesInfoMap.put(nodeName, nodeDetails);
    }
}
