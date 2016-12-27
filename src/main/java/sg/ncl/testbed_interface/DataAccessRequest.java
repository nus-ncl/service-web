package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by dcsjnh on 12/27/2016.
 */
@Getter
@Setter
public class DataAccessRequest implements Serializable {

    private Long id;
    private Long dataId;
    private String requesterId;
    private String reason;
    private ZonedDateTime requestDate;
    private ZonedDateTime approvedDate;

    private User2 requester;
    private Dataset dataset;

    public String getRequestDateString() {
        if (requestDate == null) {
            return "";
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM-d-yyyy");
        return requestDate.format(format);
    }

    public String getApprovedDateString() {
        if (approvedDate == null) {
            return "";
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM-d-yyyy");
        return approvedDate.format(format);
    }

}
