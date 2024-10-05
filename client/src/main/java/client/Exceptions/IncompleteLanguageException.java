package client.Exceptions;

public class IncompleteLanguageException extends RuntimeException {
    /**
     * Thrown when a language template has a property that has no value attached to it.
     */
    public IncompleteLanguageException() {
        super("Language has an empty property");
    }
}
