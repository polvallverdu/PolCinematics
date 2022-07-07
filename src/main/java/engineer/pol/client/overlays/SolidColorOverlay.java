package engineer.pol.client.overlays;

import engineer.pol.cinematic.timeline.core.CompositionProperty;
import engineer.pol.utils.ColorUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class SolidColorOverlay extends Overlay {

    public class SolidColorProperties {
        public static CompositionProperty COLOR = new CompositionProperty("color");
    }

    public SolidColorOverlay() {
        super();
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
