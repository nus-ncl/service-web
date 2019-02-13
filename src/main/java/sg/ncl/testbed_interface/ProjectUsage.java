package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Getter
@Setter
public class ProjectUsage implements Serializable {
    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("MMM-yyyy").toFormatter();

    private Integer id = 0;
    @NotEmpty
    @DateTimeFormat(pattern = "MMM-yyyy")
    private String month = "";
    private int usage;
    private double incurred;
    private double waived;

    public boolean hasUsageWithinPeriod(YearMonth start, YearMonth end) {
        YearMonth current = YearMonth.parse(month, formatter);
        return !(current.isBefore(start) || current.isAfter(end)) && usage > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProjectUsage that = (ProjectUsage) o;
        return (id.equals(that.getId()) && month.equals(that.getMonth()));
    }

    @Override
    public int hashCode() {
        return (id.hashCode() + month.hashCode());
    }
}
