package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.value.EValueType;
import dev.polv.polcinematics.client.players.AudioPlayer;
import dev.polv.polcinematics.client.players.IMediaPlayer;
import dev.polv.polcinematics.client.players.VideoPlayer;
import dev.polv.polcinematics.utils.BasicCompositionData;
import net.minecraft.client.util.math.MatrixStack;

import java.util.UUID;

public class VideoOverlay extends OverlayComposition {

    private VideoPlayer videoPlayer;

    public static final String VIDEO_URL_KEY = "VIDEO_URL";

    @Override
    protected void declareVariables() {
        this.declareProperty(VIDEO_URL_KEY, "The url of the video", EValueType.STRING);

        this.declareAttribute("X", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("ALPHA", "Goes from 0.0 to 1.0", EValueType.DOUBLE);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getAttribute("X").getValue(time);
        int y = (int) this.getAttribute("Y").getValue(time);
        int width = (int) this.getAttribute("WIDTH").getValue(time);
        int height = (int) this.getAttribute("HEIGHT").getValue(time);
        double alpha = (double) this.getAttribute("ALPHA").getValue(time);

        this.videoPlayer.render(matrixStack, x, y, width, height, (float) alpha);
    }

    public void setVideo(String videoUrl) {
        this.getProperty(VIDEO_URL_KEY).setValue(videoUrl);
        this.initPlayer();
    }

    private void initPlayer() {
        if (this.videoPlayer != null)
            this.videoPlayer.stop();
        this.videoPlayer = (VideoPlayer) IMediaPlayer.createPlayer(VideoPlayer.class, this.getProperty(VIDEO_URL_KEY).getValueAsString());
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
