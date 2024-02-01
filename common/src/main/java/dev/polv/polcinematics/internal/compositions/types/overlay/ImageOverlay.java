package dev.polv.polcinematics.internal.compositions.types.overlay;

import dev.polv.polcinematics.async.DownloadHandler;
import dev.polv.polcinematics.internal.compositions.values.EValueType;
import dev.polv.polcinematics.utils.DeclarationUtils;
import dev.polv.polcinematics.utils.render.DynamicImage;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public class ImageOverlay extends OverlayComposition {

    private DynamicImage image;

    public static final String IMAGE_URL_KEY = "IMAGE_URL";

    @Override
    protected void declare() {
        this.declareConstant(IMAGE_URL_KEY, "The url of the image", EValueType.STRING);

        DeclarationUtils.declareScreenTimevars(this);
        DeclarationUtils.declareAlphaTimevar(this);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getTimeVariable(DeclarationUtils.X_KEY).getValue(time);
        int y = (int) this.getTimeVariable(DeclarationUtils.Y_KEY).getValue(time);
        int width = (int) this.getTimeVariable(DeclarationUtils.WIDTH_KEY).getValue(time);
        int height = (int) this.getTimeVariable(DeclarationUtils.HEIGHT_KEY).getValue(time);
        float alpha = (float) (int) this.getTimeVariable(DeclarationUtils.ALPHA_KEY).getValue(time);

        var dimensions = RenderUtils.calculateDimensions(x, y, width, height);

        RenderUtils.renderImage(this.image, matrixStack, x, y, dimensions.getLeft(), dimensions.getRight(), alpha/100);
    }

    private void download() {
        this.image = DownloadHandler.INSTANCE.downloadImage(this.getConstant(IMAGE_URL_KEY).getValueAsString());
    }

    @Override
    public void onCinematicLoad() {
        this.download();
    }
}
