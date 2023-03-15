package engineer.pol.cinematic.compositions.core;

import engineer.pol.cinematic.compositions.audio.AudioComposition;
import engineer.pol.cinematic.compositions.camera.CameraComposition;
import engineer.pol.cinematic.compositions.OverlayComposition;
import engineer.pol.cinematic.compositions.core.attributes.Attribute;

public enum CompositionType {

    BASIC(0, Attribute.class),
    CAMERA_COMPOSITION(1, CameraComposition.class),
    OVERLAY_COMPOSITION(2, OverlayComposition.class),
    AUDIO_COMPOSITION(3, AudioComposition.class);

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
