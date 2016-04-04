
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.AxisFault;

import net.deterlab.testbed.api.*;
import deterlab.testbed.api.net.*;

import javax.xml.soap.*;

public class simpleClient {
    
    private static String SPI_HOST_IP = "https://192.168.56.103";
    private static String SPI_HOST_PORT = "52323";
    private static String SPI_HOST_ADDR = SPI_HOST_IP + ":" + SPI_HOST_PORT;
    
    static public class AttributeComparator implements 
    Comparator<UsersStub.Attribute> {
        /**
         * Constructor
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
    
    public static void mainDemo(String[] args) {
        try {    
            /**
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
            */
            
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

            System.out.println("Test Connection to local SPI...");
            
            ApiInfoStub myStub = new ApiInfoStub(SPI_HOST_ADDR + "/axis2/services/ApiInfo");
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
            
            System.out.println("Success");
            
            
            System.out.println("Testing Manual SOAP XML Message");
            
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = "https://192.168.56.103:52323/axis2/services/Users";
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);

            // print SOAP Response
            System.out.print("Response SOAP Message:");
            soapResponse.writeTo(System.out);

            soapConnection.close();

        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SOAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        String serverURI = "http://api.testbed.deterlab.net/xsd";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("xsd", serverURI);

        /*
        Constructed SOAP Request Message:
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:example="http://ws.cdyne.com/">
            <SOAP-ENV:Header/>
            <SOAP-ENV:Body>
                <example:VerifyEmail>
                    <example:email>mutantninja@gmail.com</example:email>
                    <example:LicenseKey>123</example:LicenseKey>
                </example:VerifyEmail>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
         */
        
        /*
            <x:Body>
                <xsd:createUserNoConfirm>
                    <xsd:uid>?</xsd:uid>
                    <xsd:profile>
                        <xsd:access>?</xsd:access>
                        <xsd:dataType>?</xsd:dataType>
                        <xsd:description>?</xsd:description>
                        <xsd:format>?</xsd:format>
                        <xsd:formatDescription>?</xsd:formatDescription>
                        <xsd:lengthHint>0</xsd:lengthHint>
                        <xsd:name>?</xsd:name>
                        <xsd:optional>false</xsd:optional>
                        <xsd:orderingHint>0</xsd:orderingHint>
                        <xsd:value>?</xsd:value>
                    </xsd:profile>
                    <xsd:clearpassword>?</xsd:clearpassword>
                    <xsd:hash>?</xsd:hash>
                    <xsd:hashtype>?</xsd:hashtype>
                </xsd:createUserNoConfirm>
            </x:Body>
         */

        // SOAP Body
        /*
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("getVersion", "xsd");
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI  + "getVersion");
        */
        
        // SOAP Body for CreateUsersNoConfirm
        /*
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("createUserNoConfirm", "xsd");
        SOAPElement soapBodyElemUid = soapBodyElem.addChildElement("uid", "xsd");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("profile", "xsd");
        SOAPElement soapBodyElemAccess = soapBodyElem1.addChildElement("access", "xsd");
        SOAPElement soapBodyElemDataType = soapBodyElem1.addChildElement("dataType", "xsd");
        SOAPElement soapBodyElemDescription = soapBodyElem1.addChildElement("description", "xsd");
        SOAPElement soapBodyElemFormat = soapBodyElem1.addChildElement("format", "xsd");
        SOAPElement soapBodyElemFormatDesc = soapBodyElem1.addChildElement("formatDescription", "xsd");
        SOAPElement soapBodyElemLengthHint = soapBodyElem1.addChildElement("lengthHint", "xsd");
        SOAPElement soapBodyElemProfileName = soapBodyElem1.addChildElement("name", "xsd");
        SOAPElement soapBodyElemOptional = soapBodyElem1.addChildElement("optional", "xsd");
        SOAPElement soapBodyElemOrderingHint = soapBodyElem1.addChildElement("orderingHint", "xsd");
        SOAPElement soapBodyElemValue = soapBodyElem1.addChildElement("value", "xsd");
        
        // SOAPElement soapBodyElemClearPassword = soapBodyElem.addChildElement("clearpassword", "xsd");
        SOAPElement soapBodyElemHash = soapBodyElem.addChildElement("hash", "xsd");
        SOAPElement soapBodyElemHashType = soapBodyElem.addChildElement("hashtype", "xsd");
        
        // add values
        soapBodyElemUid.addTextNode("888");
        soapBodyElemAccess.addTextNode("READ_WRITE");
        soapBodyElemDataType.addTextNode("STRING");
        soapBodyElemDescription.addTextNode("a simple description");
        soapBodyElemFormat.addTextNode("?");
        soapBodyElemFormatDesc.addTextNode("?");
        soapBodyElemLengthHint.addTextNode("0");
        soapBodyElemProfileName.addTextNode("email");
        soapBodyElemOptional.addTextNode("true");
        soapBodyElemOrderingHint.addTextNode("0");
        soapBodyElemValue.addTextNode("teye@example.com");
        
        soapBodyElemHash.addTextNode("$1$saltsalt$qjXMvbEw8oaL.Czf1DtaK/");
        soapBodyElemHashType.addTextNode("crypt");
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI  + "createUserNoConfirm");
        */
        
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("getUserProfile", "xsd");
        SOAPElement soapBodyElemUid = soapBodyElem.addChildElement("uid", "xsd");
        soapBodyElemUid.addTextNode("faber");
        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }

}
