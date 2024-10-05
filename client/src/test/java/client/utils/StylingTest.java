package client.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class StylingTest {
    private Styling styling;
    private Button button;
    private Label label;
    private TextField textField;
    @BeforeAll
    static void testFXSetup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
    }
    @BeforeEach
    void setUp() {
        styling = new Styling();
        button = new Button();
        label = new Label();
        textField = new TextField();
    }
    @Test
    void applyStyling() {
        assertFalse(button.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(button, "backgroundLight");
        assertTrue(button.getStyleClass().contains("backgroundLight"));
        assertFalse(textField.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(textField, "backgroundLight");
        assertTrue(textField.getStyleClass().contains("backgroundLight"));
        assertFalse(label.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(label, "backgroundLight");
        assertTrue(label.getStyleClass().contains("backgroundLight"));

        styling.applyStyling(button, "backgroundLight");
        ObservableList<String> style = button.getStyleClass();
        int count = 0;
        for (int i = 0; i < style.size(); i++) {
            if (style.get(i).equals("backgroundLight")) {
                count ++;
            }
        }
        assertEquals(1, count);

        styling.applyStyling(button, "style1");
        styling.applyStyling(button, "style2");
        assertTrue(button.getStyleClass().contains("style1"));
        assertTrue(button.getStyleClass().contains("style2"));
    }

    @Test
    void changeStyling() {
        assertFalse(button.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(button, "backgroundLight");
        assertTrue(button.getStyleClass().contains("backgroundLight"));
        styling.changeStyling(button, "backgroundLight", "backgroundDark");
        assertFalse(button.getStyleClass().contains("backgroundLight"));
        assertTrue(button.getStyleClass().contains("backgroundDark"));

        assertFalse(label.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(label, "backgroundLight");
        assertTrue(label.getStyleClass().contains("backgroundLight"));
        styling.changeStyling(label, "backgroundLight", "backgroundDark");
        assertFalse(label.getStyleClass().contains("backgroundLight"));
        assertTrue(label.getStyleClass().contains("backgroundDark"));

        assertFalse(textField.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(textField, "backgroundLight");
        assertTrue(textField.getStyleClass().contains("backgroundLight"));
        styling.changeStyling(textField, "backgroundLight", "backgroundDark");
        assertFalse(textField.getStyleClass().contains("backgroundLight"));
        assertTrue(textField.getStyleClass().contains("backgroundDark"));
    }

    @Test
    void removeStyling() {
        assertFalse(button.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(button, "backgroundLight");
        assertTrue(button.getStyleClass().contains("backgroundLight"));
        styling.removeStyling(button, "backgroundLight");
        assertFalse(button.getStyleClass().contains("backgroundLight"));
        styling.removeStyling(button, "backgroundLight");
        assertFalse(button.getStyleClass().contains("backgroundLight"));

        assertFalse(textField.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(textField, "backgroundLight");
        assertTrue(textField.getStyleClass().contains("backgroundLight"));
        styling.removeStyling(textField, "backgroundLight");
        assertFalse(textField.getStyleClass().contains("backgroundLight"));
        styling.removeStyling(textField, "backgroundLight");
        assertFalse(textField.getStyleClass().contains("backgroundLight"));

        assertFalse(label.getStyleClass().contains("backgroundLight"));
        styling.applyStyling(label, "backgroundLight");
        assertTrue(label.getStyleClass().contains("backgroundLight"));
        styling.removeStyling(label, "backgroundLight");
        assertFalse(label.getStyleClass().contains("backgroundLight"));
        styling.removeStyling(label, "backgroundLight");
        assertFalse(label.getStyleClass().contains("backgroundLight"));
    }
}