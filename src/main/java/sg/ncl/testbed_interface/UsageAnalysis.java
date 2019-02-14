package sg.ncl.testbed_interface;

import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
public class UsageAnalysis implements Serializable {

    private double incurred;
    private double waived;

    public void addIncurred(double incurred) {
        this.incurred += incurred;
    }

    public void addWaived(double waived) {
        this.waived += waived;
    }
}
