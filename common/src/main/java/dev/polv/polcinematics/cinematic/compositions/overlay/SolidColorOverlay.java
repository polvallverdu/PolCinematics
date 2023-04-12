package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.value.EValueType;
import dev.polv.polcinematics.utils.BasicCompositionData;
import dev.polv.polcinematics.utils.ColorUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.UUID;

public class SolidColorOverlay extends OverlayComposition {

    @Override
    protected void declareVariables() {
        this.declareAttribute("COLOR", "Color for the solid", EValueType.COLOR);
        this.declareAttribute("X", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EValueType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EValueType.INTEGER);
    }

    @Override
    public void tick(MatrixStack MatrixStack, long time) {
        Color color = (Color) this.getAttribute("COLOR").getValue(time);

        int x = (int) this.getAttribute("X").getValue(time);
        int y = (int) this.getAttribute("Y").getValue(time);
        int width = (int) this.getAttribute("WIDTH").getValue(time);
        int height = (int) this.getAttribute("HEIGHT").getValue(time);

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
