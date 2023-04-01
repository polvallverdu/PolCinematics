package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.EAttributeType;
import dev.polv.polcinematics.client.players.IMediaPlayer;
import dev.polv.polcinematics.client.players.VideoPlayer;
import dev.polv.polcinematics.utils.BasicCompositionData;
import net.minecraft.client.util.math.MatrixStack;

import java.util.UUID;

public class VideoOverlay extends OverlayComposition {

    private final String mediaPath;
    private final VideoPlayer videoPlayer;

    public VideoOverlay(UUID uuid, String name, String mediaPath, long duration, AttributeList attributes) {
        super(uuid, name, EOverlayType.VIDEO_OVERLAY, duration, attributes);

        this.mediaPath = mediaPath;
        this.videoPlayer = (VideoPlayer) IMediaPlayer.createPlayer(VideoPlayer.class, mediaPath);

        this.declareAttribute("X", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("ALPHA", "Goes from 0.0 to 1.0", EAttributeType.DOUBLE);
    }

    public VideoOverlay(String name, String mediaPath, long duration) {
        this(UUID.randomUUID(), name, mediaPath, duration, new AttributeList());
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

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("mediaPath", this.mediaPath);
        return json;
    }

    public static VideoOverlay fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json.get("attributes").getAsJsonObject());
        String mediaPath = json.get("mediaPath").getAsString();

        return new VideoOverlay(data.uuid(), data.name(), mediaPath, data.duration(), attributes);
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
