package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = Participant.class)
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String legalName;
    private String iban;
    private String bic;
    private String email;

    /***
     * Constructor used by Object Mappers
     */
    @SuppressWarnings("unused")
    public Participant() {}

    /***
     * Default constructor with only a name parameter
     * @param name the name of the Participant
     */
    public Participant(String name) {
        this.name = name;
    }

    /**
     * Constructor used for testing where IDs need to be set.
     * @param id ID of the participant
     * @param name Name of the participant
     */
    public Participant(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /***
     * Specifies whether the Participant's bank account details have been filled in
     * @return a boolean confirming that all bank details are available
     */
    public boolean hasBankAccount(){
        return (!Objects.equals(legalName, "")
                && !Objects.equals(iban, "")
                && !Objects.equals(bic, "")
                && legalName != null
                && iban != null
                && bic != null);
    }

    /***
     * Provides the ID of the Participant
     * @return the Participant's ID
     */
    public long getId() {
        return id;
    }

    /***
     * Changes the Participant's name
     * @param name the new name given
     */
    public void setName(String name) {
        this.name = name;
    }

    /***
     * Provides the name of the Participant
     * @return the Participant's Name
     */
    public String getName() {
        return name;
    }

    /***
     * Provides the real name of the Participant, for bank transfers
     * @return the Participant's bank account holder's name
     */
    public String getLegalName() {
        return legalName;
    }

    /***
     * Changes the real name of the Participant, for bank transfers
     * @param legalName the new Participant's bank account holder's name
     */
    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    /***
     * Provides the IBAN of the Participant, for bank transfers
     * @return the Participant's bank account IBAN
     */
    public String getIban() {
        return iban;
    }

    /***
     * Changes the IBAN of the Participant, for bank transfers
     * @param iban the new Participant's bank account IBAN
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /***
     * Provides the BIC of the Participant, for bank transfers
     * @return the Participant's bank account BIC
     */
    public String getBic() {
        return bic;
    }

    /***
     * Changes the BIC of the Participant, for bank transfers
     * @param bic the new Participant's bank account BIC
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * Gets the email of the Participant
     * @return the Participant's email
     */

    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the Participant
     * @param email the new email of the Participant
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /***
     * Equals method for any Object
     * @param obj the Object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /***
     * Generates a HashCode for the Participant
     * @return a HashCode for the Participant
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /***
     * Generates a String representation for the Participant
     * @return a String representation for the Participant
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
