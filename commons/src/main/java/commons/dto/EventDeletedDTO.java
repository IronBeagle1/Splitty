package commons.dto;

public class EventDeletedDTO {
    private String eventId;

    /***
     * Standard DTO constructor taking the deleted event's ID
     * @param eventId the ID of the deleted Event
     */
    public EventDeletedDTO(String eventId) {
        this.eventId = eventId;
    }

    /***
     * Constructor for Jackson
     */
    @SuppressWarnings("unused")
    public EventDeletedDTO(){
    }

    /***
     * Provides the deleted event's ID
     * @return the ID of the deleted event
     */
    public String getEventId() {
        return eventId;
    }
}
