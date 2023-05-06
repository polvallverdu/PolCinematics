package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.value.EValueType;
import dev.polv.polcinematics.client.players.BrowserView;
import net.minecraft.client.util.math.MatrixStack;

public class WebBrowserOverlay extends OverlayComposition {

    private BrowserView browserView;

    public static final String URL_KEY = "URL";
    public static final String CUSTOMCSS_KEY = "CUSTOMCSS";

    @Override
    protected void declareVariables() {
        this.declareProperty(URL_KEY, "The url of the video", EValueType.STRING);
        this.declareProperty(CUSTOMCSS_KEY, "Custom CSS to apply to the webpage", EValueType.STRING);

        this.declareAttribute("X", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EValueType.INTEGER);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getAttribute("X").getValue(time);
        int y = (int) this.getAttribute("Y").getValue(time);
        int width = (int) this.getAttribute("WIDTH").getValue(time);
        int height = (int) this.getAttribute("HEIGHT").getValue(time);

        this.browserView.render(matrixStack, x, y, width, height);
    }

    public String getUrl() {
        return this.getProperty(URL_KEY).getValueAsString();
    }

    public String getCustomCSS() {
        return this.getProperty(CUSTOMCSS_KEY).getValueAsString();
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
