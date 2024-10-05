package client.Exceptions;

public class InvalidLanguageFormatException extends RuntimeException {
    /**
     * Thrown when the language string doesn't follow the format laid out in the comment in the config file.
     */
    public InvalidLanguageFormatException() {
        super("Language has incorrect format");
    }
}
