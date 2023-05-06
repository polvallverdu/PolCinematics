package dev.polv.polcinematics.cinematic.timelines;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraComposition;

import java.util.List;

public class CameraTimeline extends Timeline {

    public CameraTimeline() {
        super();
    }

    public CameraTimeline(List<WrappedComposition> compositions) {
        super(compositions);
    }

    public CameraComposition getCameraComposition(long time) {
        return (CameraComposition) this.getComposition(time);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("camera", true);
        return json;
    }

}