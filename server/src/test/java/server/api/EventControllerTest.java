package server.api;

import commons.Event;
import commons.Participant;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;
import server.websockets.WebSocketService;


import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    EventService eventService;
    @Mock
    private WebSocketService socketService;
    @InjectMocks
    EventController controller;
    @Captor
    ArgumentCaptor<String> nameCaptor;

    Answer<?> stubCreate;

    @BeforeEach
    void setup() {
        EventRepository repository = new TestEventRepository();
        controller.setRepository(repository);

        stubCreate = (Answer<Event>) invocation -> {
            Date currentDate = new Date();
            Event event = new Event(invocation.getArgument(0), currentDate);
            repository.save(event);
            return event;
        };
    }

    @Test
    void addEventTitleIsNull() {
        String nullString = null;
        ResponseEntity<Event> response = controller.add(nullString);
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addEventIsNull() {
        Event nullEvent = null;
        ResponseEntity<Event> response = controller.add(nullEvent);
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addEventEmptyTitle() {
        ResponseEntity<Event> response = controller.add("");
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addValidEvent() {
        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
        String title = "Party";
        ResponseEntity<Event> response = controller.add(title);
        assertNotNull(response.getBody());
        assertEquals(title, response.getBody().getTitle());
    }

    @Test
    void joinNonExistingEvent() {
        String invitationCode = "unkown entity";
        ResponseEntity<Event> response = controller.join(invitationCode);
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void joinExistingEvent() {
        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
        Event persistedEvent = controller.add("Party").getBody();
        assertNotNull(persistedEvent);
        ResponseEntity<Event> response = controller.join(persistedEvent.getId());
        Event responseEvent = response.getBody();
        assertNotNull(responseEvent);
        assertEquals(persistedEvent.getTitle(), responseEvent.getTitle());
        assertEquals(persistedEvent.getId(), responseEvent.getId());
    }

    @Test
    void getMultipleEvents() {
        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
        Event expectedEvent1 = controller.add("Party").getBody();
        Event expectedEvent2 = controller.add("Holiday").getBody();
        assertNotNull(expectedEvent1);
        assertNotNull(expectedEvent2);
        List<Event> expectedEvents = List.of(expectedEvent1, expectedEvent2);
        List<Event> retrievedEvents = controller.all().getBody();
        assertEquals(expectedEvents, retrievedEvents);
    }

    @Test
    void removeNonExistingEvent() {
        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
        controller.add("Party");
        String invitationCode = "Fake invitation code";
        ResponseEntity<Event> response = controller.remove(invitationCode);
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void removeExistingEvent() {
        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
        Event persistedEvent = controller.add("Party").getBody();
        assertNotNull(persistedEvent);
        ResponseEntity<Event> response = controller.remove(persistedEvent.getId());
        assertEquals(persistedEvent, response.getBody());
    }

    @Test
    void orderUnorderedListByTitle() {
        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
        Event persistedEvent1 = controller.add("B").getBody();
        Event persistedEvent2 = controller.add("A").getBody();
        ResponseEntity<List<Event>> response = controller.orderByTitle();
        assertNotNull(persistedEvent1);
        assertNotNull(persistedEvent2);
        List<Event> orderedList = List.of(persistedEvent2, persistedEvent1);
        assertEquals(orderedList, response.getBody());
    }

    @Test
    void orderUnorderedListByCreationDate() {
        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
        Event persistedEvent1 = controller.add("First").getBody();
        Event persistedEvent2 = controller.add("Second").getBody();
        assertNotNull(persistedEvent1);
        assertNotNull(persistedEvent2);
        ResponseEntity<List<Event>> response = controller.orderByCreationDate();
        List<Event> orderedList = List.of(persistedEvent1, persistedEvent2);
        assertEquals(orderedList, response.getBody());
    }

//    @Test
//    void orderUnorderedListByLastActivityStandard(){
//        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
//        Event event1 = controller.add("event1").getBody();
//        Event event2 = controller.add("event2").getBody();
//        Event event3 = controller.add("event3").getBody();
//        ResponseEntity<List<Event>> response = controller.orderByLastActivity();
//        assert event3 != null;
//        assert event2 != null;
//        assert event1 != null;
//        List<Event> list = List.of(event3, event2, event1);
//        //TODO: Make these tests deterministic!
//        assertEquals(list, response.getBody());
//    }

    /**
     * tests last activity with methods
     */
    @Test
    void orderUnorderedListByLastActivitySetters(){
        when(eventService.createEvent(anyString())).thenAnswer(stubCreate);
        Event event1 = controller.add("event1").getBody();
        Event event2 = controller.add("event2").getBody();
        assert event2 != null;
        assert event1 != null;
        event2.setTitle("newTitle");
        event1.addParticipant(new Participant());
        ResponseEntity<List<Event>> response = controller.orderByLastActivity();
        List<Event> list = List.of(event1, event2);
        assertEquals(list, response.getBody());
    }

//    @Test
//    @Transactional
//    void editTitleExisting(){
//        EventRepository erepository = mock(EventRepository.class);
//        ParticipantRepository prepository= mock(ParticipantRepository.class);
//        EventService service = new EventService(erepository, prepository);
//        EventController eventController = new EventController(service, erepository);
//        Event event = new Event("Title", null);
//        erepository.save(event);
//        String id = event.getId();
//        when(service.editTitle(id, eq("New Title"))).thenReturn(event);
//        ResponseEntity<Event> test = eventController.editTitle(id, "New Title");
//        assertEquals(200, test.getStatusCodeValue());
//        assertEquals("New Title", test.getBody().getTitle());
//        verify(service).editTitle(id, eq("New Title"));
//    }

    /**
     * tests trying to edit the title of an event which does not exist
     */
    @Test
    void editTitleNotExisting(){
        Event event = new Event("Title", null);
        when(eventService.editTitle(anyString(), anyString())).thenThrow(new EntityNotFoundException(":{"));
        assertThrows(EntityNotFoundException.class, () -> {
            controller.editTitle(event.getId(), "New Title");});
        verify(eventService).editTitle(event.getId(), "New Title");
    }

//    @Test
//    void addParticipantModification(){
//        Event event = new Event("test", null);
//        doNothing().when(eventService).addParticipantToEvent(anyString(), anyString());
//        controller.addParticipantToEvent(event.getId(), "Andy");
//        verify(eventService).addParticipantToEvent(eq(event.getId()), eq("Andy"));
//        Set<Participant> participants = event.getParticipants();
//        assertEquals(1, participants.size());
//    }

}
