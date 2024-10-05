package server.api;

import commons.Event;
import commons.Expense;
import commons.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.TagRepository;

import java.util.Set;

@Service
public class TagService {
    private final EventRepository eventRepository;
    private final ExpenseRepository expenseRepository;
    private final TagRepository tagRepository;

    /**
     * Constructor
     * @param eventRepository EventRepository to use
     * @param expenseRepository  ExpenseRepository to use
     * @param tagRepository TagRepository to use
     */
    @Autowired
    public TagService(EventRepository eventRepository,
                      ExpenseRepository expenseRepository, TagRepository tagRepository){
        this.eventRepository = eventRepository;
        this.expenseRepository = expenseRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Adds a tag to an event
     * @param eventId id of the event to add the tag to
     * @param tagName name of the tag
     * @param colorCode color code of the tag
     */
    public void addTagToEvent(String eventId, String tagName, String colorCode){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        Tag tag = new Tag(tagName, colorCode);
        event.addTag(tag);
        eventRepository.save(event);
    }

    /**
     * Removes a tag from an event
     * @param eventId id of the event to remove the tag from
     * @param tagId id of the tag to remove
     */
    public void removeTag(String eventId, Long tagId){
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        Set<Expense> expenses = event.getExpenses();

        Tag tagIfDefaultNotFound = new Tag("default", "#000000");
        Tag defaultTag = event.getEventTags().stream()
                .filter(foundTag->foundTag.getTagName().equals("default"))
                .findFirst().orElse(tagIfDefaultNotFound);

        for(Expense expense : expenses){
            if(expense.getExpenseTag().equals(tag)){
                expense.setExpenseTag(defaultTag);
                expenseRepository.save(expense);
            }
        }
        event.removeTag(tag);
        eventRepository.save(event);
    }

    /**
     * Save edited tag to database
     * @param id ID of edited tag
     * @param newTag Edited tag
     * @throws EntityNotFoundException Tag is not found in the database
     * @return Tag that is edited
     */
    public Tag editTag(Long id, Tag newTag) throws EntityNotFoundException {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag is not found"));
        tag.setTagName(newTag.getTagName());
        tag.setColorCode(newTag.getColorCode());
        return tagRepository.save(tag);
    }
}
