package engineer.pol.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.core.attributes.AttributeList;
import engineer.pol.cinematic.compositions.core.attributes.EAttributeType;
import engineer.pol.utils.BasicCompositionData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.math.MatrixStack;

import java.util.UUID;

public class VideoOverlay extends OverlayComposition {

    private String videoPath;
    @Environment(EnvType.CLIENT)
    private final engineer.pol.client.players.VideoPlayer videoPlayer;

    public VideoOverlay(UUID uuid, String name, String videoPath, long duration, AttributeList attributes) {
        super(uuid, name, EOverlayType.VIDEO_OVERLAY, duration, attributes);
        this.videoPath = videoPath;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.videoPlayer = new engineer.pol.client.players.VideoPlayer(videoPath);
        } else {
            this.videoPlayer = null;
        }

        this.declareAttribute("X", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
    }

    public VideoOverlay(String name, String videoPath, long duration) {
        this(UUID.randomUUID(), name, videoPath, duration, new AttributeList());
    }

    public void setNewVideoPath(String newVideoPath) {
        this.videoPath = newVideoPath;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            this.videoPlayer.changeMediaPath(newVideoPath);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getAttribute("X").getValue(time);
        int y = (int) this.getAttribute("Y").getValue(time);
        int width = (int) this.getAttribute("WIDTH").getValue(time);
        int height = (int) this.getAttribute("HEIGHT").getValue(time);

        this.videoPlayer.render(matrixStack, x, y, width, height);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("videoPath", this.videoPath); // Injecting videopath variable
        return json;
    }

    public static VideoOverlay fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json);
        String videoPath = json.get("videoPath").getAsString();

        return new VideoOverlay(data.uuid(), data.name(), videoPath, data.duration(), attributes);
    }
}
