package dev.polv.polcinematics.cinematic.compositions.types.camera;

import com.google.gson.JsonObject;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public class CameraFrame {

    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;
    private final float roll;

    public CameraFrame(double x, double y, double z, float pitch, float yaw, float roll) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public Vec3d getVec3d() {
        return new Vec3d(x, y, z);
    }

    public Vector3d getVector3d() {
        return new Vector3d(x, y, z);
    }

    public double[] getArray() {
        return new double[] {x, y, z, pitch, yaw, roll};
    }

    @Override
    public String toString() {
        return "CameraFrame{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                ", roll=" + roll +
                '}';
    }
}
