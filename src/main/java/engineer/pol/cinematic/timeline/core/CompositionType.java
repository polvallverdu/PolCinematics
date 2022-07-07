package engineer.pol.cinematic.timeline.core;

public enum CompositionType {

    BASIC(0),
    CAMERA_COMPOSITION(1),
    OVERLAY_COMPOSITION(2);

    private final int id;

    CompositionType(int id) {
        this.id = id;
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

}
