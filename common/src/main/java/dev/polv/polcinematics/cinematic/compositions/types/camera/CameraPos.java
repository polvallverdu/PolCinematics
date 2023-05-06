package dev.polv.polcinematics.cinematic.compositions.types.camera;

import com.google.gson.JsonObject;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public class CameraPos {

    private final double x;
    private final double y;
    private final double z;

    public CameraPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public Vec3d getVec3d() {
        return new Vec3d(x, y, z);
    }

    public Vector3d getVector3d() {
        return new Vector3d(x, y, z);
    }

    public double[] getArray() {
        return new double[] {x, y, z};
    }

    public static CameraPos fromJson(JsonObject json) {
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();

        return new CameraPos(x, y, z);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);

        return json;
    }

}
