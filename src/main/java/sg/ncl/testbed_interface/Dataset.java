package sg.ncl.testbed_interface;

public class Dataset {
	
	
	private String datasetName;
	private String releaseDate;
	private String datasetDescription;
	private String license;
	private int ownerId;
	private int datasetId;
	private boolean isRestricted; // dataset visibility: open and restricted
	private boolean requireAuthorization; // true: need to request access, false: can just download
	
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

	public boolean getIsRestricted() {
		return isRestricted;
	}

	public void setIsRestricted(boolean isRestricted) {
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
}
