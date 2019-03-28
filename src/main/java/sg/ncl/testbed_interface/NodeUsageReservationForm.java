package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class NodeUsageReservationForm implements Serializable {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    @Min(value = 1)
    private Integer noOfNodes = 1;

    private String teamId;

    @NotEmpty
    private String projectId;

    public ZonedDateTime getZonedStartDate() {
        return ZonedDateTime.of(LocalDate.parse(startDate, formatter), LocalTime.of(0, 0, 0, 0), ZoneId.of("Asia/Singapore"));
    }

    public ZonedDateTime getZonedEndDate() {
        return ZonedDateTime.of(LocalDate.parse(endDate, formatter), LocalTime.of(0, 0, 0, 0), ZoneId.of("Asia/Singapore"));
    }

    public String toString() {
        return "NodeUsageReservationForm{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", noOfNodes=" + noOfNodes +
                ", projectId=" + projectId + "}";
    }

}
