package engineer.pol.client.overlays;

import engineer.pol.utils.BezierCurve;
import engineer.pol.utils.InterpolatedTimedTask;
import engineer.pol.utils.LegacySplineInterpolator;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.time.Duration;

public class BlackBarsOverlay implements Overlay {

    private final MinecraftClient minecraft;
    private InterpolatedTimedTask interpolatedTimedTask;

    public BlackBarsOverlay() {
        this.minecraft = MinecraftClient.getInstance();
        this.interpolatedTimedTask = new InterpolatedTimedTask(Duration.ofSeconds(1), false);
    }

    @Override
    public void appear() {
        this.interpolatedTimedTask.startReverse();
    }

    @Override
    public void disappear() {
        this.interpolatedTimedTask.startNormal();
    }

    @Override
    public void render(MatrixStack matrix) {
        double t = this.interpolatedTimedTask.getCurveRelative();
        int maxHeight = minecraft.getWindow().getScaledHeight();
        int barHeight = (int) (maxHeight*0.1);
        int height = (int) (t * barHeight);

        DrawableHelper.fill(matrix, 0, 0, minecraft.getWindow().getScaledWidth(), height, 0xFF000000);
        DrawableHelper.fill(matrix, 0, minecraft.getWindow().getScaledHeight(), minecraft.getWindow().getScaledWidth(), minecraft.getWindow().getScaledHeight()-height, 0xFF000000);
    }


}
