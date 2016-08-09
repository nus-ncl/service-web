package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class ExperimentFormTest {

    @Test
    public void testGetTeamId() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getTeamId(), is(nullValue()));
    }

    @Test
    public void testSetTeamId() {
        final ExperimentForm one = new ExperimentForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamId(str);
        assertThat(one.getTeamId(), is(str));
    }

    @Test
    public void testGetTeamName() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getTeamName(), is(nullValue()));
    }

    @Test
    public void testSetTeamName() {
        final ExperimentForm one = new ExperimentForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamName(str);
        assertThat(one.getTeamName(), is(str));
    }

    @Test
    public void testGetTeamNameWithId() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getTeamNameWithId(), is(nullValue()));
    }

    @Test
    public void testSetTeamNameWithId() {
        final ExperimentForm one = new ExperimentForm();
        final String teamName = RandomStringUtils.randomAlphanumeric(20);
        final String teamId = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamNameWithId(teamName + ":" + teamId);
        assertThat(one.getTeamNameWithId(), is(teamName + ":" + teamId));
    }

    @Test
    public void testGetExpName() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getName(), is(nullValue()));
    }

    @Test
    public void testSetExpName() {
        final ExperimentForm one = new ExperimentForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setName(str);
        assertThat(one.getName(), is(str));
    }

    @Test
    public void testGetDescription() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getDescription(), is(nullValue()));
    }

    @Test
    public void testSetDescription() {
        final ExperimentForm one = new ExperimentForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setDescription(str);
        assertThat(one.getDescription(), is(str));
    }

    @Test
    public void testGetNsFile() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getNsFile(), is(nullValue()));
    }

    @Test
    public void testSetNsFile() {
        final ExperimentForm one = new ExperimentForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setNsFile(str);
        assertThat(one.getNsFile(), is(str));
    }

    @Test
    public void testGetNsFileContent() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getNsFileContent(), is(nullValue()));
    }

    @Test
    public void testSetNsFileContent() {
        final ExperimentForm one = new ExperimentForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setNsFileContent(str);
        assertThat(one.getNsFileContent(), is(str));
    }

    @Test
    public void testGetIdleSwap() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getIdleSwap(), is(nullValue()));
    }

    @Test
    public void testSetIdleSwap() {
        final ExperimentForm one = new ExperimentForm();
        final Integer num = Integer.parseInt(RandomStringUtils.randomNumeric(5));
        one.setIdleSwap(num);
        assertThat(one.getIdleSwap(), is(num));
    }

    @Test
    public void testGetMaxDuration() {
        final ExperimentForm one = new ExperimentForm();
        assertThat(one.getMaxDuration(), is(nullValue()));
    }

    @Test
    public void testSetMaxDuration() {
        final ExperimentForm one = new ExperimentForm();
        final Integer num = Integer.parseInt(RandomStringUtils.randomNumeric(5));
        one.setMaxDuration(num);
        assertThat(one.getMaxDuration(), is(num));
    }
}
