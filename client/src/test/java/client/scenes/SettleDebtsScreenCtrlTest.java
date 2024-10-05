package client.scenes;

import client.utils.*;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.ArrayList;
import java.util.List;

import static client.TestObservableUtils.stringToObservable;
import static javafx.geometry.Pos.TOP_CENTER;
import static javafx.geometry.Pos.TOP_LEFT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class SettleDebtsScreenCtrlTest {
    @Mock
    MainCtrl mainCtrl;
    @Mock
    Translation translation;
    @Mock
    SettleDebtsUtils settleUtils;
    @Mock
    ImageUtils imageUtils;
    @Mock
    EmailHandler emailHandler;
    @Mock
    Styling styling;
    @InjectMocks
    SettleDebtsScreenCtrl sut;
    Event event;
    Participant participant1;
    Participant participant2;
    Expense expense1;
    @BeforeAll
    static void testFXSetup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
    }

    @BeforeEach
    void setup(){
        event = new Event("Title!", null);
        participant1 = new Participant("Alastor");
        participant1.setEmail("irefusetousetechnologymadeafter1933@null");
        participant2 = new Participant("Vox");
        participant2.setLegalName("Vox V.");
        participant2.setIban("VOXTEK534645645");
        participant2.setBic("HELLBANK666");
        participant2.setEmail("vox@voxtek.com");
        expense1 = new Expense("Cameras!", 666, null, participant2);
        expense1.addParticipantToExpense(participant1);
        expense1.addParticipantToExpense(participant2);
    }

    @Test
    void populateVBoxTestOneTransfer(){
        Image testImage = new WritableImage(1,1);
        ImageView testImageView = new ImageView(testImage);
        doReturn(testImage).when(imageUtils).loadImageFile("singlearrow.png");
        doReturn(testImageView).when(imageUtils).generateImageView(testImage, 25);

        String testDescriptionString = participant2.getName() + " " + participant2.getLegalName()
                + " " + participant2.getIban() + " " + participant2.getBic();
        ObservableValue<String> bankDetails = stringToObservable(testDescriptionString);
        doReturn(bankDetails).when(settleUtils).getBankDetails(participant2);

        Transfer transferGenerated = new Transfer(participant1, 333, participant2);
        String testTransferString = "Alastor gives 6.66\u20ac to Vox";
        ObservableValue<String> transferDetails = stringToObservable(testTransferString);
        doReturn(transferDetails).when(settleUtils).createTransferString(transferGenerated);

        ObservableValue<String> received = stringToObservable("Mark Received");
        doReturn(received).when(translation).getStringBinding("SettleDebts.Button.received");

        ObservableValue<String> emailInstructions = stringToObservable("Send Email");
        doReturn(emailInstructions).when(translation).getStringBinding("SettleDebts.Button.sendEmailInstructions");

        List<Transfer> transfers = List.of(transferGenerated);
        doReturn(transfers).when(settleUtils).calculateTransferInstructions(any());

        VBox testBox = new VBox();
        sut.populateVBox(testBox, event);
        ObservableList<Node> children = testBox.getChildren();
        assertEquals(2, children.size());
        assertEquals(TOP_LEFT, testBox.getAlignment());
        HBox transferBox = (HBox) children.getFirst();
        VBox detailsPane = (VBox) children.getLast();

        Label transferLabel = (Label) transferBox.getChildren().get(2);
        StringProperty transferTextResult = transferLabel.textProperty();
        assertEquals(transferDetails.getValue(), transferTextResult.getValue());

        TextArea bankDetailsArea = (TextArea) detailsPane.getChildren().getFirst();
        var bankDetailsResult = bankDetailsArea.textProperty();
        assertEquals(bankDetails.getValue(), bankDetailsResult.getValue());
    }

    @Test
    void populateVBoxTestNoTransfers() {
        VBox textBox = new VBox();
        doReturn(new ArrayList<Transfer>()).when(settleUtils).calculateTransferInstructions(any());
        ObservableValue<String> noTransfers = stringToObservable("No Transfers!");
        doReturn(noTransfers).when(translation).getStringBinding("SettleDebts.Label.noTransfers");
        sut.populateVBox(textBox, event);
        verify(translation).getStringBinding("SettleDebts.Label.noTransfers");
        assertEquals(1, textBox.getChildren().size());
        assertEquals(TOP_CENTER, textBox.getAlignment());
    }

    @Test
    void participantBankTextTest(){
        String testDescriptionString = participant2.getName() + " " + participant2.getLegalName()
                + " " + participant2.getIban() + " " + participant2.getBic();
        ObservableValue<String> bankDetails = stringToObservable(testDescriptionString);
        doReturn(bankDetails).when(settleUtils).getBankDetails(participant2);
        TextArea result = sut.generateParticipantText(participant2);

        assertEquals(bankDetails.getValue(), result.textProperty().getValue());
    }

    @Test
    void bankTextPaneTest(){
        TextArea testArea = new TextArea("Text!!!");
        Pane result = sut.generateBankDetailsPane(testArea);
        assertEquals(testArea, result.getChildren().getFirst());
        assertFalse(result.isVisible());
    }

    @Test
    void transferDetailsBoxTest(){
        Button buttonL = new Button();
        Label midLabel = new Label();
        Button buttonR = new Button();
        Button buttonFarR = new Button();

        HBox result = sut.generateTransferDetailsBox(buttonL, midLabel ,buttonR, buttonFarR);
        ObservableList<Node> children = result.getChildren();
        assertEquals(6, children.size());
        assertEquals(buttonL, children.get(0));
        assertEquals(midLabel, children.get(2));
        assertEquals(buttonR, children.get(4));
        assertEquals(buttonFarR, children.get(5));
    }

    @Test
    void settleButtonTest(){
        Transfer transfer = new Transfer(participant1, 333, participant2);
        ObservableValue<String> buttonText = stringToObservable("Mark Received");
        doReturn(buttonText).when(translation).getStringBinding("SettleDebts.Button.received");
        final boolean[] handlerCalled = {false};
        EventHandler<ActionEvent> onAction = event -> handlerCalled[0] = true;
        doReturn(onAction).when(settleUtils).createSettleAction(transfer, event);

        Button result = sut.generateSettleButton(transfer, event);

        verify(translation).getStringBinding("SettleDebts.Button.received");
        assertEquals(buttonText.getValue(), result.textProperty().getValue());

        verify(settleUtils).createSettleAction(transfer, event);
        ObjectProperty<EventHandler<ActionEvent>> onClick = result.onActionProperty();
        onClick.get().handle(new ActionEvent());
        assertTrue(handlerCalled[0]);
    }

    @Test
    void twoExpandButtonsTest(){
        Pane pane1 = new VBox();
        Pane pane2 = new VBox();
        pane1.setVisible(false);
        pane1.setManaged(false);
        pane2.setVisible(false);
        pane2.setManaged(false);
        Image testImage = new WritableImage(1,1);
        ImageView testImageView = new ImageView(testImage);

        Button buttonForPane1 = sut.generateExpandButton(pane1, testImageView);
        Button buttonForPane2 = sut.generateExpandButton(pane2, testImageView);

        assertFalse(pane1.isVisible());
        assertFalse(pane2.isVisible());

        buttonForPane1.fire(); //Expand one

        assertTrue(pane1.isVisible());
        assertFalse(pane2.isVisible());

        buttonForPane2.fire(); //Expand one, close the other

        assertFalse(pane1.isVisible());
        assertTrue(pane2.isVisible());

        buttonForPane2.fire(); //Close the open one

        assertFalse(pane1.isVisible());
        assertFalse(pane2.isVisible());
    }

    @Test
    void validTransferLabelText(){
        Transfer exampleTransfer = new Transfer(new Participant("A!"), 19216801, new Participant("B!"));
        ObservableValue<String> observableText = stringToObservable("A! gives 192168.01\u20ac to B!");
        when(settleUtils.createTransferString(exampleTransfer)).thenReturn(observableText);
        Label transferLabel = sut.generateTransferLabel(exampleTransfer);
        assertEquals(observableText.getValue(), transferLabel.textProperty().getValue());
    }

    @Test
    void emailLabelAvailableTest(){
        ObservableValue<String> buttonText = stringToObservable("Send Email");
        doReturn(buttonText).when(translation).getStringBinding("SettleDebts.Button.sendEmailInstructions");
        doReturn(true).when(emailHandler).isConfigured();
        Transfer transfer = new Transfer(participant1, 333, participant2);
        var result = sut.generateSendEmailButton(transfer);

        result.fire();
        //we can not run threads on the build server, so here we make sure it's not disabled instead
        verify(styling, never()).applyStyling(any(), eq("disabledButton"));
    }

    @Test
    void emailLabelUnavailableTest(){
        ObservableValue<String> buttonText = stringToObservable("Send Email");
        doReturn(buttonText).when(translation).getStringBinding("SettleDebts.Button.sendEmailInstructions");
        participant1.setEmail("");
        Transfer transfer = new Transfer(participant1, 333, participant2);
        var result = sut.generateSendEmailButton(transfer);

        result.fire();
        verify(styling).applyStyling(any(), eq("disabledButton"));
        verify(settleUtils, never()).sendEmailTransferEmail(transfer);
    }

    @Test
    void switchToEventTest(){
        sut.switchToEventScreen();
        verify(mainCtrl).switchScreens(EventScreenCtrl.class);
    }

    @Test
    void bindLabelsTest(){
        Label titleLabel = new Label();
        ObservableValue<String> buttonText = stringToObservable("Open Debts");
        doReturn(buttonText).when(translation).getStringBinding("SettleDebts.Label.title");
        sut.bindLabels(titleLabel);
        assertEquals(buttonText.getValue(), titleLabel.textProperty().getValue());
    }

    @Test
    void generateImageTest(){
        Button testButton = new Button();
        Image testImage = new WritableImage(1,1);
        ImageView testImageView = new ImageView(testImage);
        when(imageUtils.generateImageView("goBack.png", 20)).thenReturn(testImageView);
        sut.setGraphics(testButton);
        assertEquals(testImageView, testButton.getGraphic());
    }
}