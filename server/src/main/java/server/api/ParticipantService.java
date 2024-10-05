package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;


import java.util.Set;

@Service
public class ParticipantService {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * constructor
     * autowired - automatically inject instances of the parameters when creaitng an EventService
     * @param eventRepository used for handling events
     * @param participantRepository used for handling participants
     * @param expenseRepository used for handling expenses
     */
    @Autowired
    public ParticipantService(EventRepository eventRepository,
                              ParticipantRepository participantRepository,
                              ExpenseRepository expenseRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
    }

    /**
     * Add a new participant to an event, add a new participant to the repository
     * @param participant the Participant to add
     * @param eventId the event to which we want to add a participant
     */
    public void addParticipantToEvent(String eventId, Participant participant) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        Participant dbParticipant = new Participant(participant.getName());
        dbParticipant.setLegalName(participant.getLegalName());
        dbParticipant.setIban(participant.getIban());
        dbParticipant.setBic(participant.getBic());
        dbParticipant.setEmail(participant.getEmail());
        event.addParticipant(dbParticipant);
        eventRepository.save(event);
    }

    /**
     * remove a participant, make sure both the event and participants exist or else
     * throw an exception
     * @param eventId the id of the event of the participant
     * @param participantId the participant we want to remove
     */
    public void removeParticipant(String eventId, Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        var expenses = expenseRepository.findByOwedTo(participant);
        for (Expense expense : expenses)
            event.removeExpense(expense);
        Set<Expense> expensesInEvent = event.getExpenses();
        for(Expense expense: expensesInEvent) {
            Set<Participant> participantsInExpense = expense.getParticipantsInExpense();
            participantsInExpense.remove(participant);
            expenseRepository.save(expense);
        }
        event.removeParticipant(participant);
        eventRepository.save(event);
    }

    /**
     * edit the details of a participant that is in an event
     * @param participantId the participant whose details we want to change
     * @param participant the details of the participant
     * @return the participants modified details are now saved in the database
     */

    public Participant editParticipant(Long participantId, Participant participant) {
        Participant dbParticipant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        dbParticipant.setName(participant.getName());
        dbParticipant.setLegalName(participant.getLegalName());
        dbParticipant.setIban(participant.getIban());
        dbParticipant.setBic(participant.getBic());
        dbParticipant.setEmail(participant.getEmail());
        return participantRepository.save(participant);
    }


}
