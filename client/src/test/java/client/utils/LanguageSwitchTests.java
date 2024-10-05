package client.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LanguageSwitchTests {
    private File dir;
    private LanguageSwitchUtils utils;

    /**
     * Test setup
     */
    @BeforeEach
    public void setup() {
        this.dir = mock(File.class);
        utils = new LanguageSwitchUtils(dir);
    }

    /**
     * Languages in the language directory should be added to the language observable list.
     */
    @Test
    public void findLanguages() {
        File english = mock(File.class);
        File german = mock(File.class);
        when(dir.listFiles()).thenReturn(new File[] {english, german});
        when(english.getName()).thenReturn("en_GB.properties");
        when(german.getName()).thenReturn("de_DE.properties");
        utils.refreshLanguages();
        ObservableList<Locale> expected = FXCollections.observableArrayList(Locale.of("en", "GB"),
                Locale.of("de", "DE"), Locale.ROOT);
        assertEquals(expected, utils.getLanguages());
    }
}
