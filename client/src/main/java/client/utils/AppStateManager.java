package client.utils;

import client.scenes.StartupScreenCtrl;
import com.google.inject.Inject;
import commons.Event;
import commons.dto.EventDeletedDTO;
import commons.dto.EventNameChangeDTO;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.*;
import java.util.concurrent.ExecutionException;


public class AppStateManager {
    private final WebSocketUtils socketUtils;
    private final ServerUtils server;
    private final LPUtils lpUtils;
    private StompSession.Subscription currentClientSubscription;
    private HashMap<Class<?>, ScreenInfo> screenInfoMap;
    private ScreenInfo currentlyOpen;
    private Event event;
    private StartupScreenCtrl startupScreen;
    private final Set<String> relevantEvents;
    private Runnable onCurrentEventDeletedCallback;

    /***
     * Constructor for the AppStateManager
     * @param socketUtils the WebSocketUtils to use
     * @param server the ServerUtils to use
     * @param lpUtils the Long Polling utils to use
     */
    @Inject
    public AppStateManager(WebSocketUtils socketUtils, ServerUtils server, LPUtils lpUtils) {
        this.socketUtils = socketUtils;
        this.server = server;
        this.currentClientSubscription = null;
        this.currentlyOpen = null;
        this.event = null;
        this.relevantEvents = new HashSet<>(5);
        this.onCurrentEventDeletedCallback = null;
        this.lpUtils = lpUtils;
    }

    /***
     * Runs any time the Event is updated from the backend
     * @param event the new Event
     */
    public void onEventUpdate(Event event){
        this.event = event;
        if(currentlyOpen!=null && currentlyOpen.shouldLiveRefresh())
            currentlyOpen.controller().refresh(this.event);
    }

    /***
     * Runs when the client changes the Event being observed
     * @param eventID the ID of the new Event
     */
    public void switchClientEvent(String eventID){
        Event newEvent = server.getEvent(eventID);
        onEventUpdate(newEvent);
        if(currentClientSubscription!=null) currentClientSubscription.unsubscribe();
        String url = "/topic/events/" + eventID;
        this.currentClientSubscription = socketUtils.registerForMessages(this::onEventUpdate,
                url, Event.class);
    }

    /***
     * Runs when the client backs out of an event
     */
    public void closeOpenedEvent(){
        event = null;
        if(currentClientSubscription!=null){
            try {
                this.currentClientSubscription.unsubscribe();
            } catch(IllegalStateException e){
                //This occurs if the server disconnected in the meantime, and is handled in WebSocketUtils and MainCtrl
            }
            this.currentClientSubscription = null;
        }
    }

    /***
     * Runs when switching screens in the client
     * @param target the Class of the new screen controller
     */
    public void onSwitchScreens(Class<?> target) {
        ScreenInfo switchedScreenInfo = screenInfoMap.get(target);
        switchedScreenInfo.controller().refresh(this.event);
        this.currentlyOpen = switchedScreenInfo;
    }

    /***
     * Sets the map of Classes to their instances (all under one interface)
     * @param screenInfoMap the map of Controller Classes to their ScreenInfo
     */
    public void setScreenInfoMap(HashMap<Class<?>, ScreenInfo> screenInfoMap){
        this.screenInfoMap = screenInfoMap;
    }

    /***
     * Register for messages of deletion and name changes
     * @param onConnectionErrorCallback the Runnable to execute if there is a connection error
     */
    public void subscribeToUpdates(Runnable onConnectionErrorCallback){
        try {
            socketUtils.startConnection(onConnectionErrorCallback);
            socketUtils.registerForMessages(this::onDeletion, "/topic/events/deletions", EventDeletedDTO.class);
            lpUtils.registerForNameUpdates(this::onNameChange);
        } catch (ExecutionException e){
            //The error callback is called within handleTransferError, so no extra handling is needed here
        }
    }

    /***
     * Sets the StartupScreenCtrl to use
     * @param startupScreen the StartupScreenCtrl to call with updates
     */
    public void setStartupScreen(StartupScreenCtrl startupScreen){
        this.startupScreen = startupScreen;
    }

    /***
     * Adds a subscribed to event
     * @param eventId the ID of the event
     */
    public void addSubscription(String eventId){
        relevantEvents.add(eventId);
    }
    /***
     * Removes a subscribed to event
     * @param eventId the ID of the event
     */
    public void removeSubscription(String eventId){
        relevantEvents.remove(eventId);
    }

    /***
     * Sets the runnable to execute when the currently open event is deleted
     * @param callback the new Runnable to execute
     */
    public void setOnCurrentEventDeletedCallback(Runnable callback){
        this.onCurrentEventDeletedCallback = callback;
    }

    /***
     * Runs on deletion of an event
     * @param dto A DTO containing the ID of the deleted event
     */
    public void onDeletion(EventDeletedDTO dto){
        String eventId = dto.getEventId();
        if(relevantEvents.contains(eventId)){
            startupScreen.removeFromHistoryIfExists(eventId);
            //Client currently has the relevant event open
            if(event != null && event.getId().equals(eventId)){
                onCurrentEventDeletedCallback.run();
            }
        }
    }
    /***
     * Runs on name change of an event
     * @param dto a DTO containing the event ID and new name
     */
    public void onNameChange(EventNameChangeDTO dto){
        String eventId = dto.getEventId();
        if(relevantEvents.contains(eventId)){
            String eventName = dto.getNewTitle();
            startupScreen.addToHistory(eventId, eventName);
        }
    }

    /***
     * Runs when the application stops, in order to shut down the Long Polling thread
     */
    public void onStop(){
        lpUtils.stopLP();
    }
}
