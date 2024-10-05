package server.api;
import commons.Event;
import commons.dto.EventNameChangeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.websockets.WebSocketService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final LPController lpController;
    private EventRepository repository;
    private final WebSocketService socketService;
    /**
     * Constructor of EventController.
     *
     * @param eventService  the EventService used for backend handling of events
     * @param repository    the EventRepository storing Events
     * @param socketService the WebSocketService propagating updates
     * @param lpController  the Long Polling controller to use to propagate name changes
     */
    @Autowired
    public EventController(EventService eventService, EventRepository repository,
                           WebSocketService socketService, LPController lpController) {
        this.eventService = eventService;
        this.repository = repository;
        this.socketService = socketService;
        this.lpController = lpController;
    }

    /**
     * Updates the title of the event
     * @param eventId identifies the event for which we want to update the title
     * @param newTitle the new title
     * @return returns "ok" if the operation succeeds
     */
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> editTitle(@PathVariable String eventId,
                                           @RequestBody String newTitle){
        Event updatedEvent = eventService.editTitle(eventId, newTitle);
        socketService.propagateEventUpdate(eventId);
        EventNameChangeDTO dto = new EventNameChangeDTO(eventId, updatedEvent.getTitle());
        lpController.propagateToAllListeners(dto);
        return ResponseEntity.ok(updatedEvent);
    }

    /**
     * Orders the events by last modification
     * @return
     */
    @GetMapping("ordered/lastActivity")
    ResponseEntity<List<Event>> orderByLastActivity(){
        List<Event> events = all().getBody();
        assert events != null;
        Collections.sort(events, Comparator.comparing(Event::getLastActivity).reversed());
        return ResponseEntity.ok(events);
    }

    /**
     * Endpoint for adding an event. Title needs to be a valid, and any other field gets ignored.
     * @param eventName - the name of the Event to be created
     * @return The added event iff the title is valid. Else return a bad request.
     */
    @PostMapping("/")
    ResponseEntity<Event> add(@RequestBody String eventName) {
        if(eventName==null || eventName.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Event createdEvent = eventService.createEvent(eventName);
        socketService.propagateCreation(createdEvent);
        return ResponseEntity.ok(createdEvent);
    }

    /**
     * Endpoint for joining an event.
     * @param id ID of the event
     * @return ResponseEntity with the event iff the event can be found. Else return a bad request.
     */
    @GetMapping("/{id}")
    ResponseEntity<Event> join(@PathVariable String id) {
        Optional<Event> event = repository.findById(id);
        if(event.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(event.get());
    }

    /**
     * Endpoint for retrieving all events.
     * @return All events from the database.
     */
    @GetMapping("/all")
    ResponseEntity<List<Event>> all() {
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     * Endpoint for removing an event from the database.
     * @param id ID of the to be removed event.
     * @return OK iff event was found in the database. Else a bad request.
     */
    @DeleteMapping("/remove/{id}")
    ResponseEntity<Event> remove(@PathVariable String id) {
        Optional<Event> event = repository.findById(id);
        if(event.isEmpty())
            return ResponseEntity.badRequest().build();
        repository.deleteById(id);
        socketService.propagateDeletion(id);
        return ResponseEntity.ok(event.get());
    }

    /**
     * endpoint for deleting all the events
     * @return a string telling us whether we successfully deleted all events or that there was no events
     * to be deleted
     */
    @DeleteMapping("/delete/all")
    ResponseEntity<String> deleteAll(){
        List<Event> allEvents = repository.findAll();
        List<String> allIds = allEvents.stream().map(Event::getId).toList();
        if(allEvents.isEmpty()){
            return ResponseEntity.ok("No events do be deleted");
        }
        repository.deleteAll();
        allIds.forEach(socketService::propagateDeletion);
        return ResponseEntity.ok("Successfully deleted all the events");
    }

    /**
     * Endpoint for giving an ordered list of events by title.
     * @return A list of events ordered by title
     */
    @GetMapping("ordered/title")
    ResponseEntity<List<Event>> orderByTitle() {
        List<Event> events = all().getBody();
        events.sort(Comparator.comparing(Event::getTitle));
        return ResponseEntity.ok(events);
    }


    /**
     * Endopoint for giving all events ordered by creation date.
     * @return List of all events ordered by creation date
     */
    @GetMapping("ordered/date")
    ResponseEntity<List<Event>> orderByCreationDate() {
        List<Event> events = all().getBody();
        events.sort(Comparator.comparing(Event::getCreationDate));
        return ResponseEntity.ok(events);
    }
    /***
     * For the purpose of placing a TestEventRepository in the tests
     * @param repository - the TestEventRepository
     */
    public void setRepository(EventRepository repository) {
        this.repository = repository;
    }

    /**
     * Endpoint for adding an event.
     * @param event - the event to be added
     * @return The added event.
     */
    @PutMapping("/")
    ResponseEntity<Event> add(@RequestBody Event event) {
        if(event==null || event.getTitle().isEmpty() || event.getTitle()==null){
            return ResponseEntity.badRequest().build();
        }
        Event createdEvent = eventService.saveEvent(event);
        socketService.propagateCreation(createdEvent);
        return ResponseEntity.ok(createdEvent);
    }

}