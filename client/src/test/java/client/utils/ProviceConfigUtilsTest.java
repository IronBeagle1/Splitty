package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ProviceConfigUtilsTest {
    Properties testProperties;
    Properties expectedProperties;
    String versionString;
    ConfigUtils sut;
    @BeforeEach
    void setUp() {
        testProperties = new Properties();
        expectedProperties = new Properties();
        expectedProperties.put("config.version", String.valueOf(ConfigUtils.CONFIG_VERSION));
        versionString = "config.version=".concat(String.valueOf(ConfigUtils.CONFIG_VERSION));
        sut = new ConfigUtils();
    }

    /***
     * Correct loading from Reader
     */
    @Test
    void loadValidPropertiesFromReader() {
        expectedProperties.put("key2", "value2");
        Reader validReader = new StringReader(versionString.concat("\nkey2=value2"));
        boolean success = sut.loadPropertiesFromReader(testProperties, validReader);
        assertTrue(success);
        assertEquals(expectedProperties, testProperties);
    }

    /***
     * Loading failure from out-of-date config
     */
    @Test
    void loadOutdatedPropertiesFromReader() {
        Reader outOfDateReader = new StringReader("no=versioning");
        boolean success = sut.loadPropertiesFromReader(testProperties, outOfDateReader);
        assertFalse(success);
    }

    /***
     * Comprehensive config loading test - by the nature of config loading this is dependent on the file system working
     */
    @Test
    void easyLoadProperties() {
        Properties returned = sut.easyLoadProperties();
        assertNotNull(returned);
        assertNotEquals(0, returned.size());
    }

    /***
     * Ensures the getDefault() method returns a usable Properties object
     */
    @Test
    void getDefaultNotEmpty() {
        Properties returned = sut.getDefault();
        assertNotNull(returned);
        assertNotEquals(0, returned.size());
    }
}