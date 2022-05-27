package sg.ncl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import sg.ncl.testbed_interface.Team;
import sg.ncl.testbed_interface.User;

public class TeamManager {
    
    /* teamId, team */
	private final int NO_EXP_COUNT = 0;
    private static TeamManager TEAM_MANAGER_SINGLETON = null;
    private HashMap<Integer, Team> teamMap; /* teamId - Team */
    private String infoMsg = "";
    
    private HashMap<Integer, List<Team>> invitedToParticipateMap2; /* userId, arraylists of team */
    private HashMap<Integer, List<Team>> joinRequestMap2; /* userId, arraylists of team */
    
    private final String OWNER_POSITION = "owner";
    private final String MEMBER_POSITION = "member";
    private final String PENDING_ACCEPT_INVITATION_POSITION = "pending for acceptance";

    private static final String DESCRIPTION = "this is a simple desc";
    private static final String NUS_WEBSITE = "http://www.nus.edu.sg";
    private static final String ORGANIZATION_TYPE = "academia";
    private static final String NTU_WEBSITE = "http://www.ntu.edu.sg";
    private static final Random RANDOM = new SecureRandom();

    private TeamManager() {
        Team team1 = new Team();
        team1.setId(110);
        team1.setName("hybridcloud");
        team1.setDescription(DESCRIPTION);
        team1.setWebsite(NUS_WEBSITE);
        team1.setOrganizationType(ORGANIZATION_TYPE);
        team1.setInstitution("NUS");
        team1.setIsApproved(true);
        team1.setIsPublic(true);
        team1.setExperimentsCount(1);
        team1.setCharges(1.50);
        team1.setTeamOwnerId(200);
        team1.addMembers(200, OWNER_POSITION);
        team1.addMembers(201, MEMBER_POSITION);
        
        Team team2 = new Team();
        team2.setId(111);
        team2.setName("hybridcloud");
        team2.setDescription(DESCRIPTION);
        team2.setWebsite(NUS_WEBSITE);
        team2.setOrganizationType(ORGANIZATION_TYPE);
        team2.setInstitution("NUS");
        team2.setIsApproved(true);
        team2.setIsPublic(true);
        team2.setExperimentsCount(1);
        team2.setCharges(2.50);
        team2.setTeamOwnerId(200);
        team2.addMembers(200, OWNER_POSITION);
        team2.addMembers(201, MEMBER_POSITION);
        
        Team team3 = new Team();
        team3.setId(112);
        team3.setName("myPrivateProject");
        team3.setDescription("this project is not supposed to show up on public");
        team3.setWebsite(NUS_WEBSITE);
        team3.setOrganizationType(ORGANIZATION_TYPE);
        team3.setInstitution("NUS");
        team3.setIsApproved(true);
        team3.setIsPublic(false);
        team3.setExperimentsCount(1);
        team3.setCharges(3.50);
        team3.setTeamOwnerId(201);
        team3.addMembers(201, OWNER_POSITION);
        team3.addMembers(200, MEMBER_POSITION);
        
        Team team4 = new Team();
        team4.setId(113);
        team4.setName("Incident Response");
        team4.setDescription("simulate large scale network attack on critical infrastructure to test incident response team");
        team4.setWebsite(NTU_WEBSITE);
        team4.setOrganizationType(ORGANIZATION_TYPE);
        team4.setInstitution("NTU");
        team4.setIsApproved(true);
        team4.setIsPublic(false);
        team4.setExperimentsCount(NO_EXP_COUNT);
        team4.setCharges(4.50);
        team4.setTeamOwnerId(202);
        team4.addMembers(202, OWNER_POSITION);
        
        Team team5 = new Team();
        team5.setId(114);
        team5.setName("Nessus");
        team5.setDescription(DESCRIPTION);
        team5.setWebsite(NTU_WEBSITE);
        team5.setOrganizationType(ORGANIZATION_TYPE);
        team5.setInstitution("NTU");
        team5.setIsApproved(true);
        team5.setIsPublic(true);
        team5.setExperimentsCount(NO_EXP_COUNT);
        team5.setCharges(5.50);
        team5.setTeamOwnerId(202);
        team5.addMembers(202, OWNER_POSITION);
        
        Team team6 = new Team();
        team6.setId(115);
        team6.setName("Dave Private Project");
        team6.setDescription(DESCRIPTION);
        team6.setWebsite(NTU_WEBSITE);
        team6.setOrganizationType(ORGANIZATION_TYPE);
        team6.setInstitution("NTU");
        team6.setIsApproved(false);
        team6.setIsPublic(false);
        team6.setExperimentsCount(NO_EXP_COUNT);
        team6.setTeamOwnerId(203);
        team6.addMembers(203, OWNER_POSITION);
        
        // add to global team map
        teamMap = new HashMap<Integer, Team>();
        teamMap.put(110, team1);
        teamMap.put(111, team2);
        teamMap.put(112, team3);
        teamMap.put(113, team4);
        teamMap.put(114, team5);
        teamMap.put(115, team6);
        
        joinRequestMap2 = new HashMap<Integer, List<Team>>();

        invitedToParticipateMap2 = new HashMap<Integer, List<Team>>();

    }
    
    public static TeamManager getInstance() {
        if (TEAM_MANAGER_SINGLETON == null) {
            TEAM_MANAGER_SINGLETON = new TeamManager();
        }
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
     * @return list of team information that contains the user
     * user can be a member or owner of the team
     * used for create experiment page
     */
    public HashMap<Integer, Team> getTeamMap(int userId) {
        HashMap<Integer, Team> rv = new HashMap<Integer, Team>();
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            
            HashMap<Integer, String> membersMap = currTeam.getMembersMap();
            for (Map.Entry<Integer, String> membersEntry : membersMap.entrySet()) {
                if (membersEntry.getKey() == userId && !membersEntry.getValue().equals(PENDING_ACCEPT_INVITATION_POSITION)) {
                    // don't allow users if user is still pending for acceptance
                	rv.put(currTeamId, currTeam);
                }
            }
        }
        return rv;
    }
    
    /**
     * @return list of team information that contains the user
     * user must be the owner of the team
     */
    public HashMap<Integer, Team> getTeamMapByTeamOwner(int userId) {
        HashMap<Integer, Team> rv = new HashMap<Integer, Team>();
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            
            if (currTeam.getTeamOwnerId() == userId) {
            	rv.put(currTeamId, currTeam);
            }
        }
        return rv;
    }
    
    /**
     * Check to ensure if user is in only one team and that team must be validated
     * @param userId
     * @return true if at least one team is approved, false otherwise
     */
    public boolean checkTeamValidation(int userId) {
        int teamApprovedCount = 0;
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            Team currTeam = entry.getValue();
            
            // check team is validated
            HashMap<Integer, String> membersMap = currTeam.getMembersMap();
            for (Map.Entry<Integer, String> membersEntry : membersMap.entrySet()) {
                if (membersEntry.getKey() == userId && currTeam.getIsApproved()) {
                    teamApprovedCount++;
                }
            }
        }
        
        if (teamApprovedCount <= 0) {
            return false;
        } else {
            return true;
        }
    }
    
    // returns the number of approved teams that the member is in
    public int getApprovedTeams(int userId) {
        int approvedTeamCount = 0;
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            Team currTeam = entry.getValue();
            
            // check team is validated
            HashMap<Integer, String> membersMap = currTeam.getMembersMap();
            for (Map.Entry<Integer, String> membersEntry : membersMap.entrySet()) {
                if (membersEntry.getKey() == userId && currTeam.getIsApproved()) {
                	approvedTeamCount++;
                }
            }
        }
        return approvedTeamCount;
    }
    
    // returns the number of unapproved teams that the member is in
    public int getUnApprovedTeams(int userId) {
        int unApprovedTeamCount = 0;
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            Team currTeam = entry.getValue();
            
            // check team is validated
            HashMap<Integer, String> membersMap = currTeam.getMembersMap();
            for (Map.Entry<Integer, String> membersEntry : membersMap.entrySet()) {
                if (membersEntry.getKey() == userId && !currTeam.getIsApproved()) {
                	unApprovedTeamCount++;
                }
            }
        }
        return unApprovedTeamCount;
    }
    
    public void addJoinRequestTeamMap2(int userId, int teamId, User requestIssuer) {
        if (joinRequestMap2.containsKey(userId)) {
            // ensure user is not already in the team or have submitted the application
            for (Map.Entry<Integer, List<Team>> entry : joinRequestMap2.entrySet()) {
                int currUserId = entry.getKey();
                if (currUserId == userId) {
                    List<Team> currJoinTeamList = entry.getValue();
                    for (ListIterator<Team> iter = currJoinTeamList.listIterator(); iter.hasNext();) {
                        Team currTeam = iter.next();
                        if (currTeam.getId() == teamId) {
                            return;
                        }
                    }
                    Team toBeAddedTeam = TEAM_MANAGER_SINGLETON.getTeamByTeamId(teamId);
                    // have existing join request but have not submitted before
                    currJoinTeamList.add(toBeAddedTeam);
                    toBeAddedTeam.addUserToJoinRequestMap(userId, requestIssuer);
                }
            }
        } else {
            // first time user make join request
            // add to user join team list
            // get team obj from teamName
            // put to map
            List<Team> myJoinRequestList = new ArrayList<Team>();
            Team toBeAddedTeam = TEAM_MANAGER_SINGLETON.getTeamByTeamId(teamId);
            myJoinRequestList.add(toBeAddedTeam);
            joinRequestMap2.put(userId, myJoinRequestList);
            toBeAddedTeam.addUserToJoinRequestMap(userId, requestIssuer);
        }
    }
    
    public List<Team> getJoinRequestTeamMap2(int userId) {
        List<Team> rv = null;
        for (Map.Entry<Integer, List<Team>> entry : joinRequestMap2.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                rv = entry.getValue();
                return rv;
            }
        }
        return rv;
    }
    
    public void removeUserJoinRequest2(int userId, int teamId) {
        for (Map.Entry<Integer, List<Team>> entry : joinRequestMap2.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                List<Team> joinTeamList = entry.getValue();
                for (ListIterator<Team> iter = joinTeamList.listIterator(); iter.hasNext();) {
                    Team currTeam = iter.next();
                    if (currTeam.getId() == teamId) {
                        iter.remove();
                    }
                }
            }
        }
    }
    
    // team owner's accept join request
    public void acceptJoinRequest(int userId, int teamId) {
        // reduce member approval
        
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            if (currTeamId == teamId) {
                // remove join request from team side
                currTeam.removeUserFromJoinRequestMap(userId);
                
                // add userid to members map
                currTeam.addMembers(userId, MEMBER_POSITION);
                teamMap.put(currTeamId, currTeam);
                
                // also need to remove join request from user side
                removeUserJoinRequest2(userId, teamId);
                return;
            }
        }
    }
    
    // team owner's reject join request
    public void rejectJoinRequest(int userId, int teamId) {
        // reduce member approval
        
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            if (currTeamId == teamId) {
                // remove join request from team side
                currTeam.removeUserFromJoinRequestMap(userId);
                
                // need to put back to map to make the changes
                teamMap.put(currTeamId, currTeam);
                
                // also need to remove join request from user side
                removeUserJoinRequest2(userId, teamId);
                return;
            }
        }
    }
    
    public void acceptParticipationRequest(int userId, int teamId) {
        // TODO check if userId indeed have a participation request
        // add as member
        // update team
    	// member position will be overwritten to "member"
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            if (currTeamId == teamId) {
                currTeam.addMembers(userId, MEMBER_POSITION);
                teamMap.put(currTeamId, currTeam);
                return;
            }
        }
    }
    
    public void setInfoMsg(String msg) {
        infoMsg = msg;
    }
    
    public String getInfoMsg() {
        return infoMsg;
    }
    
    public int getTeamIdByTeamName(String teamName) {
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            if (currTeam.getName().equalsIgnoreCase(teamName)) {
                return currTeamId;
            }
        }
        return 0;
    }
    
    public String getTeamNameByTeamId(int teamId) {
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            if (currTeamId == teamId) {
                return currTeam.getName();
            }
        }
        return "";
    }
    
    public Team getTeamByTeamId(int teamId) {
        Team rv = null;
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            rv = entry.getValue();
            if (currTeamId == teamId) {
                return rv;
            }
        }
        return rv;
    }
    
    public Team getTeamByTeamName(String teamName) {
        Team rv = null;
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            rv = entry.getValue();
            if (rv.getName().equals(teamName)) {
                return rv;
            }
        }
        return rv;
    }
    
    public void removeMembers(int userId, int teamId) {
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            if (currTeamId == teamId) {
                currTeam.removeMembers(userId);
            }
        }
    }
    
    public void addInvitedToParticipateMap(int userId, int teamId) {
        if (invitedToParticipateMap2.containsKey(userId)) {
            // ensure user is not already in the team or have already been invited
            for (Map.Entry<Integer, List<Team>> entry : invitedToParticipateMap2.entrySet()) {
                int currUserId = entry.getKey();
                if (currUserId == userId) {
                    List<Team> currInviteList = entry.getValue();
                    for (ListIterator<Team> iter = currInviteList.listIterator(); iter.hasNext();) {
                        Team currTeam = iter.next();
                        if (currTeam.getId() == teamId && currTeam.isUserInTeam(userId)) {
                            return;
                        }
                    }
                    currInviteList.add(TEAM_MANAGER_SINGLETON.getTeamByTeamId(teamId));
                    
                    // add to members map to show on team profile page that member is pending
                    addUserAsPending(userId, teamId);
                }
            }
        } else {
            // first time user is invited
            // add to user invited list
            // get team obj from teamName
            // put to map
            List<Team> myInvitedList = new ArrayList<Team>();
            Team inviteToThisTeam = TEAM_MANAGER_SINGLETON.getTeamByTeamId(teamId);
            if (inviteToThisTeam.isUserInTeam(userId) == false) {
                myInvitedList.add(inviteToThisTeam);
                invitedToParticipateMap2.put(userId, myInvitedList);
                
                // add to members map to show on team profile page that member is pending
                addUserAsPending(userId, teamId);
            }
        }
    }
    
 	// add to members map to show on team profile page that member is pending
    // but don't increase the members count
    public void addUserAsPending(int userId, int teamId) {
    	Team currTeam = teamMap.get(teamId);
        HashMap<Integer, String> membersMap = currTeam.getMembersMap();
        membersMap.put(userId, PENDING_ACCEPT_INVITATION_POSITION);
        teamMap.put(teamId, currTeam);
    }
    
    // user rejects the team owner invitation
    // need to remove from the members map
    // need to remove the info from team profile page
    public void removeUserPending(int userId, int teamId) {
    	Team currTeam = teamMap.get(teamId);
        HashMap<Integer, String> membersMap = currTeam.getMembersMap();
        membersMap.remove(userId);
        teamMap.put(teamId, currTeam);
    }

    public List<Team> getInvitedToParticipateMap2(int userId) {
        List<Team> rv = null;
        for (Map.Entry<Integer, List<Team>> entry : invitedToParticipateMap2.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                rv = entry.getValue();
                return rv;
            }
        }
        return rv;
    }
    
    public void removeParticipationRequest(int userId, int teamId) {
        for (Map.Entry<Integer, List<Team>> entry : invitedToParticipateMap2.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                List<Team> invitedToParticipateTeamList = entry.getValue();
                for (ListIterator<Team> iter = invitedToParticipateTeamList.listIterator(); iter.hasNext();) {
                    Team currTeam = iter.next();
                    if (currTeam.getId() == teamId) {
                        iter.remove();
                    }
                }
            }
        }
    }
    
    public void ignoreParticipationRequest2(int userId, int teamId) {
        for (Map.Entry<Integer, List<Team>> entry : invitedToParticipateMap2.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                List<Team> invitedToParticipateTeamList = entry.getValue();
                for (ListIterator<Team> iter = invitedToParticipateTeamList.listIterator(); iter.hasNext();) {
                    Team currTeam = iter.next();
                    if (currTeam.getId() == teamId) {
                    	removeUserPending(userId, teamId);
                        iter.remove();
                    }
                }
            }
        }
    }
    
    public void incrementExperimentCount(int teamId) {
    	Team teamToAddExpCount = teamMap.get(teamId);
    	teamToAddExpCount.setExperimentsCount(teamToAddExpCount.getExperimentsCount()+1);
    }
    
    public void decrementExperimentCount(int teamId) {
    	Team teamToAddExpCount = teamMap.get(teamId);
    	teamToAddExpCount.setExperimentsCount(teamToAddExpCount.getExperimentsCount()-1);
    }
    
    public void addNewTeam(Team newTeam) {
    	int teamId = generateRandomTeamId();
    	newTeam.setId(teamId);
    	teamMap.put(teamId, newTeam);
    }
    
    public int generateRandomTeamId() {
    	int teamId = RANDOM.nextInt(Integer.MAX_VALUE) + 1;
    	while (teamMap.containsKey(teamId)) {
    		teamId = RANDOM.nextInt();
    	}
    	return teamId;
    }
    
    public HashMap<Integer, Team> getTeamsPendingApproval() {
        HashMap<Integer, Team> rv = new HashMap<Integer, Team>();
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
        	int currTeamId = entry.getKey();
            Team currTeam = entry.getValue();
            if (currTeam.getIsApproved() == false) {
                rv.put(currTeamId, currTeam);
            }
        }
        return rv;
    }
    
    public void approveTeamApplication(int teamId) {
    	Team toBeApproved = teamMap.get(teamId);
    	toBeApproved.setIsApproved(true);
    	teamMap.put(teamId, toBeApproved);
    }
    
    public void rejectTeamApplication(int teamId) {
    	teamMap.remove(teamId);
    }
    
    //------------------------for admin overview------------------------
    public int getTotalTeamsCount() {
    	return teamMap.size();
    }
    
    public int getTotalMembersCount() {
    	int totalMembers = 0;
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            Team currTeam = entry.getValue();
            totalMembers = totalMembers + currTeam.getMembers();
        }
        return totalMembers;
    }
    
    public int getTotalMembersAwaitingApproval() {
    	int totalMembersAwaitingApproval = 0;
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            Team currTeam = entry.getValue();
            totalMembersAwaitingApproval = totalMembersAwaitingApproval + currTeam.getMembersAwaitingApproval();
        }
        return totalMembersAwaitingApproval;
    }
}
