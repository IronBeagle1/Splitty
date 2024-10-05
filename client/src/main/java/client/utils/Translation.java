package client.utils;

import client.Exceptions.IncompleteLanguageException;
import client.Exceptions.InvalidLanguageFormatException;
import client.Exceptions.MissingLanguageTemplateException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class Translation {
    private final ObservableResourceFactory resourceFactory;
    private Locale locale;
    public static final String LANGUAGE_PATH = "client/lang/";
    private final Properties config;

    /***
     * Constructor that takes the resourceFactory the Translation encapsulates
     * @param resourceFactory - the ObservableResourceFactory to be used
     * @param config - Properties from the config file loaded at the start of the client
     */
    @Inject
    public Translation(ObservableResourceFactory resourceFactory, @Named("config") Properties config) {
        this.resourceFactory = resourceFactory;
        this.config = config;
    }

    /***
     * Provides a binding for the translated value corresponding to a text key
     * @param key the text key (e.g. "startup.label.create")
     * @return a live-updating binding for the translated value
     */
    public ObservableValue<String> getStringBinding(String key) {
        return resourceFactory.getStringBinding(key);
    }

    /***
     * Provides a binding for the translated value corresponding to a text key, with placeholders replaced based on the given Map
     * @param key the text key (e.g. "startup.label.create")
     * @param values the values to substitute in the final String
     * @return a live-updating binding for the translated value (values do NOT live update)
     */
    public ObservableValue<String> getStringSubstitutionBinding(String key, Map<String, String> values) {
        return resourceFactory.getStringSubstitutionBinding(key, values);
    }

    /***
     * Changes the language resources supplied to those from a corresponding language file
     * @param locale - the Local corresponding to the language file to switch to
     */
    public void changeLanguage(Locale locale) throws IncompleteLanguageException, InvalidLanguageFormatException {
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String fileName = languageCode + "_" + countryCode + ".properties";

        Properties languageProperties = new Properties();
        File file = new File(LANGUAGE_PATH, fileName);
        try {
            languageProperties.load(new FileReader(file));
            if(languageProperties.contains(""))
                throw new IncompleteLanguageException();
        } catch (IOException e) {
            // no such localization file
            if(ConfigUtils.DEFAULT_PROPS_LANGUAGE.equals(locale.getLanguage())){
                // a default language is missing!!!
                throw new RuntimeException("Bundled language files could not be found");
            } else
                throw new MissingLanguageTemplateException();
        }
        this.locale = locale;
        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, String> map = new HashMap<String, String>((Map) languageProperties);
        resourceFactory.setResources(map);
        try {
            FileWriter writer = new FileWriter(ConfigUtils.CONFIG_NAME);
            config.setProperty("client.language", languageCode + "_" + countryCode);
            config.store(writer, ConfigUtils.CONFIG_COMMENTS);
        } catch(IOException e) {
            throw new RuntimeException("Writing to config file went wrong");
        }

    }

    /***
     * Returns the Locale currently in use
     * @return the Locale currently in use
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Binds a label to a text
     * @param label the label to bind
     * @param binding the binding to use
     */
    public void bindLabel(Label label, String binding){
        label.textProperty().bind(this.getStringBinding(binding));
    }

    /**
     * Binds a textfield to a text
     * @param textField the textfield to bind
     * @param binding the binding to use
     */
    public void bindTextField(TextField textField, String binding){
        textField.textProperty().bind(this.getStringBinding(binding));
    }
    /**
     * Binds a textfield's prompt to a text
     * @param textField the textfield to bind
     * @param binding the binding to use
     */
    public void bindTextFieldPrompt(TextField textField, String binding){
        textField.promptTextProperty().bind(this.getStringBinding(binding));
    }

    /**
     * Binds a button to a text
     * @param button the button to bind
     * @param binding the binding to use
     */
    public void bindButton(Button button, String binding){
        button.textProperty().bind(this.getStringBinding(binding));
    }

}
