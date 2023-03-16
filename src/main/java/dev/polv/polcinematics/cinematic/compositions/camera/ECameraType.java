package dev.polv.polcinematics.cinematic.compositions.camera;

public enum ECameraType {

    PLAYER("player", PlayerCameraComposition.class),
    /*FIRST_PERSON("first_person"),
    THIRD_PERSON("third_person"),*/
    FIXED("fixed", FixedCameraComposition.class),;

    private final String name;
    private final Class<? extends CameraComposition> clazz;

    ECameraType(String name, Class<? extends CameraComposition> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<? extends CameraComposition> getClazz() {
        return clazz;
    }

    public static ECameraType fromName(String name) {
        for (ECameraType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

}
