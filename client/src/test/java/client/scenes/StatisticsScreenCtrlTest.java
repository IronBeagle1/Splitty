package client.scenes;

import client.utils.FormattingUtils;
import client.utils.ImageUtils;
import client.utils.Translation;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class StatisticsScreenCtrlTest {
    @InjectMocks
    StatisticsScreenCtrl sut;
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shareTableContainsCells() {
        TableView<Participant> result = sut.generateShareTable(testEvent);
        assertEquals(2, result.getColumns().size());

        LinkedList<ObservableValue> stringObservablesInTable = new LinkedList<>();
        result.getColumns().forEach( column -> {
            TableColumn.CellDataFeatures cellDataParticipant1 = new TableColumn.CellDataFeatures(result, column, participant1);
            TableColumn.CellDataFeatures cellDataParticipant2 = new TableColumn.CellDataFeatures(result, column, participant2);

            stringObservablesInTable.add(column.getCellValueFactory().call(cellDataParticipant1));
            stringObservablesInTable.add(column.getCellValueFactory().call(cellDataParticipant2));
        });

        LinkedList<String> stringsInTable = new LinkedList<>();
        stringObservablesInTable.forEach(string -> stringsInTable.add((String) string.getValue()));

        assertTrue(stringsInTable.contains(participant1.getName()));
        assertTrue(stringsInTable.contains(participant2.getName()));
        String share1 = FormattingUtils.getFormattedPrice(500);
        String share2 = FormattingUtils.getFormattedPrice(250);
        assertTrue(stringsInTable.contains(share1));
        assertTrue(stringsInTable.contains(share2));
    }

    @Test
    void shareTableEmpty() {
        Event eventWithoutExpensesOrParticipants = new Event("Title", null);
        TableView<Participant> result = sut.generateShareTable(eventWithoutExpensesOrParticipants);
        assertEquals(2, result.getColumns().size());
    }

    @Test
    void pieChartDataCorrect(){
        PieChart result = sut.generatePieChart(testEvent);

        PieChart.Data dataTag1 = null;
        PieChart.Data dataTag2 = null;
        ObservableList<PieChart.Data> chartData = result.getData();
        assertEquals(2, chartData.size());
        for(PieChart.Data data: chartData){
            String dataName = data.getName();
            if(dataName.contains(tag1.getTagName())) dataTag1 = data;
            else if(dataName.contains(tag2.getTagName())) dataTag2 = data;
            else fail();
        }
        assertNotNull(dataTag1);
        assertNotNull(dataTag2);

        assertEquals(expense1.getPriceInCents(), (int) dataTag1.getPieValue());
        assertEquals(expense2.getPriceInCents(), (int) dataTag2.getPieValue());
    }

    @Test
    void pieChartEmpty(){
        Event emptyEvent = new Event("Event!", null);
        PieChart result = sut.generatePieChart(emptyEvent);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void segmentLabelGeneration(){
        PieChart testPieChart = new PieChart();
        PieChart.Data data = new PieChart.Data("Cameras!", 250);
        testPieChart.getData().add(data);

        StringExpression result = sut.generateSegmentLabel(data, 250 + 500);
        String resultString = result.getValue();
        String expectedResult = "Cameras!\n2.5\u20ac (33.3%)";
        assertEquals(expectedResult, resultString);
    }

    @Test
    void populationTest(){
        AnchorPane parent = new AnchorPane();
        sut.populateParentPane(parent, testEvent);
        ObservableList<Node> children = parent.getChildren();
        assertEquals(2, children.size());
    }

    @Test
    void bindingTest(){
        List<Label> labels = new LinkedList<>();
        Label stats = new Label();
        Label cost = new Label();
        Label pie = new Label();
        Label share = new Label();
        labels.add(stats);
        labels.add(cost);
        labels.add(pie);
        labels.add(share);

        sut.bindLabels(stats, cost, pie, share);

        for(Label label: labels){
            assertEquals("Binding!", label.textProperty().getValue());
        }
    }

    @Test
    void sumLabelTest(){
        Label resultLabel = new Label();
        sut.setTotalSumOfExpenses(resultLabel, testEvent);
        var resultText = resultLabel.getText();
        String expectedText = FormattingUtils.getFormattedPrice(250 + 500);
        assertEquals(expectedText, resultText);
    }

    @Test
    void generateImageTest(){
        Button testButton = new Button();
        Image testImage = new WritableImage(1,1);
        ImageView testImageView = new ImageView(testImage);
        when(imageUtils.generateImageView("goBack.png", 15)).thenReturn(testImageView);
        sut.addGeneratedImages(testButton);
        assertEquals(testImageView, testButton.getGraphic());
    }
}