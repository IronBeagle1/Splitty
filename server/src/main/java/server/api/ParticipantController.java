package server.api;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.websockets.WebSocketService;

@RestController
@RequestMapping("/api/events")
public class ParticipantController {
    private final ParticipantService participantService;
    private final WebSocketService socketService;

    /**
     * Participant controller constructor
     *
     * @param participantService the eventService handling logic
     * @param socketService the WebSocketService propagating updates
     */
    @Autowired
    public ParticipantController(ParticipantService participantService,
                                 WebSocketService socketService) {
        this.participantService = participantService;
        this.socketService = socketService;
    }

    /**
     * Creates a new instance of a participant, which is then tied to an event
     * @param eventId identifies the id for which we want to add a participant
     * @param participant the participant to add
     * @return returns the status of the operation
     */
    @PostMapping("/{eventId}/participants")
    public ResponseEntity<Void> addParticipantToEvent(@PathVariable String eventId,
                                                      @RequestBody Participant participant) {
        participantService.addParticipantToEvent(eventId, participant);
        socketService.propagateEventUpdate(eventId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Update the details of a participant associated with a specific event
     * in a RESTful way
     * @param eventId the event the participant belongs to
     * @param participantId the participant whose details we want to change
     * @param participantData the details of the participant that we want to change
     * @return whether this worked (gives ok) or not
     */
    @PutMapping("/{eventId}/participants/{participantId}")
    public ResponseEntity<Participant> editParticipant(@PathVariable String eventId,
                                                       @PathVariable Long participantId,
                                                       @RequestBody Participant participantData) {
        Participant updatedParticipant = participantService.editParticipant(participantId,
                participantData);
        socketService.propagateEventUpdate(eventId);
        return ResponseEntity.ok(updatedParticipant);
    }

    /**
     * handles the DELETE request in a RESTful way
     * @param participantId the eliminated participant
     * @param eventId ID of the event the participant is part of
     * @return a response entity object with a HTTP 200 OK status
     */
    @DeleteMapping("/{eventId}/participants/{participantId}")
    public ResponseEntity<?> removeParticipant(@PathVariable String eventId,
                                               @PathVariable Long participantId) {
        participantService.removeParticipant(eventId, participantId);
        socketService.propagateEventUpdate(eventId);
        return ResponseEntity.ok().build();
    }
}