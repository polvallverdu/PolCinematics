package engineer.pol.cinematic.compositions.overlay;

import engineer.pol.cinematic.compositions.core.CompositionProperty;
import engineer.pol.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class DEPRECATEDBLACKBARS extends DEPRECATEDCOLOROVERLAY {

    private final MinecraftClient minecraft;

    public class BlackBarsProperty {
        public static CompositionProperty length = new CompositionProperty("length", 0d, 1d);
    }

    public DEPRECATEDBLACKBARS() {
        super();
        this.minecraft = MinecraftClient.getInstance();
        this.addTimelineProperty(BlackBarsProperty.length);
    }

    @Override
    public void render(MatrixStack matrix, int x, int y, int width, int heightt, double alpha, long time) {
        int maxHeight = minecraft.getWindow().getScaledHeight();
        int barHeight = (int) (maxHeight*0.1);
        int height = (int) (this.timelines.get(BlackBarsProperty.length).getValue(time) * barHeight);

        int color = ColorUtils.applyAlphaToColor(this.getColor(time), alpha);

        DrawableHelper.fill(matrix, 0, 0, minecraft.getWindow().getScaledWidth(), height, color);
        DrawableHelper.fill(matrix, 0, minecraft.getWindow().getScaledHeight(), minecraft.getWindow().getScaledWidth(), minecraft.getWindow().getScaledHeight()-height, color);
    }
}
