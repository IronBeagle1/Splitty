package commons.dto;

public class EventNameChangeDTO {
    private String eventId;
    private String newTitle;

    /***
     * Standard DTO constructor taking the modified event's ID and name
     * @param eventId the ID of the Event
     * @param newTitle the new title of the Event
     */
    public EventNameChangeDTO(String eventId, String newTitle) {
        this.eventId = eventId;
        this.newTitle = newTitle;
    }

    /***
     * Constructor for Jackson
     */
    @SuppressWarnings("unused")
    public EventNameChangeDTO(){
    }

    /***
     * Provides the event's ID
     * @return the ID of the event
     */
    public String getEventId() {
        return eventId;
    }

    /***
     * Provides the event's new title
     * @return the new title of the event
     */
    public String getNewTitle() {
        return newTitle;
    }
}
