package dev.polv.polcinematics.cinematic.compositions.values;

import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraFrame;
import dev.polv.polcinematics.utils.ColorUtils;

public enum EValueType {

    BOOLEAN("boolean", false, Boolean.class, false),
    INTEGER("integer", 0, Integer.class, true),
    DOUBLE("double", 0D, Double.class, true),
    STRING("string", "", String.class, false),
    COLOR("color", ColorUtils.getColor(0, 0, 0), Integer.class, true),
    ;

    private final String name;
    private final Object defaultValue;
    private final Class<?> objClazz;
    private final boolean easing;

    EValueType(String id, Object defaultValue, Class<?> objClazz, boolean easing) {
        this.name = id;
        this.defaultValue = defaultValue;
        this.objClazz = objClazz;
        this.easing = easing;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Class<?> getClazz() {
        return objClazz;
    }

    public boolean isEasing() {
        return easing;
    }

    public static EValueType fromName(String name) {
        for (EValueType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
