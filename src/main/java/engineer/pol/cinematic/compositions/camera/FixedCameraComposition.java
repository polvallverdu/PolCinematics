package engineer.pol.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.core.attributes.AttributeList;
import engineer.pol.utils.BasicCompositionData;

import java.util.UUID;

public class FixedCameraComposition extends CameraComposition {

    private double x;
    private double y;
    private double z;
    private double pitch;
    private double yaw;
    private double roll;
    private double fov;

    public FixedCameraComposition(String name, CameraPos cameraPos, long duration) {
        this(UUID.randomUUID(), name, cameraPos, duration, new AttributeList());
    }

    private FixedCameraComposition(UUID uuid, String name, CameraPos cameraPos, long duration, AttributeList attributeList) {
        super(uuid, name, ECameraType.FIXED, duration, attributeList);
        x = cameraPos.getX();
        y = cameraPos.getY();
        z = cameraPos.getZ();
        pitch = cameraPos.getPitch();
        yaw = cameraPos.getYaw();
        roll = cameraPos.getRoll();
        fov = cameraPos.getFov();
    }

    @Override
    public CameraPos getCameraPos(long time) {
        return getCameraPos();
    }

    public CameraPos getCameraPos() {
        return new CameraPos(x, y, z, pitch, yaw, roll, fov);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();

        json.add("cameraPos", getCameraPos().toJson());

        return json;
    }

    public static FixedCameraComposition fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);
        AttributeList attributes = AttributeList.fromJson(json);

        CameraPos cameraPos = CameraPos.fromJson(json.get("cameraPos").getAsJsonObject());

        return new FixedCameraComposition(data.uuid(), data.name(), cameraPos, data.duration(), attributes);
    }
}
