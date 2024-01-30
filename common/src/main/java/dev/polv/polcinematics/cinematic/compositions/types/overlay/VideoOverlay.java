package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.client.players.DummyPlayer;
import dev.polv.polcinematics.client.players.IMediaPlayer;
import dev.polv.polcinematics.client.players.VideoPlayer;
import dev.polv.polcinematics.utils.DeclarationUtils;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public class VideoOverlay extends OverlayComposition {

    private IMediaPlayer videoPlayer;

    public static final String VIDEO_URL_KEY = "VIDEO_URL";

    @Override
    protected void declare() {
        this.declareConstant(VIDEO_URL_KEY, "The url of the video", EValueType.STRING);

        DeclarationUtils.declareScreenTimevars(this);
        DeclarationUtils.declareAlphaTimevar(this);
        DeclarationUtils.declareVolumeTimevar(this);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        if (this.videoPlayer instanceof DummyPlayer) return;

        double x = (double) this.getTimeVariable(DeclarationUtils.X_KEY).getValue(time);
        double y = (double) this.getTimeVariable(DeclarationUtils.Y_KEY).getValue(time);
        double width = (double) this.getTimeVariable(DeclarationUtils.WIDTH_KEY).getValue(time);
        double height = (double) this.getTimeVariable(DeclarationUtils.HEIGHT_KEY).getValue(time);
        float alpha = (float) (int) this.getTimeVariable(DeclarationUtils.ALPHA_KEY).getValue(time);

        var position = RenderUtils.calculateXYAxis(x, y);
        var dimensions = RenderUtils.calculateXYAxis(width, height);

        ((VideoPlayer) this.videoPlayer).render(matrixStack, position.getLeft(), position.getRight(), dimensions.getLeft(), dimensions.getRight(), alpha/100);
    }

    private void initPlayer() {
        if (this.videoPlayer != null)
            this.videoPlayer.stop();
        this.videoPlayer = IMediaPlayer.createPlayer(VideoPlayer.class, this.getConstant(VIDEO_URL_KEY).getValueAsString());
    }

    @Override
    public void onCinematicLoad() {
        this.initPlayer();
    }

    @Override
    public void onCinematicUnload() {
        this.videoPlayer.stop();
    }

    @Override
    public void onCompositionStart() {
        this.videoPlayer.play();
    }

    @Override
    public void onCompositionPause() {
        this.videoPlayer.pause();
    }

    @Override
    public void onCompositionEnd() {
        this.videoPlayer.pause();
    }

    @Override
    public void onCompositionResume() {
        this.videoPlayer.resume();
    }

    @Override
    public void onCinematicTimeChange(long time) {
        this.videoPlayer.setTime(time);
    }

    @Override
    public void onCompositionTick(long time) {
        double volume = (double) this.getTimeVariable(DeclarationUtils.VOLUME_KEY).getValue(time);
        volume = Math.min(100, Math.max(0, volume));
        float transVolume = (float) volume / 100;
        transVolume = transVolume * transVolume;
        if (this.videoPlayer.getVolumeFloat() != transVolume)
            this.videoPlayer.setVolume(transVolume);
    }
}
