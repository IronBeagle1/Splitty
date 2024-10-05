package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Entity
public class Event{
    @Id
    private String id;
    private String title;
    private Date creationDate;
    private Date lastActivity;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Participant> participants;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Expense> expenses;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tag> eventTags;


    /***
     * Constructor for object mappers
     */
    @SuppressWarnings("unused")
    public Event() {}

    /***
     * Constructor with a known title and date
     * @param title Event title
     * @param creationDate Event creation date
     */
    public Event(String title, Date creationDate) {
        this.title = title;
        this.participants = new HashSet<>();
        this.expenses = new HashSet<>();
        this.id = generateId();
        this.creationDate = creationDate;
        this.lastActivity = new Date();
        this.eventTags = new HashSet<>();
    }

    /***
     * Calculates the total spending for the event
     * Negative expenses are ignored.
     * @return an int of the total cost of the event, in cents
     */
    @JsonIgnore
    public int getTotalSpending(){
        return expenses.stream().mapToInt(Expense::getPriceInCents).filter(x -> x >= 0).sum();
    }

    private final static BigDecimal initialBalance = BigDecimal.valueOf(0);

    /***
     * Calculates the total expense share per person
     * @param includingTransfers whether negative balance expenses should be included (true for splitting, false for statistics)
     * @return A Map of participants to the total split cost of expenses they are in
     */
    @JsonIgnore
    public HashMap<Participant,BigDecimal> getExpenseShare(boolean includingTransfers) {
        HashMap<Participant, BigDecimal> shareMap = new HashMap<>();

        for (Participant participant:
                participants) {
            shareMap.put(participant, initialBalance);
        }

        for (Expense expense:
                expenses) {
            Set<Participant> members = expense.getParticipantsInExpense();
            int amountToSplit = expense.getPriceInCents();
            if(!includingTransfers && amountToSplit <= 0) continue;
            splitAmountEqually(shareMap, members, amountToSplit);
        }

        return shareMap;
    }

    /***
     * Splits the submitted cost across all members provided, adding it to the Map
     * @param shareMap a Map containing share per Participant
     * @param members the Participants partaking
     * @param costInCents the cost to split equally between the given members
     */
    @JsonIgnore
    public void splitAmountEqually(HashMap<Participant, BigDecimal> shareMap,
                              Set<Participant> members,
                              int costInCents) {

        BigDecimal totalCost = convertIntToDecimal(costInCents);
        BigDecimal peopleToDivideBy = BigDecimal.valueOf(members.size());
        BigDecimal sharePerPerson = totalCost.divide(peopleToDivideBy, RoundingMode.HALF_UP);

        for (Participant participant:
                members) {
            BigDecimal balance = shareMap.get(participant);
            balance = balance.add(sharePerPerson);
            shareMap.put(participant, balance);
        }
    }

    private final static int precision = 4;
    /***
     * Converts an int to an appropriate BigDecimal
     * @param number the number to convert
     * @return a BigDecimal of the int, with additional digits after the dot
     */
    @JsonIgnore
    private BigDecimal convertIntToDecimal(int number){
        long scale = (long) Math.pow(10, precision);
        BigInteger numberScaled = BigInteger.valueOf(scale * number);
        return new BigDecimal(numberScaled, precision);
    }

    /**
     * Calculates the share owed to (credit) per Participant for all expenses
     * @return a Map of participants to the amount they are owed (credit)
     */
    @JsonIgnore
    public HashMap<Participant,BigDecimal> getOwedShares(){
        HashMap<Participant, BigDecimal> creditMap = new HashMap<>();

        HashMap<Participant, BigDecimal> shareMap = getExpenseShare(true);
        HashMap<Participant, Integer> spendingMap = getSpendingPerPerson();

        for(Participant participant: getParticipants()){
            BigDecimal spendingShare = shareMap.get(participant);
            BigDecimal amountActuallySpent = convertIntToDecimal(spendingMap.get(participant));
            BigDecimal credit = amountActuallySpent.subtract(spendingShare);
            creditMap.put(participant, credit);
        }

        return creditMap;
    }

    /***
     * Calculates the total expense spending
     * @return A Map of participants to the total cost of expenses they paid for
     */
    @JsonIgnore
    public HashMap<Participant, Integer> getSpendingPerPerson(){
        HashMap<Participant, Integer> spendingMap = new HashMap<>();

        for (Participant participant:
                participants) {
            spendingMap.put(participant, 0);
        }

        for(Expense expense:
            expenses){
            Participant participant = expense.getOwedTo();
            Integer balance = spendingMap.get(participant);
            spendingMap.put(participant, balance + expense.getPriceInCents());
        }
        return spendingMap;
    }

    static final char[] validCharacters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    // I, 1, O, and 0 omitted for UX (user experience)
    static final int codeLength = 6;
    // 32^6 = 1,073,741,824 combinations

    /***
     * Generates a random Event ID
     * @return a codeLength length ID consisting of human-readable characters
     */
    public static String generateId(){
        int maxRandomBound = validCharacters.length;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < codeLength; i++){
            int charIndex = random.nextInt(0, maxRandomBound);
            char character = validCharacters[charIndex];
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    /***
     * Getter for Event ID
     * @return Event ID
     */
    public String getId() {
        return id;
    }

    /***
     * Setter for Event title
     * @param title the new Event title
     */
    public void setTitle(String title) {
        this.title = title;
        this.lastActivity = new Date();
    }

    /***
     * Getter for Event title
     * @return Event title
     */
    public String getTitle() {
        return title;
    }

    /***
     * Getter for Event creation date
     * @return Event creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /***
     * Adds a Participant to the Participant set
     * @param participant the Participant to add
     */
    public void addParticipant(Participant participant){
        participants.add(participant);
        this.lastActivity = new Date();
    }

    /***
     * Removes a Participant from the Participant set
     * @param participant the Participant to remove
     */
    public void removeParticipant(Participant participant){
        participants.remove(participant);
        this.lastActivity = new Date();
    }

    /***
     * Getter for Event Participant Set
     * @return Event Participant Set
     */
    public Set<Participant> getParticipants() {
        return participants;
    }

    /***
     * Adds an Expense to the Expense set
     * @param expense the Expense to add
     */
    public void addExpense(Expense expense){
        expenses.add(expense);
        this.lastActivity = new Date();
    }

    /***
     * Removes an Expense from the Expense set
     * @param expense the Expense to remove
     */
    public void removeExpense(Expense expense){
        expenses.remove(expense);
        this.lastActivity = new Date();
    }

    /***
     * Getter for Event Expense Set
     * @return Event Expense Set
     */
    public Set<Expense> getExpenses(){
        return expenses;
    }

    /**
     * Returns the tags of the event
     * @return  the tags of the event
     */
    public Set<Tag> getEventTags() {
        return eventTags;
    }

    /**
     * Sets the tags of the event
     * @param eventTags the tags of the event to set it to
     */
    public void setEventTags(Set<Tag> eventTags) {
        this.eventTags = eventTags;
    }

    /**
     * Adds a tag to the event
     * @param eventTag the tag to add
     */
    public void addTag(Tag eventTag){
        this.eventTags.add(eventTag);
    }

    /**
     * Removes a tag from the event
     * @param tag the tag to remove
     */
    public void removeTag(Tag tag){
        this.eventTags.remove(tag);
    }

    /***
     * Getter for Event last date of activity
     * @return Event last date of activity
     */
    public Date getLastActivity(){
        return lastActivity;
    }

    /**
     * Sets the last activity of the event
     * @param lastActivity the last activity of the event
     */
    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    /***
     * Equals method using EqualsBuilder
     * @param obj the Object to compare equality to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /***
     * HashCode method using HashCodeBuilder
     * @return a hashcode for the Event
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /***
     * String representation method using HashCodeBuilder
     * @return a String representation of the Event
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}