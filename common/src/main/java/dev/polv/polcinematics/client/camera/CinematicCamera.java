package dev.polv.polcinematics.client.camera;

import dev.polv.polcinematics.internal.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.internal.compositions.types.camera.CameraFrame;
import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.client.cinematic.ClientCinematic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class CinematicCamera extends Camera {

    private CameraFrame getCameraFrame() {
        ClientCinematic cin = PolCinematicsClient.getCCM().getLastRunningCinematic();
        // We assume that cin won't be null since this method is only called when a cinematic is running

        CameraComposition comp = (CameraComposition) cin.getCinematic().getCameraLayer().getComposition(cin.getElapsedTime());
        if (comp == null) return null;
        return comp.getCameraFrame(cin.getElapsedTime());
    }

    @Override
    public Vec3d getPos() {
        CameraFrame frame = getCameraFrame();
        if (frame == null) return getPlayerCamera().getPos();
        return new Vec3d(frame.getX(), frame.getY(), frame.getZ());
    }

    @Override
    public float getPitch() {
        CameraFrame frame = getCameraFrame();
        if (frame == null) return getPlayerCamera().getPitch();
        return frame.getPitch();
    }

    @Override
    public float getYaw() {
        CameraFrame frame = getCameraFrame();
        if (frame == null) return getPlayerCamera().getYaw();
        return frame.getYaw();
    }

    private Camera getPlayerCamera() {
        return MinecraftClient.getInstance().gameRenderer.getCamera();
    }
}
