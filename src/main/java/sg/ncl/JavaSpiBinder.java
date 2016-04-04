package sg.ncl;


import org.apache.axis2.AxisFault;

import net.deterlab.testbed.api.*;
import deterlab.testbed.api.net.*;

import net.deterlab.abac.ABACException;
import net.deterlab.abac.Identity;

public class JavaSpiBinder {
    
    private String SPI_HOST_ADDR;
    private Utility myUtility;
    
    public JavaSpiBinder(String hostIP, String hostPort) {
        myUtility = new Utility();
        this.SPI_HOST_ADDR = "https://" + hostIP + ":" + hostPort;
    }
    
    public String getVersion() throws Exception {
        ApiInfoStub myStub = new ApiInfoStub(SPI_HOST_ADDR + "/axis2/services/ApiInfo");
        ApiInfoStub.GetVersion req = new ApiInfoStub.GetVersion();
        ApiInfoStub.GetVersionResponse resp = myStub.getVersion(req);

        ApiInfoStub.VersionDescription r = resp.get_return();
        
        return r.getVersion();
    }
    
    public String getKeyID() throws Exception {
        ApiInfoStub myStub = new ApiInfoStub(SPI_HOST_ADDR + "/axis2/services/ApiInfo");
        ApiInfoStub.GetVersion req = new ApiInfoStub.GetVersion();
        ApiInfoStub.GetVersionResponse resp = myStub.getVersion(req);

        ApiInfoStub.VersionDescription r = resp.get_return();
        
        return r.getKeyID();
    }
    
    public String getPatchLevel() throws Exception {
        ApiInfoStub myStub = new ApiInfoStub(SPI_HOST_ADDR + "/axis2/services/ApiInfo");
        ApiInfoStub.GetVersion req = new ApiInfoStub.GetVersion();
        ApiInfoStub.GetVersionResponse resp = myStub.getVersion(req);

        ApiInfoStub.VersionDescription r = resp.get_return();
        
        return r.getPatchLevel();
    }
    
    public Identity login(String uid, String password) throws Exception {
        Identity i = null;
        UsersStub stub = new UsersStub(SPI_HOST_ADDR + "/axis2/services/Users");
        i = myUtility.login(stub, uid, password);
        return i;
    }
}
