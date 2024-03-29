package dev.polv.polcinematics.internal.compositions.types.overlay;

import dev.polv.polcinematics.internal.compositions.values.EValueType;
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
        this.declareConstant(URL_KEY, "The url of the website", EValueType.STRING);
        this.declareConstant(CUSTOMCSS_KEY, "Custom CSS to apply to the webpage", EValueType.STRING);

        DeclarationUtils.declareScreenTimevars(this);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        double x = (double) this.getTimeVariable(DeclarationUtils.X_KEY).getValue(time);
        double y = (double) this.getTimeVariable(DeclarationUtils.Y_KEY).getValue(time);
        double width = (double) this.getTimeVariable(DeclarationUtils.WIDTH_KEY).getValue(time);
        double height = (double) this.getTimeVariable(DeclarationUtils.HEIGHT_KEY).getValue(time);

        var dimensions = RenderUtils.calculateDimensions(x, y, width, height);

        this.browserView.render(matrixStack, RenderUtils.calculateXAxis(x), RenderUtils.calculateYAxis(y), dimensions.getLeft(), dimensions.getRight());
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
