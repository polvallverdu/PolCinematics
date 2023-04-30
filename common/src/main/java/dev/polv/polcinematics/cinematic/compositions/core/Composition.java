package dev.polv.polcinematics.cinematic.compositions.core;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.audio.AudioComposition;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.camera.ECameraType;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.Attribute;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.helpers.CompositionInfo;
import dev.polv.polcinematics.cinematic.compositions.core.value.CompositionProperties;
import dev.polv.polcinematics.cinematic.compositions.core.value.EValueType;
import dev.polv.polcinematics.cinematic.compositions.core.value.Value;
import dev.polv.polcinematics.cinematic.compositions.overlay.EOverlayType;
import dev.polv.polcinematics.exception.CompositionException;
import dev.polv.polcinematics.utils.BasicCompositionData;
import dev.polv.polcinematics.utils.EnumUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Composition {

    private UUID uuid;
    private String name;
    private long duration;
    private ECompositionType type;

    private CompositionProperties properties;
    private AttributeList attributes;

    protected Composition() {
    }

    protected void init(String name, long duration, ECompositionType type) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.duration = duration;
        this.type = type;

        this.properties = new CompositionProperties();
        this.attributes = new AttributeList();

        this.declareVariables();
    }

    protected abstract void declareVariables();

    protected void readComposition(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        this.uuid = data.uuid();
        this.name = data.name();
        this.duration = data.duration();
        this.type = ECompositionType.getById(json.get("type").getAsInt());
    }

    protected CompositionProperties readProperties(JsonObject json) {
        this.properties = CompositionProperties.fromJson(json.get("properties").getAsJsonObject());
        return CompositionProperties.fromJson(json.get("properties").getAsJsonObject());
    }

    protected AttributeList readAttributeList(JsonObject json) {
        this.attributes = AttributeList.fromJson(json.get("attributes").getAsJsonObject());
        return AttributeList.fromJson(json.get("attributes").getAsJsonObject());
    }

    protected void configure(JsonObject json) {
        // TO IMPLEMENT
    };

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public ECompositionType getType() {
        return type;
    }

    /*protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }*/

    /*protected void setName(String name) {
        this.name = name;
    }*/

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public CompositionProperties getProperties() {
        return properties;
    }

    public Value getProperty(String key) {
        return properties.getValue(key);
    }

    public Value declareProperty(String key, String description, EValueType type) { // Not using description. Leaving here for the future.
        Value value = this.getProperty(key);
        if (value != null) {
            return value;
        }

        return properties.createProperty(key, type, type.getDefaultValue());
    }

    public Attribute getAttribute(String attributeName) {
        return attributes.getAttribute(attributeName);
    }

    public AttributeList getAttributesList() {
        return attributes;
    }

    public Attribute declareAttribute(String name, String description, EValueType type) {
        Attribute atr = this.getAttribute(name);
        if (atr != null) {
            atr.setDescription(description);
            return atr;
        }

        return attributes.createAttribute(name, description, type);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", this.getUuid().toString());
        json.addProperty("name", this.getName());
        json.addProperty("duration", this.getDuration());
        json.addProperty("type", this.getType().getId());

        json.add("properties", this.properties.toJson());
        json.add("attributes", this.getAttributesList().toJson());

        return json;
    }

    public static Composition fromJson(JsonObject json) throws CompositionException {
        ECompositionType type = ECompositionType.getById(json.get("type").getAsInt());
        Class<? extends Composition> clazz = type.getClazz();
        /*var compositionClass = type.getClazz();
        Method m = compositionClass.getMethod("fromJson", JsonObject.class);
        Composition compo = (Composition) m.invoke(null, json);*/

        /*switch (type) {
            case CAMERA_COMPOSITION -> {
                ECameraType subtype = ECameraType.fromName(json.get("subtype").getAsString());
                clazz = subtype.getClazz();
            }
            case OVERLAY_COMPOSITION -> {
                EOverlayType subtype = EOverlayType.fromName(json.get("subtype").getAsString());
                clazz = subtype.getClazz();
            }
        }*/

        if (type.hasSubtypes()) {
            ICompositionType subtype = EnumUtils.findSubtype(type, json.get("subtype").getAsString());
            if (subtype != null) {
                clazz = subtype.getClazz();
            }
        }

        Composition compo;
        try {
            compo = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CompositionException("Could not create new instance of composition class " + clazz.getName(), e);
        }

        compo.readComposition(json);
        compo.readProperties(json);
        compo.readAttributeList(json);
        compo.configure(json);
        return compo;
    }

    // Functions that can be overwritten

    /**
     * Called when the cinematic is loaded at the client
     */
    public void onCinematicLoad() {}

    /**
     * Called when the cinematic is unloaded at the client
     */
    public void onCinematicUnload() {}

    /**
     * Called when the cinematic starts
     */
    public void onCinematicStart() {}

    /**
     * Called when the cinematic ticks
     */
    //public void onCinematicTick() {}

    /**
     * Called when the cinematic stops
     */
    public void onCinematicStop() {}

    /**
     * Called when the cinematic is paused
     */
    public void onCinematicPause() {}

    /**
     * Called when the cinematic is resumed
     */
    public void onCinematicResume() {}

    /**
     * Called when the cinematic time is changed
     * @param time new time
     */
    public void onCinematicTimeChange(long time) {}

    /**
     * Called when the composition is shown
     */
    public void onCompositionStart() {}

    /**
     * Called when the cinematic is paused and the composition is visible
     */
    public void onCompositionPause() {}

    /**
     * Called when the cinematic is resumed and the composition is visible
     */
    public void onCompositionResume() {}

    /**
     * Called when the composition ticks
     */
    public void onCompositionTick(long time) {}

    /**
     * Called when the composition is no longer shown
     */
    public void onCompositionEnd() {}

    /**
     * Called when requesting info about the composition
     */
    public void onInfoRequest(CompositionInfo info) {}

}
