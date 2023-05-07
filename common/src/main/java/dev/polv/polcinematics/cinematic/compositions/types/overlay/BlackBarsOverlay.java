package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class BlackBarsOverlay extends OverlayComposition {

    @Override
    protected void declare() {
        this.declareTimeVariable("COLOR", "Color for the bars", EValueType.COLOR);
        this.declareTimeVariable("SIZE", "Goes from 0.0 to 1.0", EValueType.DOUBLE);
    }

    @Override
    public void tick(MatrixStack MatrixStack, long time) {
        int maxHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        double sizeMultiplier = (double) this.getTimeVariable("SIZE").getValue(time);
        sizeMultiplier = Math.max(0.0, Math.min(1.0, sizeMultiplier));
        int barHeight = (int) (maxHeight*0.5*sizeMultiplier);

        int color = ColorUtils.getColor((Color) this.getTimeVariable("COLOR").getValue(time));

        DrawableHelper.fill(MatrixStack, 0, 0, MinecraftClient.getInstance().getWindow().getScaledWidth(), barHeight, color);
        DrawableHelper.fill(MatrixStack, 0, MinecraftClient.getInstance().getWindow().getScaledHeight(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight()-barHeight, color);
    }

}
