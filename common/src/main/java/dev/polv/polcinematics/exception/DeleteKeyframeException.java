package dev.polv.polcinematics.exception;

public class DeleteKeyframeException extends Exception {

    String message;

    public DeleteKeyframeException() {
        this.message = "You can't delete the first keyframe";
    }

    public DeleteKeyframeException(String message) {
        this.message = message;
    }

}
