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
    protected void declare() {
        this.declareConstant("IMAGE_URL", "The url of the image", EValueType.STRING);

        this.declareTimeVariable("X", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("Y", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("WIDTH", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("HEIGHT", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("ALPHA", "Goes from 0.0 to 1.0", EValueType.DOUBLE);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getTimeVariable("X").getValue(time);
        int y = (int) this.getTimeVariable("Y").getValue(time);
        int width = (int) this.getTimeVariable("WIDTH").getValue(time);
        int height = (int) this.getTimeVariable("HEIGHT").getValue(time);
        double alpha = (double) this.getTimeVariable("ALPHA").getValue(time);

        RenderUtils.renderImage(this.image, matrixStack, x, y, width, height, (float) alpha);
    }

    public void setImageUrl(String imageUrl) {
        this.getConstant(IMAGE_URL_KEY).setValue(imageUrl);
        download();
    }

    private void download() {
        this.image = DownloadHandler.INSTANCE.downloadImage(this.getConstant(IMAGE_URL_KEY).getValueAsString());
    }

    @Override
    public void onCinematicLoad() {
        this.download();
    }
}
