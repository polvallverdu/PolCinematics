package dev.polv.polcinematics.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class CameraPos {

    private final double x;
    private final double y;
    private final double z;
    private final double pitch;
    private final double yaw;
    private final double roll;
    private final double fov;

    public CameraPos(double x, double y, double z, double pitch, double yaw, double roll, double fov) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.fov = fov;
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

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public double getRoll() {
        return roll;
    }

    public double getFov() {
        return fov;
    }

    public Vec3d getVec3d() {
        return new Vec3d(x, y, z);
    }

    public Vec2f getVec2f() {
        return new Vec2f((float) pitch, (float) yaw);
    }

    public static CameraPos fromJson(JsonObject json) {
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        double pitch = json.get("pitch").getAsDouble();
        double yaw = json.get("yaw").getAsDouble();
        double roll = json.get("roll").getAsDouble();
        double fov = json.get("fov").getAsDouble();

        return new CameraPos(x, y, z, pitch, yaw, roll, fov);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);
        json.addProperty("pitch", pitch);
        json.addProperty("yaw", yaw);
        json.addProperty("roll", roll);
        json.addProperty("fov", fov);

        return json;
    }

}
