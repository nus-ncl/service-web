package sg.ncl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SimpleController {
    
    DummyJavaSpiBinder myJavaBinder = new DummyJavaSpiBinder();
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String index() throws Exception {
        return "index";
    }
    
    @RequestMapping("/signup")
    public String signup() {
        return "signup";
    }
    
    @RequestMapping("/passwordReset")
    public String passwordReset() {
        return "passwordreset";
    }
}