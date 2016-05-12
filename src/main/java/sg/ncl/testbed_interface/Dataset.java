package sg.ncl.testbed_interface;

import java.util.HashSet;
import java.util.Set;

public class Dataset {
	
	
	private String datasetName;
	private String releaseDate;
	private String datasetDescription;
	private String license;
	private int ownerId;
	private int datasetId;
	private String isRestricted = "open"; // dataset visibility: open and restricted
	private boolean requireAuthorization; // true: need to request access, false: can just download
	private Set<Integer> usersAccessSet = new HashSet<Integer>(); // stores a list of userids that have accessed to this dataset including the owner
	private boolean hasAcceptDataOwnerPolicy;
	
	public Dataset() {
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getIsRestricted() {
		return isRestricted;
	}

	public void setIsRestricted(String isRestricted) {
		this.isRestricted = isRestricted;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public String getDatasetDescription() {
		return datasetDescription;
	}

	public void setDatasetDescription(String datasetDescription) {
		this.datasetDescription = datasetDescription;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public int getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}

	public boolean getRequireAuthorization() {
		return requireAuthorization;
	}

	public void setRequireAuthorization(boolean requireAuthorization) {
		this.requireAuthorization = requireAuthorization;
	}
	
	public void addUsersAccess(int userId) {
		usersAccessSet.add(userId);
	}
	
	public boolean isUserHasAccess(int userId) {
		if (usersAccessSet.contains(userId)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getHasAcceptDataOwnerPolicy() {
		return hasAcceptDataOwnerPolicy;
	}

	public void setHasAcceptDataOwnerPolicy(boolean hasAcceptDataOwnerPolicy) {
		this.hasAcceptDataOwnerPolicy = hasAcceptDataOwnerPolicy;
	}
	
	public boolean updateName(String editedName) {
		if (!datasetName.equals(editedName)) {
			datasetName = editedName;
			return true;
		}
		return false;
	}
	
	public boolean updateDescription(String editedDesc) {
		if (!datasetDescription.equals(editedDesc)) {
			datasetDescription = editedDesc;
			return true;
		}
		return false;
	}
	
	public boolean updateLicense(String editedLicense) {
		if (!license.equals(editedLicense)) {
			license = editedLicense;
			return true;
		}
		return false;
	}
	
	public boolean updateRestricted(String editedRestricted) {
		if (!isRestricted.equals(editedRestricted)) {
			isRestricted = editedRestricted;
			return true;
		}
		return false;
	}
	
	public boolean updateAuthorization(boolean editedIsRequiredAuthorization) {
		if (requireAuthorization != editedIsRequiredAuthorization) {
			requireAuthorization = editedIsRequiredAuthorization;
			return true;
		}
		return false;
	}
}
