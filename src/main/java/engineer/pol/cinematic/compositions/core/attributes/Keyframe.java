package engineer.pol.cinematic.compositions.core.attributes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import engineer.pol.utils.math.Easing;

public class Keyframe {

    private long time;
    private Easing easing;
    private double value;

    public Keyframe(long time, double value) {
        this(time, value, Easing.LINEAR);
    }

    public Keyframe(long time, double value, Easing easing) {
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("time", new JsonPrimitive(time));
        json.add("easing", new JsonPrimitive(easing.getId()));
        json.add("value", new JsonPrimitive(value));
        return json;
    }

    public static Keyframe fromJson(JsonObject json) {
        long time = json.get("time").getAsLong();
        Easing easing = Easing.fromId(json.get("easing").getAsInt());
        double value = json.get("value").getAsDouble();
        return new Keyframe(time, value, easing);
    }

}
