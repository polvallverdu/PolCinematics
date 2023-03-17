package dev.polv.polcinematics.cinematic.compositions.core;

import dev.polv.polcinematics.cinematic.compositions.audio.AudioComposition;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.overlay.OverlayComposition;

public enum ECompositionType {

    CAMERA_COMPOSITION(0, CameraComposition.class),
    OVERLAY_COMPOSITION(1, OverlayComposition.class),
    AUDIO_COMPOSITION(2, AudioComposition.class);

    private final int id;
    private final Class<? extends Composition> clazz;

    ECompositionType(int id, Class<? extends Composition> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public int getId() {
        return id;
    }

    public static ECompositionType getById(int id) {
        for (ECompositionType type : ECompositionType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public Class<? extends Composition> getClazz() {
        return clazz;
    }

}
