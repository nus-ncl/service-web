package main.java;

import net.deterlab.abac.ABACException;
import net.deterlab.abac.Identity;

public interface BinderInterface {
    
    // ApiInfo
    String getVersion() throws Exception;
    
    String getKeyID() throws Exception;
    
    String getPatchLevel() throws Exception;
    
    String echo(String input);
    
    String getClientCertificate(String certAttachedName) throws Exception;
    
    String getServerCertificate() throws Exception;
    
    // Users
    Identity login(String uid, String password);
    
    boolean logout();
    
    // createUser
    
    // removeUser
    
    // get profile
}
