package dev.polv.polcinematics.exception;

public class CompositionException extends RuntimeException {

    public CompositionException(String message) {
        super(message);
    }

    public CompositionException(String message, Throwable cause) {
        super(message, cause);
    }

}
