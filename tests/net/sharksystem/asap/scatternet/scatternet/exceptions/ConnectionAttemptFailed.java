package net.sharksystem.asap.scatternet.scatternet.exceptions;

/**
 * Exception thrown in case that the TIME_OUT for the connection attempt ran out and no connection has been
 * established.
 */
public class ConnectionAttemptFailed extends Exception {
    public ConnectionAttemptFailed(String message) {
        super(message);
    }
}
