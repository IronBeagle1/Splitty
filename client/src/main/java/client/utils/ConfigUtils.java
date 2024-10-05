package client.utils;

import java.io.*;
import java.util.Locale;
import java.util.Properties;

public class ConfigUtils {
    public static final String CONFIG_NAME = "client/splitty.properties";
    // update this whenever you add/remove/change default properties
    public static final int CONFIG_VERSION = 4;
    public static final String DEFAULT_PROPS_SERVER_URL = "http://localhost:8080/";
    // Flag of Great Britain is used for representing English
    public static final String DEFAULT_PROPS_LANGUAGE = Locale.ENGLISH.getLanguage() + "_GB";
    public static final String CONFIG_COMMENTS = "Language should be in format: <languageCode>_<countryCode>." +
            System.lineSeparator() + "languageCode must be a ISO 639 alpha-2 language code." +
            System.lineSeparator() + "countryCode must be ISO 3166 alpha-2 country code." + System.lineSeparator();

    /***
     * Full method for loading properties from the config file, or generating a default one
     * @return all properties found in the config file
     */
    public Properties easyLoadProperties() {
        Properties properties = new Properties();
        boolean validConfigLoaded;

        FileReader reader;
        try {
            reader = new FileReader(CONFIG_NAME);
            validConfigLoaded = loadPropertiesFromReader(properties, reader);
        } catch (FileNotFoundException e) {
            validConfigLoaded = false;
        }

        if(!validConfigLoaded){
            properties = getDefault();
            try{
                FileWriter writer = new FileWriter(CONFIG_NAME);
                properties.store(writer, CONFIG_COMMENTS);
            } catch (IOException e) {
                // this should never happen unless we don't have write access at all
                throw new RuntimeException(e);
            }

        }

        return properties;
    }

    /***
     * Attempts to load up-to-date properties from a Reader
     * @param properties - the Properties object to load values into
     * @param propertyReader - the Reader to load values from
     * @return true if the file was found and is of the current version
     * false if either condition is not met
     */
    public boolean loadPropertiesFromReader(Properties properties, Reader propertyReader){
        try {
            properties.load(propertyReader);
        } catch (IOException e) {
            return false;
        }

        // return false if our config has been updated in code, and thus the read one is invalid
        return  String.valueOf(CONFIG_VERSION).equals(properties.getProperty("config.version"));
    }

    /***
     * Returns a Properties object with all default values
     * @return a new, up-to-date and valid Properties object with default values
     */
    public Properties getDefault(){
        Properties properties = new Properties();
        properties.setProperty("config.version", String.valueOf(CONFIG_VERSION));
        properties.setProperty("connection.URL", DEFAULT_PROPS_SERVER_URL);
        properties.setProperty("client.language", DEFAULT_PROPS_LANGUAGE);
        properties.setProperty("spring.mail.host", "");
        properties.setProperty("spring.mail.port", "");
        properties.setProperty("spring.mail.username", "");
        properties.setProperty("spring.mail.password", "");
        properties.setProperty("spring.mail.properties.mail.smtp.auth", "true");
        properties.setProperty("spring.mail.properties.mail.smtp.starttls.enable", "true");
        return properties;
    }
}
