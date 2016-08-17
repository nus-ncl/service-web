package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class TeamPageApplyTeamFormTest {

    @Test
    public void testGetTeamName() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        assertThat(one.getTeamName(), is(nullValue()));
    }

    @Test
    public void testSetLoginEmail() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamName(str);
        assertThat(one.getTeamName(), is(str));
    }

    @Test
    public void testGetTeamDescription() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        assertThat(one.getTeamDescription(), is(nullValue()));
    }

    @Test
    public void testSetTeamDescription() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamDescription(str);
        assertThat(one.getTeamDescription(), is(str));
    }

    @Test
    public void testGetTeamWebsite() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        assertThat(one.getTeamDescription(), is(nullValue()));
    }

    @Test
    public void testSetTeamWebsite() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamWebsite(str);
        assertThat(one.getTeamWebsite(), is(str));
    }

    @Test
    public void testGetTeamOrganizationType() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        assertThat(one.getTeamOrganizationType(), is(nullValue()));
    }

    @Test
    public void testSetTeamOrganizationType() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamOrganizationType(str);
        assertThat(one.getTeamOrganizationType(), is(str));
    }

    @Test
    public void testGetTeamIsPublic() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        assertThat(one.getIsPublic(), is(TeamVisibility.PUBLIC.toString()));
    }

    @Test
    public void testSetTeamIsPublic() {
        final TeamPageApplyTeamForm one = new TeamPageApplyTeamForm();
        one.setIsPublic(TeamVisibility.PRIVATE.toString());
        assertThat(one.getIsPublic(), is(TeamVisibility.PRIVATE.toString()));
    }

}
