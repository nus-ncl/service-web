package sg.ncl.testbed_interface;

import lombok.Getter;

@Getter
public class DayUsage {

    private String date;
    private Long usage;

    public DayUsage(String date, Long usage) {
        this.date = date;
        this.usage = usage;
    }

    public Double getUsageInHours() {
        return usage.doubleValue() / 60;
    }
}
