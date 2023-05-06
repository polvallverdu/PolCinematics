package dev.polv.polcinematics.cinematic.compositions.types.camera;

import dev.polv.polcinematics.cinematic.compositions.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.ICompositionType;

public enum ECameraType implements ICompositionType {

    PLAYER("player", PlayerCameraComposition.class),
    FIXED("fixed", FixedCameraComposition.class),
    SLERP("slerp", SlerpCameraComposition.class),
    LERP("lerp", LerpCameraComposition.class),
    ;

    private final String name;
    private final Class<? extends CameraComposition> clazz;

    ECameraType(String name, Class<? extends CameraComposition> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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

    @Override
    public ECompositionType getParent() {
        return ECompositionType.CAMERA_COMPOSITION;
    }
}
