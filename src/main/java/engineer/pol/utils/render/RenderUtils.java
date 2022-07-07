package engineer.pol.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import engineer.pol.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RenderUtils {

    public static void bindTexture(Identifier texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
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
