package server.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Service
public class AdminPasswordService {
    private final char[] passwordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private final Random random;
    private String password = "";

    /***
     * Constructor for the AdminPasswordService
     * @param random the Random object to use
     */
    public AdminPasswordService(Random random){
        this.random = random;
    }

    /**
     * generate a random password
     * @return the randomly generated password
     */
    public String generatePassword() {
        int passwordLength = 20;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < passwordLength; i++) {
            builder.append(passwordChars[random.nextInt(passwordChars.length)]);
        }
        return builder.toString();
    }
    private static final Logger log = LoggerFactory.getLogger(AdminPasswordService.class);

    /**
     * initialise and log in the console the randomly generated password
     * @return a log of the password for an admin to check
     */
    @Bean
    CommandLineRunner initPassword() {
        return args -> {
            this.password = generatePassword();
            log.info(password);
        };
    }

    /**
     * check if this is the correct password
     * @param inputPassword the password we check it is correct
     * @return whether the password is matching or not, boolean
     */
    public boolean passwordChecker(String inputPassword){
        return inputPassword.equals(this.password);
    }
}
