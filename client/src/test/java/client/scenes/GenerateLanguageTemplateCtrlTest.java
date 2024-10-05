package client.scenes;

import client.utils.Translation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class GenerateLanguageTemplateCtrlTest {
    private GenerateLanguageTemplateCtrl ctrl;

    /**
     * Test setup
     */
    @BeforeEach
    public void setup() {
        Translation translation = mock(Translation.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        ctrl = new GenerateLanguageTemplateCtrl(translation, mainCtrl);
    }

    /**
     * This is a valid Locale for German with Germany.
     */
    @Test
    public void validLanguage() {
        assertTrue(ctrl.isLanguageValid("de_DE"));
    }

    /**
     * Has to be an ISO 3166 alpha-2 country code
     */
    @Test
    public void invalidCountryCode() {
        assertFalse(ctrl.isLanguageValid("de_XX"));
    }

    /**
     * Has to be an ISO 639 alpha-2 language code
     */
    @Test
    public void invalidLanguageCode() {
        assertFalse(ctrl.isLanguageValid("xx_DE"));
    }

    /**
     * Make sure that incorrect format delivers no unexpected result.
     */
    @Test
    public void invalidFormat() {
        assertFalse(ctrl.isLanguageValid("English"));
    }
}