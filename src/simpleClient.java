import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.axis2.AxisFault;

import net.deterlab.testbed.api.*;
import deterlab.testbed.api.net.*;

public class simpleClient {
    
    private static String SPI_HOST_IP = "https://192.168.56.103";
    private static String SPI_HOST_PORT = "52323";
    private static String SPI_HOST_ADDR = SPI_HOST_IP + ":" + SPI_HOST_PORT;
    
    static public class AttributeComparator implements 
    Comparator<UsersStub.Attribute> {
        /**
         * Constructor.
         */
        public AttributeComparator() { }
        /**
         * Compares its two arguments for order.
         * @param a Attribute to compare 
         * @param b Attribute to compare 
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second. 
         */
        public int compare(UsersStub.Attribute a, UsersStub.Attribute b) {
        return a.getOrderingHint() - b.getOrderingHint();
        }
        /**
         * Indicates whether some other object is "equal to" this
         * comparator. 
         * @param o the object to test
         * @return true if o refers to this
         */
        public boolean equals(Object o) {
        if ( o == null ) return false;
        if ( !(o instanceof AttributeComparator ) ) return false;
        AttributeComparator a = (AttributeComparator) o;
        return (this == o);
        }
    }
    
    public static void main(String[] args) {        
        try {
            UsersStub myUsersStub = new UsersStub(SPI_HOST_ADDR + "/axis2/services/Users");
            UsersStub.CreateUserNoConfirm createReq = new UsersStub.CreateUserNoConfirm();
            
            // create profile
            UsersStub.GetProfileDescription descReq = new UsersStub.GetProfileDescription();
            UsersStub.GetProfileDescriptionResponse descResp = myUsersStub.getProfileDescription(descReq);
            UsersStub.Profile up = descResp.get_return();
            UsersStub.Attribute[] profile = up.getAttributes();
            Arrays.sort(profile, new AttributeComparator());
            
            String uid = "777";
            createReq.setUid(uid);
            createReq.setHashtype("clear");
            createReq.setClearpassword("password");
            createReq.setProfile(profile);
            // need to set profile param here
            UsersStub.CreateUserNoConfirmResponse createResp = myUsersStub.createUserNoConfirm(createReq);
            
            /**
            UsersStub.CreateUserAttribute createAttributeReq = new UsersStub.CreateUserAttribute();
            
            createAttributeReq.setName("demo777");
            createAttributeReq.setType("clear");
            createAttributeReq.setOptional(true);
            createAttributeReq.setAccess("");
            createAttributeReq.setDescription("a test of something");
            createAttributeReq.setFormat("");
            createAttributeReq.setFormatdescription("");
            createAttributeReq.setOrder(1);
            createAttributeReq.setLength(30);
            createAttributeReq.setDef("");
            myUsersStub.createUserAttribute(createAttributeReq);
            
            UsersStub.Profile myProfile = new UsersStub.Profile();
            UsersStub.Attribute[] profile = myProfile.getAttributes();
        
            String uid = "777";
            createReq.setUid(uid);
            createReq.setHashtype("clear");
            createReq.setClearpassword("password");
            createReq.setProfile(profile);
            UsersStub.CreateUserNoConfirmResponse createResp = myUsersStub.createUserNoConfirm(createReq);
            */

            
            ApiInfoStub myStub = new ApiInfoStub(SPI_HOST_ADDR + "/axis2/services/ApiInfo");
            ApiInfoStub.GetVersion req = new ApiInfoStub.GetVersion();
            ApiInfoStub.GetVersionResponse resp = myStub.getVersion(req);

            ApiInfoStub.VersionDescription r = resp.get_return();
            System.out.println("Version: " + r.getVersion());
            System.out.println("PatchLevel: " + r.getPatchLevel());
            
            // System.out.println("Created user " + result);
            
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
        } catch (UsersDeterFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
