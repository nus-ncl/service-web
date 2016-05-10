package sg.ncl;

import java.util.Hashtable;

import sg.ncl.testbed_interface.Domain;

public class DomainManager {
	
    private static DomainManager DOMAIN_MANAGER_SINGLETON = null;
    private Hashtable<String, Domain> domainTable = new Hashtable<String, Domain>();

	private DomainManager() {
    	// seed some data
		Domain nus = new Domain("nus.edu.sg");
		Domain ntu = new Domain("ntu.edu.sg");
		Domain smu = new Domain("smu.edu.sg");
    	domainTable.put("nus", nus);
    	domainTable.put("ntu", ntu);
    	domainTable.put("smu", smu);
    }
    
    public static DomainManager getInstance() {
        if (DOMAIN_MANAGER_SINGLETON == null) {
            DOMAIN_MANAGER_SINGLETON = new DomainManager();
        }
        return DOMAIN_MANAGER_SINGLETON;
    }
    
    public Hashtable<String, Domain> getDomainTable() {
		return domainTable;
	}

	public void setDomainTable(Hashtable<String, Domain> domainTable) {
		this.domainTable = domainTable;
	}
    
    public boolean addDomains(String domain) {
    	String[] parts = domain.split("\\.");
    	String institutionShortForm = parts[0].trim();
    	if (domainTable.containsKey(institutionShortForm) || domain.isEmpty()) {
    		// domain already exists
    		// domain is empty
    		return false;
    	} else {
    		Domain toBeAddedDomain = new Domain(domain);
    		domainTable.put(institutionShortForm, toBeAddedDomain);
    		return true;
    	}
    }
    
    public boolean removeDomains(String domainKey) {
    	if (domainTable.containsKey(domainKey)) {
    		domainTable.remove(domainKey);
    		return true;
    	} else {
    		// no such domain to be removed
    		return false;
    	}
    }
}
