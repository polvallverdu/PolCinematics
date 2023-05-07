package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.client.players.IMediaPlayer;
import dev.polv.polcinematics.client.players.VideoPlayer;
import net.minecraft.client.util.math.MatrixStack;

public class VideoOverlay extends OverlayComposition {

    private VideoPlayer videoPlayer;

    public static final String VIDEO_URL_KEY = "VIDEO_URL";

    @Override
    protected void declare() {
        this.declareConstant(VIDEO_URL_KEY, "The url of the video", EValueType.STRING);

        this.declareTimeVariable("X", "Goes from 0% to 100%", EValueType.INTEGER);
        this.declareTimeVariable("Y", "Goes from 0% to 100%", EValueType.INTEGER);
        this.declareTimeVariable("WIDTH", "Goes from 0% to 100%", EValueType.INTEGER);
        this.declareTimeVariable("HEIGHT", "Goes from 0% to 100%", EValueType.INTEGER);
        this.declareTimeVariable("ALPHA", "Goes from 0.0 to 1.0", EValueType.DOUBLE);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getTimeVariable("X").getValue(time);
        int y = (int) this.getTimeVariable("Y").getValue(time);
        int width = (int) this.getTimeVariable("WIDTH").getValue(time);
        int height = (int) this.getTimeVariable("HEIGHT").getValue(time);
        double alpha = (double) this.getTimeVariable("ALPHA").getValue(time);

        this.videoPlayer.render(matrixStack, x, y, width, height, (float) alpha);
    }

    public void setVideo(String videoUrl) {
        this.getConstant(VIDEO_URL_KEY).setValue(videoUrl);
        this.initPlayer();
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
}
