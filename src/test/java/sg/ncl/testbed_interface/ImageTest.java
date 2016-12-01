package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import sg.ncl.domain.ImageVisibility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * Created by dcsyeoty on 30-Oct-16.
 */
public class ImageTest {

    @Test
    public void testGetId() {
        final Image one = new Image();
        assertThat(one.getId(), is(nullValue()));
    }

    @Test
    public void testSetId() {
        final Image one = new Image();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setId(str);
        assertThat(one.getId(), is(str));
    }

    @Test
    public void testGetTeamId() {
        final Image one = new Image();
        assertThat(one.getTeamId(), is(nullValue()));
    }

    @Test
    public void testSetTeamId() {
        final Image one = new Image();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setTeamId(str);
        assertThat(one.getTeamId(), is(str));
    }

    @Test
    public void testGetNodeId() {
        final Image one = new Image();
        assertThat(one.getNodeId(), is(nullValue()));
    }

    @Test
    public void testSetNodeId() {
        final Image one = new Image();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setNodeId(str);
        assertThat(one.getNodeId(), is(str));
    }

    @Test
    public void testGetImageName() {
        final Image one = new Image();
        assertThat(one.getImageName(), is(nullValue()));
    }

    @Test
    public void testSetImageName() {
        final Image one = new Image();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setImageName(str);
        assertThat(one.getImageName(), is(str));
    }

    @Test
    public void testGetDescription() {
        final Image one = new Image();
        assertThat(one.getDescription(), is(nullValue()));
    }

    @Test
    public void testSetDescription() {
        final Image one = new Image();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setDescription(str);
        assertThat(one.getDescription(), is(str));
    }

    @Test
    public void testGetVisibility() {
        final Image one = new Image();
        assertThat(one.getVisibility(), is(nullValue()));
    }

    @Test
    public void testSetVisibility() {
        final Image one = new Image();
        one.setVisibility(ImageVisibility.PUBLIC);
        assertThat(one.getVisibility(), is(ImageVisibility.PUBLIC));
    }

    @Test
    public void testGetCurrentOS() {
        final Image one = new Image();
        assertThat(one.getCurrentOS(), is(nullValue()));
    }

    @Test
    public void testSetCurrentOS() {
        final Image one = new Image();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setCurrentOS(str);
        assertThat(one.getCurrentOS(), is(str));
    }
}
