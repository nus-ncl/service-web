package sg.ncl.testbed_interface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by Desmond.
 */
public class Experiment2 {

    private Long id;
    private String userId;
    private String teamId;
    private String teamName;
    private String name;
    private String description;
    private String nsFile;
    private String nsFileContent;
    private Integer idleSwap;
    private Integer maxDuration;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastModifiedDate;
    private Integer platform;

    public Experiment2() {
        maxDuration = 0;
    }

    public Experiment2(final Long id,
                       final String userId,
                       final String teamId,
                       final String teamName,
                       final String name,
                       final String description,
                       final String nsFile,
                       final String nsFileContent,
                       final Integer idleSwap,
                       final Integer maxDuration,
                       final Integer platform) {

        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.name = name;
        this.description = description;
        this.nsFile = nsFile;
        this.nsFileContent = nsFileContent;
        this.idleSwap = idleSwap;
        this.maxDuration = maxDuration;
        this.platform = platform;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNsFile() {
        return nsFile;
    }

    public void setNsFile(String nsFile) {
        this.nsFile = nsFile;
    }

    public String getNsFileContent() {
        return nsFileContent;
    }

    public void setNsFileContent(String nsFileContent) {
        this.nsFileContent = nsFileContent;
    }

    public Integer getIdleSwap() {
        return idleSwap;
    }

    public void setIdleSwap(Integer idleSwap) {
        this.idleSwap = idleSwap;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate.withZoneSameInstant(ZoneId.of("GMT+08:00"));
    }

    public void setCreatedDate(String createdDate) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            this.createdDate = mapper.readValue(createdDate, ZonedDateTime.class);
        } catch (IOException e) {
            this.createdDate = null;
        }
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate.withZoneSameInstant(ZoneId.of("GMT+08:00"));
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            this.lastModifiedDate = mapper.readValue(lastModifiedDate, ZonedDateTime.class);
        } catch (IOException e) {
            this.lastModifiedDate = null;
        }
    }
}
