package client.scenes;

import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class GenerateLanguageTemplateCtrl implements Initializable, SimpleRefreshable {
    @FXML
    private Label header;
    @FXML
    private TextField nameTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label nameLabel;
    private final Translation translation;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for this controller
     * @param translation Translation used to bind nodes
     * @param mainCtrl MainCtrl used for switching between screens
     */
    @Inject
    public GenerateLanguageTemplateCtrl(Translation translation, MainCtrl mainCtrl) {
        this.translation = translation;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initialize this controller
     * @param url URL
     * @param rsc ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rsc) {
        header.textProperty().bind(translation.getStringBinding("Language.header"));
        nameTextField.promptTextProperty().bind(translation.getStringBinding("Language.nameTextField"));
        cancelButton.textProperty().bind(translation.getStringBinding("Language.cancelButton"));
        confirmButton.textProperty().bind(translation.getStringBinding("Language.confirmButton"));
        nameLabel.textProperty().bind(translation.getStringBinding("Language.nameLabel"));
    }

    /**
     * Nothing needs to refreshed
     * @param event the new Event data to process
     */
    @Override
    public void refresh(Event event) {}

    /**
     * Event handler for cancel button
     */
    public void cancel() {
        nameTextField.clear();
        mainCtrl.closeLanguageGeneration();
    }

    /**
     * Confirm button event handler.
     */
    public void confirm() {
        try {
            if(isLanguageValid(nameTextField.getText())) {
                Files.copy(Path.of(Translation.LANGUAGE_PATH + "template.properties"),
                        Path.of(Translation.LANGUAGE_PATH + nameTextField.getText() + ".properties"));
                mainCtrl.closeLanguageGeneration();
                nameTextField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, translation.getStringBinding("Language.format").getValue()).showAndWait();
            }
        } catch (FileAlreadyExistsException e) {
            new Alert(Alert.AlertType.ERROR, translation.getStringBinding("Language.languageExists").getValue())
                    .showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether the language is a valid.
     * Language code has to conform to ISO 639 alpha-2.
     * Country code has to conform to ISO 3166 alpha-2.
     * @param text String of language that should be in the format languageCode_countryCode
     * @return True iff it is valid, else false.
     */
    public boolean isLanguageValid(String text) {
        if(text.matches(".+_{1}.+")) {
            String[] language = text.split("_");
            return List.of(Locale.getISOLanguages()).contains(language[0]) &&
                    List.of(Locale.getISOCountries()).contains(language[1]);
        }
        return false;
    }
}
