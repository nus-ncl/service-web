package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class ExperimentTest {

    @Test
    public void testGetId() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getId(), is(nullValue()));
    }

    @Test
    public void testSetId() {
        final Experiment2 exp = new Experiment2();
        final Long id = Long.parseLong(RandomStringUtils.randomNumeric(5));
        exp.setId(id);
        assertThat(exp.getId(), is(id));
    }

    @Test
    public void testGetUserId() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getUserId(), is(nullValue()));
    }

    @Test
    public void testSetUserId() {
        final Experiment2 exp = new Experiment2();
        final String id = RandomStringUtils.randomAlphanumeric(20);
        exp.setUserId(id);
        assertThat(exp.getUserId(), is(id));
    }

    @Test
    public void testGetTeamId() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getTeamId(), is(nullValue()));
    }

    @Test
    public void testSetTeamId() {
        final Experiment2 exp = new Experiment2();
        final String id = RandomStringUtils.randomAlphanumeric(20);
        exp.setTeamId(id);
        assertThat(exp.getTeamId(), is(id));
    }

    @Test
    public void testGetTeamName() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getTeamName(), is(nullValue()));
    }

    @Test
    public void testSetTeamName() {
        final Experiment2 exp = new Experiment2();
        final String name = RandomStringUtils.randomAlphanumeric(20);
        exp.setTeamName(name);
        assertThat(exp.getTeamName(), is(name));
    }

    @Test
    public void testGetName() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getName(), is(nullValue()));
    }

    @Test
    public void testSetName() {
        final Experiment2 exp = new Experiment2();
        final String name = RandomStringUtils.randomAlphanumeric(20);
        exp.setName(name);
        assertThat(exp.getName(), is(name));
    }

    @Test
    public void testGetDesc() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getDescription(), is(nullValue()));
    }

    @Test
    public void testSetDesc() {
        final Experiment2 exp = new Experiment2();
        final String one = RandomStringUtils.randomAlphanumeric(20);
        exp.setDescription(one);
        assertThat(exp.getDescription(), is(one));
    }

    @Test
    public void testGetNSFile() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getNsFile(), is(nullValue()));
    }

    @Test
    public void testSetNSFile() {
        final Experiment2 exp = new Experiment2();
        final String one = RandomStringUtils.randomAlphanumeric(20);
        exp.setNsFile(one);
        assertThat(exp.getNsFile(), is(one));
    }

    @Test
    public void testGetNSFileContent() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getNsFileContent(), is(nullValue()));
    }

    @Test
    public void testSetNSFileContent() {
        final Experiment2 exp = new Experiment2();
        final String one = RandomStringUtils.randomAlphanumeric(20);
        exp.setNsFileContent(one);
        assertThat(exp.getNsFileContent(), is(one));
    }

    @Test
    public void testGetIdleSwap() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getIdleSwap(), is(nullValue()));
    }

    @Test
    public void testSetIdleSwap() {
        final Experiment2 exp = new Experiment2();
        final int one = Integer.parseInt(RandomStringUtils.randomNumeric(5));
        exp.setIdleSwap(one);
        assertThat(exp.getIdleSwap(), is(one));
    }

    @Test
    public void testGetMaxDuration() {
        final Experiment2 exp = new Experiment2();
        assertThat(exp.getMaxDuration(), is(0));
    }

    @Test
    public void testSetMaxDuration() {
        final Experiment2 exp = new Experiment2();
        final int one = Integer.parseInt(RandomStringUtils.randomNumeric(5));
        exp.setMaxDuration(one);
        assertThat(exp.getMaxDuration(), is(one));
    }
}
