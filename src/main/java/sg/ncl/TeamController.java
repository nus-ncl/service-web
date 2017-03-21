package sg.ncl;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sg.ncl.domain.ExceptionState;
import sg.ncl.domain.MemberStatus;
import sg.ncl.exceptions.WebServiceRuntimeException;
import sg.ncl.testbed_interface.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tran Ly Vu, James
 */

@Slf4j
public class TeamController extends MainController {

    @RequestMapping(value={"/team_quota/{teamId}"}, method= RequestMethod.POST)
    public String validateTeamQuota(@PathVariable String teamId,
                                    @ModelAttribute("teamQuota") TeamQuota teamQuota,
                                    final RedirectAttributes redirectAttributes,
                                    HttpSession session) {


        return null;
    }
}
