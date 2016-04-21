package sg.ncl;

import java.util.HashMap;

import sg.ncl.testbed_interface.Experiment;

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
        
        Experiment exp01 = new Experiment();
        exp01.setExperimentId(50);
        exp01.setName("clientserver");
        exp01.setDescription("A DDOS scenario");
        exp01.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        exp01.setStatus(EXPERIMENT_STATUS_STOP);
        exp01.setTeamId(110);
        
        Experiment exp02 = new Experiment();
        exp02.setExperimentId(51);
        exp02.setName("myexperiment");
        exp02.setDescription("A test experiment");
        exp02.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        exp02.setStatus(EXPERIMENT_STATUS_STOP);
        exp02.setTeamId(111);
        
        Experiment exp03 = new Experiment();
        exp03.setExperimentId(52);
        exp03.setName("myexperiment02");
        exp03.setDescription("A test experiment");
        exp03.setExperimentOwnerId(CURRENT_LOGGED_IN_USER);
        exp03.setStatus(EXPERIMENT_STATUS_STOP);
        exp03.setTeamId(112);
        
        experimentMap.put(exp01.getExperimentId(), exp01);
        experimentMap.put(exp02.getExperimentId(), exp02);
        experimentMap.put(exp03.getExperimentId(), exp03);
    }
    
    public HashMap<Integer, Experiment> getExperimentMap() {
        return experimentMap;
    }

}
