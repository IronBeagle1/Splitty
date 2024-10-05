package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagementOverviewPasswordCtrl implements Initializable {
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label inputPasswordLabel;
    @FXML
    private Button logInButton;
    @FXML
    private Label logInFeedback;
    @FXML
    private Button goBackToMainScreen;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    /**
     * Constructor
     * @param server the ServerUtils instance
     * @param mainCtrl the MainCtrl instance
     * @param translation the Translation to use
     */
    @Inject
    public ManagementOverviewPasswordCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
    }
    /**
     * Initialize basic features for the Management Overview Screen Password (log in) screen
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        translation.bindTextFieldPrompt(passwordField, "MOPCtrl.Password.Field");
        translation.bindLabel(inputPasswordLabel, "MOPCtrl.Input.Password.Label");
        translation.bindButton(logInButton, "MOPCtrl.Log.In.Button");
    }

    /**
     * check if the password in the passwordField matches with the one randomly generated
     */
    public void logInCheck() {
        String inputPassword = getPasswordFieldText(passwordField);
        if(inputPassword == null || inputPassword.isEmpty() || !server.checkPassword(inputPassword)){
            translation.bindLabel(logInFeedback, "MOPCtrl.Log.In.Feedback");
            clearPasswordField();
        }
        else{
            translation.bindLabel(logInFeedback, "Empty");
            clearPasswordField();
            mainCtrl.switchToManagementOverviewScreen();
        }
    }

    /**
     * go back to the main menu
     */
    public void goBackToMain() {
        translation.bindLabel(logInFeedback, "Empty");
        mainCtrl.showMainScreen();
    }

    /**
     * Gets the text from the passwordField
     * @param passwordField the passwordField
     * @return the text from the passwordField
     */
    public String getPasswordFieldText(PasswordField passwordField){
        return passwordField.getText();
    }

    /**
     * Clears the passwordField
     */
    public void clearPasswordField(){
        passwordField.clear();
    }
}
