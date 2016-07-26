package sg.ncl.testbed_interface;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import sg.ncl.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Te Ye
 */
public class TeamTest {

    @Test
    public void testGetId() {
        final Team2 one = new Team2();
        assertThat(one.getId(), is(nullValue()));
    }

    @Test
    public void testSetId() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setId(str);
        assertThat(one.getId(), is(str));
    }

    @Test
    public void testGetName() {
        final Team2 one = new Team2();
        assertThat(one.getName(), is(nullValue()));
    }

    @Test
    public void testSetName() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setName(str);
        assertThat(one.getName(), is(str));
    }

    @Test
    public void testGetDescription() {
        final Team2 one = new Team2();
        assertThat(one.getDescription(), is(nullValue()));
    }

    @Test
    public void testSetDescription() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setDescription(str);
        assertThat(one.getDescription(), is(str));
    }

    @Test
    public void testGetWebsite() {
        final Team2 one = new Team2();
        assertThat(one.getWebsite(), is(nullValue()));
    }

    @Test
    public void testSetWebsite() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setWebsite(str);
        assertThat(one.getWebsite(), is(str));
    }

    @Test
    public void testGetOrganisationType() {
        final Team2 one = new Team2();
        assertThat(one.getOrganisationType(), is(nullValue()));
    }

    @Test
    public void testSetOrganisationType() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setOrganisationType(str);
        assertThat(one.getOrganisationType(), is(str));
    }

    @Test
    public void testGetStatus() {
        final Team2 one = new Team2();
        assertThat(one.getStatus(), is(nullValue()));
    }

    @Test
    public void testSetStatus() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setStatus(str);
        assertThat(one.getStatus(), is(str));
    }

    @Test
    public void testGetCreatedDate() {
        final Team2 one = new Team2();
        assertThat(one.getCreatedDate(), is(nullValue()));
    }

    @Test
    public void testSetCreatedDate() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setCreatedDate(str);
        assertThat(one.getCreatedDate(), is(str));
    }

    @Test
    public void testGetVisibility() {
        final Team2 one = new Team2();
        assertThat(one.getVisibility(), is(nullValue()));
    }

    @Test
    public void testSetVisibility() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setVisibility(str);
        assertThat(one.getVisibility(), is(str));
    }

    @Test
    public void testGetMembersCount() {
        final Team2 one = new Team2();
        assertThat(one.getMembersCount(), is(0));
    }

    @Test
    public void testSetMembersCount() {
        final Team2 one = new Team2();
        final int random = new Random(20).nextInt();
        one.setMembersCount(random);
        assertThat(one.getMembersCount(), is(random));
    }

    @Test
    public void testGetOwner() {
        final Team2 one = new Team2();
        assertThat(one.getOwner(), is(nullValue()));
    }

    @Test
    public void testSetOwner() {
        final Team2 one = new Team2();
        final User2 user = Util.getUser();
        one.setOwner(user);
        assertThat(one.getOwner().getId(), is(user.getId()));
    }

    @Test
    public void testGetMembersList() {
        final Team2 one = new Team2();
        assertThat(one.getMembersList().size(), is(0));
    }

    @Test
    public void testSetMembersList() {
        final Team2 one = new Team2();
        List<User2> membersList = Util.getMembersList();
        one.setMembersList(membersList);

        List<User2> result = one.getMembersList();
        for (User2 members : result) {
            assertThat(members.getId(), is(membersList.get(0).getId()));
        }
    }

    @Test
    public void testGetPendingMembersList() {
        final Team2 one = new Team2();
        assertThat(one.getPendingMembersList().size(), is(0));
    }

    @Test
    public void testSetPendingMembersList() {
        final Team2 one = new Team2();
        List<User2> membersList = Util.getMembersList();
        one.setPendingMembersList(membersList);

        List<User2> result = one.getPendingMembersList();
        for (User2 members : result) {
            assertThat(members.getId(), is(membersList.get(0).getId()));
        }
    }

    @Test
    public void testGetMembersStatusMap() {
        final Team2 one = new Team2();
        assertThat(one.getMembersStatusMap().size(), is(0));
    }

    @Test
    public void testSetMembersStatusMap() {
        final Team2 one = new Team2();
        HashMap<String, String> membersStatusMap = new HashMap<>();
        String key = RandomStringUtils.randomAlphanumeric(20);
        String value = RandomStringUtils.randomAlphanumeric(20);
        membersStatusMap.put(key, value);
        one.setMembersStatusMap(membersStatusMap);

        HashMap<String, String> resultMap = one.getMembersStatusMap();

        for (Map.Entry<String,String> entry : resultMap.entrySet()) {
            String resultKey = entry.getKey();
            String resultValue = entry.getValue();
            assertThat(resultKey, is(key));
            assertThat(resultValue, is(value));
        }

    }

}
