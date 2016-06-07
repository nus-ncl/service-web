package sg.ncl;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Te Ye
 */
@ConfigurationProperties(prefix = ConnectionProperties.PREFIX)
public class ConnectionProperties {

    public static final String PREFIX = "connection.properties";

    private String USERS_URI = "http://localhost:80/users/";
    private String TEAMS_URI = "http://localhost:80/teams/";
    private String AUTHENTICATION_URI = "http://localhost:80/authentication/";
    private String CREDENTIALS_URI = "http://localhost:80/credentials/";

    public String getUSERS_URI() {
        return USERS_URI;
    }

    public void setUSERS_URI(String USERS_URI) {
        this.USERS_URI = USERS_URI;
    }

    public String getTEAMS_URI() {
        return TEAMS_URI;
    }

    public void setTEAMS_URI(String TEAMS_URI) {
        this.TEAMS_URI = TEAMS_URI;
    }

    public String getAUTHENTICATION_URI() {
        return AUTHENTICATION_URI;
    }

    public void setAUTHENTICATION_URI(String AUTHENTICATION_URI) {
        this.AUTHENTICATION_URI = AUTHENTICATION_URI;
    }

    public String getCREDENTIALS_URI() {
        return CREDENTIALS_URI;
    }

    public void setCREDENTIALS_URI(String CREDENTIALS_URI) {
        this.CREDENTIALS_URI = CREDENTIALS_URI;
    }
}

