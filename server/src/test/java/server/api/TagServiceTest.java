package server.api;

import commons.Event;
import commons.Expense;
import commons.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.TagRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    /**
     * Tests adding a tag to an event that exists.
     */
    @Test
    void addTagToExistingEvent() {
        Event event = new Event("Event1", null);
        String eventId = "1";
        String tagName = "Important";
        String colorCode = "#FF0000";

        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));
        doAnswer(invocation -> {
            Event savedEvent = invocation.getArgument(0);
            savedEvent.addTag(new Tag(tagName, colorCode));
            return null;
        }).when(eventRepository).save(any(Event.class));

        tagService.addTagToEvent(eventId, tagName, colorCode);

        verify(eventRepository).save(eventCaptor.capture());
        assertEquals(1, eventCaptor.getValue().getEventTags().size());
        assertTrue(eventCaptor.getValue().getEventTags().iterator().next().getTagName().equals(tagName));
    }
    /**
     * Tests adding a tag to an event that does not exist.
     */
    @Test
    void addTagToNonExistingEvent() {
        String eventId = "1";
        String tagName = "Important";
        String colorCode = "#FF0000";

        when(eventRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            tagService.addTagToEvent(eventId, tagName, colorCode);
        });
    }

    /**
     * Tests removing a tag from an event, ensuring associated expenses are updated.
     */
    @Test
    void removeTagFromEvent() {
        String eventId = "1";
        Long tagId = 1L;
        Event event = new Event("Event1", null);
        Tag tagToRemove = new Tag("Urgent", "#FF0000");
        Tag defaultTag = new Tag("default", "#000000");

        Expense expense = new Expense();
        expense.setExpenseTag(tagToRemove);

        event.setEventTags(new HashSet<>(Set.of(tagToRemove, defaultTag)));
        event.getExpenses().add(expense);

        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tagToRemove));
        when(expenseRepository.save(any(Expense.class))).then(returnsFirstArg());

        tagService.removeTag(eventId, tagId);

        verify(expenseRepository).save(expense);
        assertEquals(defaultTag, expense.getExpenseTag());
        verify(eventRepository).save(event);
        assertFalse(event.getEventTags().contains(tagToRemove));
    }
    @Test
    void removeNonExistingTag() {
        String eventId = "1";
        Long tagId = 1L;
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            tagService.removeTag(eventId, tagId);
        });
        
        verify(eventRepository, never()).findById(anyString());
    }

    /**
     * The tag received from the repository should have the same name and color as the new tag.
     * Tag repository should save updated tag.
     */
    @Test
    public void editExistingTag() {
        long id = 5;
        Tag newTag = new Tag("Food", "#FFFFFF");
        Tag oldTag = new Tag("Travel", "#000000");
        when(tagRepository.findById(id)).thenReturn(Optional.of(oldTag));
        tagService.editTag(id, newTag);
        assertEquals(oldTag, newTag);
        verify(tagRepository).save(oldTag);
    }

    /**
     * EntityNotFoundException should be thrown when a tag is not found by the given ID.
     * The exception message should indicate that it cannot be found.
     */
    @Test
    public void editNonExistingTag() {
        long id = 5;
        Tag newTag = new Tag("Food", "#FFFFFF");
        when(tagRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                tagService.editTag(id, newTag));
        assertEquals(exception.getMessage(), "Tag is not found");
    }
}
