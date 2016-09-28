package sg.ncl;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class MyErrorResourceTest {

    @Test
    public void testSetError() {
        final MyErrorResource myErrorResource = new MyErrorResource();
        String exceptionName = "sg.ncl.Exception." + RandomStringUtils.randomAlphanumeric(20);
        myErrorResource.setError(exceptionName);
        assertThat(myErrorResource.getError(), is(exceptionName));
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
