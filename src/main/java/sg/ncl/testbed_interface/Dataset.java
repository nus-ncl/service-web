package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Dataset implements Serializable {

	private Integer id;
	private String name;
	private String description;
	private String contributorId;
	private String visibility;
	private String accessibility;
	private List<DataResource> dataResources;
	private List<String> approvedUsers;

	private User2 contributor;
	
	public Dataset() {
	    dataResources = new ArrayList<>();
	    approvedUsers = new ArrayList<>();
    }

	public boolean isOpen() {
	    return accessibility.equalsIgnoreCase("open");
    }

	public boolean isPublic() {
	    return visibility.equalsIgnoreCase("public");
    }

    public boolean isAccessible() {
        return isOpen();
    }

    public boolean isAccessible(String userId) {
        return isOpen() || contributorId.equals(userId) || isApprovedUser(userId);
    }

    public boolean isApprovedUser(String userId) {
        return approvedUsers.contains(userId);
    }

	public void addApprovedUser(String userId) {
		approvedUsers.add(userId);
	}

}
