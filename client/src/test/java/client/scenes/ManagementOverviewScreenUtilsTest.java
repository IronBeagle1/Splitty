package client.scenes;

import client.utils.ManagementOverviewUtils;
import client.utils.ServerUtils;
import client.utils.Translation;
import client.utils.WebSocketUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.dto.EventDeletedDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ManagementOverviewScreenUtilsTest {

    private Translation translation;
    private ServerUtils server;
    private WebSocketUtils socketUtils;
    private ManagementOverviewUtils utils;
    private Event e1;
    private Event e2;
    private Event e3;
    private StringProperty ascending;
    private StringProperty descending;
    private StringProperty title;
    private StringProperty creationDate;
    private StringProperty lastActivity;

    @BeforeEach
    public void setup() {
        translation = mock(Translation.class);
        server = mock(ServerUtils.class);
        socketUtils = mock(WebSocketUtils.class);
        utils = new ManagementOverviewUtils(translation, server, socketUtils);
        e1 = new Event("Party", new Date(0));
        e2 = new Event("Holiday", new Date(5));
        e3 = new Event("party", new Date(2));
        ascending = new SimpleStringProperty("Ascending");
        descending = new SimpleStringProperty("Descending");
        title = new SimpleStringProperty("Title");
        creationDate = new SimpleStringProperty("Creation Date");
        lastActivity = new SimpleStringProperty("Last Activity");
        utils.setEvents(FXCollections.observableArrayList(e1, e2, e3));
        utils.bindAscending(ascending);
        utils.bindDescending(descending);
        utils.bindTitle(title);
        utils.bindCreationDate(creationDate);
        utils.bindLastActivity(lastActivity);
    }

    @Test
    public void creationTest(){
        when(server.retrieveAllEvents()).thenReturn(List.of(e1, e2));
        utils.retrieveEvents();
        ObservableList<Event> events = utils.getEvents();
        assertEquals(2, events.size());

        utils.onCreateEvent(e3);
        assertEquals(3, events.size());
    }

    @Test
    public void editingTest(){
        String eventId = e2.getId();
        Event newE2 = new Event("Different title!", new Date(123));
        when(server.retrieveAllEvents()).thenReturn(List.of(e1, e2, e3));
        utils.retrieveEvents();
        ObservableList<Event> events = utils.getEvents();

        utils.editEvent(newE2, eventId);
        assertFalse(events.contains(e2));
        assertTrue(events.contains(newE2));
        assertEquals(events.size(), 3);
    }

    @Test
    public void deletionTest(){
        String eventId = e2.getId();
        when(server.retrieveAllEvents()).thenReturn(List.of(e1, e2, e3));
        utils.retrieveEvents();
        ObservableList<Event> events = utils.getEvents();

        utils.onDeleteEvent(new EventDeletedDTO(eventId));
        assertFalse(events.contains(e2));
        assertEquals(events.size(), 2);
    }

    @Test
    public void subscriptionTest(){
        utils.subscribeToUpdates();
        verify(socketUtils, times(3)).registerForMessages(any(), any(), any());
    }

    /**
     * Tests whether the elements of the return value are bound to the values injected by Translation.
     */
    @Test
    public void setOrderTypes() {
        when(translation.getStringBinding("ManagementOverview.ComboBox.title"))
                .thenReturn(title);
        when(translation.getStringBinding("ManagementOverview.ComboBox.creationDate"))
                .thenReturn(creationDate);
        when(translation.getStringBinding("ManagementOverview.ComboBox.lastActivity"))
                .thenReturn(lastActivity);
        ObservableList<StringProperty> actual = utils.setOrderTypes();
        assertEquals("Title", actual.get(0).getValue());
        assertEquals("Creation Date", actual.get(1).getValue());
        assertEquals("Last Activity", actual.get(2).getValue());
        assertTrue(actual.get(0).isBound());
        assertTrue(actual.get(1).isBound());
        assertTrue(actual.get(2).isBound());
    }

    /**
     * Events from ServerUtils should be retrieved, and sorted correctly.
     */
    @Test
    public void retrieveEvents() {
        when(server.retrieveAllEvents()).thenReturn(List.of(e1, e2, e3));
        ObservableList<Event> expected = FXCollections.observableArrayList(e2, e1, e3);
        ObservableList<Event> actual = utils.retrieveEvents();
        assertEquals(expected, actual);
        assertEquals(expected, utils.getEvents());
    }

    /**
     * Ascending and descending fields should be bound correctly by Translation.
     * The initial state of order type should be ascending.
     */
    @Test
    public void bindSortButton() {
        when(translation.getStringBinding("ManagementOverview.Button.ascending"))
                .thenReturn(new SimpleStringProperty("Ascending"));
        when(translation.getStringBinding("ManagementOverview.Button.descending"))
                .thenReturn(new SimpleStringProperty("Descending"));
        StringProperty order = utils.bindSortButton();
        assertEquals("Descending", utils.getDescending().getValue());
        assertEquals("Ascending", utils.getAscending().getValue());
        assertEquals(utils.getAscending().getValue(), order.getValue());
        assertTrue(order.isBound());
        assertEquals(utils.getOrder(), order);
    }

    /**
     * When button is set at ascending order, then the event list should be sorted by title in descending order.
     * Make sure that the order field is bound to the new state.
     */
    @Test
    public void sortEventsByTitleInDescendingOrder() {
        utils.bindOrder(descending);
        utils.sortEvents(title);
        ObservableList<Event> actual = utils.getEvents();
        ObservableList<Event> expected = FXCollections.observableArrayList(e1, e3, e2);
        assertEquals(expected, actual);
    }

    /**
     * When the button is at descending order, then the event list should be sorted by title in ascending order.
     * Make sure that the order field is bound to the new state.
     */
    @Test
    public void sortEventsByTitleInAscendingOrder() {
        utils.bindOrder(ascending);
        utils.sortEvents(title);
        ObservableList<Event> actual = utils.getEvents();
        ObservableList<Event> expected = FXCollections.observableArrayList(e2, e1, e3);
        assertEquals(expected, actual);
    }

    /**
     * Sorts events by creation date in ascending order
     */
    @Test
    public void sortEventsByCreationDateInAscendingOrder() {
        utils.bindOrder(ascending);
        utils.sortEvents(creationDate);
        assertEquals(FXCollections.observableArrayList(e1, e3, e2), utils.getEvents());
    }

    /**
     * Sorts events by creation date in descending order
     */
    @Test
    public void sortEventsByCreationDateInDescendingOrder() {
        utils.bindOrder(descending);
        utils.sortEvents(creationDate);
        assertEquals(FXCollections.observableArrayList(e2, e3, e1), utils.getEvents());
    }

    /**
     * Sorts events by last activity in ascending order
     */
    @Test
    public void sortEventsByLastActivityInAscendingOrder() {
        Date date1 = new Date(10000);
        Date date2 = new Date(20000);
        Date date3 = new Date(30000);
        e1.setLastActivity(date1);
        e2.setLastActivity(date2);
        e3.setLastActivity(date3);
        utils.bindOrder(ascending);
        utils.sortEvents(lastActivity);
        assertEquals(FXCollections.observableArrayList(e1, e2, e3), utils.getEvents());
    }

    /**
     * Sorts events by last activity in descending order
     */
    @Test
    public void sortEventsByLastActivityInDescendingOrder() {
        Date date1 = new Date(10000);
        Date date2 = new Date(20000);
        Date date3 = new Date(30000);
        e1.setLastActivity(date1);
        e2.setLastActivity(date2);
        e3.setLastActivity(date3);
        utils.bindOrder(descending);
        utils.sortEvents(lastActivity);
        assertEquals(FXCollections.observableArrayList(e3, e2, e1), utils.getEvents());
    }

    /**
     * Events are ordered in the same order that has been set
     */
    @Test
    public void sortEventsSameOrder() {
        utils.bindOrder(ascending);
        utils.sortEventsSameOrder(title);
        assertEquals(FXCollections.observableArrayList(e2, e1, e3), utils.getEvents());
    }

    /**
     * If order is ascending then events will be sorted in descending order
     */
    @Test
    public void sortEventsFromAscendingToDescending() {
        utils.bindOrder(ascending);
        utils.sortEventsOtherOrder(title);
        assertEquals(FXCollections.observableArrayList(e1, e3, e2), utils.getEvents());
        assertEquals(utils.getOrder().getValue(), descending.getValue());
        assertTrue(utils.getOrder().isBound());
    }

    /**
     * If order is descending then events will be sorted in ascending order
     */
    @Test
    public void sortEventsFromDescendingToAscending() {
        utils.bindOrder(descending);
        utils.sortEventsOtherOrder(title);
        assertEquals(FXCollections.observableArrayList(e2, e1, e3), utils.getEvents());
        assertEquals(utils.getOrder().getValue(), ascending.getValue());
        assertTrue(utils.getOrder().isBound());
    }

    /**
     * Testing getter and setters for events field.
     */
    @Test
    public void getAndSetEvents() {
        Event e = new Event("Party", null);
        ObservableList<Event> expected = FXCollections.observableArrayList(e);
        utils.setEvents(expected);
        assertEquals(expected, utils.getEvents());
    }

    /**
     * Testing getter and binder for ascending field.
     */
    @Test
    public void getAndBindAscending() {
        SimpleStringProperty ascending = new SimpleStringProperty("New");
        utils.bindAscending(ascending);
        assertEquals(ascending.getValue(), utils.getAscending().getValue());
        assertTrue(utils.getAscending().isBound());
    }

    /**
     * Testing getting and binding the descending field.
     */
    @Test
    public void getAndBindDescending() {
        SimpleStringProperty descending = new SimpleStringProperty("New");
        utils.bindDescending(descending);
        assertEquals(descending.getValue(), utils.getDescending().getValue());
        assertTrue(utils.getDescending().isBound());
    }

    /**
     * Testing getting and binding the order field.
     */
    @Test
    public void getAndBindOrder() {
        SimpleStringProperty order = new SimpleStringProperty("New");
        utils.bindOrder(order);
        assertEquals("New", utils.getOrder().getValue());
        assertTrue(utils.getOrder().isBound());
    }

    /**
     * Participants of the given event should be returned as an observable list.
     */
    @Test
    public void initializeParticipants() {
        Event e = new Event("Party", null);
        Participant p = new Participant("Jack");
        e.addParticipant(p);
        ObservableList<Participant> actual = utils.initializeParticipantsList(e);
        ObservableList<Participant> expected = FXCollections.observableArrayList(p);
        assertEquals(actual, expected);
    }

    @Test
    public void initializeExpenses() {
        Event event = new Event("Party", null);
        Expense expense = new Expense("Drinks", 100, null, null);
        event.addExpense(expense);
        ObservableList<Expense> actual = utils.initializeExpenseList(event);
        ObservableList<Expense> expected = FXCollections.observableArrayList(expense);
        assertEquals(expected, actual);
    }
}
