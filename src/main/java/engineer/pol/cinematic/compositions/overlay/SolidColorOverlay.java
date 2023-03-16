package engineer.pol.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.core.attributes.AttributeList;
import engineer.pol.cinematic.compositions.core.attributes.EAttributeType;
import engineer.pol.utils.BasicCompositionData;
import engineer.pol.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.UUID;

public class SolidColorOverlay extends OverlayComposition {

    public SolidColorOverlay(UUID uuid, String name, long duration, AttributeList attributes) {
        super(uuid, name, EOverlayType.SOLID_COLOR_OVERLAY, duration, attributes);

        this.declareAttribute("COLOR", "Color for the solid", EAttributeType.COLOR);
        this.declareAttribute("X", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("Y", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("WIDTH", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
        this.declareAttribute("HEIGHT", "Goes from 0 to niputaidea", EAttributeType.INTEGER);
    }

    @Override
    public void tick(MatrixStack matrixStack, long time) {
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

        //this.render(matrixStack, (int) x, (int) y, (int) width, (int) height, alpha, time);

        DrawableHelper.fill(matrixStack, x, y, width, height, ColorUtils.getColor(color));
    }

    public static SolidColorOverlay fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json);

        return new SolidColorOverlay(data.uuid(), data.name(), data.duration(), attributes);
    }
}
