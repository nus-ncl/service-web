package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotEmpty;
import sg.ncl.domain.DataAccessibility;
import sg.ncl.domain.DataVisibility;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
@Setter
@Slf4j
public class Dataset implements Serializable {

	private Integer id;
    @NotEmpty
	private String name;
    @NotEmpty
	private String description;
	private String contributorId;
	@Min(value = 1)
	private Integer categoryId;
    @Min(value = 1)
    private Integer licenseId;
	private DataVisibility visibility;
	private DataAccessibility accessibility;
    private ZonedDateTime releasedDate;
	private List<DataResource> dataResources;
	private List<String> approvedUsers;
	private String keywords; // to be converted to list upon access

	private User2 contributor;
	private DataCategory category;
	private DataLicense license;
	private HashMap<String, String> displayCodeMap;
	
	public Dataset() {
        visibility = DataVisibility.PUBLIC;
        accessibility = DataAccessibility.OPEN;
        releasedDate = ZonedDateTime.now();
	    dataResources = new ArrayList<>();
	    approvedUsers = new ArrayList<>();
	    displayCodeMap = new HashMap<>();

        displayCodeMap.put("data-resource-gray", "This resource has not been scanned by our anti-virus engine yet.");
        displayCodeMap.put("data-resource-green", "This resource is clean according to our best effort.");
        displayCodeMap.put("data-resource-red", "This resource is malicious. Please use with caution.");
    }

    public void setAccessibility(DataAccessibility accessibility) {
        if (accessibility == null) {
            this.accessibility = DataAccessibility.OPEN;
        } else {
            this.accessibility = accessibility;
        }
    }

	public boolean isOpen() {
	    return accessibility == DataAccessibility.OPEN;
    }

    public boolean isQuarantined() {
	    return accessibility == DataAccessibility.QUARANTINED;
    }

	public boolean isPublic() {
	    return visibility == DataVisibility.PUBLIC;
    }

    public boolean isAccessible() {
        return isOpen();
    }

    public boolean isAccessible(String userId) {
        return isOpen() || isContributor(userId) || isApprovedUser(userId);
    }

    public boolean isDownloadable(String userId) {
	    return accessibility != DataAccessibility.QUARANTINED && isAccessible(userId);
    }

    public boolean isContributor(String userId) {
	    return contributorId.equals(userId);
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

    public void addAccessibility(String accessibility) {
        if (accessibility.equals(DataAccessibility.OPEN.toString())) {
            setAccessibility(DataAccessibility.OPEN);
        } else if (accessibility.equals(DataAccessibility.RESTRICTED.toString())) {
            setAccessibility(DataAccessibility.RESTRICTED);
        } else if (accessibility.equals(DataAccessibility.QUARANTINED.toString())) {
            setAccessibility(DataAccessibility.QUARANTINED);
        }
    }

    public void addVisibility(String visibility) {
        if (visibility.equals(DataVisibility.PRIVATE.toString())) {
            setVisibility(DataVisibility.PRIVATE);
        } else if (visibility.equals(DataVisibility.PROTECTED.toString())) {
            setVisibility(DataVisibility.PROTECTED);
        } else if (visibility.equals(DataVisibility.PUBLIC.toString())) {
            setVisibility(DataVisibility.PUBLIC);
        }
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

    /**
     * Sets the color coding for the data resources in Thymeleaf
     * Is_malicious + Is_scanned = Red
     * !Is_malicious + !Is_scanned = Gray
     * !Is_malicious + Is_scanned = Green
     * @return a list of css color classes
     */
    public List<String> getResourceMaliciousColorCodeInList() {
        List<String> displayCode = new ArrayList<>();
        for (DataResource current : dataResources) {
            if (current.isMalicious() && current.isScanned()) {
                displayCode.add("data-resource-red");
            } else if (!current.isMalicious() && current.isScanned()) {
                displayCode.add("data-resource-green");
            } else {
                displayCode.add("data-resource-gray");
            }
        }
        return displayCode;
    }

    public String getDisplayColor(String color) {
        return displayCodeMap.get(color);
    }

    /**
     * Sets the color coding for the data resources in Javascript
     * need double quotes to be display in Javascript
     * @return a string that indicates the css class names
     */
    public String getResourceMaliciousColorCodeInArrayString() {
        List<String> displayCodeWithQuotes = new ArrayList<>();
        for (String displayCode : getResourceMaliciousColorCodeInList()) {
            displayCodeWithQuotes.add("\"" + displayCode + "\"");
        }
        String displayCodeStr = displayCodeWithQuotes.toString();
        log.debug(displayCodeStr);
        return displayCodeStr;
    }

    public String getReleasedDateString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM-d-yyyy");
        return releasedDate.format(format);
    }

    public boolean isContainMaliciousResources() {
	    return dataResources.stream().anyMatch(DataResource::isMalicious);
    }

    public List<String> getKeywordList() {
	    // http://stackoverflow.com/questions/33691430/bind-comma-separated-string-to-list
        return Arrays.asList(keywords.split("\\s*,\\s*"));
    }

    public void setKeywordList(List<String> keywordList) {
	    // http://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
        keywords = String.join(", ", keywordList);
    }

}
