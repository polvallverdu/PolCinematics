package dev.polv.polcinematics.cinematic.compositions.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CameraTimeline extends Timeline {

    public CameraTimeline() {
        super();
    }

    public CameraTimeline(List<WrappedComposition> compositions) {
        super(compositions);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("camera", true);
        return json;
    }

}
