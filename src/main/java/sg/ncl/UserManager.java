package sg.ncl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.ncl.testbed_interface.Team;
import sg.ncl.testbed_interface.User;

public class UserManager {
    
    /* userid, User */
    private static HashMap<Integer, User> usersMap = new HashMap<Integer, User>();
    
    static {
        User johnDoe = new User();
        johnDoe.setUserId(200);
        johnDoe.setRole("normal");
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
        bob.setEmail("bobby@nus.edu.sg");
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
        dave.setEmail("charlie@nus.edu.sg");
        dave.setPassword("password");
        dave.setJobTitle("Research Charlie");
        dave.setInstitution("National University of Singapore");
        dave.setInstitutionAbbreviation("NUS");
        dave.setWebsite("http://www.nus.edu.sg");
        dave.setAddress1("Boon Lay Drive 222");
        dave.setCountry("Singapore");
        dave.setCity("Singapore");
        dave.setProvince("SG");
        dave.setPostalCode("600123");
        dave.setEmailVerified(true);
        
        usersMap.put(johnDoe.getUserId(), johnDoe); // 200
        usersMap.put(bob.getUserId(), bob);         // 201
        usersMap.put(charlie.getUserId(), charlie); // 202
        usersMap.put(dave.getUserId(), dave);       // 203
    }
    
    public HashMap<Integer, User> getUserMap() {
        return usersMap;
    }
}
