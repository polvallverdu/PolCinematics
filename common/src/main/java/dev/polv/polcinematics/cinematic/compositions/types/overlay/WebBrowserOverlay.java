package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.client.players.BrowserView;
import net.minecraft.client.util.math.MatrixStack;

public class WebBrowserOverlay extends OverlayComposition {

    private BrowserView browserView;

    public static final String URL_KEY = "URL";
    public static final String CUSTOMCSS_KEY = "CUSTOMCSS";

    @Override
    protected void declare() {
        this.declareConstant(URL_KEY, "The url of the video", EValueType.STRING);
        this.declareConstant(CUSTOMCSS_KEY, "Custom CSS to apply to the webpage", EValueType.STRING);

        this.declareTimeVariable("X", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("Y", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("WIDTH", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("HEIGHT", "Goes from 0 to niputaidea", EValueType.INTEGER);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getTimeVariable("X").getValue(time);
        int y = (int) this.getTimeVariable("Y").getValue(time);
        int width = (int) this.getTimeVariable("WIDTH").getValue(time);
        int height = (int) this.getTimeVariable("HEIGHT").getValue(time);

        this.browserView.render(matrixStack, x, y, width, height);
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
