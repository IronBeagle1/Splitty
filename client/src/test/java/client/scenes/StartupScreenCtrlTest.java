package client.scenes;

import client.utils.AppStateManager;
import client.utils.ImageUtils;
import client.utils.ServerUtils;
import client.utils.Translation;
import commons.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.*;
import java.util.List;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
public class StartupScreenCtrlTest{

    private TestStartupScreenCtrl sut;
    private TestServerUtils testServerUtils;
    private TestMainController testMainController;
    private LanguageIndicatorCtrl languageCtrl;
    private ImageUtils imageUtils;
    private AppStateManager manager;
    private Translation translation;
    private Stage currentStage;

    @BeforeAll
    static void testFXSetup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
    }

    @BeforeEach
    public void setup() {
        this.testServerUtils = new TestServerUtils();
        this.testMainController =  new TestMainController();
        this.imageUtils = mock(ImageUtils.class);
        this.languageCtrl = mock(LanguageIndicatorCtrl.class);
        this.manager = mock(AppStateManager.class);
        this.translation = mock(Translation.class);
        this.currentStage = mock(Stage.class);
        sut = new TestStartupScreenCtrl(this.testServerUtils, this.testMainController, translation,
                languageCtrl, manager, imageUtils);

        lenient().doReturn(stringToObservable("Binding!")).when(translation).getStringBinding(anyString());
        Image testImage = new WritableImage(1,1);
        lenient().doReturn(new ImageView(testImage)).when(imageUtils).generateImageView(anyString(), anyInt());

    }

    @Test
    public void testInitialization(){
        assertTrue(sut.labelBindings.isEmpty());
        assertTrue(sut.textBoxBindings.isEmpty());
        assertTrue(sut.buttonBindings.isEmpty());
        sut.initialize(null, null);
        assertEquals(4, sut.labelBindings.size());
        assertEquals(2, sut.textBoxBindings.size());
        assertEquals(3, sut.buttonBindings.size());
    }

    @Test
    public void testCreateEventEmptyTitle(){
        String title = "";
        sut.textBoxText = title;
        sut.createEvent();
        assertTrue(sut.joinEventCalls.isEmpty());
        assertTrue(testServerUtils.calls.isEmpty());
    }

    @Test
    public void testCreateEventSuccess(){
        String title = "title";
        sut.textBoxText = title;

        sut.createEvent();
        assertEquals(1, testServerUtils.calls.size());
        assertEquals( 2, testMainController.calls.size());
    }
    @Test
    public void testJoinEventInvalidLength(){
        String inviteCode = "invalid";
        sut.textBoxText = inviteCode;
        sut.joinEventClicked();
        assertEquals(0, testServerUtils.calls.size());
        assertEquals(0, testMainController.calls.size());
        assertFalse(sut.labelBindings.isEmpty());
        assertTrue(sut.labelBindings.contains("Startup.Label.InvalidCode"));
    }

    @Test
    public void testJoinEventInvalidCode(){
        //The only valid code is "aaaaaa"
        String inviteCode = "aaaaab";
        sut.textBoxText = inviteCode;
        sut.joinEventClicked();
        assertEquals(1, testServerUtils.calls.size());
        assertFalse(sut.labelBindings.isEmpty());
        assertTrue(sut.labelBindings.contains("Startup.Label.InvalidCode"));
    }

    @Test
    public void testJoinEventValidCode(){
        String inviteCode = "aaaaaa";
        sut.textBoxText = inviteCode;
        sut.joinEventClicked();
        assertEquals(1, testServerUtils.calls.size());
        assertFalse(testMainController.calls.isEmpty());
    }

    @Test
    public void testRemoveFromVbox(){
        HBox testBox = new HBox();
        sut.getHistoryNodes().add(testBox);
        sut.removeFromVBox(testBox);
        assertTrue(sut.getHistoryNodes().isEmpty());
    }

    @Test
    public void testRemoveFromHistoryIfExists(){
        HBox hBox = new HBox();
        sut.getEventsAndHBoxes().put("ABC123", hBox);
        assertFalse(sut.getEventsAndHBoxes().isEmpty());
        sut.removeFromHistoryIfExists("ABC123");
        assertTrue(sut.getEventsAndHBoxes().isEmpty());
    }

    @Test
    public void testOverfillHistory(){
        sut.addToHistory("A", "A");
        sut.addToHistory("B", "B");
        sut.addToHistory("C", "C");
        sut.addToHistory("D", "D");
        sut.addToHistory("E", "E");
        assertEquals(5, sut.getHistoryNodes().size());
        sut.addToHistory("Overfilled!", "Too much!");

        HBox removed = sut.getEventsAndHBoxes().get("A");
        assertFalse(sut.getHistoryNodes().contains(removed));
        assertEquals(5, sut.getHistoryNodes().size());
    }

    @Test
    public void testFindKeyByValue(){
        HBox testBox = new HBox();
        sut.getEventsAndHBoxes().put("A!", new HBox());
        sut.getEventsAndHBoxes().put("B!!!", testBox);
        sut.getEventsAndHBoxes().put("C!", new HBox());

        String result = sut.findKeyByValue(testBox);
        assertEquals("B!!!", result);
    }

    @Test
    public void moveToTopTest(){
        sut.addToHistory("A", "A");
        sut.addToHistory("B", "B");
        sut.addToHistory("C", "C");

        HBox hBoxA = sut.getEventsAndHBoxes().get("A");
        HBox hBoxC = sut.getEventsAndHBoxes().get("C");

        Node firstNode = sut.getHistoryNodes().getFirst();
        assertEquals(hBoxC, firstNode);
        sut.moveHistoryToTop("A");
        firstNode = sut.getHistoryNodes().getFirst();
        assertEquals(hBoxA, firstNode);
    }

    @Test
    public void switchToEventTest(){
        String eventId = "ABC568";
        sut.switchToEvent(eventId);
        System.out.println(testMainController.calls.toString());
        assertTrue(testMainController.calls.contains("join ABC568"));
        assertTrue(testMainController.calls.contains("switch"));
    }

    private class TestServerUtils extends ServerUtils{
        public List<String> calls = new LinkedList<>();
        public String validInvitationCode = "aaaaaa";
        @Override
        public Event getEvent(String inviteCode){
            calls.add("getEvent: " + inviteCode);
            //valid code
            if(inviteCode.equals(validInvitationCode)){
                return new Event();
            }
            //invalid code
            throw new jakarta.ws.rs.BadRequestException();
        }

        @Override
        public Event createEvent(String inviteCode){
            calls.add("createEvent: " + inviteCode);
            return new Event();
        }
    }

    private class TestMainController extends MainCtrl{
        public List<String> calls = new LinkedList<>();

        public TestMainController() {
            super(null, null, "URL", "EN", Locale.of("en", "GB"), currentStage);
        }

        @Override
        public void switchEvents(String eventCode){
            calls.add("join " + eventCode);
        }

        @Override
        public void switchScreens(Class<?> target){
            calls.add("switch");
        }
    }

    private class TestStartupScreenCtrl extends StartupScreenCtrl{
        public String textBoxText;
        public List<String> joinEventCalls = new ArrayList<>();
        public List<String> clearCalls = new ArrayList<>();

        public List<String> labelBindings = new ArrayList<>();
        public List<String> textBoxBindings = new ArrayList<>();
        public List<String> buttonBindings = new ArrayList<>();
        public List<Node> historyNodes = new ArrayList<>();
        /**
         * Constructor
         *
         * @param server      the ServerUtils instance
         * @param mainCtrl    the MainCtrl instance
         * @param translation the Translation instance
         * @param imageUtils  the ImageUtils instance
         */
        public TestStartupScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation,
                                     LanguageIndicatorCtrl languageCtrl, AppStateManager manager, ImageUtils imageUtils) {
            super(server, mainCtrl, translation, manager, languageCtrl, imageUtils);
        }

        @Override
        public String getTextBoxText(TextField textBox){
            return textBoxText;
        }

        @Override
        public void bindLabel(Label label,String str){
            labelBindings.add(str);
        }

        @Override
        public void bindTextBox(TextField textBox, String str){
            textBoxBindings.add(str);
        }

        @Override
        public void bindButton(Button button, String str){
            buttonBindings.add(str);
        }

        @Override
        public void clearField(TextField field){
            clearCalls.add("cleared");
        }

        @Override
        public List<Node> getHistoryNodes(){
            return historyNodes;
        }
    }
}
