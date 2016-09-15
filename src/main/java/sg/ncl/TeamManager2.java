package sg.ncl;

import sg.ncl.testbed_interface.Team2;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Te Ye
 */
public class TeamManager2 {

    private HashMap<String, Team2> teamMap; /* teamId - Team */
    private HashMap<String, Team2> publicTeamMap;
    private HashMap<String, Team2> userJoinRequestMap; /* teamId - Team*/

    public TeamManager2() {
        teamMap = new HashMap<>();
        publicTeamMap = new HashMap<>();
        userJoinRequestMap = new HashMap<>();
    }


    public HashMap<String, Team2> getTeamMap() {
        return teamMap;
    }

    public void setTeamMap(HashMap<String, Team2> teamMap) {
        this.teamMap = teamMap;
    }

    public void addTeamToTeamMap(Team2 team2) {
        teamMap.put(team2.getId(), team2);
    }

    public HashMap<String, Team2> getPublicTeamMap() {
        return publicTeamMap;
    }

    public void setPublicTeamMap(HashMap<String, Team2> publicTeamMap) {
        this.publicTeamMap = publicTeamMap;
    }

    public void addTeamToPublicTeamMap(Team2 team2) {
        publicTeamMap.put(team2.getId(), team2);
    }

    public HashMap<String, Team2> getUserJoinRequestMap() {
        return userJoinRequestMap;
    }

    void setUserJoinRequestMap(HashMap<String, Team2> userJoinRequestMap) {
        this.userJoinRequestMap = userJoinRequestMap;
    }

    public void addTeamToUserJoinRequestTeamMap(Team2 team2) {
        userJoinRequestMap.put(team2.getId(), team2);
    }
}
