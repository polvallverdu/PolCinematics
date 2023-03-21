package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.EAttributeType;
import dev.polv.polcinematics.client.renders.VideoPlayer;
import dev.polv.polcinematics.utils.BasicCompositionData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.math.MatrixStack;

import java.util.UUID;

public class WebBrowserOverlay extends OverlayComposition {

    private String videoPath;
    @Environment(EnvType.CLIENT)
    private final VideoPlayer videoPlayer;

    public WebBrowserOverlay(UUID uuid, String name, String videoPath, long duration, AttributeList attributes) {
        super(uuid, name, EOverlayType.VIDEO_OVERLAY, duration, attributes);
        this.videoPath = videoPath;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.videoPlayer = new VideoPlayer(videoPath);
        } else {
            this.videoPlayer = null;
        }

        this.declareAttribute("X", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
    }

    public WebBrowserOverlay(String name, String videoPath, long duration) {
        this(UUID.randomUUID(), name, videoPath, duration, new AttributeList());
    }

    public void setNewVideoPath(String newVideoPath) {
        this.videoPath = newVideoPath;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            this.videoPlayer.changeMediaPath(newVideoPath);
    }

    @Override
    public void tick(MatrixStack MatrixStack, long time) {
        int x = (int) this.getAttribute("X").getValue(time);
        int y = (int) this.getAttribute("Y").getValue(time);
        int width = (int) this.getAttribute("WIDTH").getValue(time);
        int height = (int) this.getAttribute("HEIGHT").getValue(time);

        this.videoPlayer.render(MatrixStack, x, y, width, height);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("videoPath", this.videoPath); // Injecting videopath variable
        return json;
    }

    public static WebBrowserOverlay fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json.get("attributes").getAsJsonObject());
        String videoPath = json.get("videoPath").getAsString();

        return new WebBrowserOverlay(data.uuid(), data.name(), videoPath, data.duration(), attributes);
    }

    @Override
    public void onCompositionEnd() {
        this.videoPlayer.stop();
    }

    @Override
    public void onCompositionPause() {
        this.videoPlayer.pause();
    }

    @Override
    public void onCompositionResume() {
        this.videoPlayer.play();
    }

    @Override
    public void onCompositionStart() {
        this.videoPlayer.play();
    }

    @Override
    public void onCinematicTimeChange(long time) {
        this.videoPlayer.setTime(time);
    }
}
