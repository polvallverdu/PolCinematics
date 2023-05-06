package dev.polv.polcinematics.exception;

import dev.polv.polcinematics.cinematic.timelines.WrappedComposition;

public class OverlapException extends RuntimeException {

    public OverlapException(WrappedComposition wc, WrappedComposition wcOverlapping) {
        super("Composition " + wc.getComposition().getName() + " (" + wc.getUuid() + ") overlaps with " + wcOverlapping.getComposition().getName() + " (" + wcOverlapping.getUuid() + ")");
    }

}
