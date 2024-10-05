package server.exceptions;

public class TagNotFoundException extends RuntimeException {

    private long tag;

    /**
     * Unchecked exception thrown when tag is not found
     * @param tagId The tag
     */
    public TagNotFoundException(long tagId) {
        super("Tag with ID: " + tagId + " is not in the repository.");
        this.tag = tagId;
    }

    /**
     * Get the tag that is not found
     * @return The tag
     */
    public long getTag() {
        return tag;
    }
}
