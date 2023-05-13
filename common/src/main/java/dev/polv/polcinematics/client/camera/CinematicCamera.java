package dev.polv.polcinematics.client.camera;

import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraFrame;
import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.client.cinematic.ClientCinematic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class CinematicCamera extends Camera {

    private CameraFrame getCameraFrame() {
        ClientCinematic cin = PolCinematicsClient.getCCM().getLastRunningCinematic();
        // We assume that cin won't be null since this method is only called when a cinematic is running

        return ((CameraComposition) cin.getCinematic().getCameraTimeline().getComposition(cin.getElapsedTime())).getCameraFrame(cin.getElapsedTime());
    }

    @Override
    public Vec3d getPos() {
        CameraFrame frame = getCameraFrame();
        return new Vec3d(frame.getX(), frame.getY(), frame.getZ());
    }

    @Override
    public float getPitch() {
        CameraFrame frame = getCameraFrame();
        return frame.getPitch();
    }

    @Override
    public float getYaw() {
        CameraFrame frame = getCameraFrame();
        return frame.getYaw();
    }
}