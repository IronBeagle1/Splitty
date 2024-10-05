package server.api;
import commons.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.websockets.WebSocketService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class TagControllerTest {
    @Mock
    private TagService tagService;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private TagController tagController;

    @Captor
    ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setup() {
    }

    @Test
    void addTagToEventSuccess() {
        String eventId = "event1";
        String tagName = "urgent";
        String colorCode = "#FF0000";

        doNothing().when(tagService).addTagToEvent(anyString(), anyString(), anyString());
        doNothing().when(webSocketService).propagateEventUpdate(anyString());

        ResponseEntity<Void> response = tagController.addTagToEvent(eventId, tagName, colorCode);

        verify(tagService).addTagToEvent(eventId, tagName, colorCode);
        verify(webSocketService).propagateEventUpdate(eventId);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    @Test
    void addTagToEventFail() {
        String eventId = "event1";
        String tagName = "urgent";
        String colorCode = "#FF0000";

        doThrow(new IllegalArgumentException()).when(tagService).addTagToEvent(anyString(), anyString(), anyString());

        assertThrows(IllegalArgumentException.class, () -> {
            tagController.addTagToEvent(eventId, tagName, colorCode);
        });
    }

    @Test
    void removeTagSuccess() {
        String eventId = "event1";
        Long tagId = 1L;

        doNothing().when(tagService).removeTag(anyString(), anyLong());
        doNothing().when(webSocketService).propagateEventUpdate(anyString());

        ResponseEntity<?> response = tagController.removeTag(eventId, tagId);

        verify(tagService).removeTag(eventId, tagId);
        verify(webSocketService).propagateEventUpdate(eventId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void removeTagFail() {
        String eventId = "event1";
        Long tagId = 1L;

        doThrow(new IllegalArgumentException()).when(tagService).removeTag(anyString(), anyLong());

        assertThrows(IllegalArgumentException.class, () -> {
            tagController.removeTag(eventId, tagId);
        });
    }

    /**
     * A 200 OK response should be sent after a tag, already existing in the database, is edited.
     */
    @Test
    public void editExistingTag() {
        String eventId = "Holiday";
        Long tagId = 5L;
        Tag newTag = new Tag("Food", "#FFFFFF");
        when(tagService.editTag(tagId, newTag)).thenReturn(newTag);
        ResponseEntity<Tag> response = tagController.editTag(eventId, tagId, newTag);
        verify(webSocketService).propagateEventUpdate(eventId);
        assertEquals(OK, response.getStatusCode());
        assertEquals(newTag.getTagName(), response.getBody().getTagName());
        assertEquals(newTag.getColorCode(), response.getBody().getColorCode());
    }

    /**
     * A 400 BAD_REQUEST response should be returned when a tag, that is not in the database, is edited.
     */
    @Test
    public void editNonExistingTag() {
        String eventId = "Holiday";
        Long tagId = 5L;
        Tag newTag = new Tag("Food", "#FFFFFF");
        doThrow(EntityNotFoundException.class).when(tagService).editTag(tagId, newTag);
        ResponseEntity<Tag> response = tagController.editTag(eventId, tagId, newTag);
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }
}
