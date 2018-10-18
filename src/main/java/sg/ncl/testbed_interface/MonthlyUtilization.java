package sg.ncl.testbed_interface;

import lombok.Getter;

import java.io.Serializable;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Getter
public class MonthlyUtilization implements Serializable {
    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("MMM-yyyy").toFormatter();

    private String month;
    private double nodeHours = 0;

    public MonthlyUtilization(String month) {
        this.month = month;
    }

    public double getUtilizationInPercentage() {
        YearMonth monthYear = YearMonth.parse(month, formatter);
        int maxHours = monthYear.lengthOfMonth() * 24 * UtilizationUtil.getNumberofNodes(monthYear);
        return nodeHours / maxHours * 100;
    }

    public void addNodeHours(double nodeHours) {
        this.nodeHours += nodeHours;
    }
}
