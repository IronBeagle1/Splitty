package client.utils;

import client.scenes.StartupScreenCtrl;
import commons.Event;
import commons.dto.EventDeletedDTO;
import commons.dto.EventNameChangeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppStateManagerTest {
    @InjectMocks
    AppStateManager sut;
    @Mock
    WebSocketUtils webSocketUtils;
    @Mock
    ServerUtils serverUtils;
    TestRefreshable refreshable;
    Event event1;

    @BeforeEach
    void setUp() {
        refreshable = new TestRefreshable();
        HashMap<Class<?>, ScreenInfo> screenMap = new HashMap<>();
        screenMap.put(Void.class, new ScreenInfo(refreshable, true, null, null));
        sut.setScreenInfoMap(screenMap);
        event1 = new Event("Title!", null);
    }

    /***
     * Updating the Event causes Refreshable to refresh
     */
    @Test
    void onEventUpdateLiveRefresh() {
        sut.onSwitchScreens(Void.class);
        assertNull(refreshable.getCurrentEvent());
        sut.onEventUpdate(event1);
        assertEquals(event1, refreshable.getCurrentEvent());
    }

    /***
     * Updating the Event causes a non-live Refreshable to not refresh
     */
    @Test
    void onEventUpdateDoesNotLiveRefresh() {
        HashMap<Class<?>, ScreenInfo> screenMap = new HashMap<>();
        screenMap.put(Void.class, new ScreenInfo(refreshable, false, null, null));
        sut.setScreenInfoMap(screenMap);

        sut.onSwitchScreens(Void.class);
        assertNull(refreshable.getCurrentEvent());
        sut.onEventUpdate(event1);
        assertNull(refreshable.getCurrentEvent());
    }

    /***
     * Ensures that switching observed Events refreshes data
     */
    @Test
    void switchClientEventRefreshesEvent() {
        String eventID = "ABC123";
        String url = "/topic/events/" + eventID;

        when(serverUtils.getEvent(eventID)).thenReturn(event1);
        when(webSocketUtils.registerForMessages(any(), eq(url), eq(Event.class))).thenReturn(null);

        sut.onSwitchScreens(Void.class);
        assertNull(refreshable.getCurrentEvent());
        assertEquals(1, refreshable.getEventsRefreshed().size());

        sut.switchClientEvent(eventID);
        assertEquals(event1, refreshable.getCurrentEvent());
        assertEquals(2, refreshable.getEventsRefreshed().size());
    }

    /***
     * Ensures the Consumer sent to the WebSocketUtils refreshes the Event data
     */
    @Test
    void consumerUpdatesRefreshable() {
        String eventID = "ABC123";
        String url = "/topic/events/" + eventID;
        final Consumer[] eventConsumer = new Consumer[]{null};

        when(serverUtils.getEvent(eventID)).thenReturn(event1);
        Answer<?> answer = (Answer<StompSession.Subscription>) invocation -> {
            eventConsumer[0] = invocation.getArgument(0);
            return null;
        };
        when(webSocketUtils.registerForMessages(any(), eq(url), eq(Event.class))).then(answer);

        sut.onSwitchScreens(Void.class);
        assertEquals(1, refreshable.getEventsRefreshed().size());
        sut.switchClientEvent(eventID);
        assertEquals(2, refreshable.getEventsRefreshed().size());
        Consumer<Event> consumer = eventConsumer[0];

        Event eventSentOverSocket = new Event("Different Title!", null);
        assertEquals(event1, refreshable.getCurrentEvent());
        consumer.accept(eventSentOverSocket);
        assertEquals(eventSentOverSocket, refreshable.getCurrentEvent());
        assertEquals(3, refreshable.getEventsRefreshed().size());
    }

    /***
     * onSwitchScreens() properly updates any Refreshable
     */
    @Test
    void onSwitchScreens() {
        HashMap<Class<?>, ScreenInfo> screenMap = new HashMap<>();
        screenMap.put(Void.class, new ScreenInfo(refreshable, false, null, null));
        sut.setScreenInfoMap(screenMap);

        sut.onEventUpdate(event1);
        assertNull(refreshable.getCurrentEvent());
        sut.onSwitchScreens(Void.class);
        assertEquals(event1, refreshable.getCurrentEvent());
    }

    @Test
    void deletionRemovesHistoryAndRunsCallback() {
        String eventID = event1.getId();
        String url = "/topic/events/" + eventID;

        when(serverUtils.getEvent(eventID)).thenReturn(event1);
        when(webSocketUtils.registerForMessages(any(), eq(url), eq(Event.class))).thenReturn(null);
        sut.switchClientEvent(eventID);
        sut.addSubscription(eventID);

        StartupScreenCtrl testCtrl = mock(StartupScreenCtrl.class);
        sut.setStartupScreen(testCtrl);

        final boolean[] callbackCalledBack = {false};
        sut.setOnCurrentEventDeletedCallback(()-> callbackCalledBack[0] = true);

        EventDeletedDTO dto = new EventDeletedDTO(eventID);
        sut.onDeletion(dto);
        assertTrue(callbackCalledBack[0]);
        verify(testCtrl).removeFromHistoryIfExists(eventID);
    }

    @Test
    void renamingRenamesHistory() {
        String eventID = "F7DS14";
        String newTitle = "Best Party Ever";
        sut.addSubscription(eventID);

        StartupScreenCtrl testCtrl = mock(StartupScreenCtrl.class);
        sut.setStartupScreen(testCtrl);

        EventNameChangeDTO dto = new EventNameChangeDTO(eventID, newTitle);
        sut.onNameChange(dto);
        verify(testCtrl).addToHistory(eventID, newTitle);
    }

    @Test
    void closeOpenedActuallyCloses(){
        String eventID = "ABC123";
        String url = "/topic/events/" + eventID;

        when(serverUtils.getEvent(eventID)).thenReturn(event1);
        when(webSocketUtils.registerForMessages(any(), eq(url), eq(Event.class))).thenReturn(new StompSession.Subscription() {
            @Override
            public String getSubscriptionId() {
                return null;
            }

            @Override
            public StompHeaders getSubscriptionHeaders() {
                return null;
            }

            @Override
            public void unsubscribe() {
            }

            @Override
            public void unsubscribe(StompHeaders headers) {
            }

            @Override
            public String getReceiptId() {
                return null;
            }

            @Override
            public void addReceiptTask(Runnable task) {
            }

            @Override
            public void addReceiptTask(Consumer<StompHeaders> task) {
            }

            @Override
            public void addReceiptLostTask(Runnable task) {
            }
        });
        sut.switchClientEvent(eventID);

        final boolean[] callbackCalledBack = {false};
        sut.setOnCurrentEventDeletedCallback(()-> callbackCalledBack[0] = true);
        sut.closeOpenedEvent();
        sut.addSubscription(eventID);

        StartupScreenCtrl testCtrl = mock(StartupScreenCtrl.class);
        sut.setStartupScreen(testCtrl);

        sut.onDeletion(new EventDeletedDTO(eventID));
        assertFalse(callbackCalledBack[0]);
        verify(testCtrl).removeFromHistoryIfExists(eventID);
    }
}