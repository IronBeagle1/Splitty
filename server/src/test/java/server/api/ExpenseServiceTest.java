package server.api;

import commons.Event;
import commons.Expense;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.TagRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository mockExpenseRepository;

    @Mock
    private EventRepository mockEventRepository;
    @Mock
    private TagRepository mockTagRepository;

    @InjectMocks
    private ExpenseService mockExpenseService;

    /**
     * Tests the getExpensesForEvents
     */
    @Test
    public void getAllExpensesForEventTest() {
        String eventId = "mockEventId";
        Expense expense1 = new Expense("Expense 1", 100,
                null, null);
        Expense expense2 = new Expense("Expense 2", 200,
                null, null);
        Set<Expense> expectedExpenses = Set.of(expense1, expense2);
        Event fakeEvent = new Event("title", null);
        fakeEvent.addExpense(expense1);
        fakeEvent.addExpense(expense2);
        when(mockEventRepository.findById(anyString()))
            .thenReturn(Optional.of(fakeEvent));
        Set<Expense> actualExpenses =
            mockExpenseService.getAllExpenses(eventId);
        assertEquals(expectedExpenses, actualExpenses);
    }

    /**
     * Tests the getExpensesForEvents
     */
    @Test
    public void deleteExpenseTest() {
        Event mockEvent = new Event("Sample Event", null);
        Expense mockExpense = new Expense("mockExpense", 100,
            null, null);
        mockEvent.addExpense(mockExpense);
        when(mockEventRepository.findById(anyString()))
                .thenReturn(Optional.of(mockEvent));
        when(mockExpenseRepository.findById(anyLong()))
            .thenReturn(Optional.of(mockExpense));
        mockExpenseService.deleteExpense(mockEvent.getId(), mockExpense.getId());
        assertFalse(mockEvent.getExpenses().contains(mockExpense));
        verify(mockEventRepository).save(mockEvent);
    }
}