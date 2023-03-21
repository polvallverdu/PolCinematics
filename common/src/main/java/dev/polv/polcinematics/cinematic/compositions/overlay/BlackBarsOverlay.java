package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.EAttributeType;
import dev.polv.polcinematics.utils.BasicCompositionData;
import dev.polv.polcinematics.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.UUID;

public class BlackBarsOverlay extends OverlayComposition {

    public BlackBarsOverlay(UUID uuid, String name, long duration, AttributeList attributes) {
        super(uuid, name, EOverlayType.BLACK_BARS_OVERLAY, duration, attributes);

        this.declareAttribute("COLOR", "Color for the bars", EAttributeType.COLOR);
        this.declareAttribute("SIZE", "Goes from 0.0 to 1.0", EAttributeType.DOUBLE);
    }

    @Override
    public void tick(MatrixStack MatrixStack, long time) {
        int maxHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int sizeMultiplier = (int) this.getAttribute("SIZE").getValue(time);
        int barHeight = (int) (maxHeight*0.5*sizeMultiplier);

        int color = ColorUtils.getColor((Color) this.getAttribute("COLOR").getValue(time));

        DrawableHelper.fill(MatrixStack, 0, 0, MinecraftClient.getInstance().getWindow().getScaledWidth(), barHeight, color);
        DrawableHelper.fill(MatrixStack, 0, MinecraftClient.getInstance().getWindow().getScaledHeight(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight()-barHeight, color);
    }

    public static BlackBarsOverlay fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json.get("attributes").getAsJsonObject());

        return new BlackBarsOverlay(data.uuid(), data.name(), data.duration(), attributes);
    }
}
