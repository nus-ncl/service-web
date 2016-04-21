package sg.ncl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sg.ncl.testbed_interface.Team;

public class TeamManager {
    
    /* teamId, team */
    private static TeamManager TEAM_MANAGER_SINGLETON = new TeamManager();
    private HashMap<Integer, Team> teamMap;
    private HashMap<Integer, Team> invitedToParticipateMap;
    private HashMap<Integer, Team> joinRequestTeamMap;
    
    private TeamManager() {
        Team team1 = new Team();
        team1.setId(110);
        team1.setName("hybridcloud");
        team1.setDescription("this is a simple desc");
        team1.setWebsite("http://www.nus.edu.sg");
        team1.setOrganizationType("academia");
        team1.setMembers(10);
        team1.setMembersAwaitingApproval(2);
        team1.setInstitution("NUS");
        team1.setIsApproved(true);
        team1.setIsPublic(true);
        team1.setExperimentsCount(99);
        team1.setTeamOwnerId(200);
        team1.addMembers(200, "owner");
        team1.addMembers(201, "member");
        
        Team team2 = new Team();
        team2.setId(111);
        team2.setName("hybridcloud");
        team2.setDescription("this is a simple desc");
        team2.setWebsite("http://www.nus.edu.sg");
        team2.setOrganizationType("academia");
        team2.setMembers(10);
        team2.setMembersAwaitingApproval(2);
        team2.setInstitution("NUS");
        team2.setIsApproved(true);
        team2.setIsPublic(true);
        team2.setExperimentsCount(99);
        team2.setTeamOwnerId(200);
        team2.addMembers(200, "owner");
        team2.addMembers(201, "member");
        
        Team team3 = new Team();
        team3.setId(112);
        team3.setName("myPrivateProject");
        team3.setDescription("this project is not supposed to show up on public");
        team3.setWebsite("http://www.nus.edu.sg");
        team3.setOrganizationType("academia");
        team3.setMembers(10);
        team3.setMembersAwaitingApproval(2);
        team3.setInstitution("NUS");
        team3.setIsApproved(true);
        team3.setIsPublic(false);
        team3.setExperimentsCount(99);
        team3.setTeamOwnerId(201);
        team3.addMembers(201, "owner");
        team3.addMembers(200, "member");
        
        Team team4 = new Team();
        team4.setId(113);
        team4.setName("Incident Response");
        team4.setDescription("simulate large scale network attack on critical infrastructure to test incident response team");
        team4.setWebsite("http://www.ntu.edu.sg");
        team4.setOrganizationType("academia");
        team4.setMembers(10);
        team4.setMembersAwaitingApproval(2);
        team4.setInstitution("NTU");
        team4.setIsApproved(true);
        team4.setIsPublic(false);
        team4.setExperimentsCount(99);
        team4.setTeamOwnerId(202);
        team4.addMembers(202, "owner");
        
        Team team5 = new Team();
        team5.setId(114);
        team5.setName("Nessus");
        team5.setDescription("this is a simple desc");
        team5.setWebsite("http://www.ntu.edu.sg");
        team5.setOrganizationType("academia");
        team5.setMembers(10);
        team5.setMembersAwaitingApproval(2);
        team5.setInstitution("NTU");
        team5.setIsApproved(true);
        team5.setIsPublic(true);
        team5.setExperimentsCount(99);
        team5.setTeamOwnerId(203);
        team5.addMembers(203, "owner");
        
        // add to global team map
        teamMap = new HashMap<Integer, Team>();
        teamMap.put(110, team1);
        teamMap.put(111, team2);
        teamMap.put(112, team3);
        teamMap.put(113, team4);
        teamMap.put(114, team5);
        
        invitedToParticipateMap = new HashMap<Integer, Team>();
        invitedToParticipateMap.put(113, team4);
        joinRequestTeamMap = new HashMap<Integer, Team>();
        joinRequestTeamMap.put(114, team5);
    }
    
    public static TeamManager getInstance() {
        return TEAM_MANAGER_SINGLETON;
    }
    
    public HashMap<Integer, Team> getPublicTeamMap() {
        HashMap<Integer, Team> rv = new HashMap<Integer, Team>();
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            if (currTeam.getIsPublic()) {
                rv.put(currTeamId, currTeam);
            }
        }
        return rv;
    }

    public HashMap<Integer, Team> getTeamMap() {
        return teamMap;
    }
    
    /**
     * @return list of team information by userid
     */
    public HashMap<Integer, Team> getTeamMap(int userId) {
        HashMap<Integer, Team> rv = new HashMap<Integer, Team>();
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            
            HashMap<Integer, String> membersMap = currTeam.getMembersMap();
            for (Map.Entry<Integer, String> membersEntry : membersMap.entrySet()) {
                if (membersEntry.getKey() == userId) {
                    rv.put(currTeamId, currTeam);
                }
            }
        }
        return rv;
    }

    public HashMap<Integer, Team> getInvitedToParticipateMap() {
        return invitedToParticipateMap;
    }

    public void setInvitedToParticipateMap(HashMap<Integer, Team> invitedToParticipateMap) {
        this.invitedToParticipateMap = invitedToParticipateMap;
    }

    public HashMap<Integer, Team> getJoinRequestTeamMap() {
        return joinRequestTeamMap;
    }

    public void setJoinRequestTeamMap(HashMap<Integer, Team> joinRequestTeamMap) {
        this.joinRequestTeamMap = joinRequestTeamMap;
    }
}
