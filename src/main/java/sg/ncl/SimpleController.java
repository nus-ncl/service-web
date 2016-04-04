package sg.ncl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SimpleController {
    
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}