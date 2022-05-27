package sg.ncl;

import sg.ncl.testbed_interface.Dataset;

import java.util.*;

public class DatasetManager {
	
	private HashMap<Integer, Dataset> datasetMap; // datasetID, dataset - holds all the dataset information
	
	public DatasetManager() {
		datasetMap = new HashMap<Integer, Dataset>();
	}

	public boolean removeDataset(int id) {
		if (datasetMap.containsKey(id)) {
			datasetMap.remove(id);
			return true;
		} else {
			return false;
		}
	}
	
	public void addDataset(Dataset dataset) {
		datasetMap.put(dataset.getId(), dataset);
	}
	
	public Dataset getDataset(int id) {
		return datasetMap.get(id);
	}

	public Map getDatasetMap() {
	    return datasetMap;
    }
}
