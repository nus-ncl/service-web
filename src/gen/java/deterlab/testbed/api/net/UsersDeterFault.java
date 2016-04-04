
/**
 * UsersDeterFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.1  Built on : Feb 20, 2016 (10:01:29 GMT)
 */

package deterlab.testbed.api.net;

public class UsersDeterFault extends java.lang.Exception{

    private static final long serialVersionUID = 1458633283414L;
    
    private deterlab.testbed.api.net.UsersStub.UsersDeterFault faultMessage;

    
        public UsersDeterFault() {
            super("UsersDeterFault");
        }

        public UsersDeterFault(java.lang.String s) {
           super(s);
        }

        public UsersDeterFault(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public UsersDeterFault(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(deterlab.testbed.api.net.UsersStub.UsersDeterFault msg){
       faultMessage = msg;
    }
    
    public deterlab.testbed.api.net.UsersStub.UsersDeterFault getFaultMessage(){
       return faultMessage;
    }
}
    