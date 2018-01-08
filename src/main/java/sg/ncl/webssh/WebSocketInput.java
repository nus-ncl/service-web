package sg.ncl.webssh;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketInput {

    private String command;
    private Double keyCode;

    public WebSocketInput() {}

    public WebSocketInput(String command, Double keyCode) {
        this.command = command;
        this.keyCode = keyCode;
    }

}
