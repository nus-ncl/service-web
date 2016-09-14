package sg.ncl;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import sg.ncl.testbed_interface.Team2;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class MyErrorResourceTest {

    @Test
    public void testSetName() {
        final MyErrorResource myErrorResource = new MyErrorResource();
        String exceptionName = RandomStringUtils.randomAlphanumeric(20);
        myErrorResource.setName("exceptions." + exceptionName);
        assertThat(myErrorResource.getName(), is(exceptionName));
    }

    @Test
    public void testGetMessage() {
        final MyErrorResource myErrorResource = new MyErrorResource();
        assertThat(myErrorResource.getMessage(), is(nullValue()));
    }

    @Test
    public void testSetMessage() {
        final MyErrorResource myErrorResource = new MyErrorResource();
        String one = RandomStringUtils.randomAlphanumeric(20);
        myErrorResource.setMessage(one);
        assertThat(myErrorResource.getMessage(), is(one));
    }

    @Test
    public void testGetLocalizedMessage() {
        final MyErrorResource myErrorResource = new MyErrorResource();
        assertThat(myErrorResource.getLocalizedMessage(), is(nullValue()));
    }

    @Test
    public void testSetLocalizedMessage() {
        final MyErrorResource myErrorResource = new MyErrorResource();
        String one = RandomStringUtils.randomAlphanumeric(20);
        myErrorResource.setLocalizedMessage(one);
        assertThat(myErrorResource.getLocalizedMessage(), is(one));
    }

}
