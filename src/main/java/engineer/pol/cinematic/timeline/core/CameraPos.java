package engineer.pol.cinematic.timeline.core;

import com.google.gson.JsonObject;

public class CameraPos {

    private final double x;
    private final double y;
    private final double z;
    private final double pitch;
    private final double yaw;
    private final double roll;

    public CameraPos(double x, double y, double z, double pitch, double yaw, double roll) {
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

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public double getRoll() {
        return roll;
    }

    public static CameraPos fromConfig(JsonObject json) {
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        double pitch = json.get("pitch").getAsDouble();
        double yaw = json.get("yaw").getAsDouble();
        double roll = json.get("roll").getAsDouble();

        return new CameraPos(x, y, z, pitch, yaw, roll);
    }

    public JsonObject toConfig() {
        JsonObject json = new JsonObject();

        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);
        json.addProperty("pitch", pitch);
        json.addProperty("yaw", yaw);
        json.addProperty("roll", roll);

        return json;
    }

}
