package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import sg.ncl.TeamManager2;
import sg.ncl.Util;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class TeamManagerTest {

    @Test
    public void testGetTeamMap() {
        final TeamManager2 teamManager = new TeamManager2();
        HashMap<String, Team2> one = new HashMap<>();
        assertThat(teamManager.getTeamMap(), is(one));
    }

    @Test
    public void testSetTeamMap() {
        final TeamManager2 teamManager = new TeamManager2();
        HashMap<String, Team2> one = new HashMap<>();
        one.put(RandomStringUtils.randomAlphanumeric(20), Util.getTeam());
        teamManager.setTeamMap(one);
        assertThat(teamManager.getTeamMap(), is(one));
    }

    @Test
    public void testAddTeamToTeamMap() {
        final TeamManager2 teamManager = new TeamManager2();
        final Team2 team = Util.getTeam();
        teamManager.addTeamToTeamMap(team);
        assertThat(teamManager.getTeamMap().size(), is(1));
        assertThat(teamManager.getTeamMap().get(team.getId()).getId(), is(team.getId()));
    }

    @Test
    public void testGetPublicTeamMap() {
        final TeamManager2 teamManager = new TeamManager2();
        HashMap<String, Team2> one = new HashMap<>();
        assertThat(teamManager.getPublicTeamMap(), is(one));
    }

    @Test
    public void testSetPublicTeamMap() {
        final TeamManager2 teamManager = new TeamManager2();
        HashMap<String, Team2> one = new HashMap<>();
        one.put(RandomStringUtils.randomAlphanumeric(20), Util.getTeam());
        teamManager.setPublicTeamMap(one);
        assertThat(teamManager.getPublicTeamMap(), is(one));
    }

    @Test
    public void testAddTeamToPublicTeamMap() {
        final TeamManager2 teamManager = new TeamManager2();
        final Team2 team = Util.getTeam();
        teamManager.addTeamToPublicTeamMap(team);
        assertThat(teamManager.getPublicTeamMap().size(), is(1));
        assertThat(teamManager.getPublicTeamMap().get(team.getId()).getId(), is(team.getId()));
    }

    @Test
    public void testGetUserJoinRequestTeamMap() {
        final TeamManager2 teamManager = new TeamManager2();
        HashMap<String, Team2> one = new HashMap<>();
        assertThat(teamManager.getUserJoinRequestMap(), is(one));
    }

    @Test
    public void testAddTeamToUserJoinRequestTeamMap() {
        final TeamManager2 teamManager = new TeamManager2();
        final Team2 team = Util.getTeam();
        teamManager.addTeamToUserJoinRequestTeamMap(team);
        assertThat(teamManager.getUserJoinRequestMap().get(team.getId()).getId(), is(team.getId()));
    }

}
