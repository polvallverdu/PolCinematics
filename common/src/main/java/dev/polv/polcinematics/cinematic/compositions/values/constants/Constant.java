package dev.polv.polcinematics.cinematic.compositions.values.constants;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraFrame;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.cinematic.compositions.values.Value;

import java.awt.*;
import java.util.UUID;

public class Constant {

    private final UUID uuid;
    private final String key;
    private final String description;
    private final Value value;

    protected Constant(UUID uuid, String key, String description, EValueType type) {
        this(uuid, key, description, new Value(type));
    }

    protected Constant(UUID uuid, String key, String description, Value value) {
        this.uuid = uuid;
        this.key = key;
        this.description = description;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public Object getValue() {
        return value.getValue();
    }

    public String getValueAsString() {
        return value.getValueAsString();
    }

    public Integer getValueAsInteger() {
        return value.getValueAsInteger();
    }

    public Double getValueAsDouble() {
        return value.getValueAsDouble();
    }

    public Boolean getValueAsBoolean() {
        return value.getValueAsBoolean();
    }

    public Color getValueAsColor() {
        return value.getValueAsColor();
    }

    public CameraFrame getValueAsCameraPos() {
        return value.getValueAsCameraPos();
    }

    public void setValue(Object value) {
        this.value.setValue(value);
    }

    public EValueType getType() {
        return value.getType();
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", uuid.toString());
        jsonObject.addProperty("key", key);
        jsonObject.addProperty("description", description);
        jsonObject.add("value", value.toJson());
        return jsonObject;
    }

    public static Constant fromJson(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String key = json.get("key").getAsString();
        String description = json.get("description").getAsString();
        Value value = Value.fromJson(json.get("value").getAsJsonObject());
        return new Constant(uuid, key, description, value);
    }
}
