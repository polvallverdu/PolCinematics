package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.async.DownloadHandler;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.EAttributeType;
import dev.polv.polcinematics.client.players.VideoPlayer;
import dev.polv.polcinematics.utils.BasicCompositionData;
import dev.polv.polcinematics.utils.render.DynamicImage;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

import java.util.UUID;

public class ImageOverlay extends OverlayComposition {

    private String imageUrl;
    private DynamicImage image;

    public ImageOverlay(UUID uuid, String name, String imageUrl, long duration, AttributeList attributes) {
        super(uuid, name, EOverlayType.VIDEO_OVERLAY, duration, attributes);

        this.imageUrl = imageUrl;
        this.image = DownloadHandler.INSTANCE.downloadImage(imageUrl);

        this.declareAttribute("X", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("ALPHA", "Goes from 0.0 to 1.0", EAttributeType.DOUBLE);
    }

    public ImageOverlay(String name, String mediaPath, long duration) {
        this(UUID.randomUUID(), name, mediaPath, duration, new AttributeList());
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getAttribute("X").getValue(time);
        int y = (int) this.getAttribute("Y").getValue(time);
        int width = (int) this.getAttribute("WIDTH").getValue(time);
        int height = (int) this.getAttribute("HEIGHT").getValue(time);
        double alpha = (double) this.getAttribute("ALPHA").getValue(time);

        RenderUtils.renderImage(this.image, matrixStack, x, y, width, height, (float) alpha);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("imageUrl", this.imageUrl);
        return json;
    }

    public static ImageOverlay fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json.get("attributes").getAsJsonObject());
        String imageUrl = json.get("imageUrl").getAsString();

        return new ImageOverlay(data.uuid(), data.name(), imageUrl, data.duration(), attributes);
    }


}
