package dev.polv.polcinematics.internal.compositions.types.overlay;

import dev.polv.polcinematics.utils.ColorUtils;
import dev.polv.polcinematics.utils.DeclarationUtils;
import dev.polv.polcinematics.utils.render.RenderUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class SolidColorOverlay extends OverlayComposition {

    @Override
    protected void declare() {
        DeclarationUtils.declareColorTimevar(this);
        DeclarationUtils.declareScreenTimevars(this);
        DeclarationUtils.declareAlphaTimevar(this);
    }

    @Override
    public void tick(MatrixStack MatrixStack, long time) {
        Color color = (Color) this.getTimeVariable(DeclarationUtils.COLOR_KEY).getValue(time);

        int x = (int) this.getTimeVariable(DeclarationUtils.X_KEY).getValue(time);
        int y = (int) this.getTimeVariable(DeclarationUtils.Y_KEY).getValue(time);
        int width = (int) this.getTimeVariable(DeclarationUtils.WIDTH_KEY).getValue(time);
        int height = (int) this.getTimeVariable(DeclarationUtils.HEIGHT_KEY).getValue(time);
        float alpha = (float) (int) this.getTimeVariable(DeclarationUtils.ALPHA_KEY).getValue(time);

        var dimensions = RenderUtils.calculateDimensions(x, y, width, height);

        DrawableHelper.fill(MatrixStack, x, y, dimensions.getLeft(), dimensions.getRight(), ColorUtils.applyAlphaToColor(ColorUtils.getColor(color), alpha/100f));
    }

}
