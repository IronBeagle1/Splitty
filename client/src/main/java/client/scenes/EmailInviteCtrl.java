package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailInviteCtrl implements Initializable, SimpleRefreshable {
    @FXML
    private Button goBackButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Button inviteButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label nameFeedbackLabel;
    @FXML
    private Label emailFeedbackLabel;
    private Translation translation;
    private ServerUtils server;
    private MainCtrl mainCtrl;
    private EmailHandler emailHandler;
    private Event event;

    /**
     * Constructor
     * @param translation the translation to use
     * @param server the server to use
     * @param mainCtrl the main controller
     * @param emailHandler the email handler to use
     */
    @Inject
    public EmailInviteCtrl(Translation translation, ServerUtils server, MainCtrl mainCtrl, EmailHandler emailHandler) {
        this.translation = translation;
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.emailHandler = emailHandler;
    }

    /**
     * Initialize basic features for the Email Invite Screen
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
        titleLabel.textProperty().bind(translation.getStringBinding("Email.TitleLabel"));
        nameLabel.textProperty().bind(translation.getStringBinding("Email.NameLabel"));
        nameTextField.promptTextProperty().bind(translation.getStringBinding("Email.NameTextField"));
        emailTextField.promptTextProperty().bind(translation.getStringBinding("Email.EmailTextField"));
        nameFeedbackLabel.textProperty().bind(translation.getStringBinding("Empty"));
        emailFeedbackLabel.textProperty().bind(translation.getStringBinding("Empty"));
        cancelButton.textProperty().bind(translation.getStringBinding("Email.CancelButton"));
        inviteButton.textProperty().bind(translation.getStringBinding("Email.InviteButton"));
    }

    /**
     *Sends an invitation to the email address given in the email text field
     */
    public void sendInvite() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        Participant participant = new Participant(name);
        participant.setEmail(email);
        if (name.isEmpty()) {
            nameFeedbackLabel.textProperty().bind(translation.getStringBinding("Email.NameFeedbackLabel"));
        } else {
            nameFeedbackLabel.textProperty().bind(translation.getStringBinding("Empty"));
        }
        if (email.isEmpty() || !checkEmail(email)){
            emailFeedbackLabel.textProperty().bind(translation.getStringBinding("Email.EmailFeedbackLabel"));
        } else {
            emailFeedbackLabel.textProperty().bind(translation.getStringBinding("Empty"));
        }
        if (!name.isEmpty() && !email.isEmpty() && checkEmail(email)) {
            Thread emailThread = setupEmailThread(email, participant);
            emailThread.start();
            clearFields();
            mainCtrl.switchScreens(EventScreenCtrl.class);
        }
    }

    /**
     * checks if the inserted email has an appropriate pattern
     * @param email the value inserted by the user
     * @return true if the value is correct, false otherwise
     */
    public boolean checkEmail (String email){
        if (email == null || email.isEmpty()){
            return false;
        }
        String emailLike = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(emailLike);
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.matches()) {
            return true;
        } else {
            System.out.println("Invalid Email");
        }
        return false;
    }

    /**
     * Sets up a thread to send an email so the whole app doesn't freeze while the email is sent
     * @param email the email to send the invitation to
     * @param participant the participant to add to the event
     * @return the thread that sends the email
     */
    private Thread setupEmailThread(String email, Participant participant) {
        return new Thread(() -> {
            boolean result = emailHandler.sendEmail(email, "Invited to splitty!", emailHandler.getInviteText(event));
            if (result){
                server.addParticipant(event.getId(), participant);

            }
            Platform.runLater(() -> {
                mainCtrl.showEmailPrompt(result);
            });
        });
    }



    /**
     * Cancels the invitation and switches back to the event screen
     */
    public void cancel(){
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }

    /**
     * Clears the text fields
     */
    public void clearFields(){
        nameTextField.clear();
        emailTextField.clear();
    }

    /**
     * Refreshes the data of the EmailInviteCtrl
     * @param event the new Event data to process
     */
    @Override
    public void refresh(Event event) {
        this.event = event;
    }

    /**
     * Sets the name text field
     * @param nameTextField the textField to set it to
     */
    public void setNameTextField(TextField nameTextField) {
        this.nameTextField = nameTextField;
    }

    /**
     * Sets the email text field
     * @param emailTextField the textField to set it to
     */
    public void setEmailTextField(TextField emailTextField) {
        this.emailTextField = emailTextField;
    }

    /**
     * Sets the invite button
     * @param inviteButton the button to set it to
     */
    public void setInviteButton(Button inviteButton) {
        this.inviteButton = inviteButton;
    }

    /**
     * Sets the name feedback label
     * @param nameFeedbackLabel the label to set it to
     */
    public void setNameFeedbackLabel(Label nameFeedbackLabel) {
        this.nameFeedbackLabel = nameFeedbackLabel;
    }

    /**
     * Sets the email feedback label
     * @param emailFeedbackLabel the label to set it to
     */
    public void setEmailFeedbackLabel(Label emailFeedbackLabel) {
        this.emailFeedbackLabel = emailFeedbackLabel;
    }


}
