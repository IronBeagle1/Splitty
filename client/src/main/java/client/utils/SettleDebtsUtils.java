package client.utils;

import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class SettleDebtsUtils {

    private final Translation translation;
    private final ServerUtils server;
    private final TransferMoneyUtils transferUtils;
    private EmailHandler emailHandler;

    /***
     * Constructor for the utility class for the SettleDebts screen
     * @param translation - the Translation to use
     * @param server - the SeverUtils to use
     * @param transferUtils - the TransferMoneyUtils to use
     * @param emailHandler - the emailHandler to use
     */
    @Inject
    public SettleDebtsUtils(Translation translation, ServerUtils server, TransferMoneyUtils transferUtils, EmailHandler emailHandler) {
        this.translation = translation;
        this.server = server;
        this.transferUtils = transferUtils;
        this.emailHandler = emailHandler;
    }

    //Pseudocode adapted from: https://stackoverflow.com/questions/4554655/who-owes-who-money-optimization
    /***
     * Calculates at most N-1 transfer instructions for N participants
     * @param creditMap a Map of participants to credit(+)/debt(-)
     * @return a List of Transfer instructions (sender, amount, receiver)
     */
    public List<Transfer> calculateTransferInstructions(HashMap<Participant, BigDecimal> creditMap){
        List<Transfer> result = new LinkedList<>();
        //We probably don't want to be mutating the inserted creditMap, re-running should yield the same result
        HashMap<Participant, BigDecimal> unroundedMap = new HashMap<>(creditMap);

        HashMap<Participant, Integer> roundedMap = RoundUtils.roundMap(unroundedMap, RoundingMode.HALF_UP);
        HashMap<Participant, Integer> processMap = new HashMap<>(roundedMap);

        List<Map.Entry<Participant, Integer>> creditorsSorted = processMap.entrySet().stream()
                .filter(entry->entry.getValue()>0)
                .sorted(Map.Entry.<Participant, Integer>comparingByValue().
                        thenComparing(entry->entry.getKey().getName())) //Alphabetical order used for consensus when net balance is not 0
                .toList();

        List<Map.Entry<Participant, Integer>> debtorsSorted = processMap.entrySet().stream()
                .filter(entry->entry.getValue()<0)
                .sorted(Map.Entry.<Participant, Integer>comparingByValue().
                        thenComparing(entry->entry.getKey().getName())) //Alphabetical order used for consensus when net balance is not 0
                .toList();

        ArrayList<Map.Entry<Participant, Integer>> creditors = new ArrayList<>(creditorsSorted);
        ArrayList<Map.Entry<Participant, Integer>> debtors = new ArrayList<>(debtorsSorted);

        for(Map.Entry<Participant, Integer> debtorEntry: debtors){
            Participant debtor = debtorEntry.getKey();
            int negativeBalance = debtorEntry.getValue();
            while(negativeBalance < 0){
                if(creditors.isEmpty()) {
                    /*
                    Assumption: this means there was a slight net negative balance
                    I.E. 4 cents split between 6 participants and 1 creditor
                    4 participants pay 1 cent, 2 pay 0 cents, even though for fairness all 6 should pay 0.666666.... cents
                    But that's not possible, so the best we can do is assign them so the biggest debtors pay, and the rest don't
                    Same goes for slight net positive balances, where the creditor just doesn't get an extra 1 or 2 cents
                     */
                    break;
                }
                Map.Entry<Participant, Integer> creditorEntry = creditors.getLast();
                int credit = creditorEntry.getValue();
                int amountToTransfer = Integer.min(-negativeBalance, credit);

                creditorEntry.setValue(credit - amountToTransfer);
                negativeBalance += amountToTransfer;
                if(creditorEntry.getValue()==0){
                    creditors.removeLast();
                }

                Transfer transfer = new Transfer(debtor, amountToTransfer, creditorEntry.getKey());
                result.add(transfer);
            }
            debtorEntry.setValue(negativeBalance);
        }

        return result;
    }

    /***
     * Generates the onClick action for a button that settles a particular debt
     * @param transfer the Transfer data to use
     * @param event the corresponding event
     * @return the action a button should perform to settle the debt
     */
    public EventHandler<ActionEvent> createSettleAction(Transfer transfer, Event event){
        return (actionEvent) -> {
            Expense settleExpense = transferUtils.transferMoney(transfer, event);
            server.addExpense(event.getId(), settleExpense);
        };
    }

    /***
     * Generates a localized Observable String representation of Transfer instructions
     * @param transfer the Transfer data to use
     * @return an ObservableValue String stating who owes who and how much
     */
    public ObservableValue<String> createTransferString(Transfer transfer) {
        int amount = transfer.amount();
        if(amount<=0) throw new IllegalArgumentException("Negative or zero transfer: " + transfer);
        String formattedAmount = FormattingUtils.getFormattedPrice(amount);

        Map<String, String> substituteValues = new HashMap<>();
        substituteValues.put("senderName", transfer.sender().getName());
        substituteValues.put("amount", formattedAmount);
        substituteValues.put("receiverName", transfer.receiver().getName());

        return translation.getStringSubstitutionBinding("SettleDebts.String.transferInstructions",
                substituteValues);
    }

    /***
     * Generates a localized Observable String representing all bank details for a Participant
     * @param participant the Participant to check
     * @return an ObservableValue String representation of a Participant's bank details
     */
    public ObservableValue<String> getBankDetails(Participant participant) {
        String name = participant.getLegalName();
        String iban = participant.getIban();
        String bic = participant.getBic();

        if(Objects.equals(name, "")) name = "(MISSING)";
        if(Objects.equals(iban, "")) iban = "(MISSING)";
        if(Objects.equals(bic, "")) bic = "(MISSING)";

        ObservableValue<String> availability;
        if(participant.hasBankAccount()){
            availability = translation.getStringBinding("SettleDebts.String.bankAvailable");
        } else {
            availability = translation.getStringBinding("SettleDebts.String.bankUnavailable");
        }

        Map<String, String> substituteValues = new HashMap<>();
        substituteValues.put("holder", name);
        substituteValues.put("iban", iban);
        substituteValues.put("bic", bic);

        return Bindings.concat(availability, "\n", translation.getStringSubstitutionBinding("SettleDebts.String.bankDetails",
                substituteValues));
    }


    /**
     * Sends the email to the participant to pay
     * @param transfer the transfer to send the email for
     */
    public void sendEmailTransferEmail(Transfer transfer) {
        String emailBody = generateEmailBody(transfer);
        String emailSubject = "Payment Request";
        boolean result = emailHandler.sendEmail(transfer.sender().getEmail(),emailSubject,emailBody);
        if (result){
            Platform.runLater(() -> {
                emailHandler.showSuccessPrompt();
            });
        }else{
            Platform.runLater(() -> {
                emailHandler.showFailPrompt();
            });
        }
    }

    /**
     * Generates an email body for the transfer
     * @param transfer the transfer to generate the email body for
     * @return the email body
     */
    public String generateEmailBody(Transfer transfer) {
        String emailBody;
        if (transfer.receiver().hasBankAccount()) {
            emailBody= """
                Please transfer the amount of {amount} to {receiver} to the following bank account:

                {bankDetails}

                Thank you!""";
            emailBody = emailBody.replace("{bankDetails}",
                    "Name: " + transfer.receiver().getLegalName() + "\n" +
                               "IBAN: " + transfer.receiver().getIban() + "\n" +
                               "BIC: " + transfer.receiver().getBic());
        }else{
            emailBody= """
                Please transfer the amount of {amount} to {receiver}

                Thank you!""";
        }

        emailBody = emailBody.replace("{amount}", FormattingUtils.getFormattedPrice(transfer.amount()));
        emailBody = emailBody.replace("{receiver}", transfer.receiver().getName());
        return emailBody;
    }
}
