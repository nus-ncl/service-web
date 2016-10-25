package sg.ncl;

import sg.ncl.testbed_interface.Dataset;

import java.util.*;
import java.util.stream.Collectors;

public class DatasetManager {
	
	private HashMap<Integer, Dataset> datasetMap; // datasetID, dataset - holds all the dataset information
	
	public DatasetManager() {
		datasetMap = new HashMap<Integer, Dataset>();
	}

	public void initSamples() {
	    String ACCESSIBILITY_OPEN = "open";
        String ACCESSIBILITY_RESTRICTED = "restricted";
        String VISIBILITY_PUBLIC = "public";
        String VISIBILITY_PRIVATE = "private";

		Dataset dataset1 = new Dataset();
		dataset1.setId(generateRandomId());
		dataset1.setName("DataAries");
        dataset1.setDescription("this is a simple desc");
		dataset1.setContributorId("206");
		dataset1.setVisibility(VISIBILITY_PUBLIC);
		dataset1.setAccessibility(ACCESSIBILITY_OPEN);

		Dataset dataset2 = new Dataset();
        dataset2.setId(generateRandomId());
		dataset2.setName("DataTaurus");
        dataset2.setDescription("this is a simple desc");
        dataset2.setContributorId("206");
		dataset2.setVisibility(VISIBILITY_PRIVATE);
		dataset2.setAccessibility(ACCESSIBILITY_OPEN);

		Dataset dataset3 = new Dataset();
        dataset3.setId(generateRandomId());
		dataset3.setName("DataGemini");
        dataset3.setDescription("this is a simple desc");
		dataset3.setContributorId("206");
		dataset3.setVisibility(VISIBILITY_PUBLIC);
		dataset3.setAccessibility(ACCESSIBILITY_RESTRICTED);

		Dataset dataset4 = new Dataset();
        dataset4.setId(generateRandomId());
		dataset4.setName("DataCancer");
        dataset4.setDescription("this is a simple desc");
		dataset4.setContributorId("200");
		dataset4.setVisibility(VISIBILITY_PUBLIC);
        dataset4.setAccessibility(ACCESSIBILITY_RESTRICTED);

		datasetMap.put(dataset1.getId(), dataset1);
		datasetMap.put(dataset2.getId(), dataset2);
		datasetMap.put(dataset3.getId(), dataset3);
		datasetMap.put(dataset4.getId(), dataset4);
    }
    
	private int generateRandomId() {
    	Random rn = new Random();
    	int datasetId = rn.nextInt(Integer.MAX_VALUE) + 1;
    	while (isIdExists(datasetId)) {
    		datasetId = rn.nextInt();
    	}
    	return datasetId;
    }
	
	private boolean isIdExists(int id) {
		return datasetMap.containsKey(id);
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

    public Map getDatasetMapOfContributor(String userId) {
        return datasetMap.entrySet().stream()
                .filter(map -> map.getValue().getContributorId().equals(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map getDatasetMapOfNotContributor(String userId) {
        return datasetMap.entrySet().stream()
                .filter(map -> !(map.getValue().getContributorId()).equals(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
	
}
