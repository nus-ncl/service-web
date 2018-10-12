package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ProjectUsage implements Serializable {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-yyyy");

    private Integer id;
    @NotEmpty
    private String month;
    private int usage;
}
