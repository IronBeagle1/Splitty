package client.scenes;

import client.utils.EmailHandler;
import client.utils.ServerUtils;
import client.utils.Translation;
import commons.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class EmailInviteCtrlTest extends ApplicationTest {
    private EmailInviteCtrl emailInviteCtrl;
    private Translation translation;
    private ServerUtils serverUtils;
    private EmailHandler emailHandler;
    private MainCtrl mainCtrl;

    private TextField nameTextField;
    private TextField emailTextField;
    private Label nameFeedbackLabel;
    private Label emailFeedbackLabel;
    private Button inviteButton;

    @BeforeEach
    void setup(){
        translation = mock(Translation.class);
        emailHandler = mock(EmailHandler.class);
        serverUtils = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);
        emailInviteCtrl = new EmailInviteCtrl(translation,serverUtils,mainCtrl,emailHandler);
        nameFeedbackLabel = mock(Label.class);
        nameTextField = mock(TextField.class);
        emailFeedbackLabel = mock(Label.class);
        emailTextField = mock(TextField.class);
        inviteButton = mock(Button.class);
        emailInviteCtrl.setEmailTextField(emailTextField);
        emailInviteCtrl.setInviteButton(inviteButton);
        emailInviteCtrl.setNameFeedbackLabel(nameFeedbackLabel);
        emailInviteCtrl.setNameTextField(nameTextField);
        emailInviteCtrl.setEmailFeedbackLabel(emailFeedbackLabel);
        when(translation.getStringBinding("Email.NameFeedbackLabel")).thenReturn(new SimpleStringProperty("Email.NameFeedbackLabel"));
        when(translation.getStringBinding("Empty")).thenReturn(new SimpleStringProperty("Empty"));
        when(translation.getStringBinding("Email.EmailFeedbackLabel")).thenReturn(new SimpleStringProperty("Email.EmailFeedbackLabel"));
        when(translation.getStringBinding("Email.TitleLabel")).thenReturn(new SimpleStringProperty("Email.TitleLabel"));
        when(translation.getStringBinding("Email.NameLabel")).thenReturn(new SimpleStringProperty("Email.NameLabel"));
        when(translation.getStringBinding("Email.EmailLabel")).thenReturn(new SimpleStringProperty("Email.EmailLabel"));
        when(translation.getStringBinding("Email.InviteButton")).thenReturn(new SimpleStringProperty("Email.InviteButton"));
        when(translation.getStringBinding("Email.CancelButton")).thenReturn(new SimpleStringProperty("Email.CancelButton"));
        when(translation.getStringBinding("Email.NameTextField")).thenReturn(new SimpleStringProperty("Email.NameTextField"));
        when(translation.getStringBinding("Email.EmailTextField")).thenReturn(new SimpleStringProperty("Email.EmailTextField"));
        when(nameFeedbackLabel.textProperty()).thenReturn(new SimpleStringProperty());
        when(emailFeedbackLabel.textProperty()).thenReturn(new SimpleStringProperty());
    }
    @BeforeAll
    static void testFXSetup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
    }

    @Test
    void cancelButton(){
        emailInviteCtrl.cancel();
        verify(mainCtrl).switchScreens(EventScreenCtrl.class);
    }

    @Test
    void checkEmail() {
        assertTrue(emailInviteCtrl.checkEmail("test@test.com"));
        assertFalse(emailInviteCtrl.checkEmail("@test.com"));
        assertFalse(emailInviteCtrl.checkEmail("test@test"));
    }

    @Test
    public void shouldDisplayFeedbackWhenNameIsEmpty() {
        when(nameTextField.getText()).thenReturn("");
        when(emailTextField.getText()).thenReturn("joe@test.com");
        when(nameFeedbackLabel.textProperty()).thenReturn(new SimpleStringProperty());
        when(emailFeedbackLabel.textProperty()).thenReturn(new SimpleStringProperty());
        emailInviteCtrl.sendInvite();
        assertEquals(nameFeedbackLabel.textProperty().get(), "Email.NameFeedbackLabel");
    }

    @Test
    public void shouldDisplayFeedbackWhenEmailIsEmpty() {
        when(nameTextField.getText()).thenReturn("joe");
        when(emailTextField.getText()).thenReturn("");

        emailInviteCtrl.sendInvite();
        assertEquals(emailFeedbackLabel.textProperty().get(), "Email.EmailFeedbackLabel");
    }

    @Test
    public void sendInviteSuccess() {
        when(nameTextField.getText()).thenReturn("joe");
        when(emailTextField.getText()).thenReturn("test@test.com");
        when(emailHandler.sendEmail(any(), any(), any())).thenReturn(true);
        emailInviteCtrl.refresh(new Event());
        emailInviteCtrl.sendInvite();
        verify(nameTextField).clear();
        verify(emailTextField).clear();
    }
}

