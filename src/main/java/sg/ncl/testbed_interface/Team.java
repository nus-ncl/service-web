package sg.ncl.testbed_interface;

/**
 * 
 * Team model
 * @author yeoteye
 *
 */
public class Team {
    
    private long id;
    private String name;
    private String description;
    private String website;
    private String organizationType;
    private boolean isApproved;
    
    public Team() {
        id = 0;
    }
    
    public Team(long id, String name, String description, String website, String organizationType, boolean isApproved) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.website = website;
        this.organizationType = organizationType;
        this.isApproved = isApproved;
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
    
    public boolean getIsApproved() {
        return isApproved;
    }
    
    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }
}
