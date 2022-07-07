package engineer.pol.cinematic.timeline;

import engineer.pol.cinematic.timeline.core.Composition;
import engineer.pol.cinematic.timeline.core.CompositionType;
import engineer.pol.client.overlays.Overlay;

import java.util.UUID;


public class OverlayComposition extends Composition {

    private final Overlay overlay;

    public OverlayComposition(UUID uuid, String name, Overlay overlay, long duration) {
        super(uuid, name, duration, CompositionType.OVERLAY_COMPOSITION);
        this.overlay = overlay;
    }

    public Overlay getOverlay() {
        return overlay;
    }

}
