package engineer.pol.cinematic;

import engineer.pol.cinematic.timeline.CameraComposition;
import engineer.pol.cinematic.timeline.OverlayComposition;
import engineer.pol.cinematic.timeline.core.Composition;

import java.util.List;

public class Cinematic {

    private class WrappedComposition {

            private final Composition timeline;
            private long startTime;

            public WrappedComposition(Composition timeline) {
                this.timeline = timeline;
            }

            public long getDuration() {
                return timeline.getDuration();
            }

            public void setStartTime(long startTime) {
                this.startTime = startTime;
            }

            public long getStartTime() {
                return startTime;
            }

            public long getFinishTime() {
                return startTime + getDuration();
            }

            public boolean isCamera() {
                return timeline instanceof CameraComposition;
            }

            public CameraComposition getAsCameraTimeline() {
                return (CameraComposition) timeline;
            }

            public boolean isOverlay() {
                return timeline instanceof OverlayComposition;
            }

            public OverlayComposition getAsOverlayTimeline() {
                return (OverlayComposition) timeline;
            }
    }

    private final List<WrappedComposition> cameraTimeline;
    private final List<WrappedComposition>[] overlayTimeline;

    public Cinematic(List<WrappedComposition> cameraTimeline, List<WrappedComposition>[] overlayTimeline) {
        this.cameraTimeline = cameraTimeline;
        this.overlayTimeline = overlayTimeline;
    }



}
