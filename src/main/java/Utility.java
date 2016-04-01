package main.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;

import deterlab.testbed.api.net.UsersStub;
import net.deterlab.abac.ABACException;
import net.deterlab.abac.Identity;

/**
 * Base class that gives utility programs access to common functionality. eg. login and logout
 * @author yeoteye
 */

public class Utility {
    
    public Utility() {
        // default constructor
    }
    
    public Identity login(UsersStub myUsersStub, String uid, String password) throws Exception {
        Identity i = null;
        UsersStub.RequestChallenge req = new UsersStub.RequestChallenge();
        req.setUid(uid);
        req.setTypes(new String[] { "clear" });
        
        UsersStub.RequestChallengeResponse resp = myUsersStub.requestChallenge(req);
        UsersStub.UserChallenge uc = resp.get_return();
        UsersStub.ChallengeResponse chR = new UsersStub.ChallengeResponse();
        
        chR.setResponseData(putBytes(password.getBytes()));
        chR.setChallengeID(uc.getChallengeID());
        
        UsersStub.ChallengeResponseResponse chRR = myUsersStub.challengeResponse(chR);
        
        DataHandler dh = chRR.get_return();
        if ( dh != null ) {
            i = loadIdentity(getBytes(dh));
        }
        
        return i;
    }
    
    /**
     * Put a byte array into a DataHandler so that they can be made part of a
     * request.  The result is a DataHandler with MIME type
     * "application/octet-string containing the byte array.
     * @param b the bytes to store
     * @return a properly initialized DataHandler
     */
    static public DataHandler putBytes(byte[] b) {
        return new DataHandler(b, "application/octet-string");
    }

    /**
     * Get a byte array from a DataHandler.  This is the usual way to retrieve
     * opaque values from an RPC call.
     * @param dh the DataHandler
     * @return a byte array with the data handler's contents
     * @throws IOException if there are I/O problems.
     */
    static public byte[] getBytes(DataHandler dh) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        dh.writeTo(bos);
        return bos.toByteArray();
    }
    
    /**
     * Create an ABAC Identity from a byte array containing a PEM file.
     * @param b the PEM file contents
     * @return an ABAC ID
     * @throws ABACException if there are problems with the ID
     */
    static public Identity loadIdentity(byte[] b) throws ABACException {
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        return new Identity(bi);
    }
}
