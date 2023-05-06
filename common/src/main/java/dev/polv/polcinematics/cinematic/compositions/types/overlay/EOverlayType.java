package dev.polv.polcinematics.cinematic.compositions.types.overlay;


import dev.polv.polcinematics.cinematic.compositions.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.ICompositionType;

public enum EOverlayType implements ICompositionType {

    SOLID_COLOR_OVERLAY("solid", SolidColorOverlay.class),
    BLACK_BARS_OVERLAY("black_bars", BlackBarsOverlay.class),
    BROWSER_OVERLAY("browser", WebBrowserOverlay.class),
    VIDEO_OVERLAY("video", VideoOverlay.class),
    IMAGE_OVERLAY("image", ImageOverlay.class),
    //TEXT_OVERLAY("text", TextOverlay.class),
    ;

    private final String name;
    private final Class<? extends OverlayComposition> clazz;

    EOverlayType(String name, Class<? extends OverlayComposition> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Override
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

    @Override
    public Class<? extends OverlayComposition> getClazz() {
        return clazz;
    }

    @Override
    public ECompositionType getParent() {
        return ECompositionType.OVERLAY_COMPOSITION;
    }

}
