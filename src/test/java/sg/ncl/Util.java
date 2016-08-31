package sg.ncl;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import sg.ncl.domain.UserStatus;
import sg.ncl.domain.UserType;
import sg.ncl.testbed_interface.Team2;
import sg.ncl.testbed_interface.User2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Te Ye
 */
public class Util {

    public static User2 getUser() {
        final User2 user = new User2();
        user.setId(RandomStringUtils.randomAlphanumeric(20));
        user.setFirstName(RandomStringUtils.randomAlphanumeric(20));
        user.setLastName(RandomStringUtils.randomAlphanumeric(20));
        user.setJobTitle(RandomStringUtils.randomAlphanumeric(20));
        user.setEmail(RandomStringUtils.randomAlphanumeric(20));
        user.setEmailVerified(true);
        user.setAddress1(RandomStringUtils.randomAlphanumeric(20));
        user.setAddress2(RandomStringUtils.randomAlphanumeric(20));
        user.setInstitution(RandomStringUtils.randomAlphanumeric(20));
        user.setInstitutionAbbreviation(RandomStringUtils.randomAlphanumeric(20));
        user.setInstitutionWeb(RandomStringUtils.randomAlphanumeric(20));
        user.setCountry(RandomStringUtils.randomAlphanumeric(20));
        user.setCity(RandomStringUtils.randomAlphanumeric(20));
        user.setPhone(RandomStringUtils.randomAlphanumeric(20));
        user.setRegion(RandomStringUtils.randomAlphanumeric(20));
        user.setPostalCode(RandomStringUtils.randomAlphanumeric(20));

        user.setStatus(UserStatus.APPROVED.toString());
        user.setRoles(UserType.USER.toString());
        return user;
    }

    public static List<User2> getMembersList() {
        List<User2> membersList = new ArrayList<>();
        membersList.add(getUser());
        return membersList;
    }

    public static JSONObject getTeamJsonObject() {
        final JSONObject one = new JSONObject();
        one.put("id", RandomStringUtils.randomAlphanumeric(20));
        one.put("name", RandomStringUtils.randomAlphanumeric(20));
        one.put("description", RandomStringUtils.randomAlphanumeric(20));
        one.put("website", RandomStringUtils.randomAlphanumeric(20));
        one.put("organisationType", RandomStringUtils.randomAlphanumeric(20));
        one.put("visibility", "PUBLIC");
        one.put("privacy", "OPEN");
        one.put("status", "APPROVED");
        one.put("members", new ArrayList());
        return one;
    }

    public static JSONObject createUserJson() {
        JSONObject object = new JSONObject();
        JSONObject userDetails = new JSONObject();
        JSONObject address = new JSONObject();

        object.put("id", RandomStringUtils.randomAlphanumeric(20));
        userDetails.put("firstName", RandomStringUtils.randomAlphanumeric(20));
        userDetails.put("lastName", RandomStringUtils.randomAlphanumeric(20));
        userDetails.put("jobTitle", RandomStringUtils.randomAlphanumeric(20));
        userDetails.put("email", RandomStringUtils.randomAlphanumeric(8) + "@ncl.sg");
        userDetails.put("phone", RandomStringUtils.randomNumeric(8));
        userDetails.put("address", address);
        userDetails.put("institution", RandomStringUtils.randomAlphanumeric(20));
        userDetails.put("institutionAbbreviation", RandomStringUtils.randomAlphanumeric(20));
        userDetails.put("institutionWeb", RandomStringUtils.randomAlphanumeric(20));

        address.put("address1", RandomStringUtils.randomAlphanumeric(20));
        address.put("address2", RandomStringUtils.randomAlphanumeric(20));
        address.put("country", RandomStringUtils.randomAlphanumeric(20));
        address.put("region", RandomStringUtils.randomAlphanumeric(20));
        address.put("city", RandomStringUtils.randomAlphanumeric(20));
        address.put("zipCode", RandomStringUtils.randomNumeric(8));

        object.put("userDetails", userDetails);
        object.put("status", UserStatus.APPROVED);
        object.put("roles", UserType.USER);
        return object;
    }
}
