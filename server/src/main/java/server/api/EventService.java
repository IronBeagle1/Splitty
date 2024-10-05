package server.api;
import commons.Event;
import commons.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.EventRepository;

import java.util.Date;


@Service
public class EventService {
    private final EventRepository eventRepository;

    /**
     * constructor
     * autowired - automatically inject instances of the parameters when creating an EventService
     * @param eventRepository used for handling events
     */
    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /***
     * Creates a new Event based on a given title
     * @param title - the title of the new Event
     * @return a full, persisted Event object
     */
    public Event createEvent(String title){
        Date currentDate = new Date();
        Event event = new Event(title, currentDate);
        Tag defaultTag = new Tag("default", "#808080");
        Tag food = new Tag("food", "#008000");
        Tag entranceFees = new Tag("entrance fees", "#0000FF");
        Tag travel = new Tag("travel", "#FF0000");
        Tag moneyTransfer = new Tag("money transfer", "#000000");
        event.addTag(defaultTag);
        event.addTag(food);
        event.addTag(entranceFees);
        event.addTag(travel);
        event.addTag(moneyTransfer);
        return eventRepository.save(event);
    }

    /***
     * Saves an Event object directly to the repository
     * @param event the Event to save
     * @return the JPA generated Event saved
     */
    public Event saveEvent(Event event){
        return eventRepository.save(event);
    }

    /**
     * edit the title of an event
     * @param eventId the event whose title we want to edit
     * @param newTitle the new title
     * @return the new title of the event is saved in the database
     */
    public Event editTitle (String eventId, String newTitle){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        event.setTitle(newTitle);
        return eventRepository.save(event);
    }
}
