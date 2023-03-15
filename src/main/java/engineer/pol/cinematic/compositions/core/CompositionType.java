package engineer.pol.cinematic.compositions.core;

import engineer.pol.cinematic.compositions.audio.AudioComposition;
import engineer.pol.cinematic.compositions.camera.CameraComposition;
import engineer.pol.cinematic.compositions.core.attributes.Attribute;
import engineer.pol.cinematic.compositions.overlay.OverlayComposition;

public enum CompositionType {

    CAMERA_COMPOSITION(0, CameraComposition.class),
    OVERLAY_COMPOSITION(1, OverlayComposition.class),
    AUDIO_COMPOSITION(2, AudioComposition.class);

    private final int id;
    private final Class<? extends Composition> clazz;

    CompositionType(int id, Class<? extends Composition> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public int getId() {
        return id;
    }

    public static CompositionType getById(int id) {
        for (CompositionType type : CompositionType.values()) {
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
