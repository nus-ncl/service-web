package sg.ncl;

import sg.ncl.testbed_interface.Team2;

import java.util.HashMap;

/**
 * Created by Te Ye
 */
public class TeamManager2 {

    private static TeamManager2 TEAM_MANAGER_SINGLETON = null;
    private HashMap<String, Team2> teamMap; /* teamId - Team */
    private HashMap<String, Team2> publicTeamMap;

    private TeamManager2() {
        teamMap = new HashMap<String, Team2>();
        publicTeamMap = new HashMap<String, Team2>();
    }

    public static TeamManager2 getInstance() {
        if (TEAM_MANAGER_SINGLETON == null) {
            TEAM_MANAGER_SINGLETON = new TeamManager2();
        }
        return TEAM_MANAGER_SINGLETON;
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
}
