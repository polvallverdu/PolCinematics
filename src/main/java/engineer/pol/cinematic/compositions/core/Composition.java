package engineer.pol.cinematic.compositions.core;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.camera.CameraComposition;

import java.util.UUID;

public abstract class Composition {

    private UUID uuid;
    private String name;
    private long duration;
    private final CompositionType type;

    public Composition(UUID uuid, String name, long duration, CompositionType type) {
        this.uuid = uuid;
        this.name = name;
        this.duration = duration;
        this.type = type;
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

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", this.getUuid().toString());
        json.addProperty("name", this.getName());
        json.addProperty("duration", this.getDuration());
        json.addProperty("type", this.getType().getId());

        return json;
    }

    public static Composition fromJson(JsonObject json) {
        /*CompositionType type = CompositionType.getById(json.get("type").getAsInt());

        if (type == null) return null;

        return switch (type) {
            case BASIC -> null; // No basic type
            case CAMERA_COMPOSITION -> CameraComposition.fromJson(json);
            case OVERLAY_COMPOSITION -> OverlayComposition.fromJson(json);
            case AUDIO_COMPOSITION -> null; // TODO
        };*/
        CompositionType type = CompositionType.getById(json.get("type").getAsInt());
        var compositionClass =  type.getClazz();
        Composition composition = compositionClass.cast(Composition.fromJson(json));
        return composition;
    }

}
