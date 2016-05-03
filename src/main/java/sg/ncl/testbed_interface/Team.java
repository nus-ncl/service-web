package sg.ncl.testbed_interface;

import java.util.HashMap;

/**
 * 
 * Team model
 * @author yeoteye
 * Note: set and get naming must be equivalent to variables, eg. name > setName > getName cannot be setNameX
 * 
 */
public class Team {
    
    private long id;
    private String name;
    private String description;
    private String website;
    private String organizationType;
    private String institution;
    private boolean isApproved;
    private boolean isPublic;
    private int members = 0;
    private int membersAwaitingApproval = 0;
    private int experimentsCount;
    private int teamOwnerId;
    private HashMap<Integer, String> membersMap = new HashMap<Integer, String>(); /* Members hash table containing uid and team position, e.g. 110 - member */
    private HashMap<Integer, User> joinRequestMap = new HashMap<Integer, User>(); /* Join request from users per team, userId - User */
    
    public Team() {
    }
    
    public Team(long id, String name, String description, String website, String organizationType, String institution, boolean isApproved, boolean isPublic, int members, int membersAwaitingApproval, int experimentsCount, int teamOwnerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.website = website;
        this.organizationType = organizationType;
        this.institution = institution;
        this.isApproved = isApproved;
        this.isPublic = isPublic;
        this.members = members;
        this.membersAwaitingApproval = membersAwaitingApproval;
        this.experimentsCount = experimentsCount;
        this.teamOwnerId = teamOwnerId;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getOrganizationType() {
        return organizationType;
    }
    
    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }
    
    public String getInstitution() {
        return institution;
    }
    
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public boolean getIsApproved() {
        return isApproved;
    }
    
    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }
    
    public boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public int getMembers() {
        return members;
    }
    
    public void setMembers(int members) {
        this.members = members;
    }
    
    public int getMembersAwaitingApproval() {
        return membersAwaitingApproval;
    }
    
    public void setMembersAwaitingApproval(int membersAwaitingApproval) {
        this.membersAwaitingApproval = membersAwaitingApproval;
    }
    
    public int getExperimentsCount() {
        return experimentsCount;
    }
    
    public void setExperimentsCount(int experimentsCount) {
        this.experimentsCount = experimentsCount;
    }
    
    public int getTeamOwnerId() {
        return teamOwnerId;
    }
    
    public void setTeamOwnerId(int teamOwnerId) {
        this.teamOwnerId = teamOwnerId;
    }
    
    /**
    @Override
    public String toString() {
        return "Team {" + 
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", website=" + website +
                ", organizationType=" + organizationType +
                ", isApproved=" + isApproved + "}";
    }*/

    public HashMap<Integer, String> getMembersMap() {
        return membersMap;
    }

    public void setMembersMap(HashMap<Integer, String> membersMap) {
        this.membersMap = membersMap;
    }
    
    public void addMembers(int userId, String position) {
        members++;
        if (membersAwaitingApproval > 0) {
            membersAwaitingApproval--;
        }
        membersMap.put(userId, position);
    }
    
    public void removeMembers(int userId) {
        members--;
        membersMap.remove(userId);
    }
    
    public boolean isUserTeamOwner(int userId) {
        if (userId == teamOwnerId) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isUserInTeam(int userId) {
        if (membersMap.containsKey(userId)) {
            return true;
        }
        return false;
    }

    public HashMap<Integer, User> getJoinRequestMap() {
        return joinRequestMap;
    }

    public void setJoinRequestMap(HashMap<Integer, User> joinRequestMap) {
        this.joinRequestMap = joinRequestMap;
    }
    
    public void addUserToJoinRequestMap(int userId, User requestIssuer) {
        if (joinRequestMap.containsKey(userId)) {
            // user has already issue a join request before
            return;
        } else {
            // user first time issue a join request
            joinRequestMap.put(userId, requestIssuer);
            membersAwaitingApproval++;
        }
    }
    
    public void removeUserFromJoinRequestMap(int userId) {
        if (joinRequestMap.containsKey(userId)) {
            joinRequestMap.remove(userId);
            membersAwaitingApproval--;
        }
    }
}
