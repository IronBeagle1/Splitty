package client.Exceptions;

public class MissingLanguageTemplateException extends RuntimeException {
    /**
     * Thrown when the language doesn't have a corresponding language template
     */
    public MissingLanguageTemplateException() {
        super("Language template is missing");
    }
}
