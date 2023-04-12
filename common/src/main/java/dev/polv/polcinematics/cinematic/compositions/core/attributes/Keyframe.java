package dev.polv.polcinematics.cinematic.compositions.core.attributes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.polv.polcinematics.cinematic.compositions.core.value.EValueType;
import dev.polv.polcinematics.cinematic.compositions.core.value.Value;
import dev.polv.polcinematics.utils.math.Easing;

import java.awt.*;

public class Keyframe {

    private long time;
    private Easing easing;
    private Value value;

    public Keyframe(long time, Value value) {
        this(time, value, Easing.LINEAR);
    }

    public Keyframe(long time, Value value, Easing easing) {
        this.time = time;
        this.value = value;
        this.easing = easing;
    }

    public boolean hasEasing() {
        return easing != null && easing != Easing.LINEAR;
    }

    public Easing getEasing() {
        return easing;
    }

    public void setEasing(Easing easing) {
        this.easing = easing;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Value getValue() {
        return value;
    }

    public EValueType getType() {
        return this.value.getType();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("time", new JsonPrimitive(time));
        json.add("easing", new JsonPrimitive(easing.getId()));
        //json.add("value", new JsonPrimitive(value));
        json.add("value", value.toJson());
        return json;
    }

    public static Keyframe fromJson(JsonObject json) {
        long time = json.get("time").getAsLong();
        Easing easing = Easing.fromId(json.get("easing").getAsInt());
        Value value = Value.fromJson(json.get("value").getAsJsonObject());
        return new Keyframe(time, value, easing);
    }


}
