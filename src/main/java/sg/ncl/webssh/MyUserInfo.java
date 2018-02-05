package sg.ncl.webssh;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class MyUserInfo implements UserInfo, UIKeyboardInteractive {

    private String passphrase;
    private String password;

    @Override
    public String getPassphrase() {
        return passphrase;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassphrase(String message) {
        log.info("Prompt Passphrase: {}", message);
        return passphrase != null;
    }

    @Override
    public boolean promptPassword(String message) {
        log.info("Prompt Password: {}", message);
        return password != null;
    }

    @Override
    public boolean promptYesNo(String message) {
        log.info("Prompt Yes/No: {}", message);
        return true;
    }

    @Override
    public void showMessage(String message) {
        log.info("Show Message: {}", message);
    }

    @Override
    public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo) {
        log.info("Prompt: {}", prompt[0]);
        String[] response = new String[prompt.length];
        response[0] = password;
        return response;
    }
}
