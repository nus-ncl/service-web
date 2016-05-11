package sg.ncl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import sg.ncl.testbed_interface.Dataset;
import sg.ncl.testbed_interface.Experiment;

public class DatasetManager {
	
	private static DatasetManager DATASET_MANAGER_SINGLETON = null;
	
	private HashMap<Integer, Dataset> datasetMap; // datasetID, dataset - holds all the dataset information
	private HashMap<Integer, List<Dataset>> usersAccessDatasetMap; /* userId, arraylist of datasets has accessed to */
	
	private DatasetManager() {
		
		datasetMap = new HashMap<Integer, Dataset>();

		Dataset dataset1 = new Dataset();
		dataset1.setDatasetId(generateRandomId());
		dataset1.setDatasetName("DataAries");
		dataset1.setReleaseDate("2016-Mar");
		dataset1.setOwnerId(206);
		dataset1.setIsRestricted(false);
		dataset1.setRequireAuthorization(false);
		
		Dataset dataset2 = new Dataset();
		dataset2.setDatasetId(generateRandomId());
		dataset2.setDatasetName("DataTaurus");
		dataset2.setReleaseDate("2016-Mar");
		dataset2.setOwnerId(206);
		dataset2.setIsRestricted(true);
		dataset2.setRequireAuthorization(false);
		
		Dataset dataset3 = new Dataset();
		dataset3.setDatasetId(generateRandomId());
		dataset3.setDatasetName("DataGemini");
		dataset3.setReleaseDate("2016-Mar");
		dataset3.setOwnerId(206);
		dataset3.setIsRestricted(false);
		dataset3.setRequireAuthorization(false);
		
		Dataset dataset4 = new Dataset();
		dataset4.setDatasetId(generateRandomId());
		dataset4.setDatasetName("DataCancer");
		dataset4.setReleaseDate("2016-Mar");
		dataset4.setOwnerId(200);
		dataset4.setIsRestricted(false);
		dataset4.setRequireAuthorization(true);
		
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
}
