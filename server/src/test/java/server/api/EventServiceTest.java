package server.api;

import commons.Event;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository mockEventRepository;

    @InjectMocks
    private EventService mockEventService;

    /**
     * tests if the title editing method works, on an existing event
     */
    @Test
    public void editTitleExistingTest() {
        Event event = new Event("title", null);
        when(mockEventRepository.findById(anyString())).
                thenReturn(Optional.of(event));
        when(mockEventRepository.save(any(Event.class))).
                thenReturn(event);
        Event newEvent = mockEventService.
                editTitle(event.getId(), "newTitle");
        assertEquals(newEvent.getTitle(), "newTitle");
        verify(mockEventRepository, times(1)).save(newEvent);
    }

    /**
     * tests editing a title on an event which does not exist
     */
   @Test
    public void editTitleNotExistingTest(){
        Event event = new Event("title", null);
        assertThrows(EntityNotFoundException.class, () ->
            mockEventService.editTitle(event.getId(), "new title"));
    }

}
