package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dcszwang on 11/14/2017.
 */
@Getter
@Setter
public class RealizedExperiment {
    private String teamId;
    private String teamName;
    private Long expId;
    private String expName;
    private String userId;
    private String description;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastModifiedDate;
    private String state;
    private int nodes;
    private int minNodes;
    private Long idleHours;
    private Map<String, Map<String, String>> nodesInfoMap;

    public RealizedExperiment() {
        nodes = 0;
        minNodes = 0;
        idleHours = 0L;
        nodesInfoMap = new HashMap<>();
    }

    public void addNodeInfo(String nodeName, Map<String, String> nodeInfo) {
        nodesInfoMap.put(nodeName, nodeInfo);
    }

    public void setCreatedDate(Long epoch) {
        this.createdDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.of("GMT+08:00"));
    }

    public void setLastModifiedDate(Long epoch) {
        this.lastModifiedDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.of("GMT+08:00"));
    }
}
