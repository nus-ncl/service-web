package sg.ncl.testbed_interface;

import lombok.Getter;
import sg.ncl.domain.MemberStatus;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Te Ye
 */
@Getter
public class Team2 implements Serializable {

    private String id;
    private String name;

    @Size(min=1, message="Description cannot be empty")
    private String description;

    @Size(min=1, message="Website cannot be empty")
    private String website;

    private String organisationType;
    private String status;
    private String createdDate;
    private String visibility;
    private int membersCount;
    private User2 owner;
    private List<User2> membersList;
    private List<User2> pendingMembersList;
    private EnumMap<MemberStatus, List<User2>> membersStatusMap; // membership status, list of members with the specific status, e.g. APPROVED -> [UserA, UserB ...]

    public Team2() {
        pendingMembersList = new ArrayList<>();
        membersList = new ArrayList<>();
        membersStatusMap = new EnumMap<>(MemberStatus.class);
        membersStatusMap.put(MemberStatus.APPROVED, new ArrayList<>());
        membersStatusMap.put(MemberStatus.PENDING, new ArrayList<>());
        membersStatusMap.put(MemberStatus.REJECTED, new ArrayList<>());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setOrganisationType(String organisationType) {
        this.organisationType = organisationType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setOwner(User2 owner) {
        this.owner = owner;
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

    public void setMembersStatusMap(EnumMap<MemberStatus, List<User2>> membersStatusMap) {
        this.membersStatusMap = membersStatusMap;
    }

    public void addMembersToStatusMap(MemberStatus memberStatus, User2 user) {
        List<User2> currentList = membersStatusMap.get(memberStatus);
        currentList.add(user);
        membersStatusMap.put(memberStatus, currentList);
    }

    void setPendingMembersList(List<User2> pendingMembersList) {
        this.pendingMembersList = pendingMembersList;
    }
}
