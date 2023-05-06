package dev.polv.polcinematics.cinematic.compositions.types.camera;

import dev.polv.polcinematics.cinematic.compositions.value.EValueType;

public class FixedCameraComposition extends CameraComposition {

    public static final String X_KEY = "X";
    public static final String Y_KEY = "Y";
    public static final String Z_KEY = "Z";
    public static final String PITCH_KEY = "PITCH";
    public static final String YAW_KEY = "YAW";
    public static final String ROLL_KEY = "ROLL";

    @Override
    protected void declareVariables() {
        this.declareProperty(X_KEY, "Coordinate X", EValueType.DOUBLE);
        this.declareProperty(Y_KEY, "Coordinate Y", EValueType.DOUBLE);
        this.declareProperty(Z_KEY, "Coordinate Z", EValueType.DOUBLE);
        this.declareProperty(PITCH_KEY, "Pitch Rotation", EValueType.DOUBLE);
        this.declareProperty(YAW_KEY, "Yaw Rotation", EValueType.DOUBLE);
        this.declareProperty(ROLL_KEY, "Roll Rotation", EValueType.DOUBLE);
    }

    @Override
    public CameraPos getCameraPos(long time) {
        return new CameraPos(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public CameraRot getCameraRot(long time) {
        return new CameraRot((float) this.getPitch(), (float) this.getYaw(), (float) this.getRoll());
    }

    private double get(String key) {
        return this.getProperty(key).getValueAsDouble();
    }

    private void set(String key, double value) {
        this.getProperty(key).setValue(value);
    }

    public double getX() {
        return this.get(X_KEY);
    }

    public void setX(double x) {
        this.set(X_KEY, x);
    }

    public double getY() {
        return this.get(Y_KEY);
    }

    public void setY(double y) {
        this.set(Y_KEY, y);
    }

    public double getZ() {
        return this.get(Z_KEY);
    }

    public void setZ(double z) {
        this.set(Z_KEY, z);
    }

    public double getPitch() {
        return this.get(PITCH_KEY);
    }

    public void setPitch(double pitch) {
        this.set(PITCH_KEY, pitch);
    }

    public double getYaw() {
        return this.get(YAW_KEY);
    }

    public void setYaw(double yaw) {
        this.set(YAW_KEY, yaw);
    }

    public double getRoll() {
        return this.get(ROLL_KEY);
    }

    public void setRoll(double roll) {
        this.set(ROLL_KEY, roll);
    }

}
