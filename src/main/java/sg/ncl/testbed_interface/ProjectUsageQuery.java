package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import sg.ncl.validation.StartDateNotAfterEndDate;

import java.io.Serializable;

@Getter
@Setter
@StartDateNotAfterEndDate(pattern = "MMM-yyyy")
public class ProjectUsageQuery implements Serializable {

    @NotEmpty
    @DateTimeFormat(pattern = "MMM-yyyy")
    private String start;
    @NotEmpty
    @DateTimeFormat(pattern = "MMM-yyyy")
    private String end;
}
