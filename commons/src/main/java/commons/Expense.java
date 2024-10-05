package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

import java.util.HashSet;

import java.util.Set;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Expense.class)
public class Expense{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private int priceInCents;
    private Date date;
    private String currency;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE})
    private Tag expenseTag;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Participant owedTo;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Participant> participantsInExpense;

    /**
     * Default constructor
     */
    @SuppressWarnings("unused")
    public Expense() {}

    /**
     * Constructor
     * @param name the name of the expense
     * @param priceInCents the price of the expense in cents
     * @param date the date of the expense
     * @param owedTo the participant who paid the expense
     */
    public Expense(String name, int priceInCents, Date date, Participant owedTo) {
        this.name = name;
        this.priceInCents = priceInCents;
        this.date = date;
        this.owedTo = owedTo;
        this.participantsInExpense = new HashSet<>();
    }

    /**
     * Returns the id of the expense
     * @return the id of the expense
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the name of the expense
     * @param name the name of the expense
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the expense
     * @return the name of the expense
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the price of the expense in cents
     * @param priceInCents the price of the expense in cents
     */
    public void setPriceInCents(int priceInCents) {
        this.priceInCents = priceInCents;
    }

    /**
     * Returns the price of the expense in cents
     * @return the price of the expense in cents
     */
    public int getPriceInCents() {
        return priceInCents;
    }

    /**
     * Sets the date of the expense
     * @param date the date of the expense
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Returns the date of the expense
     * @return the date of the expense
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the currency of the expense
     * @param currency the currency of the expense
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Returns the currency of the expense
     * @return the currency of the expense
     */
    public String getCurrency() {
        return this.currency;
    }

    /**
     * Sets the participant who paid the expense
     * @param participant the participant who paid the expense
     */
    public void setOwedTo(Participant participant){
        this.owedTo = participant;
    }

    /**
     * Returns the participant who paid the expense
     * @return the participant who paid the expense
     */
    public Participant getOwedTo(){
        return owedTo;
    }

    /**
     * Getter for the expense tag
     * @return the expense tag
     */
    public Tag getExpenseTag() {
        return expenseTag;
    }

    /**
     * Setter for the expense tag
     * @param expenseTag the expense tag to set it to
     */
    public void setExpenseTag(Tag expenseTag) {
        this.expenseTag = expenseTag;
    }

    /**
     * Gets the participants in the expense
     * @return the participants in the expense
     */
    public Set<Participant> getParticipantsInExpense() {
        return participantsInExpense;
    }

    /**
     * Adds a participant to the expense
     * @param participant the participant to add
     */
    public void addParticipantToExpense(Participant participant) {
        participantsInExpense.add(participant);
    }

    /**
     * Removes a participant from the expense
     * @param participant the participant to remove
     */
    public void removeParticipantFromExpense(Participant participant) {
        this.participantsInExpense.remove(participant);
    }

    /**
     * Equality checker
     * @param obj the object to compare to
     * @return true if equal false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Returns the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns the string representation of the expense
     * @return the string representation of the expense
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    /**
     * Sets the participants in the expense
     * @param participants the participants to set
     */
    public void setParticipantToExpense(Set<Participant> participants) {
        participantsInExpense = participants;
    }
}
