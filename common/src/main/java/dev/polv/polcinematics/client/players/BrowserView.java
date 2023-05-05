package dev.polv.polcinematics.client.players;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.architectury.platform.Platform;
import dev.polv.polcinematics.client.EClientModules;
import dev.polv.polcinematics.exception.MissingModuleException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.montoyo.mcef.api.API;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.MCEFApi;

@Environment(EnvType.CLIENT)
public class BrowserView {

    private IBrowser browser;
    private int oldWidth, oldHeight;

    private String url;
    private String customCSS = "body { background-color: rgba(0, 0, 0, 0); margin: 0px auto; overflow: hidden; }";
    public static MissingModuleException MISSING_MODULE_EXCEPTION = new MissingModuleException(EClientModules.BROWSER);

    public BrowserView(String url) {
        this(url, "");
    }

    public BrowserView(String url, String customCSS) {
        this.url = url;
        if (!customCSS.isEmpty()) {
            this.customCSS = customCSS;
        }

        if (!Platform.isModLoaded("mcef")) {
            throw MISSING_MODULE_EXCEPTION;
        }

        API api = MCEFApi.getAPI();
        if (api == null) {
            throw MISSING_MODULE_EXCEPTION;
        }

        this.browser = api.createBrowser(this.url, true);

        oldWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        oldHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
    }

    private void resize() {
        this.browser.resize(this.oldWidth, this.oldHeight);
    }

    public void newUrl(String url) {
        this.url = url;
        this.browser.loadURL(this.url);
        this.injectCustomCSS();
    }

    public void setCustomCSS(String customCSS) {
        this.customCSS = customCSS;
        this.injectCustomCSS();
    }

    private void injectCustomCSS() {
        this.browser.runJS(String.format("""
                (() => {
                    let stylecustom = "%s";
                    const style = document.createElement('style');
                    style.textContent = stylecustom;
                    document.head.append(style);
                })();""", this.customCSS), "");
    }

    public void render(MatrixStack matrixStack, int x, int y, int width, int height) {
        if (oldWidth != width || oldHeight != height) {
            this.oldWidth = width;
            this.oldHeight = height;
            this.resize();
        }

        if (browser != null) {
            GlStateManager._disableDepthTest();
            GlStateManager._disableBlend();

            browser.draw(matrixStack, x, y, width, height);

            GlStateManager._enableDepthTest();
        }
    }

    public void stop() {
        if (browser != null) {
            browser.close();
        }
    }

}
