package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.EAttributeType;
import dev.polv.polcinematics.client.players.BrowserView;
import dev.polv.polcinematics.utils.BasicCompositionData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.math.MatrixStack;

import java.util.UUID;

public class WebBrowserOverlay extends OverlayComposition {

    private final String url;
    private final String customCSS;
    private final BrowserView browserView;

    public WebBrowserOverlay(UUID uuid, String name, String url, String customCSS, long duration, AttributeList attributes) {
        super(uuid, name, EOverlayType.BROWSER_OVERLAY, duration, attributes);

        this.url = url;
        this.customCSS = customCSS;
        this.browserView = new BrowserView(url, customCSS);

        this.declareAttribute("X", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
    }

    public WebBrowserOverlay(String name, String url, String customCSS, long duration) {
        this(UUID.randomUUID(), name, url, customCSS, duration, new AttributeList());
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
        int x = (int) this.getAttribute("X").getValue(time);
        int y = (int) this.getAttribute("Y").getValue(time);
        int width = (int) this.getAttribute("WIDTH").getValue(time);
        int height = (int) this.getAttribute("HEIGHT").getValue(time);

        this.browserView.render(matrixStack, x, y, width, height);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("url", this.url);
        json.addProperty("customCSS", this.customCSS);
        return json;
    }

    public static WebBrowserOverlay fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json.get("attributes").getAsJsonObject());
        String url = json.get("url").getAsString();
        String customCSS = json.get("customCSS").getAsString();

        return new WebBrowserOverlay(data.uuid(), data.name(), url, customCSS, data.duration(), attributes);
    }

    @Override
    public void onCinematicLoad() {
        this.browserView.newUrl(this.url);
    }

    @Override
    public void onCinematicUnload() {
        this.browserView.stop();
    }
}
