package dev.polv.polcinematics.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.cinematic.compositions.core.value.EValueType;
import dev.polv.polcinematics.utils.BasicCompositionData;

import java.util.UUID;

public class SlerpCameraComposition extends LerpCameraComposition {

    /*public static final String POSITION_KEY = "position";
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
    }*/

}
