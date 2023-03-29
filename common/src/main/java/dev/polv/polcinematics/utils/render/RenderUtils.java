package dev.polv.polcinematics.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.polv.polcinematics.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RenderUtils {

    /**
     * Render an image on {@link DynamicImage}
     *
     * @param dynamicImage The image to render
     * @param matrix The matrix stack
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param alpha The alpha value from 0.0 to 1.0
     */
    public static void renderImage(DynamicImage dynamicImage, MatrixStack matrix, int x, int y, int width, int height, float alpha) {
        //RenderUtils.renderBlackScreen(matrix, 1);  IF YOU DON'T WANT TRASPARENCY

        RenderUtils.bindTexture(dynamicImage.getTextureIdentifier());
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        DrawableHelper.drawTexture(matrix, x, y, 0, 0, 0, width, height, width, height);
        RenderSystem.disableBlend();
    }

    public static void bindTexture(Identifier texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
    }

    public static void renderBlackScreen(MatrixStack stack, float opacity) {
        // Get minecraft width and height
        int width = MinecraftClient.getInstance().getWindow().getWidth();
        int height = MinecraftClient.getInstance().getWindow().getHeight();

        DrawableHelper.fill(stack, 0, 0, width, height, ColorUtils.getColor(255, 255, 255, (int) (opacity * 255)));
    }

}
