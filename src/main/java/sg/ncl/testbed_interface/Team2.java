package sg.ncl.testbed_interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Te Ye.
 */
public class Team2 {

    private String id;
    private String name;
    private String description;
    private String website;
    private String organisationType;
    private String status;
    private String createdDate;
    private String visibility;
    private int membersCount;
    private User2 owner;
    private List<User2> membersList;
    private List<User2> pendingMembersList;
    private HashMap<String, String> membersStatusMap; // userId, join status: e.g. PENDING, APPROVED

    public Team2() {
        pendingMembersList = new ArrayList<>();
        membersList = new ArrayList<>();
        membersStatusMap = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(String organisationType) {
        this.organisationType = organisationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public User2 getOwner() {
        return owner;
    }

    public void setOwner(User2 owner) {
        this.owner = owner;
    }

    public List<User2> getMembersList() {
        return membersList;
    }

    public void setMembersList(List<User2> membersList) {
        this.membersList = membersList;
    }

    public void addMembers(User2 user) {
        membersList.add(user);
    }

    public void addPendingMembers(User2 user) {
        pendingMembersList.add(user);
    }

    public boolean isUserTeamOwner(String email) {
        if (owner == null) {
            return false;
        } else if (owner.getEmail().equals(email)) {
            return true;
        }
        return false;
    }

    public HashMap<String, String> getMembersStatusMap() {
        return membersStatusMap;
    }

    public void setMembersStatusMap(HashMap<String, String> membersStatusMap) {
        this.membersStatusMap = membersStatusMap;
    }

    public void addMembersStatus(String userId, String memberStatus) {
        membersStatusMap.put(userId, memberStatus);
    }

    public List<User2> getPendingMembersList() {
        return pendingMembersList;
    }
}
