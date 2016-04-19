package sg.ncl;

import java.util.ArrayList;
import java.util.List;

import sg.ncl.testbed_interface.Team;

public class TeamManager {
    
    private static List<Team> teamList;
    private static List<Team> invitedToParticipateList;
    
    static {
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
        
        Team team3 = new Team();
        team3.setId(112);
        team3.setName("myPrivateProject");
        team3.setDescription("this is a simple desc");
        team3.setWebsite("http://www.nus.edu.sg");
        team3.setOrganizationType("academia");
        team3.setMembers(10);
        team3.setMembersAwaitingApproval(2);
        team3.setInstitution("NUS");
        team3.setIsApproved(true);
        team3.setIsPublic(false);
        team3.setExperimentsCount(99);
        
        teamList = new ArrayList<Team>();
        teamList.add(team1);
        teamList.add(team2);
        teamList.add(team3);
        
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
        
        invitedToParticipateList = new ArrayList<Team>();
        invitedToParticipateList.add(team4);
    }
    
    public List<Team> getTeamList() {
        return teamList;
    }
    
    public List<Team> getPublicTeamList() {
        List<Team> publicTeamList = new ArrayList<Team>();
        for (Team currTeam : teamList) {
            if (currTeam.getIsPublic() == true) {
                publicTeamList.add(currTeam);
            }
        }
        return publicTeamList;
    }
    
    public List<Team> getInvitedParticipateList() {
        return invitedToParticipateList;
    }
}
