package sg.ncl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sg.ncl.testbed_interface.Team;

@RestController
public class TeamController {
    
    private List<Team> myTeamsList = new ArrayList<Team>();
    private final AtomicLong counter = new AtomicLong();
    
    @RequestMapping("/team")
    public Team addTeam(@RequestParam(value="name") String name) {
        Team myTeam = new Team(counter.incrementAndGet(), name, "this is a desc", "www.nus.edu.sg", "research", true);
        myTeamsList.add(myTeam);
        return myTeam;
    }
    
    @RequestMapping(value = "/team/{name}", method = RequestMethod.GET, headers="Accept=application/json")
    public String getTeamByName(@PathVariable String name)
    {
        for (Team currTeam:myTeamsList) {
            if (currTeam.getName().equalsIgnoreCase(name)) {
                return currTeam.toString();
            }
        }
        return "The team name not available";
    }
}
