package dev.polv.polcinematics.cinematic.compositions;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.values.constants.Constant;
import dev.polv.polcinematics.cinematic.compositions.values.timevariables.TimeVariable;
import dev.polv.polcinematics.cinematic.compositions.values.timevariables.CompositionTimeVariables;
import dev.polv.polcinematics.cinematic.compositions.helpers.CompositionInfo;
import dev.polv.polcinematics.cinematic.compositions.values.constants.CompositionConstants;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.exception.CompositionException;
import dev.polv.polcinematics.utils.BasicCompositionData;
import dev.polv.polcinematics.utils.EnumUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public abstract class Composition {

    private UUID uuid;
    private String name;
    private ECompositionType type;
    @Nullable private ICompositionType subtype;

    private CompositionConstants constants;
    private CompositionTimeVariables timeVariables;

    protected Composition() {
    }

    protected void init(String name, ECompositionType type) {
        init(name, type, null);
    }

    protected void init(String name, ECompositionType type, @Nullable ICompositionType subtype) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.subtype = subtype;

        this.constants = new CompositionConstants();
        this.timeVariables = new CompositionTimeVariables();

        this.declare();
    }

    public static Composition create(String name, ICompositionType typeOrSubtype) {
        Composition compo;
        // Separate type from subtype.
        ECompositionType type = typeOrSubtype.getParent();
        ICompositionType subtype = typeOrSubtype;
        if (type == null && ((ECompositionType) subtype).hasSubtypes()) {
            throw new IllegalArgumentException("Type cannot be a CompositionType with subtypes");
        }

        // new Composition() from the class in typeOrSubtype. Constructor is empty.
        try {
            var compositionClass = typeOrSubtype.getClazz();
            compo = compositionClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CompositionException("Could not create composition", e);
        }

        if (type == null) {
            compo.init(name, (ECompositionType) subtype);
        } else {
            compo.init(name, type, subtype);
        }

        return compo;
    }

    protected abstract void declare();

    protected void readComposition(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        this.uuid = data.uuid();
        this.name = data.name();
        this.type = ECompositionType.getById(json.get("type").getAsInt());
        this.subtype = this.type.hasSubtypes() ? EnumUtils.findSubtype(this.type, json.get("subtype").getAsString()) : null;
    }

    protected CompositionConstants readConstants(JsonObject json) {
        this.constants = CompositionConstants.fromJson(json.get("constants").getAsJsonObject());
        return CompositionConstants.fromJson(json.get("constants").getAsJsonObject());
    }

    protected CompositionTimeVariables readTimeVariables(JsonObject json) {
        this.timeVariables = CompositionTimeVariables.fromJson(json.get("timevariables").getAsJsonObject());
        return CompositionTimeVariables.fromJson(json.get("timevariables").getAsJsonObject());
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

    public ECompositionType getType() {
        return type;
    }

    public ICompositionType getSubtype() {
        return subtype;
    }

    /*protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }*/

    /*protected void setName(String name) {
        this.name = name;
    }*/

    public CompositionConstants getCompositionConstants() {
        return constants;
    }

    public Constant getConstant(String key) {
        return constants.getConstant(key);
    }

    public Constant declareConstant(String key, String description, EValueType type) {
        Constant value = this.getConstant(key);
        if (value != null) {
            return value;
        }

        return constants.createConstant(key, description, type);
    }

    public TimeVariable getTimeVariable(String timevariableKey) {
        return timeVariables.getTimeVariables(timevariableKey);
    }

    public CompositionTimeVariables getCompositionTimeVariables() {
        return timeVariables;
    }

    public TimeVariable declareTimeVariable(String name, String description, EValueType type) {
        TimeVariable atr = this.getTimeVariable(name);
        if (atr != null) {
            atr.setDescription(description);
            return atr;
        }

        return timeVariables.createTimeVariable(name, description, type);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", this.getUuid().toString());
        json.addProperty("name", this.getName());
        json.addProperty("type", this.getType().getId());
        if (this.getSubtype() != null)
            json.addProperty("subtype", this.getSubtype().getName());

        json.add("constants", this.constants.toJson());
        json.add("timevariables", this.timeVariables.toJson());

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
        compo.readConstants(json);
        compo.readTimeVariables(json);
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
