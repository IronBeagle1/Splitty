package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String tagName;
    private String colorCode;

    /**
     * Default constructor
     */
    public Tag(){}

    /**
     * Constructor
     * @param tagName the name of the tag
     * @param colorCode the color code of the tag
     */
    public Tag(String tagName, String colorCode){
        this.tagName = tagName;
        this.colorCode = colorCode;
    }

    /**
     * Getter for the id
     * @return the id of the tag
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the tag name
     * @return the name of the tag
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Getter for the color code
     * @return the color code of the tag
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * Setter for the tag name
     * @param tagName the name of the tag
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Setter for the color code
     * @param colorCode the color code of the tag
     */
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    /**
     * Equality checker
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
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
     * Returns the string representation of the object
     * @return the string representation of the object
     */
    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", tagName='" + tagName + '\'' +
                ", colorCode='" + colorCode + '\'' +
                '}';
    }
}
