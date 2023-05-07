package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.utils.ColorUtils;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class SolidColorOverlay extends OverlayComposition {

    @Override
    protected void declare() {
        this.declareTimeVariable("COLOR", "Color for the solid", EValueType.COLOR);
        this.declareTimeVariable("X", "Goes from 0% to 100%", EValueType.INTEGER);
        this.declareTimeVariable("Y", "Goes from 0% to 100%", EValueType.INTEGER);
        this.declareTimeVariable("WIDTH", "Goes from 0% to 100%", EValueType.INTEGER, 50);
        this.declareTimeVariable("HEIGHT", "Goes from 0% to 100%", EValueType.INTEGER, 50);
        this.declareTimeVariable("ALPHA", "Goes from 0% to 100%", EValueType.INTEGER, 100);
    }

    @Override
    public void tick(MatrixStack MatrixStack, long time) {
        Color color = (Color) this.getTimeVariable("COLOR").getValue(time);

        int x = (int) this.getTimeVariable("X").getValue(time);
        int y = (int) this.getTimeVariable("Y").getValue(time);
        int width = (int) this.getTimeVariable("WIDTH").getValue(time);
        int height = (int) this.getTimeVariable("HEIGHT").getValue(time);
        float alpha = (float) (int) this.getTimeVariable("ALPHA").getValue(time);

        var dimensions = RenderUtils.calculateDimensions(x, y, width, height);

        DrawableHelper.fill(MatrixStack, x, y, dimensions.getLeft(), dimensions.getRight(), ColorUtils.applyAlphaToColor(ColorUtils.getColor(color), alpha/100f));
    }

}
