package engineer.pol.cinematic.compositions.core.attributes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import engineer.pol.utils.math.Easing;

public class Keyframe {

    private long time;
    private Easing easing;
    private final EAttributeType type;
    private Object value;

    public Keyframe(long time, Object value, EAttributeType type) {
        this(time, value, type, Easing.LINEAR);
    }

    public Keyframe(long time, Object value, EAttributeType type, Easing easing) {
        this.time = time;
        this.value = value;
        this.type = type;
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

    public Object getValue() {
        return value;
    }

    public double getValueAsDouble() {
        return (double) value;
    }

    public int getValueAsInteger() {
        return (int) value;
    }

    public boolean getValueAsBoolean() {
        return (boolean) value;
    }

    public String getValueAsString() {
        return (String) value;
    }

    public EAttributeType getType() {
        return type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("time", new JsonPrimitive(time));
        json.add("easing", new JsonPrimitive(easing.getId()));
        //json.add("value", new JsonPrimitive(value));
        this.addCorrectValue(json);
        return json;
    }

    private void addCorrectValue(JsonObject json) {
        switch (type) {
            case DOUBLE -> json.add("value", new JsonPrimitive((double) value));
            case INTEGER -> json.add("value", new JsonPrimitive((int) value));
            case BOOLEAN -> json.add("value", new JsonPrimitive((boolean) value));
            case STRING -> json.add("value", new JsonPrimitive((String) value));
        }
    }

    public static Keyframe fromJson(JsonObject json, EAttributeType type) {
        long time = json.get("time").getAsLong();
        Easing easing = Easing.fromId(json.get("easing").getAsInt());
        //Object value = json.get("value").getAs();
        Object value = readCorrectValue(json, type);
        return new Keyframe(time, value, type, easing);
    }

    private static Object readCorrectValue(JsonObject json, EAttributeType type) {
        switch (type) {
            case DOUBLE -> {
                return json.get("value").getAsDouble();
            }
            case INTEGER -> {
                return json.get("value").getAsInt();
            }
            case BOOLEAN -> {
                return json.get("value").getAsBoolean();
            }
            case STRING -> {
                return json.get("value").getAsString();
            }
            default -> {
                return json.get("value").getAsString();
            }
        }
    }

}
