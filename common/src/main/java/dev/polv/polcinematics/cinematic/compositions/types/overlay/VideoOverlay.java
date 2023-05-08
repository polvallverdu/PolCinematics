package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.client.players.IMediaPlayer;
import dev.polv.polcinematics.client.players.VideoPlayer;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public class VideoOverlay extends OverlayComposition {

    private VideoPlayer videoPlayer;

    public static final String VIDEO_URL_KEY = "VIDEO_URL";
    public static final String VOLUME_KEY = "VOLUME";

    @Override
    protected void declare() {
        this.declareConstant(VIDEO_URL_KEY, "The url of the video", EValueType.STRING);

        this.declareTimeVariable("X", "Goes from 0% to 100%", EValueType.INTEGER);
        this.declareTimeVariable("Y", "Goes from 0% to 100%", EValueType.INTEGER);
        this.declareTimeVariable("WIDTH", "Goes from 0% to 100%", EValueType.INTEGER, 50);
        this.declareTimeVariable("HEIGHT", "Goes from 0% to 100%", EValueType.INTEGER, 50);
        this.declareTimeVariable("ALPHA", "Goes from 0% to 100%", EValueType.INTEGER, 100);

        this.declareTimeVariable(VOLUME_KEY, "Volume of the music. From 0 to 100", EValueType.INTEGER, 100);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getTimeVariable("X").getValue(time);
        int y = (int) this.getTimeVariable("Y").getValue(time);
        int width = (int) this.getTimeVariable("WIDTH").getValue(time);
        int height = (int) this.getTimeVariable("HEIGHT").getValue(time);
        float alpha = (float) (int) this.getTimeVariable("ALPHA").getValue(time);

        var dimensions = RenderUtils.calculateDimensions(x, y, width, height);

        this.videoPlayer.render(matrixStack, x, y, dimensions.getLeft(), dimensions.getRight(), alpha/100);
    }

    private void initPlayer() {
        if (this.videoPlayer != null)
            this.videoPlayer.stop();
        this.videoPlayer = (VideoPlayer) IMediaPlayer.createPlayer(VideoPlayer.class, this.getConstant(VIDEO_URL_KEY).getValueAsString());
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
        int volume = (int) this.getTimeVariable(VOLUME_KEY).getValue(time);
        volume = Math.min(100, Math.max(0, volume));
        float transVolume = (float) volume / 100;
        transVolume = transVolume * transVolume;
        if (this.videoPlayer.getVolumeFloat() != transVolume)
            this.videoPlayer.setVolume(volume);
    }
}
