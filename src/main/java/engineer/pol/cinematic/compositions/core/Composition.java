package engineer.pol.cinematic.compositions.core;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.camera.CameraComposition;
import engineer.pol.cinematic.compositions.core.attributes.Attribute;
import engineer.pol.cinematic.compositions.core.attributes.AttributeList;
import engineer.pol.cinematic.compositions.core.attributes.EAttributeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class Composition {

    private UUID uuid;
    private String name;
    private long duration;
    private final CompositionType type;

    private AttributeList attributes;

    public Composition(UUID uuid, String name, long duration, CompositionType type) {
        this(uuid, name, duration, type, new AttributeList());
    }

    public Composition(UUID uuid, String name, long duration, CompositionType type, AttributeList attributes) {
        this.uuid = uuid;
        this.name = name;
        this.duration = duration;
        this.type = type;
        this.attributes = attributes;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public CompositionType getType() {
        return type;
    }

    protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public ArrayList<Attribute> getAttributes() {
        return new ArrayList<>(attributes.getAttributes());
    }

    public Attribute getAttribute(String attributeName) {
        return attributes.getAttribute(attributeName);
    }

    public AttributeList getAttributesList() {
        return attributes;
    }

    public Attribute declareAttribute(String name, String description, EAttributeType type) {
        if (this.getAttribute(name) != null) {
            Attribute atr = this.getAttribute(name);
            atr.setDescription(description);
            return atr;
        }
        else {
            return attributes.createAttribute(name, description, type);
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", this.getUuid().toString());
        json.addProperty("name", this.getName());
        json.addProperty("duration", this.getDuration());
        json.addProperty("type", this.getType().getId());
        json.add("attributes", this.getAttributesList().toJson());

        return json;
    }

    public static Composition fromJson(JsonObject json) {
        /*CompositionType type = CompositionType.getById(json.get("type").getAsInt());

        if (type == null) return null;

        return switch (type) {
            case BASIC -> null; // No basic type
            case CAMERA_COMPOSITION -> CameraComposition.fromJson(json);
            case OVERLAY_COMPOSITION -> OverlayComposition.fromJson(json);
            case AUDIO_COMPOSITION -> null;
        };*/
        CompositionType type = CompositionType.getById(json.get("type").getAsInt());
        var compositionClass =  type.getClazz();
        Composition composition = compositionClass.cast(Composition.fromJson(json));
        return composition;
    }

}
