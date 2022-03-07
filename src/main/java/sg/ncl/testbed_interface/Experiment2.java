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
    private String stack_id;
    private String heat_template_version;
    private String resources_name;
    private String type;
    private String flavour;
    private String image;
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
                       final String stack_id,
                       final String heat_template_version,
                       final String resources_name,
                       final String type,
                       final String flavour,
                       final String image,
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
        this.stack_id = stack_id;
        this.heat_template_version = heat_template_version;
        this.resources_name = resources_name;
        this.type = type;
        this.flavour = flavour;
        this.image = image;
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

    public String getStack_id() {
        return stack_id;
    }

    public void setStack_id(String stack_id) {
        this.stack_id = stack_id;
    }

    public String getHeat_template_version() {
        return heat_template_version;
    }

    public void setHeat_template_version(String heat_template_version) { this.heat_template_version = heat_template_version; }

    public String getResources_name() {
        return resources_name;
    }

    public void setResources_name(String resources_name) {
        this.resources_name = resources_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlavour() {
        return flavour;
    }

    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
