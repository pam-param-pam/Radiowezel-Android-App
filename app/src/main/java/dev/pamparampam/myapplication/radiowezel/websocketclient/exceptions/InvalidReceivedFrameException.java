package dev.pamparampam.myapplication.radiowezel.websocketclient.exceptions;

/**
 * Exception which indicates that the handshake received from the server is
 * invalid
 *
 * @author Gustavo Avila
 *
 */
public class InvalidReceivedFrameException extends RuntimeException {
    public InvalidReceivedFrameException(String message) {
        super(message);
    }
}
