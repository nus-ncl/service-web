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
        Team myTeam = new Team(counter.incrementAndGet(), name, "this is a desc", "www.nus.edu.sg", "research", "NUS", true, false, 0, 0, 0);
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
    
    @RequestMapping(value = "/team/all", method = RequestMethod.GET, headers="Accept=application/json")
    public List<Team> getAllTeams() {
        return myTeamsList;
    }
    
    @RequestMapping(value = "/seedTeam")
    public List<Team> seedTeam() {
        Team team1 = new Team(counter.incrementAndGet(), "hybridcloud", "this is a desc", "www.nus.edu.sg", "research", "NUS", true, false, 0, 0, 0);
        Team team2 = new Team(counter.incrementAndGet(), "demo", "this is a desc", "www.nus.edu.sg", "research", "NUS", true, false, 0, 0, 0);
        Team team3 = new Team(counter.incrementAndGet(), "demo2", "this is a desc", "www.nus.edu.sg", "research", "NUS", true, false, 0, 0, 0);
        myTeamsList.add(team1);
        myTeamsList.add(team2);
        myTeamsList.add(team3);
        return myTeamsList;
    }
}
