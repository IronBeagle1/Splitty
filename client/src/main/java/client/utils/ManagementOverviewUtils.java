package client.utils;

import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.dto.EventDeletedDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class ManagementOverviewUtils {

    private final Translation translation;
    private final ServerUtils server;
    private final ObservableList<Event> events = FXCollections.observableArrayList();
    private final Map<String, Event> eventLookup = new HashMap<>();
    private final SimpleStringProperty ascending = new SimpleStringProperty();
    private final SimpleStringProperty descending = new SimpleStringProperty();
    private final StringProperty order = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty creationDate = new SimpleStringProperty();
    private final StringProperty lastActivity = new SimpleStringProperty();
    private final WebSocketUtils socketUtils;

    /**
     * Constructor
     * @param translation Translation to use
     * @param server    ServerUtils to use
     * @param socketUtils WebSocketUtils to use
     */
    @Inject
    public ManagementOverviewUtils(Translation translation, ServerUtils server, WebSocketUtils socketUtils) {
        this.translation = translation;
        this.server = server;
        this.socketUtils = socketUtils;
    }

    /**
     * Configure the translation for all order types.
     * @return an ObservableList which contains all the order types, each bound to their own translation.
     */
    public ObservableList<StringProperty> setOrderTypes() {
        title.bind(translation.getStringBinding("ManagementOverview.ComboBox.title"));
        creationDate.bind(translation.getStringBinding("ManagementOverview.ComboBox.creationDate"));
        lastActivity.bind(translation.getStringBinding("ManagementOverview.ComboBox.lastActivity"));
        return FXCollections.observableArrayList(title, creationDate, lastActivity);
    }

    /**
     * Retrieve events from the database and sorts them.
     * @return ObservableList of all events sorted by their title in ascending order.
     */
    public ObservableList<Event> retrieveEvents() {
        var allEvents = server.retrieveAllEvents();
        events.setAll(allEvents);
        events.sort(Comparator.comparing(event -> event.getTitle().toLowerCase()));
        allEvents.forEach(event -> eventLookup.put(event.getId(), event));
        return events;
    }

    /***
     * Initiates subscriptions to relevant endpoints
     */
    public void subscribeToUpdates(){
        socketUtils.registerForMessages(this::onCreateEvent, "/topic/events/creations", Event.class);
        socketUtils.registerForMessages(this::onEditEvent, "/topic/events/all", Event.class);
        socketUtils.registerForMessages(this::onDeleteEvent, "/topic/events/deletions", EventDeletedDTO.class);
    }

    /***
     * Ran when an event is created
     * @param event the created event
     */
    public void onCreateEvent(Event event){
        events.add(event);
        eventLookup.put(event.getId(), event);
    }

    /***
     * Ran when an event is edited
     * @param event the new edited event
     */
    public void onEditEvent(Event event){
        editEvent(event, event.getId());
    }

    /***
     * Executes event editing
     * @param event the new edited event
     * @param eventId the ID of the event
     */
    public void editEvent(Event event, String eventId){
        /*
         * The workaround exists for cases where there is only one event, and it is edited/removed
         * This does not work well with the current implementation of the ListView and Selection methods
         * Thus, a second Event has to be added, and then immediately removed after
         */
        Event workaroundEvent = new Event();
        Event currentEvent = eventLookup.get(eventId);
        int index = events.indexOf(currentEvent);
        if(index != -1){
            events.add(workaroundEvent);
            events.set(index, event);
            eventLookup.put(eventId, event);
            events.remove(workaroundEvent);
        }
    }

    /***
     * Ran when an event is deleted
     * @param dto an EventDeletedDTO containing the ID of the deleted event
     */
    public void onDeleteEvent(EventDeletedDTO dto){
        /*
         * The workaround exists for cases where there is only one event, and it is edited/removed
         * This does not work well with the current implementation of the ListView and Selection methods
         * Thus, a second Event has to be added, and then immediately removed after
         */
        Event workaroundEvent = new Event();
        String eventId = dto.getEventId();
        Event currentEvent = eventLookup.get(eventId);
        int index = events.indexOf(currentEvent);
        if(index != -1){
            events.add(workaroundEvent);
            events.set(index, new Event());
            events.remove(index);
            eventLookup.remove(eventId);
            events.remove(workaroundEvent);
        }
    }

    /***
     * Checks if an event with the given ID already exists
     * @param eventId the ID of the event to check
     * @return true if the event already exists, false otherwise
     */
    public boolean checkIfDuplicate(String eventId){
        return eventLookup.containsKey(eventId);
    }

    /**
     * Binds the sort button to its two states, translated by Translation.
     * @return The property bound to ascending.
     */
    public StringProperty bindSortButton() {
        ascending.bind(translation.getStringBinding("ManagementOverview.Button.ascending"));
        descending.bind(translation.getStringBinding("ManagementOverview.Button.descending"));
        order.bind(ascending);
        return order;
    }

    /**
     * Sort events according to the comparator and the current order
     * @param comparator Comparator to sort events with
     */
    public void sortEventsByComparator(Comparator<Event> comparator) {
        if(order.getValue().equals(ascending.getValue()))
            events.sort(comparator);
        else if(order.getValue().equals(descending.getValue()))
            events.sort(comparator.reversed());
    }

    /**
     * Sorts events
     * @param property Property that determines how to sort the events
     */
    public void sortEvents(StringProperty property) {
        if(property.getValue().equals(title.getValue()))
            sortEventsByComparator(Comparator.comparing(event -> event.getTitle().toLowerCase()));
        else if(property.getValue().equals(creationDate.getValue()))
            sortEventsByComparator(Comparator.comparing(Event::getCreationDate));
        else if(property.getValue().equals(lastActivity.getValue()))
            sortEventsByComparator(Comparator.comparing(Event::getLastActivity));
    }

    /**
     * Sort events by the same order
     * @param property Property to sort by
     */
    public void sortEventsSameOrder(StringProperty property) {
        sortEvents(property);
    }

    /**
     * Sort events in ascending order if the current order descending, and vice versa
     * @param property Property to sort by
     */
    public void sortEventsOtherOrder(StringProperty property) {
        order.bind(order.getValue().equals(ascending.getValue()) ? descending : ascending);
        sortEvents(property);
    }

    /**
     * Initializes the list of participants.
     * @param event Selected event
     * @return Observable list of the selects event's participant
     */
    public ObservableList<Participant> initializeParticipantsList(Event event) {
        if(event != null && event.getParticipants() != null) return FXCollections.observableArrayList(event.getParticipants());
        else return FXCollections.observableArrayList();
    }

    /**
     * Initializes the list of expenses
     * @param event Selected event
     * @return Observable list of the event's expenses
     */
    public ObservableList<Expense> initializeExpenseList(Event event) {
        if(event != null && event.getExpenses() != null) return FXCollections.observableArrayList(event.getExpenses());
        else return FXCollections.observableArrayList();
    }

    /**
     * Get the list of events for the management overview
     * @return the list of events
     */
    public ObservableList<Event> getEvents() {
        return events;
    }

    /**
     * Get ascending
     * @return ascending string property
     */
    public SimpleStringProperty getAscending() {
        return ascending;
    }

    /**
     * Get descending
     * @return Descending string property
     */
    public SimpleStringProperty getDescending() {
        return descending;
    }

    /**
     * Bind ascending
     * @param ascending String property to bind ascending to
     */
    public void bindAscending(StringProperty ascending) {
        this.ascending.bind(ascending);
    }

    /**
     * Bind descending
     * @param descending String property to bind descending to
     */
    public void bindDescending(StringProperty descending) {
        this.descending.bind(descending);
    }

    /**
     * Get order
     * @return String property of the order that events are sorted by
     */
    public StringProperty getOrder() {
        return order;
    }

    /**
     * Bind order
     * @param order String property to bind order to
     */
    public void bindOrder(StringProperty order) {
        this.order.bind(order);
    }

    /**
     * Set events
     * @param events New events
     */
    public void setEvents(ObservableList<Event> events) {
        this.events.setAll(events);
    }

    /**
     * Bind title
     * @param title String property to bind title to
     */
    public void bindTitle(StringProperty title) {
        this.title.bind(title);
    }

    /**
     * Bind creation date
     * @param creationDate String property to bind creation date to
     */
    public void bindCreationDate(StringProperty creationDate) {
        this.creationDate.bind(creationDate);
    }

    /**
     * Bind last activity
     * @param lastActivity String property to bind last activity to
     */
    public void bindLastActivity(StringProperty lastActivity) {
        this.lastActivity.bind(lastActivity);
    }
}
