package engineer.pol.cinematic.compositions.overlay;

import engineer.pol.cinematic.compositions.core.CompositionProperty;
import engineer.pol.utils.ColorUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class DEPRECATEDCOLOROVERLAY extends DEPRECATEDOBERLAY {

    public class SolidColorProperties {
        public static CompositionProperty COLOR = new CompositionProperty("color");
    }

    public DEPRECATEDCOLOROVERLAY() {
        super(EOverlayType.SOLID_COLOR_OVERLAY);
        this.addTimelineProperty(SolidColorProperties.COLOR);
    }

    public int getColor(long tick) {
        return (int) this.timelines.get(SolidColorProperties.COLOR).getValue(tick);
    }

    @Override
    public void render(MatrixStack matrix, int x, int y, int width, int height, double alpha, long time) {
        DrawableHelper.fill(matrix, x, y, width, height, ColorUtils.applyAlphaToColor(getColor(time), alpha));
    }
}
