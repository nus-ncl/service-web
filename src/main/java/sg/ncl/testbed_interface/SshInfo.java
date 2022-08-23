package sg.ncl.testbed_interface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.ZonedDateTime;

public class SshInfo {

    private ZonedDateTime createdDate;
    private ZonedDateTime lastModifiedDate;
    private long version;
    private long id;
    private String keyName;
    private String publicKey;
    private String fingerPrint;
    private String type;
    private String nclUserId;
    private String openStackUserId;

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            this.createdDate = mapper.readValue(createdDate, ZonedDateTime.class);
        } catch (IOException e) {
            this.createdDate = null;
        }
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            this.lastModifiedDate = mapper.readValue(lastModifiedDate, ZonedDateTime.class);
        } catch (IOException e) {
            this.lastModifiedDate = null;
        }
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) { this.type = type; }

    public String getNclUserId() {
        return nclUserId;
    }

    public void setNclUserId(String nclUserId) { this.nclUserId = nclUserId; }

    public String getOpenStackUserId() {
        return openStackUserId;
    }

    public void setOpenStackUserId(String openStackUserId) { this.openStackUserId = openStackUserId; }
}
