package engineer.pol.cinematic.compositions.core.attributes;

public enum EAttributeType {
    BOOLEAN("boolean", Boolean.class),
    INTEGER("integer", Integer.class),
    DOUBLE("double", Double.class),
    STRING("string", String.class),;

    private final String name;
    private final Class<?> type;

    EAttributeType(String id, Class<?> type) {
        this.name = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
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
