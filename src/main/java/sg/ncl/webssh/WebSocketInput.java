package sg.ncl.webssh;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketInput {

    private Boolean ctrlKey;
    private String key;
    private Integer keyCode;

    public WebSocketInput() {}

    public WebSocketInput(Boolean ctrlKey, String key, Integer keyCode) {
        this.ctrlKey = ctrlKey;
        this.key = key;
        this.keyCode = keyCode;
    }

    public String toString() {
        return "{ctrlKey: " + ctrlKey + ", key: " + key + ", keyCode: " + keyCode + "}";
    }
}
