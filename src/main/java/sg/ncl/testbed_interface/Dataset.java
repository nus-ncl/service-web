package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class Dataset implements Serializable {

	private Integer id;
	private String name;
	private String description;
	private String contributorId;
	private String visibility;
	private String accessibility;
	private String releaseDate;
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

	public void addResource(DataResource dataResource) {
	    dataResources.add(dataResource);
    }

    public String getResourceIdsInArrayString() {
        List<String> idList = new ArrayList<>();
        dataResources.forEach(temp -> idList.add("\"" + temp.getId() + "\""));
        String ids = idList.toString();
        log.debug(ids);
        return ids;
    }

    public String getResourceUrisInArrayString() {
        List<String> uriList = new ArrayList<>();
        dataResources.forEach(temp -> uriList.add("\"" + temp.getUri() + "\""));
        String uris = uriList.toString();
        log.debug(uris);
        return uris;
    }

}
