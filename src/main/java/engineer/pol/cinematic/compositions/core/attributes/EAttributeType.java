package engineer.pol.cinematic.compositions.core.attributes;

import engineer.pol.utils.ColorUtils;

public enum EAttributeType {

    BOOLEAN("boolean", false, Boolean.class, false),
    INTEGER("integer", 0, Integer.class, true),
    DOUBLE("double", 0D, Double.class, true),
    STRING("string", "", String.class, false),
    COLOR("color", ColorUtils.getColor(0, 0, 0), Integer.class, true),;

    private final String name;
    private final Object defaultValue;
    private final Class<?> type;
    private final boolean easing;

    EAttributeType(String id, Object defaultValue, Class<?> type, boolean easing) {
        this.name = id;
        this.defaultValue = defaultValue;
        this.type = type;
        this.easing = easing;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isEasing() {
        return easing;
    }

    public static EAttributeType fromName(String name) {
        for (EAttributeType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
