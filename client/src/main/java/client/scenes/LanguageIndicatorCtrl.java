package client.scenes;

import client.Exceptions.IncompleteLanguageException;
import client.utils.ImageUtils;
import client.utils.LanguageSwitchUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.util.Locale;

public class LanguageIndicatorCtrl {
    private final Translation translation;
    private final LanguageSwitchUtils utils;
    private final MainCtrl main;
    private final StringProperty generationMessage = new SimpleStringProperty();
    private final ImageUtils imageUtils;

    /**
     * Constructor for the LanguageIndicatorCtrl
     * @param translation - the translation to use
     * @param utils - the language switch utils to use
     * @param main - Main controller for switching screens
     * @param imageUtils Utilities for loading images
     */
    @Inject
    public LanguageIndicatorCtrl(Translation translation, LanguageSwitchUtils utils, MainCtrl main, ImageUtils imageUtils) {
        this.translation = translation;
        this.utils = utils;
        this.main = main;
        this.imageUtils = imageUtils;
    }

    /**
     * Set up the language indicator.
     * Graphics of the indicator are set here.
     * Behavior for changing the language is set here.
     * @param languageIndicator Give language indicator
     */
    public void initializeLanguageIndicator(ComboBox<Locale> languageIndicator) {
        languageIndicator.setValue(translation.getLocale());
        generationMessage.bind(translation.getStringBinding("Event.Language.Generate"));
        languageIndicator.setItems(utils.getLanguages());
        Callback<ListView<Locale>, ListCell<Locale>> cellFactory = listView -> new ListCell<>() {
            @Override
            public void updateItem(Locale item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setGraphic(null);
                    textProperty().unbind();
                } else if(item.getLanguage().isEmpty()) {
                    textProperty().bind(generationMessage);
                } else {
                    textProperty().bind(new SimpleStringProperty(item.getDisplayLanguage()));
                    ImageView flag = loadFlag(item.getCountry().toLowerCase());
                    flag.setFitWidth(50);
                    setGraphic(flag);
                }
            }
        };
        languageIndicator.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(Locale item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if(!item.equals(Locale.ROOT)) {
                        ImageView flag = loadFlag(item.getCountry().toLowerCase());
                        flag.setFitWidth(getWidth());
                        setGraphic(flag);
                    }
                }
            }
        });
        languageIndicator.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Languages will be emptied in refresh() method, which should be ignored.
            if(newValue != null) {
                if(newValue.equals(Locale.ROOT)) {
                    languageIndicator.getSelectionModel().select(translation.getLocale());
                    main.openLanguageGeneration();
                } else {
                    try {
                        translation.changeLanguage(newValue);
                    } catch(IncompleteLanguageException e) {
                        new Alert(Alert.AlertType.ERROR, translation.getStringBinding("Language.errorLabel")
                                .getValue()).showAndWait();
                        languageIndicator.setValue(oldValue);
                    }
                }
            }
        });
        languageIndicator.setCellFactory(cellFactory);
    }

    /**
     * Refresh the given language indicator.
     * Its list view will be updated as well.
     * @param languageIndicator Language Indicator combo box
     */
    public void refresh(ComboBox<Locale> languageIndicator) {
        utils.refreshLanguages();
        languageIndicator.getSelectionModel().select(translation.getLocale());
    }

    /**
     * Retrieves the flag of the given language
     * @param lang - Language code of the flag
     * @return ImageView of the language flag
     */
    public ImageView loadFlag(String lang) {
        // Should look if caching is beneficial when adding more languages
        Image defaultLanguage = new Image("images/flags/" + lang + ".png");
        ImageView iv = new ImageView();
        iv.setImage(defaultLanguage);
        iv.setPreserveRatio(true);
        return iv;
    }
}
