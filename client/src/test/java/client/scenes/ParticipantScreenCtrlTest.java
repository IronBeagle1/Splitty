package client.scenes;

import client.utils.ImageUtils;
import client.utils.ServerUtils;
import client.utils.Styling;
import client.utils.Translation;
import commons.Event;
import commons.Participant;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.ArrayList;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
public class ParticipantScreenCtrlTest {
    private Participant participant1;
    private Event event;

    @Mock
    MainCtrl mainCtrl;
    @Mock
    Translation translation;
    @Mock
    ServerUtils server;
    @InjectMocks
    ParticipantScreenCtrl participantScreenCtrl;
    @Mock
    ImageUtils imageUtils;
    @Mock
    Styling styling;
    @BeforeEach
    public void setup(){
        participant1 = new Participant(1, "John");
        Participant participant2 = new Participant(2, "Jane");
        event = new Event("Title", null);
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        lenient().doReturn(stringToObservable("Binding!")).when(translation).getStringBinding(anyString());
        Image testImage = new WritableImage(1,1);
        lenient().doReturn(new ImageView(testImage)).when(imageUtils).generateImageView(anyString(), anyInt());
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
    public void testIbanValid() {
        String iban = "NL99 9999 9999 9999 9999";
        boolean result = participantScreenCtrl.checkIban(iban);
        assertTrue(result);
    }
    @Test
    public void testIbanNotValid() {
        String iban = "AA";
        boolean result = participantScreenCtrl.checkIban(iban);
        assertFalse(result);
    }
    @Test
    public void testIbanNull() {
        boolean result = participantScreenCtrl.checkIban(null);
        assertFalse(result);
    }
    @Test
    public void testBicValid() {
        String bic = "AAAAAAAA";
        boolean result = participantScreenCtrl.checkBic(bic);
        assertTrue(result);
    }
    @Test
    public void testBicNotValid() {
        String bic = "AA";
        boolean result = participantScreenCtrl.checkBic(bic);
        assertFalse(result);
    }
    @Test
    public void testBicNull() {
        boolean result = participantScreenCtrl.checkBic(null);
        assertFalse(result);
    }
    @Test
    public void testEmailValid() {
        String email = "te@st.com";
        boolean result = participantScreenCtrl.checkEmail(email);
        assertTrue(result);
    }
    @Test
    public void testEmailNotValid() {
        String email = "test";
        boolean result = participantScreenCtrl.checkEmail(email);
        assertFalse(result);
    }
    @Test
    public void testEmailNull() {
        boolean result = participantScreenCtrl.checkEmail(null);
        assertFalse(result);
    }
    @Test
    public void findByIdTest(){
        Participant participant = participantScreenCtrl.findById(1, event);
        assertEquals(participant1, participant);
    }
    @Test
    public void findByIdNullTest(){
        Participant participant = participantScreenCtrl.findById(-10, event);
        assertNull(participant);
    }
    @Test
    public void checkNameTestTrue(){
        boolean result = participantScreenCtrl.checkParticipantName("John", event);
        assertTrue(result);
    }
    @Test
    public void checkNameTestFalse(){
        boolean result = participantScreenCtrl.checkParticipantName("Anna", event);
        assertFalse(result);
    }
    @Test
    public void checkSetParticipant(){
        TextField nameField = new TextField();
        TextField holderField = new TextField();
        TextField bicField = new TextField();
        TextField emailField = new TextField();
        TextField ibanField = new TextField();
        participant1.setEmail("email");
        participant1.setLegalName("John Christopher Depp");
        participant1.setIban("FL34 8053");
        participant1.setBic(null);
        participantScreenCtrl.setParticipant(1, event, nameField, holderField, ibanField, bicField, emailField);
        assertEquals(nameField.getText(), "John");
        assertEquals(holderField.getText(), "John Christopher Depp");
        assertEquals(ibanField.getText(), "FL34 8053");
        assertEquals(emailField.getText(), "email");
        assertNull(bicField.getText());
    }
    @Test
    public void clearFieldsTest(){
        TextField nameField = new TextField("not empty");
        TextField holderField = new TextField("not empty");
        TextField bicField = new TextField("not empty");
        TextField emailField = new TextField("not empty");
        TextField ibanField = new TextField("not empty");
        participantScreenCtrl.clearFields(nameField, holderField, ibanField, bicField, emailField);
        assertTrue(nameField.getText().isEmpty());
        assertTrue(emailField.getText().isEmpty());
        assertTrue(ibanField.getText().isEmpty());
        assertTrue(bicField.getText().isEmpty());
        assertTrue(holderField.getText().isEmpty());
    }

    @Test
    public void testAddParticipant(){
        TextField nameField = new TextField("Anna");
        TextField holderField = new TextField("Anna P");
        TextField bicField = new TextField("AAAAAAAA");
        TextField emailField = new TextField("email@email.em");
        TextField ibanField = new TextField("NL99 9999 9999 9999 9999");
        Participant participant = participantScreenCtrl.addParticipant(nameField, emailField, holderField, bicField, ibanField);
        assertEquals(participant.getName(), nameField.getText());
        assertEquals(participant.getLegalName(), holderField.getText());
        assertEquals(participant.getBic(), bicField.getText());
        assertEquals(participant.getEmail(), emailField.getText());
        assertEquals(participant.getIban(), ibanField.getText());
    }
    @Test
    public void testAddParticipantWrong(){
        TextField nameField = new TextField("Anna");
        TextField holderField = new TextField("Anna P");
        TextField bicField = new TextField("AA");
        TextField emailField = new TextField("em");
        TextField ibanField = new TextField("NL");
        Participant participant = participantScreenCtrl.addParticipant(nameField, emailField, holderField, bicField, ibanField);
        assertEquals(participant.getBic(), "wrongBic");
        assertEquals(participant.getEmail(), "wrongEmail");
        assertEquals(participant.getIban(), "wrongIban");
        emailField.setText("");
        ibanField.setText(null);
        bicField.setText(null);
        participant = participantScreenCtrl.addParticipant(nameField, emailField, holderField, bicField, ibanField);
        assertEquals(participant.getEmail(), "empty");
        assertNull(participant.getBic());
        assertNull(participant.getIban());
    }
    @Test
    public void testConfirmEdit(){
        TextField nameField = new TextField("Anna");
        TextField holderField = new TextField("Anna P");
        TextField bicField = new TextField("AAAAAAAA");
        TextField emailField = new TextField("email@email.co");
        TextField ibanField = new TextField("NL99 9999 9999 9999 9999");
        Label noName = new Label();
        Label noEmail = new Label();
        Label wrongBic = new Label();
        Label wrongIban = new Label();
        Boolean ok = false;
        ArrayList<TextField> f = participantScreenCtrl.bindTextFields(nameField, holderField, emailField, ibanField, bicField);
        Participant participant = participantScreenCtrl.addParticipant(nameField, emailField, holderField, bicField, ibanField);
        participantScreenCtrl.confirmEdit(styling, ok, participant, noName, noEmail, wrongBic, wrongIban, translation, server, mainCtrl, event, 0, f);
        verify(server).addParticipant(eq(event.getId()), eq(participant));
        verify(mainCtrl).switchScreens(eq(EventScreenCtrl.class));
    }

    @Test
    public void testConfirmEditNulls(){
        TextField nameField = new TextField(null);
        TextField holderField = new TextField("Anna P");
        TextField bicField = new TextField("AAAAAA");
        TextField emailField = new TextField("emaililco");
        TextField ibanField = new TextField("NL999 9999 9999 9999");
        Label noName = new Label();
        Label noEmail = new Label();
        Label wrongBic = new Label();
        Label wrongIban = new Label();
        boolean ok = false;
        ArrayList<TextField> f = participantScreenCtrl.bindTextFields(nameField, holderField, emailField, ibanField, bicField);
        Participant participant = participantScreenCtrl.addParticipant(nameField, emailField, holderField, bicField, ibanField);
        ObservableValue<String> noNameText = stringToObservable("Enter Name");
        doReturn(noNameText).when(translation).getStringBinding("Participants.Label.noName");
        ObservableValue<String> wrongBicText = stringToObservable("Wrong Bic");
        doReturn(wrongBicText).when(translation).getStringBinding("Participants.Label.wrongBic");
        participantScreenCtrl.confirmEdit(styling, ok, participant, noName, noEmail, wrongBic, wrongIban, translation, server, mainCtrl, event, 0, f);
        assertEquals("Enter Name", noName.getText());
        assertEquals("Wrong Bic", wrongBic.getText());
        assertFalse(ok);
    }

    @Test
    public void testConfirmEditNullsEmail(){
        TextField nameField = new TextField("Anna");
        TextField holderField = new TextField("Anna P");
        TextField bicField = new TextField("AAAAAAAA");
        TextField emailField = new TextField("emaililco");
        TextField ibanField = new TextField("NL99 9999 9999 9999 9999");
        Label noName = new Label();
        Label noEmail = new Label();
        Label wrongBic = new Label();
        Label wrongIban = new Label();
        ArrayList<TextField> f = participantScreenCtrl.bindTextFields(nameField, holderField, emailField, ibanField, bicField);
        boolean ok = false;
        Participant participant = participantScreenCtrl.addParticipant(nameField, emailField, holderField, bicField, ibanField);
        ObservableValue<String> noEmailText = stringToObservable("Wrong Email");
        doReturn(noEmailText).when(translation).getStringBinding("Participants.Label.wrongEmail");
        participantScreenCtrl.confirmEdit(styling, ok, participant, noName, noEmail, wrongBic, wrongIban, translation, server, mainCtrl, event, 0, f);
        assertEquals("Wrong Email", noEmail.getText());
        assertFalse(ok);
    }
    @Test
    public void testConfirmEditNullsEmailNull(){
        TextField nameField = new TextField("Anna");
        TextField holderField = new TextField("Anna P");
        TextField bicField = new TextField("AAAAAAAA");
        TextField emailField = new TextField(null);
        TextField ibanField = new TextField("NL99 9999 9999 9999 9999");
        Label noName = new Label();
        Label noEmail = new Label();
        Label wrongBic = new Label();
        Label wrongIban = new Label();
        ArrayList<TextField> f = participantScreenCtrl.bindTextFields(nameField, holderField, emailField, ibanField, bicField);
        boolean ok = false;
        Participant participant = participantScreenCtrl.addParticipant(nameField, emailField, holderField, bicField, ibanField);
        participantScreenCtrl.confirmEdit(styling, ok, participant, noName, noEmail, wrongBic, wrongIban, translation, server, mainCtrl, event, 0, f);
        assertFalse(ok);
        assertEquals(participant.getEmail(), "");
    }
    @Test
    public void testConfirmEditEdit(){
        TextField nameField = new TextField("Anna");
        TextField holderField = new TextField("Anna P");
        TextField bicField = new TextField("AAAAAAAA");
        TextField emailField = new TextField("email@email.co");
        TextField ibanField = new TextField("NL99 9999 9999 9999 9999");
        Label noName = new Label();
        Label noEmail = new Label();
        Label wrongBic = new Label();
        Label wrongIban = new Label();
        ArrayList<TextField> f = participantScreenCtrl.bindTextFields(nameField, holderField, emailField, ibanField, bicField);
        Boolean ok = false;
        Participant participant = participantScreenCtrl.addParticipant(nameField, emailField, holderField, bicField, ibanField);
        participantScreenCtrl.confirmEdit(styling, ok, participant, noName, noEmail, wrongBic, wrongIban, translation, server, mainCtrl, event, 1, f);
        verify(server).editParticipant(eq(event.getId()), eq(1L), eq(participant));
        verify(mainCtrl).switchScreens(eq(ParticipantListScreenCtrl.class));
    }

    @Test
    public void resetErrors(){
        Label noName = new Label();
        Label noEmail = new Label();
        Label wrongBic = new Label();
        Label wrongIban = new Label();
        ObservableValue<String> empty = stringToObservable("Nothing here");
        doReturn(empty).when(translation).getStringBinding("empty");
        participantScreenCtrl.resetErrorFields(translation, noName, noEmail, wrongBic, wrongIban);
        assertEquals("Nothing here", noName.getText());
        assertEquals("Nothing here", noEmail.getText());
        assertEquals("Nothing here", wrongBic.getText());
        assertEquals("Nothing here", wrongIban.getText());
    }
}
