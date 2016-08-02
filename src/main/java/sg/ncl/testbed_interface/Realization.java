package sg.ncl.testbed_interface;

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
    private Long idleMinutes;
    private Long runningMinutes;

    public Realization() {}

    public Realization(final Long id,
                       final Long experimentId,
                       final String experimentName,
                       final String userId,
                       final String teamId,
                       final Integer numberOfNodes,
                       final String state,
                       final Long idleMinutes,
                       final Long runningMinutes) {

        this.id = id;
        this.experimentId = experimentId;
        this.experimentName = experimentName;
        this.userId = userId;
        this.teamId = teamId;
        this.numberOfNodes = numberOfNodes;
        this.state = state;
        this.idleMinutes = idleMinutes;
        this.runningMinutes = runningMinutes;
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
}
