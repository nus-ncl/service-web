package sg.ncl.testbed_interface;

import java.util.Date;

public class NodeUsageReservationForm {

    private String startDate;
    private String endDate;
    private int noOfNodes;
    private String teamId;
    private String projectId;


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }



    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }



    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    public int getNoOfNodes() {
        return noOfNodes;
    }

    public void setNoOfNodes(int noOfNodes) {
        this.noOfNodes = noOfNodes;
    }


}
