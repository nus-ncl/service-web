
/**
 * ApiInfoCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.1  Built on : Feb 20, 2016 (10:01:29 GMT)
 */

package net.deterlab.testbed.api;

/**
 * ApiInfoCallbackHandler Callback class, Users can extend this class and
 * implement their own receiveResult and receiveError methods.
 */
public abstract class ApiInfoCallbackHandler {

    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the
     * NonBlocking Web service call is finished and appropriate method of this
     * CallBack is called.
     * 
     * @param clientData
     *            Object mechanism by which the user can pass in user data that
     *            will be avilable at the time this callback is called.
     */
    public ApiInfoCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public ApiInfoCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */

    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for echo method override this
     * method for handling normal response from echo operation
     */
    public void receiveResultecho(net.deterlab.testbed.api.ApiInfoStub.EchoResponse result) {
    }

    /**
     * auto generated Axis2 Error handler override this method for handling
     * error response from echo operation
     */
    public void receiveErrorecho(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServerCertificate method
     * override this method for handling normal response from
     * getServerCertificate operation
     */
    public void receiveResultgetServerCertificate(
            net.deterlab.testbed.api.ApiInfoStub.GetServerCertificateResponse result) {
    }

    /**
     * auto generated Axis2 Error handler override this method for handling
     * error response from getServerCertificate operation
     */
    public void receiveErrorgetServerCertificate(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getClientCertificate method
     * override this method for handling normal response from
     * getClientCertificate operation
     */
    public void receiveResultgetClientCertificate(
            net.deterlab.testbed.api.ApiInfoStub.GetClientCertificateResponse result) {
    }

    /**
     * auto generated Axis2 Error handler override this method for handling
     * error response from getClientCertificate operation
     */
    public void receiveErrorgetClientCertificate(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getVersion method override this
     * method for handling normal response from getVersion operation
     */
    public void receiveResultgetVersion(net.deterlab.testbed.api.ApiInfoStub.GetVersionResponse result) {
    }

    /**
     * auto generated Axis2 Error handler override this method for handling
     * error response from getVersion operation
     */
    public void receiveErrorgetVersion(java.lang.Exception e) {
    }

}
