package client.Exceptions;

public class InvalidTagException extends Exception {
    /**
     * Checked exception that is thrown when tag is invalid
     * @param message Message of the exception
     */
    public InvalidTagException(String message) {
        super(message);
    }
}
