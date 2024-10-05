package client.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Locale;

public class LanguageSwitchUtils {
    private final ObservableList<Locale> languages = FXCollections.observableArrayList();
    private final File dir;

    /**
     * Constructor
     * @param dir Language template directory
     */
    @Inject
    public LanguageSwitchUtils(@Named("dir") File dir) {
        this.dir = dir;
    }

    /**
     * Clears the language list and adds all languages that this application supports.
     */
    public void refreshLanguages() {
        languages.clear();
        File[] files = dir.listFiles();
        if(files != null) {
            for(File properties : files) {
                String name = properties.getName();
                if(name.equals("template.properties"))
                    continue;
                String[] parts = name.split("_|\\.");
                languages.add(Locale.of(parts[0], parts[1]));
            }
        }
        languages.add(Locale.ROOT);
    }

    /**
     * Getter for the list of languages.
     * @return List of languages
     */
    public ObservableList<Locale> getLanguages() {
        return languages;
    }
}
