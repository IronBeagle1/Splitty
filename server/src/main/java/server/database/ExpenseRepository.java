package server.database;

import commons.Expense;
import commons.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    /**
     * Returns all expenses that are owed to the given participant
     * @param participant the participant to whom the expenses are owed
     * @return the set of expenses that are owed to the participant
     */
    Set<Expense> findByOwedTo(Participant participant);

}