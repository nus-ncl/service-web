
/**
 * UsersCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.1  Built on : Feb 20, 2016 (10:01:29 GMT)
 */

    package deterlab.testbed.api.net;

    /**
     *  UsersCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class UsersCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public UsersCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public UsersCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for requestChallenge method
            * override this method for handling normal response from requestChallenge operation
            */
           public void receiveResultrequestChallenge(
                    deterlab.testbed.api.net.UsersStub.RequestChallengeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from requestChallenge operation
           */
            public void receiveErrorrequestChallenge(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for challengeResponse method
            * override this method for handling normal response from challengeResponse operation
            */
           public void receiveResultchallengeResponse(
                    deterlab.testbed.api.net.UsersStub.ChallengeResponseResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from challengeResponse operation
           */
            public void receiveErrorchallengeResponse(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for changeUserProfile method
            * override this method for handling normal response from changeUserProfile operation
            */
           public void receiveResultchangeUserProfile(
                    deterlab.testbed.api.net.UsersStub.ChangeUserProfileResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from changeUserProfile operation
           */
            public void receiveErrorchangeUserProfile(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getNotifications method
            * override this method for handling normal response from getNotifications operation
            */
           public void receiveResultgetNotifications(
                    deterlab.testbed.api.net.UsersStub.GetNotificationsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getNotifications operation
           */
            public void receiveErrorgetNotifications(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for modifyUserAttribute method
            * override this method for handling normal response from modifyUserAttribute operation
            */
           public void receiveResultmodifyUserAttribute(
                    deterlab.testbed.api.net.UsersStub.ModifyUserAttributeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from modifyUserAttribute operation
           */
            public void receiveErrormodifyUserAttribute(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for sendNotification method
            * override this method for handling normal response from sendNotification operation
            */
           public void receiveResultsendNotification(
                    deterlab.testbed.api.net.UsersStub.SendNotificationResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from sendNotification operation
           */
            public void receiveErrorsendNotification(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createUserNoConfirm method
            * override this method for handling normal response from createUserNoConfirm operation
            */
           public void receiveResultcreateUserNoConfirm(
                    deterlab.testbed.api.net.UsersStub.CreateUserNoConfirmResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createUserNoConfirm operation
           */
            public void receiveErrorcreateUserNoConfirm(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for markNotifications method
            * override this method for handling normal response from markNotifications operation
            */
           public void receiveResultmarkNotifications(
                    deterlab.testbed.api.net.UsersStub.MarkNotificationsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from markNotifications operation
           */
            public void receiveErrormarkNotifications(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for changePassword method
            * override this method for handling normal response from changePassword operation
            */
           public void receiveResultchangePassword(
                    deterlab.testbed.api.net.UsersStub.ChangePasswordResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from changePassword operation
           */
            public void receiveErrorchangePassword(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getUserProfile method
            * override this method for handling normal response from getUserProfile operation
            */
           public void receiveResultgetUserProfile(
                    deterlab.testbed.api.net.UsersStub.GetUserProfileResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getUserProfile operation
           */
            public void receiveErrorgetUserProfile(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for logout method
            * override this method for handling normal response from logout operation
            */
           public void receiveResultlogout(
                    deterlab.testbed.api.net.UsersStub.LogoutResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from logout operation
           */
            public void receiveErrorlogout(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createUserAttribute method
            * override this method for handling normal response from createUserAttribute operation
            */
           public void receiveResultcreateUserAttribute(
                    deterlab.testbed.api.net.UsersStub.CreateUserAttributeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createUserAttribute operation
           */
            public void receiveErrorcreateUserAttribute(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for removeUser method
            * override this method for handling normal response from removeUser operation
            */
           public void receiveResultremoveUser(
                    deterlab.testbed.api.net.UsersStub.RemoveUserResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from removeUser operation
           */
            public void receiveErrorremoveUser(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for changePasswordChallenge method
            * override this method for handling normal response from changePasswordChallenge operation
            */
           public void receiveResultchangePasswordChallenge(
                    deterlab.testbed.api.net.UsersStub.ChangePasswordChallengeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from changePasswordChallenge operation
           */
            public void receiveErrorchangePasswordChallenge(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for removeUserAttribute method
            * override this method for handling normal response from removeUserAttribute operation
            */
           public void receiveResultremoveUserAttribute(
                    deterlab.testbed.api.net.UsersStub.RemoveUserAttributeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from removeUserAttribute operation
           */
            public void receiveErrorremoveUserAttribute(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createUser method
            * override this method for handling normal response from createUser operation
            */
           public void receiveResultcreateUser(
                    deterlab.testbed.api.net.UsersStub.CreateUserResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createUser operation
           */
            public void receiveErrorcreateUser(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getProfileDescription method
            * override this method for handling normal response from getProfileDescription operation
            */
           public void receiveResultgetProfileDescription(
                    deterlab.testbed.api.net.UsersStub.GetProfileDescriptionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getProfileDescription operation
           */
            public void receiveErrorgetProfileDescription(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for requestPasswordReset method
            * override this method for handling normal response from requestPasswordReset operation
            */
           public void receiveResultrequestPasswordReset(
                    deterlab.testbed.api.net.UsersStub.RequestPasswordResetResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from requestPasswordReset operation
           */
            public void receiveErrorrequestPasswordReset(java.lang.Exception e) {
            }
                


    }
    