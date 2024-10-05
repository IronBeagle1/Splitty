/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.AppStateManager;
import client.utils.ScreenInfo;
import client.utils.Translation;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
public class MainCtrlTest {
    Translation translation;
    AppStateManager manager;
    String serverURL;
    String language;
    @Mock
    StartupScreenCtrl startupScreenCtrl;
    @Mock
    EventScreenCtrl eventScreenCtrl;
    @Mock
    ExpenseScreenCtrl expenseScreenCtrl;
    @Mock
    ParticipantScreenCtrl participantScreenCtrl;
    @Mock
    EditTitleCtrl editTitleCtrl;
    @Mock
    ManagementOverviewPasswordCtrl managementOverviewPasswordCtrl;
    @Mock
    ManagementOverviewScreenCtrl managementOverviewScreenCtrl;
    @Mock
    SettleDebtsScreenCtrl settleDebtsScreenCtrl;
    @Mock
    DeleteEventsScreenCtrl deleteEventsScreenCtrl;
    @Mock
    ParticipantListScreenCtrl participantListScreenCtrl;
    @Mock
    AddTagCtrl addTagCtrl;
    @Mock
    EmailInviteCtrl emailInviteCtrl;
    @Mock
    TransferMoneyCtrl transferMoneyCtrl;
    @Mock
    StatisticsScreenCtrl statisticsScreenCtrl;
    @Mock
    GenerateLanguageTemplateCtrl generateTemplateScreenCtrl;
    @Mock
    Stage primaryStage;
    @Mock
    Stage currentStage;
    private Locale defaultLocale;
    SimpleStringProperty titleProperty;
    private MainCtrl sut;
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
        translation = mock(Translation.class);
        manager = mock(AppStateManager.class);
        serverURL = "URL";
        language = "en_GB";
        defaultLocale = Locale.of("en", "GB");
        sut = new MainCtrl(translation, manager, serverURL, language, defaultLocale, currentStage);
        titleProperty = new SimpleStringProperty();
        lenient().when(primaryStage.titleProperty()).thenReturn(titleProperty);
    }

    /***
     * Runs the sut.initialize method with proper arguments, necessary for almost all the other tests, but should be run at different times ine ach
     */
    void runInitialization(){
        Pair<StartupScreenCtrl, Parent> startUp = new Pair<>(startupScreenCtrl, new AnchorPane());
        Pair<EventScreenCtrl, Parent> eventScreen = new Pair<>(eventScreenCtrl, new AnchorPane());
        Pair<ExpenseScreenCtrl, Parent> expenseScreen = new Pair<>(expenseScreenCtrl, new AnchorPane());
        Pair<ParticipantScreenCtrl, Parent> participantScreen = new Pair<>(participantScreenCtrl, new AnchorPane());
        Pair<EditTitleCtrl, Parent> editTitle = new Pair<>(editTitleCtrl, new AnchorPane());
        Pair<ManagementOverviewPasswordCtrl, Parent> managementOverviewPassword = new Pair<>(managementOverviewPasswordCtrl, new AnchorPane());
        Pair<ManagementOverviewScreenCtrl, Parent> managementOverviewScreen = new Pair<>(managementOverviewScreenCtrl, new AnchorPane());
        Pair<SettleDebtsScreenCtrl, Parent> settleDebtsScreen = new Pair<>(settleDebtsScreenCtrl, new AnchorPane());
        Pair<DeleteEventsScreenCtrl, Parent> deleteEventsScreen = new Pair<>(deleteEventsScreenCtrl, new AnchorPane());
        Pair<ParticipantListScreenCtrl, Parent> participantListScreen = new Pair<>(participantListScreenCtrl, new AnchorPane());
        Pair<AddTagCtrl, Parent> addTagScreen = new Pair<>(addTagCtrl, new AnchorPane());
        Pair<EmailInviteCtrl, Parent> emailInviteScreen = new Pair<>(emailInviteCtrl, new AnchorPane());
        Pair<TransferMoneyCtrl, Parent> transferMoney = new Pair<>(transferMoneyCtrl, new AnchorPane());
        Pair<StatisticsScreenCtrl, Parent> statisticsScreen = new Pair<>(statisticsScreenCtrl, new AnchorPane());
        Pair<GenerateLanguageTemplateCtrl, Parent> generateTemplateScreen = new Pair<>(generateTemplateScreenCtrl, new AnchorPane());

        sut.initialize(primaryStage, startUp, eventScreen, expenseScreen, participantScreen, editTitle,
                managementOverviewPassword, managementOverviewScreen, settleDebtsScreen, deleteEventsScreen,
                participantListScreen, transferMoney, addTagScreen,emailInviteScreen, statisticsScreen, generateTemplateScreen);
    }

    @Test
    void testGetEventFilterNotNull(){
        assertNotNull(sut.getEventHandlerForEventScreen());
    }
    @Test
    void testGetEventFilterMainScreen() {
        assertNotNull(sut.getEventHandlerForMainScreen());
    }
    @Test
    void initializeTest(){
        HashMap<Class<?>, ScreenInfo> screenMap = new HashMap<>();
        Answer<?> stub = invocation -> {
            screenMap.putAll(invocation.getArgument(0));
            return null;
        };
        doAnswer(stub).when(manager).setScreenInfoMap(any());
        runInitialization();

        assertEquals(11, screenMap.size());

        /*
        All parents are initialized as new AnchorPanes, so we do need to get this one bit of data from the actual result
         */
        var scene = screenMap.get(EventScreenCtrl.class).scene();
        ScreenInfo expectedScreenInfo = new ScreenInfo(eventScreenCtrl, true, scene, "Event.Window.title");
        assertTrue(screenMap.containsValue(expectedScreenInfo));
    }

    @Test
    void onStartTest(){
        runInitialization();
        lenient().doReturn(stringToObservable("Binding!")).when(translation).getStringBinding(anyString());
        sut.onStart();
        verify(translation).changeLanguage(defaultLocale);
        verify(primaryStage).show();
    }

    @Test
    void connectionErrorPopupTest() {
        String binding = "Binding!";
        ButtonType buttonTypeReconnect = new ButtonType("Reconnect", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeExit = new ButtonType("Exit", ButtonBar.ButtonData.NO);
        runInitialization();
        lenient().doReturn(stringToObservable(binding)).when(translation).getStringBinding(anyString());
        lenient().doReturn(stringToObservable(binding)).when(translation).getStringSubstitutionBinding(anyString(), any());
        Alert testAlert = mock(Alert.class);
        ObservableList<ButtonType> buttonList = FXCollections.observableArrayList();
        doReturn(buttonList).when(testAlert).getButtonTypes();
        sut.generateConnectionErrorPopup(testAlert, serverURL, buttonTypeReconnect, buttonTypeExit);

        verify(testAlert).setTitle(binding);
        verify(testAlert).setHeaderText(binding);
        verify(testAlert).setContentText(binding);
        assertTrue(buttonList.contains(buttonTypeReconnect));
        assertTrue(buttonList.contains(buttonTypeExit));
    }

    @Test
    void eventScreenHandlerTest() {
        runInitialization();
        var result = sut.getEventHandlerForEventScreen();
        result.handle(null);

        List<KeyCode> codeList = new ArrayList<>();
        codeList.add(KeyCode.A);
        codeList.add(KeyCode.T);
        codeList.add(KeyCode.S);
        codeList.add(KeyCode.W);
        codeList.add(KeyCode.Q);
        codeList.add(KeyCode.P);
        codeList.add(KeyCode.I);
        codeList.add(KeyCode.B);
        codeList.add(KeyCode.F);
        codeList.add(KeyCode.D);
        codeList.add(KeyCode.G);

        doReturn(stringToObservable("Binding!")).when(translation).getStringBinding(anyString());
        Scene eventScene = sut.getEventScene();
        for(KeyCode code: codeList){
            EventHandler<? super KeyEvent> onKeyPressed = eventScene.getOnKeyPressed();
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ANY, code.getChar(), code.getChar(), code, false, true, false, false);
            onKeyPressed.handle(keyEvent);
        }
        verify(eventScreenCtrl).sendTestEmail();
        verify(eventScreenCtrl).switchToStatistics();
        verify(eventScreenCtrl).addExpense();
        verify(eventScreenCtrl).addParticipants();
        verify(eventScreenCtrl).switchToInviteEmail();
        verify(eventScreenCtrl).switchToMainScreen();
        verify(eventScreenCtrl).switchToAddTag();
        verify(eventScreenCtrl).transferMoney();
        verify(eventScreenCtrl).settleDebts();
    }

    @Test
    void mainScreenHandlerTest() {
        runInitialization();
        var result = sut.getEventHandlerForMainScreen();

        List<KeyCode> codeList = new ArrayList<>();
        codeList.add(KeyCode.A);
        codeList.add(KeyCode.E);

        doReturn(stringToObservable("Binding!")).when(translation).getStringBinding(anyString());
        for(KeyCode code: codeList){
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ANY, code.getChar(), code.getChar(), code, false, true, false, false);
            result.handle(keyEvent);
        }
        verify(startupScreenCtrl).joinMostRecentEvent();
    }

    @Test
    void swtichScreensTest(){
        runInitialization();
        ObservableValue<String> titleText = stringToObservable("Title!!!");
        doReturn(titleText).when(translation).getStringBinding(anyString());
        sut.switchScreens(SettleDebtsScreenCtrl.class);
        verify(manager).onSwitchScreens(SettleDebtsScreenCtrl.class);
        verify(primaryStage).titleProperty();
    }

}