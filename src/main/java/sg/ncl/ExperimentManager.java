package sg.ncl;

import java.util.HashMap;
import java.util.Map;

import sg.ncl.testbed_interface.Experiment;
import sg.ncl.testbed_interface.Team;

public class ExperimentManager {
    
    // experimentId, experiment
    private final int CURRENT_LOGGED_IN_USER;
    private static HashMap<Integer, Experiment> experimentMap;
    private String EXPERIMENT_STATUS_READY;
    private String EXPERIMENT_STATUS_ALLOCATING;
    private String EXPERIMENT_STATUS_ERROR;
    private String EXPERIMENT_STATUS_STOP;
    
    public ExperimentManager() {
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
    
    public HashMap<Integer, Experiment> getTeamExperimentsMap(int teamId) {
        HashMap<Integer, Experiment> teamExpMap = new HashMap<Integer, Experiment>();
        for (Map.Entry<Integer, Experiment> entry : experimentMap.entrySet()) {
            int expId = entry.getKey();
            Experiment currExp = entry.getValue();
            if (currExp.getTeamId() == teamId) {
                teamExpMap.put(expId, currExp);
            }
        }
        return teamExpMap;
    }
    
    public void removeExperiment(int userId, int expId) {
        for (Map.Entry<Integer, Experiment> entry : experimentMap.entrySet()) {
            int currExpId = entry.getKey();
            Experiment currExp = entry.getValue();
            // TODO need to include a check if user is the team owner
            if (currExpId == expId && currExp.getExperimentOwnerId() == userId) {
                experimentMap.remove(expId);
                return;
            }
        }
    }

}
