package sg.ncl.testbed_interface;

import java.time.YearMonth;
import java.time.Month;

public class UtilizationUtil {
    private static YearMonth feb2017 = YearMonth.of(2017, Month.FEBRUARY);
    private static YearMonth nov2017 = YearMonth.of(2017, Month.NOVEMBER);
    private static YearMonth dec2017 = YearMonth.of(2017, Month.DECEMBER);
    private static YearMonth oct2019 = YearMonth.of(2019, Month.OCTOBER);

    public static int getNumberofNodes(YearMonth yearMonth) {
        int numNodes = 0;

        if (!(yearMonth.isBefore(feb2017) || yearMonth.isAfter(nov2017))) {
            numNodes = 96;
        } else if (!(yearMonth.isBefore(dec2017) || yearMonth.isAfter(oct2019))) {
            numNodes = 192;
        }

        return numNodes;
    }
}
