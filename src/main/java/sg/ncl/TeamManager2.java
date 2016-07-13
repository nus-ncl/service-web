package sg.ncl;

import sg.ncl.testbed_interface.Team2;
import sg.ncl.testbed_interface.User2;

import java.util.*;

/**
 * Created by Te Ye
 */
public class TeamManager2 {

    private static TeamManager2 TEAM_MANAGER_SINGLETON = null;
    private HashMap<String, Team2> teamMap; /* teamId - Team */
    private HashMap<String, Team2> publicTeamMap;
    private HashMap<String, List<Team2>> userJoinRequestMap; /* userId, arraylists of team */

    private TeamManager2() {
        teamMap = new HashMap<>();
        publicTeamMap = new HashMap<>();
        userJoinRequestMap = new HashMap<>();
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

    public HashMap<String, List<Team2>> getUserJoinRequestMap() {
        return userJoinRequestMap;
    }

    void setUserJoinRequestMap(HashMap<String, List<Team2>> userJoinRequestMap) {
        this.userJoinRequestMap = userJoinRequestMap;
    }

    public void addUserJoinRequestMap(String teamId, User2 requestUser) {
        if (userJoinRequestMap.containsKey(requestUser.getId())) {
            // ensure user is not already in the team or have submitted the application
            for (Map.Entry<String, List<Team2>> entry : userJoinRequestMap.entrySet()) {
                String currUserId = entry.getKey();
                if (currUserId.equals(requestUser.getId())) {
                    List<Team2> currJoinTeamList = entry.getValue();
                    for (ListIterator<Team2> iter = currJoinTeamList.listIterator(); iter.hasNext();) {
                        Team2 currTeam = iter.next();
                        if (currTeam.getId().equals(teamId)) {
                            return;
                        }
                    }
                    Team2 one = teamMap.get(teamId);
                    // have existing join request but not for this team
                    currJoinTeamList.add(one);
                    // one.addUserToJoinRequestMap(userId, requestIssuer);
                    userJoinRequestMap.put(requestUser.getId(), currJoinTeamList);
                }
            }
        } else {
            // first time user make join request
            // add to user join team list
            // get team obj from teamName
            // put to map
            List<Team2> myJoinRequestList = new ArrayList<>();
            Team2 one = teamMap.get(teamId);
            myJoinRequestList.add(one);
            userJoinRequestMap.put(requestUser.getId(), myJoinRequestList);
            // one.addUserToJoinRequestMap(userId, requestIssuer);
        }
    }

}
