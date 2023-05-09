package dev.polv.polcinematics.cinematic.compositions.types.camera;

import dev.polv.polcinematics.utils.DeclarationUtils;

public class FixedCameraComposition extends CameraComposition {

    @Override
    protected void declare() {
        DeclarationUtils.declareCameraConstants(this);
    }

    @Override
    public CameraFrame getCameraFrame(long time) {
        return new CameraFrame(this.getX(), this.getY(), this.getZ(), this.getPitch(), this.getYaw(), this.getRoll());
    }

    @Override
    public boolean shouldInjectPositionValue() {
        return true;
    }

    private double get(String key) {
        return this.getConstant(key).getValueAsDouble();
    }

    private void set(String key, double value) {
        this.getConstant(key).setValue(value);
    }

    public double getX() {
        return this.get(DeclarationUtils.X_KEY);
    }

    public void setX(double x) {
        this.set(DeclarationUtils.X_KEY, x);
    }

    public double getY() {
        return this.get(DeclarationUtils.Y_KEY);
    }

    public void setY(double y) {
        this.set(DeclarationUtils.Y_KEY, y);
    }

    public double getZ() {
        return this.get(DeclarationUtils.Z_KEY);
    }

    public void setZ(double z) {
        this.set(DeclarationUtils.Z_KEY, z);
    }

    public float getPitch() {
        return (float) this.get(DeclarationUtils.PITCH_KEY);
    }

    public void setPitch(float pitch) {
        this.set(DeclarationUtils.PITCH_KEY, pitch);
    }

    public float getYaw() {
        return (float) this.get(DeclarationUtils.YAW_KEY);
    }

    public void setYaw(float yaw) {
        this.set(DeclarationUtils.YAW_KEY, yaw);
    }

    public float getRoll() {
        return (float) this.get(DeclarationUtils.ROLL_KEY);
    }

    public void setRoll(float roll) {
        this.set(DeclarationUtils.ROLL_KEY, roll);
    }

}
