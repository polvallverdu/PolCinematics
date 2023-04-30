package dev.polv.polcinematics.exception;

public class InvalidCommandValueException extends RuntimeException {
    public InvalidCommandValueException(Throwable e) {
        super(e);
    }

    public InvalidCommandValueException(String message) {
        super(message);
    }
}
