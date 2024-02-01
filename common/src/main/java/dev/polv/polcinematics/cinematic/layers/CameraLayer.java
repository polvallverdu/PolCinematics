package dev.polv.polcinematics.cinematic.layers;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraFrame;

import java.util.List;
import java.util.UUID;

public class CameraLayer extends Layer {

    public CameraLayer() {
        super();
    }

    public CameraLayer(UUID uuid, List<WrappedComposition> compositions) {
        super(uuid, compositions);
    }

    public CameraComposition getCameraComposition(long time) {
        return (CameraComposition) this.getComposition(time);
    }

    public CameraFrame getCameraFrame(long time) {
        WrappedComposition cameraComposition = this.getWrappedComposition(time);

        if (cameraComposition == null) {
            return null;
        }

        return ((CameraComposition) cameraComposition.getComposition()).getCameraFrame(time - cameraComposition.getStartTime());
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("camera", true);
        return json;
    }

    @Override
    public ECompositionType[] getAllowedTypes() {
        return new ECompositionType[]{ECompositionType.CAMERA_COMPOSITION};
    }
}
