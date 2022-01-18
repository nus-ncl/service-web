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
public class StatefulExperiment {
    private String teamId;
    private String teamName;
    private Long id;
    private String name;
    private String userId;
    private String description;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastModifiedDate;
    private String state;
    private int nodes;
    private int minNodes;
    private int maxDuration;
    private Long idleHours;
    private Map<String, Map<String, String>> nodesInfoMap;
    private int platform;

    public StatefulExperiment() {
        nodes = 0;
        maxDuration = 0;
        minNodes = 0;
        idleHours = 0L;
        nodesInfoMap = new HashMap<>();
        platform = 0;
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
