package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.client.players.BrowserView;
import dev.polv.polcinematics.utils.DeclarationUtils;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class VideoOverlay extends OverlayComposition {

    private BrowserView browserView;

    // TODO: Temp
    private static final String URL = "http://localhost:5500";
    public static final String VIDEO_URL_KEY = "VIDEO_URL";

    private float volume = 1;

    @Override
    protected void declare() {
        this.declareConstant(VIDEO_URL_KEY, "The url of the video", EValueType.STRING);

        DeclarationUtils.declareScreenTimevars(this);
        DeclarationUtils.declareVolumeTimevar(this);
//        DeclarationUtils.declareAlphaTimevar(this);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        double x = (double) this.getTimeVariable(DeclarationUtils.X_KEY).getValue(time);
        double y = (double) this.getTimeVariable(DeclarationUtils.Y_KEY).getValue(time);
        double width = (double) this.getTimeVariable(DeclarationUtils.WIDTH_KEY).getValue(time);
        double height = (double) this.getTimeVariable(DeclarationUtils.HEIGHT_KEY).getValue(time);

        var dimensions = RenderUtils.calculateDimensions(x, y, width, height);

        this.browserView.render(matrixStack, RenderUtils.calculateXAxis(x), RenderUtils.calculateYAxis(y), dimensions.getLeft(), dimensions.getRight());
    }

    public String getVideoUrl() {
        return this.getConstant(VIDEO_URL_KEY).getValueAsString();
    }

    @Override
    public void onCinematicLoad() {
        String actualUrl = URL + "?url=" + getVideoUrl();
        this.browserView = new BrowserView(actualUrl);
        this.browserView.newUrl(actualUrl);
    }

    @Override
    public void onCinematicUnload() {
        MinecraftClient.getInstance().executeSync(this.browserView::stop);
    }

    @Override
    public void onCompositionStart() {
        this.browserView.runJS("resume();");
    }

    @Override
    public void onCompositionPause() {
        this.browserView.runJS("pause();");
    }

    @Override
    public void onCompositionEnd() {
        this.browserView.runJS("pause();");
    }

    @Override
    public void onCompositionResume() {
        this.browserView.runJS("resume();");
    }

    @Override
    public void onCinematicTimeChange(long time) {
        this.browserView.runJS("seekTo(" + (time/1000D) + ");");
    }

    @Override
    public void onCompositionTick(long time) {
        double volume = (double) this.getTimeVariable(DeclarationUtils.VOLUME_KEY).getValue(time);
        volume = Math.min(100, Math.max(0, volume));
        float transVolume = (float) volume / 100;
        transVolume = transVolume * transVolume;
        if (this.volume != transVolume) {
            this.volume = transVolume;
            this.browserView.runJS("setVolume(" + this.volume + ");");
        }
    }
}
