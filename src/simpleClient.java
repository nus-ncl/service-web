import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import net.deterlab.testbed.api.*;

public class simpleClient {

    public static void main(String[] args) {        
        try {
            ApiInfoStub myStub = new ApiInfoStub("https://192.168.56.103:52323/axis2/services/ApiInfo");
            ApiInfoStub.GetVersion req = new ApiInfoStub.GetVersion();
            ApiInfoStub.GetVersionResponse resp = myStub.getVersion(req);

            ApiInfoStub.VersionDescription r = resp.get_return();
            System.out.println("Version: " + r.getVersion());
            System.out.println("PatchLevel: " + r.getPatchLevel());

        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
