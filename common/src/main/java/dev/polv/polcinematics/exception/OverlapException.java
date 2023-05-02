package dev.polv.polcinematics.exception;

import dev.polv.polcinematics.cinematic.compositions.core.Timeline;

public class OverlapException extends RuntimeException {

    public OverlapException(Timeline.WrappedComposition wc, Timeline.WrappedComposition wcOverlapping) {
        super("Composition " + wc.getComposition().getName() + " (" + wc.getUUID() + ") overlaps with " + wcOverlapping.getComposition().getName() + "(" + wcOverlapping.getUUID() + ")");
    }

}
