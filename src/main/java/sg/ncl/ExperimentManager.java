package sg.ncl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import sg.ncl.testbed_interface.Experiment;

public class ExperimentManager {
    
    // experimentId, experiment
    private final int CURRENT_LOGGED_IN_USER;
    private static ExperimentManager EXPERIMENT_MANAGER_SINGLETON = null;
    private static HashMap<Integer, Experiment> experimentMap;
    private String EXPERIMENT_STATUS_READY;
    private String EXPERIMENT_STATUS_ALLOCATING;
    private String EXPERIMENT_STATUS_ERROR;
    private String EXPERIMENT_STATUS_STOP;
    
    private HashMap<Integer, List<Experiment>> experimentMap2; /* userId, arraylist of experiments */
    
    private ExperimentManager() {
        CURRENT_LOGGED_IN_USER = 200;
        experimentMap = new HashMap<Integer, Experiment>();
        EXPERIMENT_STATUS_READY = "READY";
        EXPERIMENT_STATUS_ALLOCATING = "ALLOCATING";
        EXPERIMENT_STATUS_ERROR = "ERROR";
        EXPERIMENT_STATUS_STOP = "STOPPED";
        
        Experiment exp01 = new Experiment();
        exp01.setExperimentId(50);
        exp01.setName("clientserver");
        exp01.setDescription("A DDOS scenario");
        exp01.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        exp01.setStatus(EXPERIMENT_STATUS_STOP);
        exp01.setTeamId(110);
        exp01.setNodesCount(7);
        exp01.setHoursIdle(2);
        exp01.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        
        Experiment exp02 = new Experiment();
        exp02.setExperimentId(51);
        exp02.setName("myexperiment");
        exp02.setDescription("A test experiment");
        exp02.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        exp02.setStatus(EXPERIMENT_STATUS_STOP);
        exp02.setTeamId(111);
        exp02.setNodesCount(7);
        exp02.setHoursIdle(2);
        exp02.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        
        Experiment exp03 = new Experiment();
        exp03.setExperimentId(52);
        exp03.setName("myexperiment02");
        exp03.setDescription("A test experiment");
        exp03.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        exp03.setStatus(EXPERIMENT_STATUS_READY);
        exp03.setTeamId(112);
        exp03.setNodesCount(7);
        exp03.setHoursIdle(2);
        exp03.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        
        experimentMap.put(exp01.getExperimentId(), exp01);
        experimentMap.put(exp02.getExperimentId(), exp02);
        experimentMap.put(exp03.getExperimentId(), exp03);
        
        experimentMap2 = new HashMap<Integer, List<Experiment>>();
        List<Experiment> experimentList = new ArrayList<Experiment>();
        experimentList.add(exp01);
        experimentList.add(exp02);
        experimentList.add(exp03);
        experimentMap2.put(CURRENT_LOGGED_IN_USER, experimentList);
    }
    
    public static ExperimentManager getInstance() {
        if (EXPERIMENT_MANAGER_SINGLETON == null) {
            EXPERIMENT_MANAGER_SINGLETON = new ExperimentManager();
        }
        return EXPERIMENT_MANAGER_SINGLETON;
    }
    
    public HashMap<Integer, Experiment> getExperimentMap() {
        return experimentMap;
    }
    
    public HashMap<Integer, Experiment> getExperimentMapByExperimentOwner(int userId) {
        HashMap<Integer, Experiment> expMap = new HashMap<Integer, Experiment>();
        for (Map.Entry<Integer, Experiment> entry : experimentMap.entrySet()) {
            int expId = entry.getKey();
            Experiment currExp = entry.getValue();
            if (currExp.getExperimentOwnerId() == userId) {
                expMap.put(expId, currExp);
            }
        }
        return expMap;
    }
    
    public List<Experiment> getExperimentListByExperimentOwner(int userId) {
        if (experimentMap2.containsKey(userId)) {
            return experimentMap2.get(userId);
        } else {
            return null;
        }
    }
    
    public HashMap<Integer, Experiment> getTeamExperimentsMap(int teamId) {
        /*
        for (Map.Entry<Integer, Experiment> entry : experimentMap.entrySet()) {
            int expId = entry.getKey();
            Experiment currExp = entry.getValue();
            if (currExp.getTeamId() == teamId) {
                teamExpMap.put(expId, currExp);
            }
        }
        */
        
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
        /*
        for (Map.Entry<Integer, Experiment> entry : experimentMap.entrySet()) {
            int currExpId = entry.getKey();
            Experiment currExp = entry.getValue();
            // TODO need to include a check if user is the team owner
            if (currExpId == expId && currExp.getExperimentOwnerId() == userId && currExp.getStatus().equals(EXPERIMENT_STATUS_STOP)) {
                experimentMap.remove(expId);
                return;
            }
        }
        */
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
    
    public void startExperiment(int userId, int expId) {
        /*
        // need to check if user have rights to start the experiment
        for (Map.Entry<Integer, Experiment> entry : experimentMap.entrySet()) {
            int currExpId = entry.getKey();
            Experiment currExp = entry.getValue();
            if (currExpId == expId && currExp.getStatus().equals(EXPERIMENT_STATUS_STOP)) {
                // suppose to be allocating
                // then when nodes are configured and allocated fully, set to active
                currExp.setStatus(EXPERIMENT_STATUS_READY);
                return;
            }
        }
        */
        
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
        /*
        for (Map.Entry<Integer, Experiment> entry : experimentMap.entrySet()) {
            int currExpId = entry.getKey();
            Experiment currExp = entry.getValue();
            if (currExpId == expId && currExp.getStatus().equals(EXPERIMENT_STATUS_READY)) {
                currExp.setStatus(EXPERIMENT_STATUS_STOP);
                return;
            }
        }
        */
        
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
    
    public int generateRandomExpId() {
    	Random rn = new Random();
    	int expId = rn.nextInt();
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
}
