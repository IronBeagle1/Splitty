package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.database.TagRepository;
import server.exceptions.TagNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final TagRepository tagRepository;

    /**
     *
     * @param expenseRepository the repository containing the expenses
     * @param eventRepository the repository containing the events
     * @param participantRepository the repository containing the participants
     * @param tagRepository the repository containing the tags
     */
    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, EventRepository eventRepository,
                          ParticipantRepository participantRepository, TagRepository tagRepository) {
        this.expenseRepository = expenseRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * The method assigns an expense to an event, identified by its id
     * @param eventId the id by which we find the event
     * @param expense the specific expense for that event
     */
    @Transactional
    public void addExpense(String eventId, Expense expense) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if(expense.getOwedTo() != null){
            long extractedParticipantId = expense.getOwedTo().getId();
            Participant participant = participantRepository.findById(extractedParticipantId)
                    .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
            expense.setOwedTo(participant);
        }
        Set<Participant> participants = new HashSet<>();
        if (expense.getParticipantsInExpense() != null) {
            for (Participant participant : expense.getParticipantsInExpense()) {
                long participantId = participant.getId();
                Participant fetchedParticipant = participantRepository.findById(participantId)
                    .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
                participants.add(fetchedParticipant);
            }
        }
        if(expense.getExpenseTag() != null){
            long extractedTagId = expense.getExpenseTag().getId();
            Tag fetchedTag = tagRepository.findById(extractedTagId)
                    .orElseThrow(() -> new TagNotFoundException(extractedTagId));
            expense.setExpenseTag(fetchedTag);
        }
        expense.setParticipantToExpense(participants);
        event.addExpense(expense);
        eventRepository.save(event);
    }

    /**
     * The method return the list of all the expenses for a specified event
     * @param eventId the id by which we find the event
     * @return a set of all the expenses of the specific event
     */
    public Set<Expense> getAllExpenses(String eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isEmpty()){
            return null;
        } else {
            Event event = eventOptional.get();
            return event.getExpenses();
        }

    }

    /**
     * Deletes an Expense
     * @param eventId the id of the corresponding event
     * @param id the id of the expense to be deleted
     */
    public void deleteExpense(String eventId, Long id) {
        Expense expense = expenseRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        event.removeExpense(expense);
        eventRepository.save(event);
    }

    /**
     * Edit the expense
     * @param id ID of the expense
     * @param newExpense The new expense
     * @return The updated expense
     */
    public Expense editExpense(long id, Expense newExpense) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        if(newExpense.getOwedTo() != null){
            long extractedParticipantId = newExpense.getOwedTo().getId();
            Participant participant = participantRepository.findById(extractedParticipantId)
                    .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
            expense.setOwedTo(participant);
        }
        Set<Participant> participants = new HashSet<>();
        if (newExpense.getParticipantsInExpense() != null) {
            for (Participant participant : newExpense.getParticipantsInExpense()) {
                long participantId = participant.getId();
                Participant fetchedParticipant = participantRepository.findById(participantId)
                    .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
                participants.add(fetchedParticipant);
            }
        }
        Tag newTag = null;
        if(newExpense.getExpenseTag() != null){
            long extractedTagId = newExpense.getExpenseTag().getId();
            newTag = tagRepository.findById(extractedTagId)
                    .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        }
        expense.setDate(newExpense.getDate());
        expense.setName(newExpense.getName());
        expense.setPriceInCents(newExpense.getPriceInCents());
        expense.setParticipantToExpense(participants);
        expense.setExpenseTag(newTag);
        return expenseRepository.save(expense);
    }
}
