

import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This is a command line interface to communicate with the java SPI binder
 * @author yeoteye
 */

public class CommandLine {

    public static void main(String[] args) {
        System.out.print("Enter a command: ");
        String userInput;
        
        Scanner scanIn = new Scanner(System.in);
        userInput = scanIn.nextLine();
        
        // JavaSpiBinder myJavaBinder = new JavaSpiBinder("192.168.56.103", "52323");
        DummyJavaSpiBinder myJavaBinder = new DummyJavaSpiBinder();
        
        try {
            if (userInput.equalsIgnoreCase("getversion")) 
            {
                System.out.println(myJavaBinder.getVersion());
            }
            else if (userInput.equalsIgnoreCase("getkeyid"))
            {
                System.out.println(myJavaBinder.getKeyID());
            } 
            else if (userInput.startsWith("login")) 
            {
                String username;
                String password;
                
                String[] parts = userInput.split("\\s+");
                username = parts[1];
                password = parts[2];
                // invoke login
                System.out.println(myJavaBinder.login(username, password));
            }
            else 
            {
                System.out.println("Error: No such command");
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("System exited.");
    }

}
