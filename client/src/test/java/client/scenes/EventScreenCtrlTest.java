package client.scenes;

import client.utils.ImageUtils;
import client.utils.ServerUtils;
import client.utils.StringGenerationUtils;
import client.utils.Translation;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Date;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class EventScreenCtrlTest {
    @Mock
    ServerUtils server;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    Translation translation;
    @Mock
    LanguageIndicatorCtrl languageCtrl;
    @Mock
    ImageUtils imageUtils;
    @Mock
    StringGenerationUtils stringUtils;

    @InjectMocks
    EventScreenCtrl sut;
    private Participant participant1;
    private Participant participant2;
    private Participant participant3;
    private Participant participant4;
    private Event event;
    private Expense expense1;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    public void setup(){
        participant1 = new Participant(1, "John");
        participant2 = new Participant(2, "Jane");
        participant3 = new Participant(3, "Mike");
        participant4 = new Participant(4, "Bob");
        event = new Event("Title", null);
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        event.addParticipant(participant3);
        event.addParticipant(participant4);
        tag1 = new Tag("Drinks!", "#000000");
        tag2 = new Tag("Food!", "#FFFFFF");
        expense1 = new Expense("Drinks", 12, new Date(1929), participant1);
        expense1.addParticipantToExpense(participant1);
        expense1.addParticipantToExpense(participant2);
        expense1.setExpenseTag(tag1);
        Expense expense2 = new Expense("Food", 20, new Date(2024), participant2);
        expense2.addParticipantToExpense(participant2);
        expense2.addParticipantToExpense(participant3);
        expense2.setExpenseTag(tag2);
        event.addExpense(expense1);
        event.addExpense(expense2);
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
    void expenseBoxGenerationTest(){
        ObservableValue<String> textDescription = stringToObservable("John paid 12\u20ac for Drinks (John, Jane)");
        Image testImage = new WritableImage(1,1);
        ImageView testImageView = new ImageView(testImage);
        doReturn(textDescription).when(stringUtils).generateTextForExpenseLabel(expense1, event.getParticipants().size());
        doReturn(testImageView).when(imageUtils).generateImageView(testImage, 15);
        HBox result = sut.generateExpenseBox(expense1, event, testImage, 500);
        ObservableList<Node> children = result.getChildren();
        HBox justExpense = (HBox)children.get(0);
        ObservableList<Node> children1 = justExpense.getChildren();
        HBox dateBox = (HBox) children1.get(0);
        Label dateLabel = (Label) dateBox.getChildren().getFirst();
        Label expenseText = (Label) children1.get(1);
        Label tagLabel = (Label) children1.get(2);
        HBox removeBox = (HBox) children.get(1);
        ImageView removeButton = (ImageView) removeBox.getChildren().getFirst();

        assertEquals("01/01/1970", dateLabel.textProperty().getValue());
        assertEquals(textDescription.getValue(), expenseText.textProperty().getValue());
        assertEquals(tag1.getTagName(), tagLabel.textProperty().getValue());
        assertEquals(testImage, removeButton.getImage());
    }

    @Test
    void labelGenerationTest(){
        ObservableValue<String> textDescription = stringToObservable("John paid 12\u20ac for Drinks (John, Jane)");
        Scene testScene = new Scene(new AnchorPane());
        doReturn(testScene).when(mainCtrl).getEventScene();
        Label result = sut.generateExpenseLabel(expense1.getId(), textDescription);

        assertEquals(textDescription.getValue(), result.textProperty().getValue());
        result.onMouseEnteredProperty().get().handle(null);
        verify(mainCtrl).getEventScene();
    }

    @Test
    void removeButtonGenerationTest(){
        Image testImage = new WritableImage(1,1);
        ImageView testImageView = new ImageView(testImage);
        doReturn(testImageView).when(imageUtils).generateImageView(testImage, 15);
        var result = sut.generateRemoveButton(expense1.getId(), testImage);
        assertEquals(testImage, result.getImage());
    }

    @Test
    void includingFilterTest(){
        ObservableValue<String> textDescription = stringToObservable("John paid 12\u20ac for Drinks (John, Jane)");
        Image testImage = new WritableImage(1,1);
        ImageView testImageView = new ImageView(testImage);
        doReturn(textDescription).when(stringUtils).generateTextForExpenseLabel(expense1, event.getParticipants().size());
        doReturn(testImage).when(imageUtils).loadImageFile("x_remove.png");
        doReturn(testImageView).when(imageUtils).generateImageView(testImage, 15);
        ListView<HBox> testListView = new ListView<>();

        sut.includingFilter(event, testListView, participant1.getName());
        ObservableList<HBox> items = testListView.getItems();
        assertEquals(1, items.size());
        HBox result = items.getFirst();
        HBox expense = (HBox) result.getChildren().get(0);
        Label expenseLabel = (Label) expense.getChildren().get(1);
        assertEquals(textDescription.getValue(), expenseLabel.textProperty().getValue());
    }

    @Test
    void fromFilterTest(){
        ObservableValue<String> textDescription = stringToObservable("Jane paid 20\u20ac for Drinks (Jane, Mike)");
        Image testImage = new WritableImage(1,1);
        ImageView testImageView = new ImageView(testImage);
        doReturn(textDescription).when(stringUtils).generateTextForExpenseLabel(expense1, event.getParticipants().size());
        doReturn(testImage).when(imageUtils).loadImageFile("x_remove.png");
        doReturn(testImageView).when(imageUtils).generateImageView(testImage, 15);
        ListView<HBox> testListView = new ListView<>();

        sut.fromFilter(event, testListView, participant1.getName());
        ObservableList<HBox> items = testListView.getItems();
        assertEquals(1, items.size());
        HBox result = items.getFirst();
        HBox expense = (HBox) result.getChildren().get(0);
        Label expenseLabel = (Label) expense.getChildren().get(1);
        assertEquals(textDescription.getValue(), expenseLabel.textProperty().getValue());
    }

    @Test
    void participantStringMultipleTest(){
        var result = sut.generateParticipantString(event);
        assertTrue(result.contains(participant1.getName()));
        assertTrue(result.contains(participant2.getName()));
        assertTrue(result.contains(participant3.getName()));
        assertTrue(result.contains(participant4.getName()));
    }

    @Test
    void participantStringNoneTest(){
        Event emptyEvent = new Event("TITLE!!", null);
        var result = sut.generateParticipantString(emptyEvent);
        assertTrue(result.contains("no participants"));
    }

    @Test
    void dropdownTest(){
        String name1 = participant1.getName();
        String name2 = participant2.getName();
        String name3 = participant3.getName();
        String name4 = participant4.getName();
        ObservableList<String> participantItems = FXCollections.observableArrayList(name1, name2, name3);
        sut.updateParticipantsDropdown(event, participantItems);
        ObservableList<String> expected = FXCollections.observableArrayList(name1, name2, name3, name4);
        assertEquals(expected, participantItems);
    }
}