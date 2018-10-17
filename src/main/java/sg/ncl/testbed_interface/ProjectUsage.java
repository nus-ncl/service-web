package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@Getter
@Setter
public class ProjectUsage implements Serializable {
    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("MMM-yyyy").parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();

    private Integer id;
    @NotEmpty
    @DateTimeFormat(pattern = "MMM-yyyy")
    private String month;
    private int usage;

    public boolean hasUsageWithinPeriod(LocalDate start, LocalDate end) {
        LocalDate current = LocalDate.parse(month, formatter);
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
