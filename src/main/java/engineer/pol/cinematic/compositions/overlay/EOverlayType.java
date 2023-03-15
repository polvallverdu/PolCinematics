package engineer.pol.cinematic.compositions.overlay;


import engineer.pol.client.players.DEPRECATEDVIDEOPLAYER;

public enum EOverlayType {

    SOLID_COLOR_OVERLAY("solid", SolidColorOverlay.class),
    BLACK_BARS_OVERLAY("black_bars", BlackBarsOverlay.class),
    /* VIDEO_OVERLAY("video", DEPRECATEDVIDEOPLAYER.class),*/;

    private final String name;
    private final Class<? extends OverlayComposition> clazz;

    EOverlayType(String name, Class<? extends OverlayComposition> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public static EOverlayType fromName(String name) {
        for (EOverlayType type : EOverlayType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public Class<? extends OverlayComposition> getClazz() {
        return clazz;
    }

}
