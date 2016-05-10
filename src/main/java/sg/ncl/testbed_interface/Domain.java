package sg.ncl.testbed_interface;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Domain {
	
    @NotNull(message="Domain cannot be empty")
    @Size(min=1, message="Domain cannot be empty")
	private String domainName;
	
	public Domain() {
	}
	
	public Domain(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

}
