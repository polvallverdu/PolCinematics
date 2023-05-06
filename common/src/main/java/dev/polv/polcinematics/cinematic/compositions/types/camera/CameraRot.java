package dev.polv.polcinematics.cinematic.compositions.types.camera;

import com.google.gson.JsonObject;

public class CameraRot {

    private final float pitch;
    private final float yaw;
    private final float roll;

    public CameraRot(float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
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

    public float[] getArray() {
        return new float[]{pitch, yaw, roll};
    }

    public static CameraRot fromJson(JsonObject json) {
        float pitch = json.get("pitch").getAsFloat();
        float yaw = json.get("yaw").getAsFloat();
        float roll = json.get("roll").getAsFloat();

        return new CameraRot(pitch, yaw, roll);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("pitch", pitch);
        json.addProperty("yaw", yaw);
        json.addProperty("roll", roll);

        return json;
    }

}
