package sg.ncl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import sg.ncl.testbed_interface.User;

public class UserManager {

    private static final int ErrorNoSuchUserID = 0;
    private static UserManager userManagerSingleton = null;
    /* userid, User */
    private HashMap<Integer, User> usersMap = new HashMap<>();
    private static final String NORMAL = "normal";
    private static final String PASSWORD = "password";
    private static final String NUS = "National University of Singapore";
    private static final String WEBSITE = "http://www.nus.edu.sg";
    private static final String ADDRESS = "Boon Lay Drive 222";
    private static final String CITY_COUNTRY = "Singapore";
    private static final String POSTALCODE = "600123";

    private UserManager() {
        User johnDoe = new User();
        johnDoe.setUserId(200);
        johnDoe.setRole(NORMAL);
        johnDoe.setName("John Doe");
        johnDoe.setEmail("johndoe@nus.edu.sg");
        johnDoe.setPassword(PASSWORD);
        johnDoe.setConfirmPassword(PASSWORD);
        johnDoe.setJobTitle("Research Assistant");
        johnDoe.setInstitution(NUS);
        johnDoe.setInstitutionAbbreviation("NUS");
        johnDoe.setWebsite(WEBSITE);
        johnDoe.setAddress1(ADDRESS);
        johnDoe.setCountry(CITY_COUNTRY);
        johnDoe.setCity(CITY_COUNTRY);
        johnDoe.setProvince("SG");
        johnDoe.setPostalCode(POSTALCODE);
        johnDoe.setEmailVerified(true);
        
        User bob = new User();
        bob.setUserId(201);
        bob.setRole(NORMAL);
        bob.setName("Bob");
        bob.setEmail("bob@nus.edu.sg");
        bob.setPassword(PASSWORD);
        bob.setConfirmPassword(PASSWORD);
        bob.setJobTitle("Research Bob");
        bob.setInstitution(NUS);
        bob.setInstitutionAbbreviation("NUS");
        bob.setWebsite(WEBSITE);
        bob.setAddress1(ADDRESS);
        bob.setCountry(CITY_COUNTRY);
        bob.setCity(CITY_COUNTRY);
        bob.setProvince("SG");
        bob.setPostalCode(POSTALCODE);
        bob.setEmailVerified(true);
        
        User charlie = new User();
        charlie.setUserId(202);
        charlie.setRole(NORMAL);
        charlie.setName("Charlie");
        charlie.setEmail("charlie@nus.edu.sg");
        charlie.setPassword(PASSWORD);
        charlie.setConfirmPassword(PASSWORD);
        charlie.setJobTitle("Research Charlie");
        charlie.setInstitution(NUS);
        charlie.setInstitutionAbbreviation("NUS");
        charlie.setWebsite(WEBSITE);
        charlie.setAddress1(ADDRESS);
        charlie.setCountry(CITY_COUNTRY);
        charlie.setCity(CITY_COUNTRY);
        charlie.setProvince("SG");
        charlie.setPostalCode(POSTALCODE);
        charlie.setEmailVerified(true);
        
        User dave = new User();
        dave.setUserId(203);
        dave.setRole(NORMAL);
        dave.setName("Dave");
        dave.setEmail("dave@nus.edu.sg");
        dave.setPassword(PASSWORD);
        dave.setConfirmPassword(PASSWORD);
        dave.setJobTitle("Research Dave");
        dave.setInstitution(NUS);
        dave.setInstitutionAbbreviation("NUS");
        dave.setWebsite(WEBSITE);
        dave.setAddress1(ADDRESS);
        dave.setCountry(CITY_COUNTRY);
        dave.setCity(CITY_COUNTRY);
        dave.setProvince("SG");
        dave.setPostalCode(POSTALCODE);
        dave.setEmailVerified(true);
        
        User eve = new User();
        eve.setUserId(204);
        eve.setRole(NORMAL);
        eve.setName("Eve");
        eve.setEmail("eve@nus.edu.sg");
        eve.setPassword(PASSWORD);
        eve.setConfirmPassword(PASSWORD);
        eve.setJobTitle("Research Eve");
        eve.setInstitution(NUS);
        eve.setInstitutionAbbreviation("NUS");
        eve.setWebsite(WEBSITE);
        eve.setAddress1(ADDRESS);
        eve.setCountry(CITY_COUNTRY);
        eve.setCity(CITY_COUNTRY);
        eve.setProvince("SG");
        eve.setPostalCode(POSTALCODE);
        eve.setEmailVerified(false);
        
        User noProject = new User();
        noProject.setUserId(205);
        noProject.setRole(NORMAL);
        noProject.setName("No Project");
        noProject.setEmail("noproject@nus.edu.sg");
        noProject.setPassword(PASSWORD);
        noProject.setConfirmPassword(PASSWORD);
        noProject.setJobTitle("Research No Project");
        noProject.setInstitution(NUS);
        noProject.setInstitutionAbbreviation("NUS");
        noProject.setWebsite(WEBSITE);
        noProject.setAddress1(ADDRESS);
        noProject.setCountry(CITY_COUNTRY);
        noProject.setCity(CITY_COUNTRY);
        noProject.setProvince("SG");
        noProject.setPostalCode(POSTALCODE);
        noProject.setEmailVerified(true);
        
        User ncl = new User();
        ncl.setUserId(206);
        ncl.setRole("admin");
        ncl.setName("NCL Admin");
        ncl.setEmail("ncladmin@ncl.sg");
        ncl.setPassword(PASSWORD);
        ncl.setConfirmPassword(PASSWORD);
        ncl.setJobTitle("NCL Admin");
        ncl.setInstitution(NUS);
        ncl.setInstitutionAbbreviation("NUS");
        ncl.setWebsite(WEBSITE);
        ncl.setAddress1(ADDRESS);
        ncl.setCountry(CITY_COUNTRY);
        ncl.setCity(CITY_COUNTRY);
        ncl.setProvince("SG");
        ncl.setPostalCode(POSTALCODE);
        ncl.setEmailVerified(true);
        
        usersMap.put(johnDoe.getUserId(), johnDoe); 	// 200
        usersMap.put(bob.getUserId(), bob);         	// 201
        usersMap.put(charlie.getUserId(), charlie); 	// 202
        usersMap.put(dave.getUserId(), dave);       	// 203
        usersMap.put(eve.getUserId(), eve);         	// 204
        usersMap.put(noProject.getUserId(), noProject); // 205
        usersMap.put(ncl.getUserId(), ncl); 			// 206
    }
    
    public static UserManager getInstance() {
        if (userManagerSingleton == null) {
            userManagerSingleton = new UserManager();
        }
        return userManagerSingleton;
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
            if (currUser.getEmail().equals(email) && currUser.getIsEmailVerified()) {
                return true;
            } else if (currUser.getEmail().equals(email) && !currUser.getIsEmailVerified()) {
                return false;
            }
        }
        return false;
    }
    
    // get a userid from email address
    public int getUserIdByEmail(String email) {
        for (Map.Entry<Integer, User> entry : usersMap.entrySet()) {
            User currUser = entry.getValue();
            if (currUser.getEmail().equals(email)) {
                return currUser.getUserId();
            }
        }
        return ErrorNoSuchUserID;
    }
    
    public User getUserById(int userId) {
        User rv = new User();
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
    
    public void updateUserDetails(User editedUser) {
    	int currUserId = editedUser.getUserId();
    	usersMap.put(currUserId, editedUser);
    }
    
    public void addNewUser(User newUser) {
    	int userId = generateRandomUserId();
    	newUser.setUserId(userId);
    	usersMap.put(userId, newUser);
    }
    
    public void removeUser(int userId) {
    	usersMap.remove(userId);
    }
    
    public int generateRandomUserId() {
    	Random rn = new Random();
    	int userId = rn.nextInt(Integer.MAX_VALUE) + 1;
    	while (usersMap.containsKey(userId)) {
    		userId = rn.nextInt();
    	}
    	return userId;
    }
    
    public boolean isUserAdmin(int userId) {
    	User currUser = usersMap.get(userId);
    	return currUser.getRole().equals("admin");
    }
}
