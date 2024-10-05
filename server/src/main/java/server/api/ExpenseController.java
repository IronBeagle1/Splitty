package server.api;
import commons.Expense;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.websockets.WebSocketService;

import java.util.Set;

@RestController
@RequestMapping("/api/events")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final WebSocketService socketService;

    /***
     * Constructor of the ExpenseController
     * @param expenseService the ExpenseService handling logic
     * @param socketService the WebSocketService propagating updates
     */
    public ExpenseController(ExpenseService expenseService, WebSocketService socketService) {
        this.expenseService = expenseService;
        this.socketService = socketService;
    }

    /**
     * Method responsible for the post command. It is used to update a specific
     * event expenses
     * @param eventId identifies the even by the specific ID
     * @param expense maps the expense to the specific even specified by the ID
     * @return the status of the task
     */
    @PostMapping("/{eventId}/expenses")
    public ResponseEntity<Void> addExpenseToEvent(@PathVariable String eventId, @RequestBody
        Expense expense) {
        expenseService.addExpense(eventId, expense);
        socketService.propagateEventUpdate(eventId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * The method is responsible for creating a GET request
     * that returns the desired information
     * @param eventId identifies the even by the specific ID
     * @return the status of the specific page and the information
     * (a list of all expenses for a specific event)
     */
    @GetMapping("/{eventId}/expenses")
    public ResponseEntity<Set<Expense>> getAllExpensesForEvent(@PathVariable String eventId) {
        Set<Expense> expenses = expenseService.getAllExpenses(eventId);
        return ResponseEntity.ok(expenses);
    }

    /**
     * Deletes an expense from an event
     * @param id the id of the expense
     * @param eventId the id of the event to delete the expense from
     * @return a status indicating whether the even was deleted
     */
    @DeleteMapping("/{eventId}/expenses/{id}")
    public ResponseEntity<?> removeExpense(@PathVariable String eventId,
                                                         @PathVariable Long id) {
        expenseService.deleteExpense(eventId, id);
        socketService.propagateEventUpdate(eventId);
        return ResponseEntity.ok().build();
    }

    /***
     * Updates all the fields of an Expense to match submitted data
     * @param eventId the Event of the expense
     * @param id the ID of the Expense
     * @param expense the new Expense data
     * @return a ResponseEntity of the updated Expense
     */
    @PutMapping("/{eventId}/expenses/{id}")
    public ResponseEntity<Expense> editExpense(@PathVariable String eventId,
                                               @PathVariable long id,
                                               @RequestBody Expense expense) {
        Expense updatedExpense = expenseService.editExpense(id, expense);
        socketService.propagateEventUpdate(eventId);
        return ResponseEntity.ok(updatedExpense);

    }
}