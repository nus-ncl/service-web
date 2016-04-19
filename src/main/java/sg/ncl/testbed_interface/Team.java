package sg.ncl.testbed_interface;

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
    private int members;
    private int membersAwaitingApproval;
    private int experimentsCount;
    
    
    public Team() {
    }
    
    public Team(long id, String name, String description, String website, String organizationType, String institution, boolean isApproved, boolean isPublic, int members, int membersAwaitingApproval, int experimentsCount) {
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
    
    @Override
    public String toString() {
        return "Team {" + 
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", website=" + website +
                ", organizationType=" + organizationType +
                ", isApproved=" + isApproved + "}";
    }
}
