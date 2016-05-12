package sg.ncl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import sg.ncl.testbed_interface.Dataset;
import sg.ncl.testbed_interface.Experiment;

public class DatasetManager {
	
	private final String LICENSE_GPL = "gpl";
	private final String LICENSE_MIT = "mit";
	private final String LICENSE_BSD = "bsd";
	private final String VISIBLITY_OPEN = "open";
	private final String VISIBILITY_RESTRICTED = "restricted";
	private static DatasetManager DATASET_MANAGER_SINGLETON = null;
	private HashMap<Integer, Dataset> datasetMap; // datasetID, dataset - holds all the dataset information
	
	private DatasetManager() {
		
		datasetMap = new HashMap<Integer, Dataset>();

		Dataset dataset1 = new Dataset();
		dataset1.setDatasetId(generateRandomId());
		dataset1.setDatasetName("DataAries");
		dataset1.setReleaseDate("2016-Mar");
		dataset1.setOwnerId(206);
		dataset1.setLicense(LICENSE_GPL);
		dataset1.setIsRestricted(VISIBLITY_OPEN);
		dataset1.setRequireAuthorization(false);
		dataset1.addUsersAccess(dataset1.getOwnerId());
		dataset1.setDatasetDescription("this is a simple desc");
		
		Dataset dataset2 = new Dataset();
		dataset2.setDatasetId(generateRandomId());
		dataset2.setDatasetName("DataTaurus");
		dataset2.setReleaseDate("2016-Mar");
		dataset2.setOwnerId(206);
		dataset2.setLicense(LICENSE_MIT);
		dataset2.setIsRestricted(VISIBILITY_RESTRICTED);
		dataset2.setRequireAuthorization(false);
		dataset2.addUsersAccess(dataset2.getOwnerId());
		dataset2.setDatasetDescription("this is a simple desc");
		
		Dataset dataset3 = new Dataset();
		dataset3.setDatasetId(generateRandomId());
		dataset3.setDatasetName("DataGemini");
		dataset3.setReleaseDate("2016-Mar");
		dataset3.setOwnerId(206);
		dataset3.setLicense(LICENSE_BSD);
		dataset3.setIsRestricted(VISIBLITY_OPEN);
		dataset3.setRequireAuthorization(false);
		dataset3.addUsersAccess(dataset3.getOwnerId());
		dataset3.setDatasetDescription("this is a simple desc");
		
		Dataset dataset4 = new Dataset();
		dataset4.setDatasetId(generateRandomId());
		dataset4.setDatasetName("DataCancer");
		dataset4.setReleaseDate("2016-Mar");
		dataset4.setOwnerId(200);
		dataset4.setLicense(LICENSE_MIT);
		dataset4.setIsRestricted(VISIBLITY_OPEN);
		dataset4.setRequireAuthorization(true);
		dataset4.addUsersAccess(dataset4.getOwnerId());
		dataset4.setDatasetDescription("this is a simple desc");
		
		datasetMap.put(dataset1.getDatasetId(), dataset1);
		datasetMap.put(dataset2.getDatasetId(), dataset2);
		datasetMap.put(dataset3.getDatasetId(), dataset3);
		datasetMap.put(dataset4.getDatasetId(), dataset4);	
    }
    
    public static DatasetManager getInstance() {
        if (DATASET_MANAGER_SINGLETON == null) {
        	DATASET_MANAGER_SINGLETON = new DatasetManager();
        }
        return DATASET_MANAGER_SINGLETON;
    }
    
	public int generateRandomId() {
    	Random rn = new Random();
    	int datasetId = rn.nextInt();
    	while (isIdExists(datasetId)) {
    		datasetId = rn.nextInt();
    	}
    	return datasetId;
    }
	
	public boolean isIdExists(int datasetId) {
		if (datasetMap.containsKey(datasetId)) {
			return true;
		} else {
			return false;
		}
	}
	
	public HashMap<Integer, Dataset> getDatasetMap() {
		return datasetMap;
	}

	public void setDatasetMap(HashMap<Integer, Dataset> datasetMap) {
		this.datasetMap = datasetMap;
	}
	
	public List<Dataset> getDatasetContributedByUser(int userId) {
		List<Dataset> rv = new ArrayList<Dataset>();
        for (Map.Entry<Integer, Dataset> entry : datasetMap.entrySet()) {
            Dataset currDataset = entry.getValue();
            if (currDataset.getOwnerId() == userId) {
            	rv.add(currDataset);
            }
        }
        return rv;
	}
	
	public List<Dataset> getDatasetAccessibleByuser(int userId) {
		List<Dataset> rv = new ArrayList<Dataset>();
        for (Map.Entry<Integer, Dataset> entry : datasetMap.entrySet()) {
            Dataset currDataset = entry.getValue();
            if (currDataset.isUserHasAccess(userId)) {
            	rv.add(currDataset);
            }
        }
        return rv;
	}
	
	public boolean removeDataset(int datasetId) {
		if (datasetMap.containsKey(datasetId)) {
			datasetMap.remove(datasetId);
			return true;
		} else {
			return false;
		}
	}
	
	public void addDataset(int userId, Dataset dataset) {
		int datasetId = generateRandomId();
		dataset.setOwnerId(userId);
		dataset.addUsersAccess(userId);
		dataset.setReleaseDate(getDateOfContribution());
		datasetMap.put(datasetId, dataset);
	}
	
	public String getDateOfContribution() {
        // TODO by right should retrieve time and date from server and not client side
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MMM");
        ft.setTimeZone(TimeZone.getTimeZone("GMT+0800"));
        String dateOfApplicationStr = ft.format(dNow).toString();
        return dateOfApplicationStr;
	}
	
	public Dataset getDataset(int datasetId) {
		return datasetMap.get(datasetId);
	}
	
	public void updateDatasetDetails(Dataset origDataset) {
		// update dataset map
		int currDatasetId = origDataset.getDatasetId();
		datasetMap.put(currDatasetId, origDataset);
	}
}
