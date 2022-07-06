package engineer.pol.client.renderer;

import engineer.pol.client.CinematicExtension;
import engineer.pol.utils.BezierCurve;
import engineer.pol.utils.InterpolatedTimedTask;
import engineer.pol.utils.LegacySplineInterpolator;
import net.minecraft.client.MinecraftClient;

import java.time.Duration;

public class FovManager implements CinematicExtension {

    private InterpolatedTimedTask fovTask;
    private double originalFov;
    private double fov;
    private double fovTarget;

    public FovManager() {
        fovTask = new InterpolatedTimedTask(Duration.ofSeconds(3), false);
        fovTarget = fov;
    }

    private void getClientFov() {
        // Get client fov
        this.originalFov = MinecraftClient.getInstance().options.fov;
    }

    public void modifyFov(double fov) {
        this.fovTarget = fov;
        this.fovTask.startNormal();
    }

    public double getMixinFov() {
        if (!this.fovTask.isRunning()) {
            return this.fov;
        }

        double t = this.fovTask.getCurveRelative();
        // Transition from fov to fovTarget with t as a multiplier
        this.fov = this.fovTarget + ((this.originalFov - this.fovTarget) * t);
        return this.fov;
    }


    @Override
    public void onStart() {
        getClientFov();
    }

    @Override
    public void onEnd() {

    }
}
