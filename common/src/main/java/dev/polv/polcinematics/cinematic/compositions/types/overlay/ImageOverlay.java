package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.async.DownloadHandler;
import dev.polv.polcinematics.cinematic.compositions.value.EValueType;
import dev.polv.polcinematics.utils.render.DynamicImage;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public class ImageOverlay extends OverlayComposition {

    private DynamicImage image;

    public static final String IMAGE_URL_KEY = "IMAGE_URL";

    @Override
    protected void declareVariables() {
        this.declareProperty("IMAGE_URL", "The url of the image", EValueType.STRING);

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

        RenderUtils.renderImage(this.image, matrixStack, x, y, width, height, (float) alpha);
    }

    public void setImageUrl(String imageUrl) {
        this.getProperty(IMAGE_URL_KEY).setValue(imageUrl);
        download();
    }

    private void download() {
        this.image = DownloadHandler.INSTANCE.downloadImage(this.getProperty(IMAGE_URL_KEY).getValueAsString());
    }

    @Override
    public void onCinematicLoad() {
        this.download();
    }
}
