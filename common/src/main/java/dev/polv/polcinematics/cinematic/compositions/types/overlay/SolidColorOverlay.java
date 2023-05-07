package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.value.EValueType;
import dev.polv.polcinematics.utils.ColorUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class SolidColorOverlay extends OverlayComposition {

    @Override
    protected void declare() {
        this.declareTimeVariable("COLOR", "Color for the solid", EValueType.COLOR);
        this.declareTimeVariable("X", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("Y", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("WIDTH", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareTimeVariable("HEIGHT", "Goes from 0 to niputaidea", EValueType.INTEGER);
    }

    @Override
    public void tick(MatrixStack MatrixStack, long time) {
        Color color = (Color) this.getTimeVariable("COLOR").getValue(time);

        int x = (int) this.getTimeVariable("X").getValue(time);
        int y = (int) this.getTimeVariable("Y").getValue(time);
        int width = (int) this.getTimeVariable("WIDTH").getValue(time);
        int height = (int) this.getTimeVariable("HEIGHT").getValue(time);

        /*if (fullscreen > 0) {
            int widthWindow = MinecraftClient.getInstance().getWindow().getWidth();
            int heightWindow = MinecraftClient.getInstance().getWindow().getHeight();

            // calculate the difference between the size (fullscreen = 0) and the minecraft widow size (fullscreen = 1) relative to fullscreen
            x = 0 + (widthWindow - width) * fullscreen;
            y = 0 + (heightWindow - height) * fullscreen;
            width = widthWindow + (widthWindow - width) * fullscreen;
            height = heightWindow + (heightWindow - height) * fullscreen;
        }*/

        //this.render(MatrixStack, (int) x, (int) y, (int) width, (int) height, alpha, time);

        DrawableHelper.fill(MatrixStack, x, y, width, height, ColorUtils.getColor(color));
    }

}
