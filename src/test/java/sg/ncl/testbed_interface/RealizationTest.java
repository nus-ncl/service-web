package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class RealizationTest {

    @Test
    public void testGetId() {
        final Realization realization = new Realization();
        assertThat(realization.getId(), is(nullValue()));
    }

    @Test
    public void testSetId() {
        final Realization realization = new Realization();
        final Long id = Long.parseLong(RandomStringUtils.randomNumeric(5));
        realization.setId(id);
        assertThat(realization.getId(), is(id));
    }

    @Test
    public void testGetExpId() {
        final Realization realization = new Realization();
        assertThat(realization.getExperimentId(), is(nullValue()));
    }

    @Test
    public void testSetExpId() {
        final Realization realization = new Realization();
        final Long id = Long.parseLong(RandomStringUtils.randomNumeric(5));
        realization.setExperimentId(id);
        assertThat(realization.getExperimentId(), is(id));
    }

    @Test
    public void testGetExpName() {
        final Realization realization = new Realization();
        assertThat(realization.getExperimentName(), is(nullValue()));
    }

    @Test
    public void testSetExpName() {
        final Realization realization = new Realization();
        final String one = RandomStringUtils.randomAlphanumeric(20);
        realization.setExperimentName(one);
        assertThat(realization.getExperimentName(), is(one));
    }

    @Test
    public void testGetUserId() {
        final Realization realization = new Realization();
        assertThat(realization.getUserId(), is(nullValue()));
    }

    @Test
    public void testSetUserId() {
        final Realization realization = new Realization();
        final String id = RandomStringUtils.randomAlphanumeric(20);
        realization.setUserId(id);
        assertThat(realization.getUserId(), is(id));
    }

    @Test
    public void testGetTeamId() {
        final Realization realization = new Realization();
        assertThat(realization.getTeamId(), is(nullValue()));
    }

    @Test
    public void testSetTeamId() {
        final Realization realization = new Realization();
        final String id = RandomStringUtils.randomAlphanumeric(20);
        realization.setTeamId(id);
        assertThat(realization.getTeamId(), is(id));
    }

    @Test
    public void testGetNumOfNodes() {
        final Realization realization = new Realization();
        assertThat(realization.getNumberOfNodes(), is(nullValue()));
    }

    @Test
    public void testSetNumOfNodes() {
        final Realization realization = new Realization();
        final int one = Integer.parseInt(RandomStringUtils.randomNumeric(5));
        realization.setNumberOfNodes(one);
        assertThat(realization.getNumberOfNodes(), is(one));
    }

    @Test
    public void testGetState() {
        final Realization realization = new Realization();
        assertThat(realization.getState(), is(nullValue()));
    }

    @Test
    public void testSetState() {
        final Realization realization = new Realization();
        final String one = RandomStringUtils.randomAlphanumeric(20);
        realization.setState(one);
        assertThat(realization.getState(), is(one));
    }

    @Test
    public void testGetDetails() {
        final Realization realization = new Realization();
        assertThat(realization.getDetails(), is(nullValue()));
    }

    @Test
    public void testSetDetails() {
        final Realization realization = new Realization();
        final String one = RandomStringUtils.randomAlphanumeric(20);
        realization.setDetails(one);
        assertThat(realization.getDetails(), is(one));
    }

    @Test
    public void testGetIdleMinutes() {
        final Realization realization = new Realization();
        assertThat(realization.getIdleMinutes(), is(nullValue()));
    }

    @Test
    public void testSetIdleMinutes() {
        final Realization realization = new Realization();
        final Long one = Long.parseLong(RandomStringUtils.randomNumeric(5));
        realization.setIdleMinutes(one);
        assertThat(realization.getIdleMinutes(), is(one));
    }

    @Test
    public void testGetRunningMinutes() {
        final Realization realization = new Realization();
        assertThat(realization.getRunningMinutes(), is(nullValue()));
    }

    @Test
    public void testSetRunningMinutes() {
        final Realization realization = new Realization();
        final Long one = Long.parseLong(RandomStringUtils.randomNumeric(5));
        realization.setRunningMinutes(one);
        assertThat(realization.getRunningMinutes(), is(one));
    }

    @Test
    public void testGetDetailsList() {
        final Realization realization = new Realization();
        assertThat(realization.getDetailsList(), is(empty()));
    }

    @Test
    public void testSetDetailsList() {
        final Realization realization = new Realization();
        List<String> detailsList = new ArrayList<>();
        detailsList.add(RandomStringUtils.randomAlphabetic(20));
        realization.setDetailsList(detailsList);
        assertThat(realization.getDetailsList(), is(detailsList));
    }
}
