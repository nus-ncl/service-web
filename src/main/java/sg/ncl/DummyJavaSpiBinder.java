package sg.ncl;


import net.deterlab.abac.Identity;

public class DummyJavaSpiBinder implements BinderInterface {
    
    /**
     * ApiInfo
     */
    @Override
    public String getVersion() throws Exception {
        return "0.3";
    }

    @Override
    public String getKeyID() throws Exception {
        return "abcdefgh";
    }

    @Override
    public String getPatchLevel() throws Exception {
        return "0.0";
    }

    @Override
    public String echo(String input) {
        return input;
    }

    @Override
    public String getClientCertificate(String certAttachedName) throws Exception {
        return "-----BEGIN CERTIFICATE-----" + "\n" + "MY CLIENT CERTIFICATE" + "\n" + "-----END CERTIFICATE-----" + "\n" + "-----BEGIN RSA PRIVATE KEY-----" + "\n" + "-----END RSA PRIVATE KEY-----";
    }

    @Override
    public String getServerCertificate() throws Exception {
        return "-----BEGIN CERTIFICATE-----" + "\n" + "MY SERVER CERTIFICATE" + "\n" + "-----END CERTIFICATE-----";
    }
    
    /**
     * Users
     */
    @Override
    public Identity login(String uid, String password) {
        Identity i = null;
        return i;
    }

    @Override
    public boolean logout() {
        System.out.println("logging out...");
        return true;
    }

}
