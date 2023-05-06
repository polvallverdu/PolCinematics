package dev.polv.polcinematics.cinematic.compositions.types.camera;

import dev.polv.polcinematics.cinematic.compositions.value.EValueType;

public class LerpCameraComposition extends CameraComposition {

    public static final String POSITION_KEY = "position";
    public static final String ROTATION_KEY = "rotation";

    @Override
    protected void declareVariables() {
        this.declareProperty(POSITION_KEY, "Position", EValueType.CAMERAPOS);
        this.declareProperty(ROTATION_KEY, "Rotation", EValueType.CAMERAROT);
    }

    @Override
    public CameraPos getCameraPos(long time) {
        return this.getAttribute(POSITION_KEY).getLerpCameraPos(time);
    }

    @Override
    public CameraRot getCameraRot(long time) {
        return this.getAttribute(ROTATION_KEY).getLerpCameraRot(time);
    }

}
