package server.api;

import commons.Expense;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.websockets.WebSocketService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {
    @InjectMocks
    private ExpenseController expenseController;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private WebSocketService socketService;

    @Test
    public void addExpenseToEventTest() {
        String eventId = "sampleEventId";
        Expense expense = new Expense("Sample Expense",
            100, null, null);
        doNothing().when(expenseService).addExpense(anyString(), any(Expense.class));
        ResponseEntity<Void> responseEntity
            = expenseController.addExpenseToEvent(eventId, expense);
        verify(expenseService).addExpense(eq(eventId), eq(expense));
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void getAllExpensesForEventTest() {
        String eventId = "sampleEventId";
        Set<Expense> mockExpenses = Set.of(
            new Expense("Expense 1", 100,
                null, null),
            new Expense("Expense 2", 200,
                null, null)
        );
        when(expenseService.getAllExpenses(eventId)).thenReturn(mockExpenses);
        ResponseEntity<Set<Expense>> responseEntity
            = expenseController.getAllExpensesForEvent(eventId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Set<Expense> returnedExpenses = responseEntity.getBody();
        assertEquals(mockExpenses, returnedExpenses);
    }

    @Test
    public void editExistingExpenseTest() {
        Expense expected = new Expense();
        when(expenseService.editExpense(anyLong(), any())).thenReturn(expected);
        ResponseEntity<Expense> response = expenseController.editExpense("ABC123", 1L, expected);
        assertEquals(expected, response.getBody());
    }

    @Test
    public void editNonExistingExpenseTest() {
        Expense expected = new Expense();
        when(expenseService.editExpense(anyLong(), any())).thenThrow(new EntityNotFoundException("test"));
        assertThrows(EntityNotFoundException.class, () -> expenseController.editExpense("ABC123", 1L, expected));
    }

    //Need to add tests for the delete method
}