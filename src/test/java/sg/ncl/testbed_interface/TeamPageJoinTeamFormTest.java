package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class TeamPageJoinTeamFormTest {

    @Test
    public void testGetTeam() {
        final TeamPageJoinTeamForm one = new TeamPageJoinTeamForm();
        assertThat(one.getTeamName(), is(nullValue()));
    }

    @Test
    public void testSetTeam() {
        final TeamPageJoinTeamForm one = new TeamPageJoinTeamForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamName(str);
        assertThat(one.getTeamName(), is(str));
    }

}
