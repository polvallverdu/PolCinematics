package engineer.pol.client.overlays;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.util.math.MatrixStack;

public class OverlayManager {

    private BlackBarsOverlay blackBarsOverlay;
    private VideoOverlay videoOverlay;

    private boolean init = false;

    public OverlayManager() {
        this.blackBarsOverlay = new BlackBarsOverlay();

        HudRenderCallback.EVENT.register(this::tick);
    }

    public void tick(MatrixStack matrixStack, float tickDelta) {
        if (!init) {
            init = true;
            this.videoOverlay = new VideoOverlay("http://distribution.bbb3d.renderfarming.net/video/mp4/bbb_sunflower_1080p_60fps_normal.mp4");
        }
        this.videoOverlay.render(matrixStack);
        this.blackBarsOverlay.render(matrixStack);
    }

    public void appear() {
        this.blackBarsOverlay.appear();
        this.videoOverlay.appear();
    }

    public void dissapear() {
        this.blackBarsOverlay.disappear();
        this.videoOverlay.disappear();
    }

}
