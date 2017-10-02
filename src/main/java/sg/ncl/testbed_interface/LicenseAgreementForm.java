package sg.ncl.testbed_interface;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;

@Getter
@Setter
public class LicenseAgreementForm {

    @AssertTrue
    private boolean licenseAgreed;

}
