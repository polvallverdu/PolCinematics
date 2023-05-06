package dev.polv.polcinematics.cinematic.compositions.value;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraPos;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraRot;

import java.awt.*;

public class Value {

    private Object value;
    private final EValueType type;

    public Value(Object value, EValueType type) {
        this.value = value;
        this.type = type;
    }

    public Value(EValueType type) {
        this.value = type.getDefaultValue();
        this.type = type;
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

    public Color getValueAsColor() {
        return new Color(this.getValueAsInteger());
    }

    public CameraPos getValueAsCameraPos() {
        return (CameraPos) value;
    }

    public CameraRot getValueAsCameraRot() {
        return (CameraRot) value;
    }

    public EValueType getType() {
        return type;
    }

    public void setValue(Object value) throws IllegalArgumentException {
        this.value = value;
        if (value.getClass() != type.getType()) {
            throw new IllegalArgumentException("Value is not of type " + type.getName());
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", new JsonPrimitive(type.getName()));
        this.addCorrectValue(json);
        return json;
    }

    private void addCorrectValue(JsonObject json) {
        switch (getType()) {
            case DOUBLE -> json.add("value", new JsonPrimitive((double) value));
            case INTEGER, COLOR -> json.add("value", new JsonPrimitive((int) value));
            case BOOLEAN -> json.add("value", new JsonPrimitive((boolean) value));
            case STRING -> json.add("value", new JsonPrimitive((String) value));
            case CAMERAPOS -> json.add("value", ((CameraPos) value).toJson());
            case CAMERAROT -> json.add("value", ((CameraRot) value).toJson());
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public static Value fromJson(JsonObject json) {
        //Object value = json.get("value").getAs();
        EValueType type = EValueType.fromName(json.get("type").getAsString());
        Object value = readCorrectValue(json, type);
        return new Value(value, type);
    }

    private static Object readCorrectValue(JsonObject json, EValueType type) {
        switch (type) {
            case DOUBLE -> {
                return json.get("value").getAsDouble();
            }
            case INTEGER, COLOR -> {
                return json.get("value").getAsInt();
            }
            case BOOLEAN -> {
                return json.get("value").getAsBoolean();
            }
            case STRING -> {
                return json.get("value").getAsString();
            }
            case CAMERAPOS -> {
                return CameraPos.fromJson(json.get("value").getAsJsonObject());
            }
            case CAMERAROT -> {
                return CameraRot.fromJson(json.get("value").getAsJsonObject());
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
