package server.websockets;

import commons.Event;
import commons.dto.EventDeletedDTO;
import commons.dto.EventNameChangeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.database.EventRepository;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate socketMessenger;
    private final EventRepository eventRepository;

    /***
     * Basic WebSocketService constructor
     * @param socketMessenger the template converting changes into WebSocket messages
     * @param eventRepository the EventRepository to fetch data from
     */
    @Autowired
    public WebSocketService(SimpMessagingTemplate socketMessenger,
                            EventRepository eventRepository) {
        this.socketMessenger = socketMessenger;
        this.eventRepository = eventRepository;
    }

    /***
     * Propagates changes to an Event to all WebSocket Clients
     * @param eventID the ID of the updated Event
     */
    public void propagateEventUpdate(String eventID){
        Event updatedEvent = eventRepository.findById(eventID)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        socketMessenger.convertAndSend(eventUpdateURL(eventID), updatedEvent);
        socketMessenger.convertAndSend("/topic/events/all", updatedEvent);
    }

    /***
     * Propagates an event name change to all listening clients
     * @param eventID the ID of the event changed
     * @param newTitle the new title of the event
     */
    public void propagateNameChange(String eventID, String newTitle){
        EventNameChangeDTO dto = new EventNameChangeDTO(eventID, newTitle);
        socketMessenger.convertAndSend("/topic/events/names", dto);
    }

    /***
     * Propagates a deletion of an event
     * @param eventID the ID of the event deleted
     */
    public void propagateDeletion(String eventID){
        EventDeletedDTO dto = new EventDeletedDTO(eventID);
        socketMessenger.convertAndSend("/topic/events/deletions", dto);
    }

    /***
     * Propagates event creation to admins
     * @param createdEvent the created event
     */
    public void propagateCreation(Event createdEvent) {
        socketMessenger.convertAndSend("/topic/events/creations", createdEvent);
    }

    /***
     * Generates the URL to send updates for a specific event
     * @param eventID the ID of the event clients subscribed to
     * @return the WebSocket URL to send updates to
     */
    private String eventUpdateURL(String eventID){
        return "/topic/events/" + eventID;
    }
}
