package dev.polv.polcinematics.internal.compositions;

import dev.polv.polcinematics.internal.compositions.types.audio.AudioComposition;
import dev.polv.polcinematics.internal.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.internal.compositions.types.camera.ECameraType;
import dev.polv.polcinematics.internal.compositions.types.overlay.EOverlayType;
import dev.polv.polcinematics.internal.compositions.types.overlay.OverlayComposition;

public enum ECompositionType implements ICompositionType {

    CAMERA_COMPOSITION(0, "camera", CameraComposition.class, ECameraType.values()),
    OVERLAY_COMPOSITION(1, "overlay", OverlayComposition.class, EOverlayType.values()),
    AUDIO_COMPOSITION(2, "audio", AudioComposition.class, null);

    private final int id;
    private final String formalName;
    private final Class<? extends Composition> clazz;
    private final ICompositionType[] subtypes;

    ECompositionType(int id, String formalName, Class<? extends Composition> clazz, ICompositionType[] subtypes) {
        this.id = id;
        this.formalName = formalName;
        this.clazz = clazz;
        this.subtypes = subtypes;
    }

    public int getId() {
        return id;
    }

    public static ECompositionType fromName(String name) {
        for (ECompositionType type : ECompositionType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static ECompositionType getById(int id) {
        for (ECompositionType type : ECompositionType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return this.formalName;
    }

    public Class<? extends Composition> getClazz() {
        return clazz;
    }

    public ICompositionType[] getSubtypes() {
        return subtypes;
    }

    public boolean hasSubtypes() {
        return subtypes != null;
    }

    @Override
    public ECompositionType getParent() {
        return null;
    }
}
