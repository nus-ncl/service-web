
/**
 * ApiInfoDeterFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.1  Built on : Feb 20, 2016 (10:01:29 GMT)
 */

package net.deterlab.testbed.api;

public class ApiInfoDeterFault extends java.lang.Exception {

    private static final long serialVersionUID = 1458626798676L;

    private net.deterlab.testbed.api.ApiInfoStub.ApiInfoDeterFault faultMessage;

    public ApiInfoDeterFault() {
        super("ApiInfoDeterFault");
    }

    public ApiInfoDeterFault(java.lang.String s) {
        super(s);
    }

    public ApiInfoDeterFault(java.lang.String s, java.lang.Throwable ex) {
        super(s, ex);
    }

    public ApiInfoDeterFault(java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(net.deterlab.testbed.api.ApiInfoStub.ApiInfoDeterFault msg) {
        faultMessage = msg;
    }

    public net.deterlab.testbed.api.ApiInfoStub.ApiInfoDeterFault getFaultMessage() {
        return faultMessage;
    }
}
