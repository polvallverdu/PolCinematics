package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.client.players.BrowserView;
import dev.polv.polcinematics.utils.DeclarationUtils;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public class WebBrowserOverlay extends OverlayComposition {

    private BrowserView browserView;

    public static final String URL_KEY = "URL";
    public static final String CUSTOMCSS_KEY = "CUSTOMCSS";

    @Override
    protected void declare() {
        this.declareConstant(URL_KEY, "The url of the video", EValueType.STRING);
        this.declareConstant(CUSTOMCSS_KEY, "Custom CSS to apply to the webpage", EValueType.STRING);

        DeclarationUtils.declareScreenTimevars(this);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getTimeVariable(DeclarationUtils.X_KEY).getValue(time);
        int y = (int) this.getTimeVariable(DeclarationUtils.Y_KEY).getValue(time);
        int width = (int) this.getTimeVariable(DeclarationUtils.WIDTH_KEY).getValue(time);
        int height = (int) this.getTimeVariable(DeclarationUtils.HEIGHT_KEY).getValue(time);

        var dimensions = RenderUtils.calculateDimensions(x, y, width, height);

        this.browserView.render(matrixStack, x, y, dimensions.getLeft(), dimensions.getRight());
    }

    public String getUrl() {
        return this.getConstant(URL_KEY).getValueAsString();
    }

    public String getCustomCSS() {
        return this.getConstant(CUSTOMCSS_KEY).getValueAsString();
    }

    @Override
    public void onCinematicLoad() {
        this.browserView = new BrowserView(getUrl(), getCustomCSS());
        this.browserView.newUrl(getUrl());
    }

    @Override
    public void onCinematicUnload() {
        this.browserView.stop();
    }
}
