package dev.polv.polcinematics.exception;

public class NameException extends RuntimeException {

    public NameException(String name) {
        super("The name " + name + " is already used");
    }

}
