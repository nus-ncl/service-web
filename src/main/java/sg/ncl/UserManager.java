package sg.ncl;

import java.util.HashMap;
import java.util.Map;

import sg.ncl.testbed_interface.Team;
import sg.ncl.testbed_interface.User;

public class UserManager {
    
    private final int ERROR_NO_SUCH_USER_ID = 0;
    private static UserManager USER_MANAGER_SINGLETON = null;
    /* userid, User */
    private static HashMap<Integer, User> usersMap = new HashMap<Integer, User>();
    
    private UserManager() {
        User johnDoe = new User();
        johnDoe.setUserId(200);
        johnDoe.setRole("normal");
        johnDoe.setName("John Doe");
        johnDoe.setEmail("johndoe@nus.edu.sg");
        johnDoe.setPassword("password");
        johnDoe.setJobTitle("Research Assistant");
        johnDoe.setInstitution("National University of Singapore");
        johnDoe.setInstitutionAbbreviation("NUS");
        johnDoe.setWebsite("http://www.nus.edu.sg");
        johnDoe.setAddress1("Boon Lay Drive 222");
        johnDoe.setCountry("Singapore");
        johnDoe.setCity("Singapore");
        johnDoe.setProvince("SG");
        johnDoe.setPostalCode("600123");
        johnDoe.setEmailVerified(true);
        
        User bob = new User();
        bob.setUserId(201);
        bob.setRole("normal");
        bob.setName("Bob");
        bob.setEmail("bob@nus.edu.sg");
        bob.setPassword("password");
        bob.setJobTitle("Research Bob");
        bob.setInstitution("National University of Singapore");
        bob.setInstitutionAbbreviation("NUS");
        bob.setWebsite("http://www.nus.edu.sg");
        bob.setAddress1("Boon Lay Drive 222");
        bob.setCountry("Singapore");
        bob.setCity("Singapore");
        bob.setProvince("SG");
        bob.setPostalCode("600123");
        bob.setEmailVerified(true);
        
        User charlie = new User();
        charlie.setUserId(202);
        charlie.setRole("admin");
        charlie.setName("Charlie");
        charlie.setEmail("charlie@nus.edu.sg");
        charlie.setPassword("password");
        charlie.setJobTitle("Research Charlie");
        charlie.setInstitution("National University of Singapore");
        charlie.setInstitutionAbbreviation("NUS");
        charlie.setWebsite("http://www.nus.edu.sg");
        charlie.setAddress1("Boon Lay Drive 222");
        charlie.setCountry("Singapore");
        charlie.setCity("Singapore");
        charlie.setProvince("SG");
        charlie.setPostalCode("600123");
        charlie.setEmailVerified(true);
        
        User dave = new User();
        dave.setUserId(203);
        dave.setRole("admin");
        dave.setName("Dave");
        dave.setEmail("dave@nus.edu.sg");
        dave.setPassword("password");
        dave.setJobTitle("Research Dave");
        dave.setInstitution("National University of Singapore");
        dave.setInstitutionAbbreviation("NUS");
        dave.setWebsite("http://www.nus.edu.sg");
        dave.setAddress1("Boon Lay Drive 222");
        dave.setCountry("Singapore");
        dave.setCity("Singapore");
        dave.setProvince("SG");
        dave.setPostalCode("600123");
        dave.setEmailVerified(true);
        
        User eve = new User();
        eve.setUserId(204);
        eve.setRole("normal");
        eve.setName("Eve");
        eve.setEmail("eve@nus.edu.sg");
        eve.setPassword("password");
        eve.setJobTitle("Research Eve");
        eve.setInstitution("National University of Singapore");
        eve.setInstitutionAbbreviation("NUS");
        eve.setWebsite("http://www.nus.edu.sg");
        eve.setAddress1("Boon Lay Drive 222");
        eve.setCountry("Singapore");
        eve.setCity("Singapore");
        eve.setProvince("SG");
        eve.setPostalCode("600123");
        eve.setEmailVerified(false);
        
        usersMap.put(johnDoe.getUserId(), johnDoe); // 200
        usersMap.put(bob.getUserId(), bob);         // 201
        usersMap.put(charlie.getUserId(), charlie); // 202
        usersMap.put(dave.getUserId(), dave);       // 203
        usersMap.put(eve.getUserId(), eve);         // 204 
    }
    
    public static UserManager getInstance() {
        if (USER_MANAGER_SINGLETON == null) {
            USER_MANAGER_SINGLETON = new UserManager();
        }
        return USER_MANAGER_SINGLETON;
    }
    
    public HashMap<Integer, User> getUserMap() {
        return usersMap;
    }
    
    public boolean validateLoginDetails(String email, String password) {
        for (Map.Entry<Integer, User> entry : usersMap.entrySet()) {
            User currUser = entry.getValue();
            if (currUser.getEmail().equals(email) && currUser.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEmailVerified(String email) {
        for (Map.Entry<Integer, User> entry : usersMap.entrySet()) {
            User currUser = entry.getValue();
            if (currUser.getEmail().equals(email) && currUser.isEmailVerified()) {
                return true;
            } else if (currUser.getEmail().equals(email) && !currUser.isEmailVerified()) {
                return false;
            }
        }
        return false;
    }
    
    // get a userid from email address
    public int getUserId(String email) {
        for (Map.Entry<Integer, User> entry : usersMap.entrySet()) {
            User currUser = entry.getValue();
            if (currUser.getEmail().equals(email)) {
                return currUser.getUserId();
            }
        }
        return ERROR_NO_SUCH_USER_ID;
    }
    
    public User getUserById(int userId) {
        User rv = null;
        for (Map.Entry<Integer, User> entry : usersMap.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                rv = entry.getValue();
                return rv;
            }
        }
        return rv;
    }
    
    public String getNameByUserId(int userId) {
        String rv = null;
        for (Map.Entry<Integer, User> entry : usersMap.entrySet()) {
            int currUserId = entry.getKey();
            if (currUserId == userId) {
                rv = entry.getValue().getName();
                return rv;
            }
        }
        return rv;
    }
}
