package engineer.pol.cinematic.compositions.core;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.camera.CameraComposition;
import engineer.pol.cinematic.compositions.core.attributes.Attribute;
import engineer.pol.cinematic.compositions.core.attributes.AttributeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class Composition {

    private UUID uuid;
    private String name;
    private long duration;
    private final CompositionType type;

    private final AttributeList attributes;

    public Composition(UUID uuid, String name, long duration, CompositionType type) {
        this(uuid, name, duration, type, null);
    }

    public Composition(UUID uuid, String name, long duration, CompositionType type, AttributeList attributes) {
        this.uuid = uuid;
        this.name = name;
        this.duration = duration;
        this.type = type;
        this.attributes = attributes == null ? new AttributeList(this) : attributes;
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

    public AttributeList getAttributesList() {
        return attributes;
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
