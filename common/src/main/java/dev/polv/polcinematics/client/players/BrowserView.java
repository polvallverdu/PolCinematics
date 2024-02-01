package dev.polv.polcinematics.client.players;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.platform.Platform;
import dev.polv.polcinematics.client.EClientModules;
import dev.polv.polcinematics.exception.MissingModuleException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class BrowserView {

    private MCEFBrowser browser;
    private int oldWidth, oldHeight;
    private boolean destroy = false;

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

//        API api = MCEFApi.getAPI();
//        if (api == null) {
//            throw MISSING_MODULE_EXCEPTION;
//        }

        this.browser = MCEF.createBrowser(this.url, true);

        oldWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        oldHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        this.resize();
        this.injectCustomCSS();
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
        this.browser.executeJavaScript(String.format("""
                (() => {
                    let stylecustom = "%s";
                    const style = document.createElement('style');
                    style.textContent = stylecustom;
                    document.head.append(style);
                })();""", this.customCSS), "", 0);
    }

    public void render(MatrixStack matrixStack, int x, int y, int width, int height) {
        if (width == 0 || height == 0) return;

        Window window = MinecraftClient.getInstance().getWindow();
        int realWidth = window.getWidth() * (width / window.getScaledHeight() );
        int realHeight = window.getWidth() * (height / window.getScaledHeight() );

        if (oldWidth != realWidth || oldHeight != realHeight) {
            this.oldWidth = realWidth;
            this.oldHeight = realHeight;
            this.resize();
        }

        if (browser != null) {
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
            Tessellator t = Tessellator.getInstance();
            BufferBuilder buffer = t.getBuffer();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(x, height + y, 0).texture(0.0f, 1.0f).color(255, 255, 255, 255).next();
            buffer.vertex(width + x, height + y, 0).texture(1.0f, 1.0f).color(255, 255, 255, 255).next();
            buffer.vertex(width + x, y, 0).texture(1.0f, 0.0f).color(255, 255, 255, 255).next();
            buffer.vertex(x, y, 0).texture(0.0f, 0.0f).color(255, 255, 255, 255).next();
            t.draw();

            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.enableDepthTest();
        }
    }

    public void renderWorld(Matrix4f matrix4f, int x, int y, int width, int height) {
        if (width == 0 || height == 0) return;

        int realWidth = width*100;
        int realHeight = height*100;

        if (oldWidth != realWidth || oldHeight != realHeight) {
            this.oldWidth = realWidth;
            this.oldHeight = realHeight;
            this.resize();
        }

        if (browser == null) {
            return;
        }
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
        Tessellator t = Tessellator.getInstance();
        BufferBuilder buffer = t.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix4f, width +x, height + y, 0).texture(1.0f, 0.0f).color(255, 255, 255, 255).next();
        buffer.vertex(matrix4f,  x, height + y, 0).texture(0.0f, 0.0f).color(255, 255, 255, 255).next();
        buffer.vertex(matrix4f,  x, y, 0).texture(0.0f, 1.0f).color(255, 255, 255, 255).next();
        buffer.vertex(matrix4f, width +x, y, 0).texture(1.0f, 1.0f).color(255, 255, 255, 255).next();
        t.draw();

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableBlend();
    }

    public void stop() {
        if (browser != null) {
            MCEFBrowser finalBrowser = browser;
            MinecraftClient.getInstance().executeSync(finalBrowser::close);
            browser = null;
        }
    }

    public void runJS(String js) {
        if (browser != null) {
            browser.executeJavaScript(js, "", 0);
        }
    }

}
