package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by deepsi on 01/28/2022.
 */
@Getter
@Setter
public class OpenstackExperiment {
    private String teamId;
    private String teamName;
    private Long id;
    private String name;
    private String userId;
    private String description;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastModifiedDate;
    private String state;
    private String stackStatusReason;
    private String stackProjectId;
    private String heatFile;
    private int maxDuration;
    private int platform;
    private String stack_id;

    public OpenstackExperiment() {
        maxDuration = 0;
        platform = 1;
    }

    public void setCreatedDate(Long epoch) {
        this.createdDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.of("GMT+08:00"));
    }

    public void setLastModifiedDate(Long epoch) {
        this.lastModifiedDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.of("GMT+08:00"));
    }
}
