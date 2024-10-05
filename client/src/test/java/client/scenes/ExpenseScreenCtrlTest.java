package client.scenes;

import client.utils.ImageUtils;
import client.utils.ServerUtils;
import client.utils.Styling;
import client.utils.Translation;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.time.LocalDate;
import java.util.*;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class ExpenseScreenCtrlTest {
    @InjectMocks
    ExpenseScreenCtrl sut;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    Translation translation;
    @Mock
    ImageUtils imageUtils;
    Event testEvent;
    Participant participant1;
    Participant participant2;
    Expense expense1;
    Expense expense2;
    Tag tag1;
    Tag tag2;

    @BeforeEach
    void setup(){
        testEvent = new Event("Title", new Date(42));
        tag1 = new Tag("New Tech", "#31f3f2");
        tag2 = new Tag("Old Tech", "#ec2151");
        testEvent.addTag(tag1);
        testEvent.addTag(tag2);
        participant1 = new Participant("Vox");
        participant2 = new Participant("Alastor");
        testEvent.addParticipant(participant1);
        testEvent.addParticipant(participant2);
        expense1 = new Expense("TVs", 500, null, participant1);
        expense1.setExpenseTag(tag1);
        expense2 = new Expense("Radios", 250, null, participant2);
        expense2.setExpenseTag(tag2);
        expense1.addParticipantToExpense(participant1);
        expense1.addParticipantToExpense(participant2);
        expense2.addParticipantToExpense(participant1);
        testEvent.addExpense(expense1);
        testEvent.addExpense(expense2);
        sut.setCurrentEvent(testEvent);
        lenient().doReturn(stringToObservable("Binding!")).when(translation).getStringBinding(anyString());
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
    public void getParticipantListTest() {
        sut.setCurrentEvent(testEvent);
        ObservableList<String> result = sut.getParticipantList();
        assertEquals(FXCollections.observableArrayList("Vox", "Alastor"), result);
    }
    @Test
    public void bindLabelsTest() {
        List<Label> labels = new LinkedList<>();
        Label addEditExpense = new Label();
        Label paidBy = new Label();
        Label purpose = new Label();
        Label amount = new Label();
        Label date = new Label();
        Label splitMethod = new Label();
        Label expenseType = new Label();
        labels.add(addEditExpense);
        labels.add(paidBy);
        labels.add(purpose);
        labels.add(amount);
        labels.add(date);
        labels.add(splitMethod);
        labels.add(expenseType);
        sut.bindLabels(addEditExpense, paidBy, purpose, amount,
            date, splitMethod, expenseType);
        for(Label label: labels) {
            assertEquals("Binding!", label.textProperty().getValue());
        }
    }

    @Test
    public void bindComboBoxesTest() {
        ComboBox<String> comboBox = new ComboBox<>();
        sut.bindComboBoxes(comboBox);
        assertEquals("Binding!", comboBox.promptTextProperty().getValue());
    }

    @Test
    public void bindTextFieldsTest() {
        List<TextField> list = new LinkedList<>();
        TextField textField1 = new TextField();
        TextField textField2 = new TextField();
        sut.bindTextFields(textField1, textField2);
        list.add(textField1);
        list.add(textField2);
        for(TextField textField: list)
            assertEquals("Binding!", textField.getPromptText());
    }

    @Test
    public void bindDatePickersTest() {
        DatePicker datePicker = new DatePicker();
        sut.bindDatePickers(datePicker);
        assertEquals("Binding!", datePicker.getPromptText());
    }

    @Test
    public void bindButtonsTest() {
        Button button1  =new Button();
        Button button2 = new Button();
        sut.bindButtons(button1, button2);
        assertEquals("Binding!", button1.getText());
        assertEquals("Binding!", button2.getText());
    }

    @Test
    public void findDefaultTagTestNoDefault() {
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        Tag result = sut.findDefaultTag(tags);
        assertNull(result);
    }

    @Test
    public void findDefaultTagTest() {
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        Tag expected = new Tag("default", "#31f3f2");
        tags.add(expected);
        Tag result = sut.findDefaultTag(tags);
        assertEquals(expected, result);
    }

    @Test
    public void resetPaidBy() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setValue("value");
        assertFalse(comboBox.getValue().isEmpty());
        sut.resetPaidBy(comboBox);
        assertTrue(comboBox.getValue().isEmpty());
    }

    @Test
    public void resetAmountTest() {
        TextField textField = new TextField("Value");
        assertFalse(textField.getText().isEmpty());
        sut.resetAmount(textField);
        assertTrue(textField.getText().isEmpty());
    }
    @Test
    public void resetExpensePurposeTest() {
        TextField textField = new TextField("Value");
        assertFalse(textField.getText().isEmpty());
        sut.resetPurpose(textField);
        assertTrue(textField.getText().isEmpty());
    }

    @Test
    public void resetSplitMethodTest() {
        CheckBox checkBox1 = new CheckBox();
        CheckBox checkBox2 = new CheckBox();
        CheckBox checkBoxInVBox = new CheckBox();
        VBox vBox = new VBox(checkBoxInVBox);
        assertFalse(vBox.getChildren().isEmpty());
        sut.resetSplitMethod(checkBox1, checkBox2, vBox);
        assertTrue(vBox.getChildren().isEmpty());

    }

    @Test
    public void createNewExpenseTest() {
        ComboBox<String> choosePayer = new ComboBox<>();
        choosePayer.setValue("Vox");
        TextField expensePurpose = new TextField("TVs");
        TextField sum = new TextField("5");
        DatePicker datePicker = new DatePicker(null);
        ComboBox<String> currency = new ComboBox<>();
        currency.setValue("EUR");
        Expense expected = new Expense("TVs", 500, null, new Participant("Vox"));
        expected.setCurrency("EUR");
        Expense result = sut.createNewExpense(choosePayer, expensePurpose, sum, currency,
            datePicker);
        assertEquals(expected, result);
    }
}