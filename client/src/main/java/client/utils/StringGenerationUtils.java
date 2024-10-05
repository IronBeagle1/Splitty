package client.utils;

import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StringGenerationUtils {
    private final Translation translation;

    /***
     * Constructor for StringGenerationUtils
     * @param translation the Translation to use
     */
    @Inject
    public StringGenerationUtils(Translation translation) {
        this.translation = translation;
    }

    /**
     * Generates the expense description for money transfers in this format: "A paid 60.0 to B"
     * @param expense The money transfer
     * @return Description of money transfer
     */
    public ObservableValue<String> generateTextForMoneyTransfer(Expense expense) {
        Participant sender = (Participant) expense.getParticipantsInExpense().toArray()[0];
        int amount = -1 * expense.getPriceInCents();
        Participant receiver = expense.getOwedTo();

        Map<String, String> substituteValues = new HashMap<>();
        substituteValues.put("senderName", sender.getName());
        substituteValues.put("amount", FormattingUtils.getFormattedPrice(amount));
        substituteValues.put("receiverName", receiver.getName());

        return translation.getStringSubstitutionBinding("SGU.String.transferString", substituteValues);
    }

    /**
     * Generates the expense description in this format: "B paid 60.0 for Y" (A,B)
     * @param expense the expense for which we are generating the label
     * @param totalParticipants the total amount of participants in the related event
     * @return the resulting generated ObservableString
     */
    public ObservableValue<String> generateTextForExpenseLabel(Expense expense, int totalParticipants) {
        Map<String, String> substituteValues = new HashMap<>();
        substituteValues.put("senderName", expense.getOwedTo().getName());
        substituteValues.put("amount", FormattingUtils.getFormattedPrice(expense.getPriceInCents()));
        substituteValues.put("expenseTitle", expense.getName());

        ObservableValue<String> descriptionString =  translation.getStringSubstitutionBinding(
                "SGU.String.expenseString", substituteValues);

        String includedString = generateIncludingString(expense, totalParticipants);
        return Bindings.concat(descriptionString, includedString);
    }

    /**
     * Generates the expense description in this format: "B paid 60.0 for Y"
     * @param expense the expense for which we are generating the label
     * @param totalParticipants the total amount of participants in the related event
     * @return the resulting generated ObservableString
     */
    public String generateIncludingString(Expense expense, int totalParticipants) {
        Set<Participant> partipantsInExpense = expense.getParticipantsInExpense();
        StringBuilder includedSB = new StringBuilder();
        if(totalParticipants == partipantsInExpense.size())
            includedSB.append("\n(All)");
        else {
            includedSB.append("\n(");
            for(Participant participant: partipantsInExpense) {
                includedSB.append(participant.getName());
                includedSB.append(", ");
            }
            if(!partipantsInExpense.isEmpty()){
                int indexToRemove = includedSB.lastIndexOf(",");
                includedSB.delete(indexToRemove, indexToRemove + 2);
            }
            includedSB.append(")");
        }
        return includedSB.toString();
    }

    /**
     * Generates the expense description in this format: "B paid 60.0 for Y on Z"
     * @param expense the expense for which we are generating the label
     * @return the resulting generated ObservableString
     */
    public ObservableValue<String> generateTextForExpenseAdminLabel(Expense expense) {
        Map<String, String> substituteValues = new HashMap<>();
        substituteValues.put("senderName", expense.getOwedTo().getName());
        substituteValues.put("amount", FormattingUtils.getFormattedPrice(expense.getPriceInCents()));
        substituteValues.put("expenseTitle", expense.getName());
        SimpleDateFormat fullDate = new SimpleDateFormat("dd/MM/yyyy");
        substituteValues.put("date", fullDate.format(expense.getDate()));
        return translation.getStringSubstitutionBinding("SGU.String.expenseStringAdmin", substituteValues);
    }

    /***
     * Generates an event description in the format Title: X, ID: Y
     * @param event the event to use
     * @return the resulting generated ObservableString
     */
    public ObservableValue<String> generateTextForEventLabel(Event event){
        Map<String, String> substituteValues = new HashMap<>();
        substituteValues.put("title", event.getTitle());
        substituteValues.put("id", event.getId());
        return translation.getStringSubstitutionBinding("SGU.String.eventString", substituteValues);
    }

    /***
     * Generates an participant description in the format Participant: X
     * @param participant the participant to use
     * @return the resulting generated ObservableString
     */
    public ObservableValue<String> generateTextForParticipantLabel(Participant participant){
        Map<String, String> substituteValues = new HashMap<>();
        substituteValues.put("participantName", participant.getName());
        return translation.getStringSubstitutionBinding("SGU.String.participantString", substituteValues);
    }
}
