package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettleDebtsUtilsTest {
    @InjectMocks
    SettleDebtsUtils sut;
    @Mock
    ServerUtils server;
    @Mock
    Translation translation;
    @Mock
    TransferMoneyUtils transferUtils;
    @Mock
    EmailHandler emailHandler;

    Participant participant1;
    Participant participant2;
    Participant participant3;

    @BeforeEach
    void setup(){
        participant1 = new Participant("Vox");
        participant2 = new Participant("Val");
        participant3 = new Participant("Vel");
    }

    BigDecimal bigDecimalize(int number){
        return BigDecimal.valueOf(number);
    }

    BigDecimal bigDecimalize(double number){
        return BigDecimal.valueOf(number);
    }

    /***
     * Randomized transfers result in correct outcome
     * Splitting expenses equally between 3 people (tends to leave rounding issues...)
     */
    @Test
    void randomTransferInstructions3X(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();
        Random rng = new Random();

        int rand1 = generateRandom(rng);
        int rand2 = generateRandom(rng);
        int balancer = -(rand1+rand2);

        creditMap.put(participant1, bigDecimalize(rand1/100.0));
        creditMap.put(participant2, bigDecimalize(rand2/100.0));
        creditMap.put(participant3, bigDecimalize(balancer/100.0));

        var initialResult = sut.calculateTransferInstructions(creditMap);
        assertTrue(initialResult.size()<creditMap.size());

        //Run one transfer at a time, and make sure it always settles the debt
        int ceiling = creditMap.size();
        for (Transfer transfer:
             initialResult) {
            BigDecimal balSender = creditMap.get(transfer.sender());
            BigDecimal balReceiver = creditMap.get(transfer.receiver());
            BigDecimal amount = bigDecimalize(transfer.amount());
            creditMap.put(transfer.sender(), balSender.add(amount));
            creditMap.put(transfer.receiver(), balReceiver.subtract(amount));

            var reranResult = sut.calculateTransferInstructions(creditMap);
            ceiling -= 1;
            assertTrue(reranResult.size()<ceiling);
        }
    }

    /***
     * Randomized transfers result in correct outcome
     * Splitting expenses equally between 4 people (usually no rounding issues)
     */
    @Test
    void randomTransferInstructions4X(){
        Participant participant4 = new Participant("Alastor!");
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();
        Random rng = new Random();

        int rand1 = generateRandom(rng);
        int rand2 = generateRandom(rng);
        int rand3 = generateRandom(rng);
        int balancer = -(rand1+rand2+rand3);

        creditMap.put(participant1, bigDecimalize(rand1/100.0));
        creditMap.put(participant2, bigDecimalize(rand2/100.0));
        creditMap.put(participant3, bigDecimalize(balancer/100.0));
        creditMap.put(participant4, bigDecimalize(balancer/100.0));

        var initialResult = sut.calculateTransferInstructions(creditMap);
        assertTrue(initialResult.size()<creditMap.size());

        //Run one transfer at a time, and make sure it always settles the debt
        int ceiling = creditMap.size();
        for (Transfer transfer:
                initialResult) {
            BigDecimal balSender = creditMap.get(transfer.sender());
            BigDecimal balReceiver = creditMap.get(transfer.receiver());
            BigDecimal amount = bigDecimalize(transfer.amount());
            creditMap.put(transfer.sender(), balSender.add(amount));
            creditMap.put(transfer.receiver(), balReceiver.subtract(amount));

            var reranResult = sut.calculateTransferInstructions(creditMap);
            ceiling -= 1;
            assertTrue(reranResult.size()<ceiling);
        }
    }

    /***
     * Randomized transfers result in correct outcome
     * Splitting expenses equally between 5 people (tends to leave rounding issues too)
     */
    @Test
    void randomTransferInstructions5X(){
        Participant participant4 = new Participant("VVV");
        Participant participant5 = new Participant("Alastor!");
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();
        Random rng = new Random();

        int rand1 = generateRandom(rng);
        int rand2 = generateRandom(rng);
        int rand3 = generateRandom(rng);
        int rand4 = generateRandom(rng);
        int balancer = -(rand1+rand2+rand3+rand4);

        creditMap.put(participant1, bigDecimalize(rand1/100.0));
        creditMap.put(participant2, bigDecimalize(rand2/100.0));
        creditMap.put(participant3, bigDecimalize(balancer/100.0));
        creditMap.put(participant4, bigDecimalize(balancer/100.0));
        creditMap.put(participant5, bigDecimalize(balancer/100.0));

        var initialResult = sut.calculateTransferInstructions(creditMap);
        assertTrue(initialResult.size()<creditMap.size());

        //Run one transfer at a time, and make sure it always settles the debt
        int ceiling = creditMap.size();
        for (Transfer transfer:
                initialResult) {
            BigDecimal balSender = creditMap.get(transfer.sender());
            BigDecimal balReceiver = creditMap.get(transfer.receiver());
            BigDecimal amount = bigDecimalize(transfer.amount());
            creditMap.put(transfer.sender(), balSender.add(amount));
            creditMap.put(transfer.receiver(), balReceiver.subtract(amount));

            var reranResult = sut.calculateTransferInstructions(creditMap);
            ceiling -= 1;
            assertTrue(reranResult.size()<ceiling);
        }
    }

    /***
     * Generates a random number between min and max
     */
    private int generateRandom(Random random){
        int min = -10000;
        int max = 10000;
        return random.nextInt(max - min) + min;
    }

    /***
     * One debtor can pay off multiple people
     */
    @Test
    void splitDebtTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();

        creditMap.put(participant1, bigDecimalize(-25));
        creditMap.put(participant2, bigDecimalize(10));
        creditMap.put(participant3, bigDecimalize(15));

        Transfer transfer1 = new Transfer(participant1, 10, participant2);
        Transfer transfer2 = new Transfer(participant1, 15, participant3);

        var result = sut.calculateTransferInstructions(creditMap);

        assertTrue(result.contains(transfer1));
        assertTrue(result.contains(transfer2));
    }

    /***
     * One creditor can receive from multiple people
     */
    @Test
    void splitCreditTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();

        creditMap.put(participant1, bigDecimalize(100));
        creditMap.put(participant2, bigDecimalize(-25));
        creditMap.put(participant3, bigDecimalize(-75));

        Transfer transfer1 = new Transfer(participant2, 25, participant1);
        Transfer transfer2 = new Transfer(participant3, 75, participant1);

        var result = sut.calculateTransferInstructions(creditMap);

        assertTrue(result.contains(transfer1));
        assertTrue(result.contains(transfer2));
    }

    /***
     * Amounts that will end up net-positive after rounding result in creditor not being paid off the missing cent fractions
     */
    @Test
    void netPositiveTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();
        Participant participant4 = new Participant("VVV");
        Participant participant5 = new Participant("Alastor");

        BigDecimal credit = bigDecimalize(1.6);
        BigDecimal debtAbovePointFive = bigDecimalize(-0.6);
        BigDecimal fractionalShare = bigDecimalize(-0.3);

        creditMap.put(participant1, credit);
        creditMap.put(participant2, fractionalShare);
        creditMap.put(participant3, fractionalShare);
        creditMap.put(participant4, fractionalShare);
        creditMap.put(participant5, debtAbovePointFive);

        Transfer expectedTransfer = new Transfer(participant5, 1, participant1);
        var result = sut.calculateTransferInstructions(creditMap);

        assertTrue(result.contains(expectedTransfer));
        assertEquals(1, result.size());
    }

    /***
     * Amounts that will end up net-negative after rounding result in creditor being paid off but debtors having negative balance
     */
    @Test
    void netNegativeTransferInstructions(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();

        creditMap.put(participant1, bigDecimalize(1.33));
        creditMap.put(participant2, bigDecimalize(-0.66));
        creditMap.put(participant3, bigDecimalize(-0.66));

        Transfer expectedTransfer = new Transfer(participant2, 1, participant1);
        var result = sut.calculateTransferInstructions(creditMap);

        assertTrue(result.contains(expectedTransfer));
        assertEquals(1, result.size());
    }

    /***
     * [FEATURE NOT YET IMPLEMENTED]
     * Checks if the Action executed when the Mark Received button is pressed
     * settles a normal debt correctly
     */
    @Test
    void createSettleAction() {
        Event event = new Event("Title!", null);
        event.addTag(new Tag("money transfer", null));
        Transfer transfer = new Transfer(participant1, 7, participant2);

        Expense settleExpense = new Expense("Money Transfer", 7, null, participant2);
        settleExpense.addParticipantToExpense(participant1);
        when(transferUtils.transferMoney(transfer, event)).thenReturn(settleExpense);

        EventHandler<ActionEvent> result = sut.createSettleAction(transfer, event);
        result.handle(new ActionEvent());
        verify(server).addExpense(event.getId(), settleExpense);
    }

    /***
     * Checks if text generated for a transfer
     * fits the structure and has the correct details
     */
    @Test
    void stringPositive() {
        Participant participantA = new Participant("NameABC");
        Participant participantB = new Participant("NameXYZ");
        Transfer transfer = new Transfer(participantA, 7, participantB);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("senderName", "NameABC");
        expectedValues.put("amount", "0.07\u20ac");
        expectedValues.put("receiverName", "NameXYZ");

        //Only returns this if the expected values are substituted in, null otherwise
        lenient().when(translation.getStringSubstitutionBinding(
                "SettleDebts.String.transferInstructions", expectedValues))
                .thenReturn(stringToObservable(
                        expectedValues.get("senderName") + " gives " +
                        expectedValues.get("amount") + " to " +
                        expectedValues.get("receiverName")));
        String result = sut.createTransferString(transfer).getValue();
        assertTrue(result.contains("NameABC"));
        assertTrue(result.contains("gives"));
        assertTrue(result.contains("0.07"));
        assertTrue(result.contains("to"));
        assertTrue(result.contains("NameXYZ"));
    }

    /***
     * Checks if a negative amount transfer is caught
     */
    @Test
    void stringNegative() {
        Transfer transfer = new Transfer(participant1, -7, participant2);
        assertThrows(IllegalArgumentException.class, () -> sut.createTransferString(transfer));
    }

    /***
     * Checks if a zero amount transfer is caught
     */
    @Test
    void stringZero() {
        Transfer transfer = new Transfer(participant1, 0, participant2);
        assertThrows(IllegalArgumentException.class, () -> sut.createTransferString(transfer));
    }

    /***
     * Checks if a Participant with all bank details
     * has their payment information generated properly
     */
    @Test
    void bankFull(){
        String name = "Vox V.";
        String iban = "NL02ABNA0123456789";
        String bic = "ABNANL2AXXX";

        participant1.setLegalName(name);
        participant1.setIban(iban);
        participant1.setBic(bic);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("holder", name);
        expectedValues.put("iban", iban);
        expectedValues.put("bic", bic);


        //Only returns this if the expected values are substituted in, null otherwise
        lenient().when(translation.getStringBinding("SettleDebts.String.bankAvailable"))
                        .thenReturn(stringToObservable("Bank information available, transfer the money to:"));
        lenient().when(translation.getStringSubstitutionBinding(
                        "SettleDebts.String.bankDetails", expectedValues))
                .thenReturn(stringToObservable(
                                "Account Holder: " + expectedValues.get("holder") +
                                "\nIBAN: " + expectedValues.get("iban") +
                                "\nBIC: " + expectedValues.get("bic")));

        var result = sut.getBankDetails(participant1).getValue();
        assertTrue(result.contains("information available"));
        assertTrue(result.contains(name));
        assertTrue(result.contains(iban));
        assertTrue(result.contains(bic));
    }

    /***
     * Checks if a Participant with missing bank details
     * has their (unavailable) payment information generated properly
     */
    @Test
    void bankMissing(){
        String name = "Vox V.";
        String iban = "";
        String bic = "ABNANL2AXXX";

        participant1.setLegalName(name);
        participant1.setIban(iban);
        participant1.setBic(bic);

        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("holder", name);
        expectedValues.put("iban", "(MISSING)");
        expectedValues.put("bic", bic);


        //Only returns this if the expected values are substituted in, null otherwise
        lenient().when(translation.getStringBinding("SettleDebts.String.bankUnavailable"))
                .thenReturn(stringToObservable("Full bank information unavailable:"));
        lenient().when(translation.getStringSubstitutionBinding(
                        "SettleDebts.String.bankDetails", expectedValues))
                .thenReturn(stringToObservable(
                        "Account Holder: " + expectedValues.get("holder") +
                                "\nIBAN: " + expectedValues.get("iban") +
                                "\nBIC: " + expectedValues.get("bic")));

        var result = sut.getBankDetails(participant1).getValue();
        assertTrue(result.contains("information unavailable"));
        assertTrue(result.contains(name));
        assertTrue(result.contains("(MISSING)"));
        assertTrue(result.contains(bic));
    }
}