import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import net.deterlab.testbed.api.*;

public class simpleClient {
    
    private static String SPI_HOST_IP = "192.168.56.103";
    private static String SPI_HOST_PORT = "52323";

    public static void main(String[] args) {        
        try {
            ApiInfoStub myStub = new ApiInfoStub("https://" + SPI_HOST_IP + ":" + SPI_HOST_PORT +  "/axis2/services/ApiInfo");
            ApiInfoStub.GetVersion req = new ApiInfoStub.GetVersion();
            ApiInfoStub.GetVersionResponse resp = myStub.getVersion(req);

            ApiInfoStub.VersionDescription r = resp.get_return();
            System.out.println("Version: " + r.getVersion());
            System.out.println("PatchLevel: " + r.getPatchLevel());
            
            if (r.isKeyIDSpecified()) {
                System.out.println("Keyid: " + r.getKeyID());
            } else {
                System.out.println("No Keyid");
            }

        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
