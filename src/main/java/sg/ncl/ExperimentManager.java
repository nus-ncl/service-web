package sg.ncl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import sg.ncl.testbed_interface.Experiment;

public class ExperimentManager {
    
    // experimentId, experiment
    private final int JOHN_DOE;
    private static ExperimentManager EXPERIMENT_MANAGER_SINGLETON = null;
    
    private String EXPERIMENT_STATUS_READY;
    private String EXPERIMENT_STATUS_ALLOCATING;
    private String EXPERIMENT_STATUS_ERROR;
    private String EXPERIMENT_STATUS_STOP;
    
    private String SCENARIOS_DIR_PATH = "src/main/resources/scenarios/";
    private HashMap<Integer, List<Experiment>> experimentMap2; /* userId, arraylist of experiments */
    
    private ExperimentManager() {
    	JOHN_DOE = 200;
        EXPERIMENT_STATUS_READY = "READY";
        EXPERIMENT_STATUS_ALLOCATING = "ALLOCATING";
        EXPERIMENT_STATUS_ERROR = "ERROR";
        EXPERIMENT_STATUS_STOP = "STOPPED";
        
        Experiment exp01 = new Experiment();
        exp01.setExperimentId(50);
        exp01.setName("clientserver");
        exp01.setDescription("A DDOS scenario");
        exp01.setExperimentOwnerId(JOHN_DOE);
        exp01.setStatus(EXPERIMENT_STATUS_STOP);
        exp01.setTeamId(110);
        exp01.setNodesCount(7);
        exp01.setHoursIdle(2);
        exp01.setExperimentOwnerId(JOHN_DOE);
        exp01.setScenarioFileName("basic.ns");
    	String exp01Config = getScenarioContents(exp01.getScenarioFileName());
    	exp01.setScenarioContents(exp01Config);
        
        Experiment exp02 = new Experiment();
        exp02.setExperimentId(51);
        exp02.setName("myexperiment");
        exp02.setDescription("A test experiment");
        exp02.setExperimentOwnerId(JOHN_DOE);
        exp02.setStatus(EXPERIMENT_STATUS_STOP);
        exp02.setTeamId(111);
        exp02.setNodesCount(7);
        exp02.setHoursIdle(2);
        exp02.setExperimentOwnerId(JOHN_DOE);
        exp02.setScenarioFileName("basic.ns");
    	String exp02Config = getScenarioContents(exp01.getScenarioFileName());
    	exp02.setScenarioContents(exp02Config);
        
        Experiment exp03 = new Experiment();
        exp03.setExperimentId(52);
        exp03.setName("myexperiment02");
        exp03.setDescription("A test experiment");
        exp03.setExperimentOwnerId(JOHN_DOE);
        exp03.setStatus(EXPERIMENT_STATUS_READY);
        exp03.setTeamId(112);
        exp03.setNodesCount(7);
        exp03.setHoursIdle(2);
        exp03.setExperimentOwnerId(JOHN_DOE);
        exp03.setScenarioFileName("basic.ns");
    	String exp03Config = getScenarioContents(exp01.getScenarioFileName());
    	exp03.setScenarioContents(exp03Config);
        
        experimentMap2 = new HashMap<Integer, List<Experiment>>();
        List<Experiment> experimentList = new ArrayList<Experiment>();
        experimentList.add(exp01);
        experimentList.add(exp02);
        experimentList.add(exp03);
        experimentMap2.put(JOHN_DOE, experimentList);
    }
    
    public static ExperimentManager getInstance() {
        if (EXPERIMENT_MANAGER_SINGLETON == null) {
            EXPERIMENT_MANAGER_SINGLETON = new ExperimentManager();
        }
        return EXPERIMENT_MANAGER_SINGLETON;
    }
    
    public HashMap<Integer, List<Experiment>> getExperimentMap2() {
        return experimentMap2;
    }
    
    public List<Experiment> getExperimentListByExperimentOwner(int userId) {
        if (experimentMap2.containsKey(userId)) {
            return experimentMap2.get(userId);
        } else {
            return null;
        }
    }
    
    public HashMap<Integer, Experiment> getTeamExperimentsMap(int teamId) {
        // for list implementation of experiment manager
        // expid, exp
        HashMap<Integer, Experiment> teamExpMap = new HashMap<Integer, Experiment>();
        
        for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
            List<Experiment> currExpList = entry.getValue();
            for (ListIterator<Experiment> iter = currExpList.listIterator(); iter.hasNext();) {
                Experiment currExp = iter.next();
                if (currExp.getTeamId() == teamId) {
                    teamExpMap.put(currExp.getExperimentId(), currExp);
                }
            }
        }
        return teamExpMap;
    }
    
    public void removeExperiment(int userId, int expId) {
        // for list version
        for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                List<Experiment> currExpList = entry.getValue();
                for (ListIterator<Experiment> iter = currExpList.listIterator(); iter.hasNext();) {
                    Experiment currExp = iter.next();
                    if (currExp.getExperimentId() == expId) {
                        iter.remove();
                    }
                }
            }
        }
    }
    
    // for ncl admin to force remove an experiment
    public void adminRemoveExperiment(int expId) {
        // for list version
        for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
	        List<Experiment> currExpList = entry.getValue();
	        for (ListIterator<Experiment> iter = currExpList.listIterator(); iter.hasNext();) {
	            Experiment currExp = iter.next();
	            if (currExp.getExperimentId() == expId) {
	                iter.remove();
	            }
	        }
        }
    }
    
    public void startExperiment(int userId, int expId) {
        // for list version
        // need to check if user have rights to start the experiment
        for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                List<Experiment> currExpList = entry.getValue();
                for (ListIterator<Experiment> iter = currExpList.listIterator(); iter.hasNext();) {
                    Experiment currExp = iter.next();
                    if (currExp.getExperimentId() == expId && currExp.getStatus().equals(EXPERIMENT_STATUS_STOP)) {
                        currExp.setStatus(EXPERIMENT_STATUS_READY);
                    }
                }
            }
        }
    }
    
    public void stopExperiment(int userId, int expId) {
        // for list version
        for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                List<Experiment> currExpList = entry.getValue();
                for (ListIterator<Experiment> iter = currExpList.listIterator(); iter.hasNext();) {
                    Experiment currExp = iter.next();
                    if (currExp.getExperimentId() == expId && currExp.getStatus().equals(EXPERIMENT_STATUS_READY)) {
                        currExp.setStatus(EXPERIMENT_STATUS_STOP);
                    }
                }
            }
        }
    }
    
    public void addExperiment(int userId, Experiment toBeAddedExp) {
    	// experiment must be set to stop first
    	
    	// TODO randomly set a exp id for now
    	int expId = generateRandomExpId();
    	toBeAddedExp.setExperimentId(expId);
    	toBeAddedExp.setExperimentOwnerId(userId);
    	toBeAddedExp.setStatus(EXPERIMENT_STATUS_STOP);
    	
    	// set the scenario contents
    	String scenarioContents = getScenarioContents(toBeAddedExp.getScenarioFileName());
    	toBeAddedExp.setScenarioContents(scenarioContents);
    	
    	if (experimentMap2.containsKey(userId)) {
    		// user has an existing record
    		// update user's record
            for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
                int currUserId = entry.getKey();
                if (currUserId == userId) {
                    List<Experiment> currExpList = entry.getValue();
                    currExpList.add(toBeAddedExp);
                    experimentMap2.put(userId, currExpList);
                }
            }
    	} else {
    		// user has not created experiment before
            List<Experiment> experimentList = new ArrayList<Experiment>();
            experimentList.add(toBeAddedExp);
            experimentMap2.put(userId, experimentList);
    	}
    }
    
    private String getScenarioContents(String scenarioFileName) {
    	StringBuilder sb = new StringBuilder();
    	try(BufferedReader br = new BufferedReader(new FileReader(SCENARIOS_DIR_PATH + scenarioFileName))) {
    		String line = br.readLine();
    		
    		while (line != null) {
    			sb.append(line);
    			sb.append(System.lineSeparator());
    			line = br.readLine();
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return sb.toString();
	}

	public int generateRandomExpId() {
    	Random rn = new Random();
    	int expId = rn.nextInt(Integer.MAX_VALUE) + 1;
    	while (isExpIdExists(expId)) {
    		expId = rn.nextInt();
    	}
    	return expId;
    }
    
    public boolean isExpIdExists(int expId) {
        for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
            List<Experiment> currExpList = entry.getValue();
            for (ListIterator<Experiment> iter = currExpList.listIterator(); iter.hasNext();) {
                Experiment currExp = iter.next();
                if (currExp.getExperimentId() == expId) {
                	return true;
                }
            }
        }
        return false;
    }
    
    public Experiment getExperimentByExpId(int expId) {
        for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
            List<Experiment> currExpList = entry.getValue();
            for (ListIterator<Experiment> iter = currExpList.listIterator(); iter.hasNext();) {
                Experiment currExp = iter.next();
                if (currExp.getExperimentId() == expId) {
                	return currExp;
                }
            }
        }
        return null;
    }
    
    // for admin overview
    public int getTotalExpCount() {
    	int totalExpCount = 0;
	    for (Map.Entry<Integer, List<Experiment>> entry : experimentMap2.entrySet()) {
	        List<Experiment> currExpList = entry.getValue();
	        totalExpCount = totalExpCount + currExpList.size();
	    }
	    return totalExpCount;
    }
}
