package dev.polv.polcinematics.internal.compositions.types.camera;

import dev.polv.polcinematics.utils.DeclarationUtils;

public class LerpCameraComposition extends CameraComposition {

    @Override
    protected void declare() {
        DeclarationUtils.declareCameraTimevars(this);
    }

    @Override
    public CameraFrame getCameraFrame(long time) {
        double x = (double) this.getTimeVariable(DeclarationUtils.X_KEY).getValue(time);
        double y = (double) this.getTimeVariable(DeclarationUtils.Y_KEY).getValue(time);
        double z = (double) this.getTimeVariable(DeclarationUtils.Z_KEY).getValue(time);
        float pitch = (float) this.getTimeVariable(DeclarationUtils.PITCH_KEY).getValue(time);
        float yaw = (float) this.getTimeVariable(DeclarationUtils.YAW_KEY).getValue(time);
        float roll = (float) this.getTimeVariable(DeclarationUtils.ROLL_KEY).getValue(time);

        return new CameraFrame(x, y, z, pitch, yaw, roll);
    }

    @Override
    public boolean shouldInjectPositionValue() {
        return true;
    }
}
