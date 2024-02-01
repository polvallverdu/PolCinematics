package dev.polv.polcinematics.client.skybox;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.polv.polcinematics.client.players.BrowserView;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class TimelineSkyboxRenderer implements Skybox {

//    public static final String URL = "https://d7b1.c16.e2-1.dev/polcinematics/video.html?url=https://a7w4.c13.e2-1.dev/testsite/quevedo.webm";
    private BrowserView browserView;

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog) {
        if (this.browserView == null) {
            this.browserView = new BrowserView("https://google.com");
        }

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
//        this.blend.applyBlendFunc(this.alpha);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        matrices.push();
        System.out.println("Drawing skybox");
        this.browserView.renderSkybox(matrices, 0, 0, 1000, 1000);
        matrices.pop();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public int getPriority() {
        return -9999;
    }
}
