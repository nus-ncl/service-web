package sg.ncl.testbed_interface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import sg.ncl.Util;
import sg.ncl.domain.MemberStatus;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Te Ye
 */
public class TeamTest {

    @Test
    public void testGetId() {
        final Team2 one = new Team2();
        assertThat(one.getId()).isNull();
    }

    @Test
    public void testSetId() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setId(str);
        assertThat(one.getId()).isEqualTo(str);
    }

    @Test
    public void testGetName() {
        final Team2 one = new Team2();
        assertThat(one.getName()).isNull();
    }

    @Test
    public void testSetName() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setName(str);
        assertThat(one.getName()).isEqualTo(str);
    }

    @Test
    public void testGetDescription() {
        final Team2 one = new Team2();
        assertThat(one.getDescription()).isNull();
    }

    @Test
    public void testSetDescription() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setDescription(str);
        assertThat(one.getDescription()).isEqualTo(str);
    }

    @Test
    public void testGetWebsite() {
        final Team2 one = new Team2();
        assertThat(one.getWebsite()).isNull();
    }

    @Test
    public void testSetWebsite() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setWebsite(str);
        assertThat(one.getWebsite()).isEqualTo(str);
    }

    @Test
    public void testGetOrganisationType() {
        final Team2 one = new Team2();
        assertThat(one.getOrganisationType()).isNull();
    }

    @Test
    public void testSetOrganisationType() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setOrganisationType(str);
        assertThat(one.getOrganisationType()).isEqualTo(str);
    }

    @Test
    public void testGetStatus() {
        final Team2 one = new Team2();
        assertThat(one.getStatus()).isNull();
    }

    @Test
    public void testSetStatus() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setStatus(str);
        assertThat(one.getStatus()).isEqualTo(str);
    }

    @Test
    public void testGetCreatedDate() {
        final Team2 one = new Team2();
        assertThat(one.getCreatedDate()).isNull();
    }

    @Test
    public void testSetCreatedDate() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setCreatedDate(str);
        assertThat(one.getCreatedDate()).isEqualTo(str);
    }

    @Test
    public void testGetProcessedDate() {
        final Team2 one = new Team2();
        assertThat(one.getProcessedDate()).isNull();
    }

    @Test
    public void testSetProcessedDate() throws IOException {
        final Team2 one = new Team2();
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        one.setProcessedDate(mapper.writeValueAsString(zonedDateTime));

        assertThat(one.getProcessedDate().getMonthValue()).isEqualTo(zonedDateTime.getMonthValue());
        assertThat(one.getProcessedDate().getDayOfMonth()).isEqualTo(zonedDateTime.getDayOfMonth());
        assertThat(one.getProcessedDate().getYear()).isEqualTo(zonedDateTime.getYear());
    }

    @Test
    public void testGetVisibility() {
        final Team2 one = new Team2();
        assertThat(one.getVisibility()).isNull();
    }

    @Test
    public void testSetVisibility() {
        final Team2 one = new Team2();
        final String str = RandomStringUtils.randomAlphanumeric(20);
        one.setVisibility(str);
        assertThat(one.getVisibility()).isEqualTo(str);
    }

    @Test
    public void testGetMembersCount() {
        final Team2 one = new Team2();
        assertThat(one.getMembersCount()).isEqualTo(0);
    }

    @Test
    public void testSetMembersCount() {
        final Team2 one = new Team2();
        final int random = new Random(20).nextInt();
        one.setMembersCount(random);
        assertThat(one.getMembersCount()).isEqualTo(random);
    }

    @Test
    public void testGetOwner() {
        final Team2 one = new Team2();
        assertThat(one.getOwner()).isNull();
    }

    @Test
    public void testSetOwner() {
        final Team2 one = new Team2();
        final User2 user = Util.getUser();
        one.setOwner(user);
        assertThat(one.getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    public void testGetMembersList() {
        final Team2 one = new Team2();
        assertThat(one.getMembersList().size()).isEqualTo(0);
    }

    @Test
    public void testSetMembersList() {
        final Team2 one = new Team2();
        List<User2> membersList = Util.getMembersList();
        one.setMembersList(membersList);

        List<User2> result = one.getMembersList();

        assertThat(result).isNotEmpty();
        assertThat(membersList.size()).isEqualTo(result.size());
        assertThat(membersList).isEqualTo(result);
    }

    @Test
    public void testGetPendingMembersList() {
        final Team2 one = new Team2();
        assertThat(one.getPendingMembersList().size()).isEqualTo(0);
    }

    @Test
    public void testSetPendingMembersList() {
        final Team2 one = new Team2();
        List<User2> membersList = Util.getMembersList();
        one.setPendingMembersList(membersList);

        List<User2> result = one.getPendingMembersList();

        assertThat(result).isNotEmpty();
        assertThat(membersList.size()).isEqualTo(result.size());
        assertThat(membersList).isEqualTo(result);
    }

    @Test
    public void testGetMembersStatusMap() {
        final Team2 one = new Team2();
        assertThat(one.getMembersStatusMap().size()).isEqualTo(3);
        assertThat(one.getMembersStatusMap()).containsKeys(MemberStatus.APPROVED, MemberStatus.PENDING, MemberStatus.REJECTED);
    }

    @Test
    public void testSetMembersStatusMap() {
        final Team2 one = new Team2();
        EnumMap<MemberStatus, List<User2>> membersStatusMap = new EnumMap<>(MemberStatus.class);
        List<User2> approvedMembersList = Util.getMembersList();
        membersStatusMap.put(MemberStatus.APPROVED, approvedMembersList);
        one.setMembersStatusMap(membersStatusMap);

        EnumMap<MemberStatus, List<User2>> resultMap = one.getMembersStatusMap();

        assertThat(resultMap).isNotEmpty();
        assertThat(membersStatusMap).isEqualTo(resultMap);

    }

}
