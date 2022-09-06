package net.sharksystem.asap.scatternet.scatternet.exceptions;

/**
 * Exception thrown in case that a SEARCHING peer was interrupted by another peer attempting to connect.
 * This exception should be handled so that the connection with the peer attempting to connect is established.
 */
public class SearchInterruptedByConnection extends Exception {
    public SearchInterruptedByConnection(String message) {
        super(message);
    }
}
