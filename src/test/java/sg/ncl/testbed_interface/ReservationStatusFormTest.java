package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class ReservationStatusFormTest {

    @Test
    public void testGetTeamId() {
        final ReservationStatusForm one = new ReservationStatusForm();
        assertThat(one.getTeamId(), is(nullValue()));
    }

    @Test
    public void testGetNumNodes() {
        final ReservationStatusForm one = new ReservationStatusForm();
        assertThat(one.getNumNodes(), is(nullValue()));
    }

    @Test
    public void testGetMachineType() {
        final ReservationStatusForm one = new ReservationStatusForm();
        assertThat(one.getMachineType(), is(nullValue()));
    }

    @Test
    public void testGetAction() {
        final ReservationStatusForm one = new ReservationStatusForm();
        assertThat(one.getAction(), is(nullValue()));
    }

    @Test
    public void testSetTeamId() {
        final ReservationStatusForm one = new ReservationStatusForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamId(str);
        assertThat(one.getTeamId(), is(str));
    }

    @Test
    public void testSetNumNodes() {
        final ReservationStatusForm one = new ReservationStatusForm();
        final int num = Integer.parseInt(RandomStringUtils.randomNumeric(3));
        one.setNumNodes(num);
        assertThat(one.getNumNodes(), is(num));
    }

    @Test
    public void testSetMachineType() {
        final ReservationStatusForm one = new ReservationStatusForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setMachineType(str);
        assertThat(one.getMachineType(), is(str));
    }

    @Test
    public void testSetAction() {
        final ReservationStatusForm one = new ReservationStatusForm();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setAction(str);
        assertThat(one.getAction(), is(str));
    }
}
