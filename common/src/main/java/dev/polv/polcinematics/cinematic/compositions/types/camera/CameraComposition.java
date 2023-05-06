package dev.polv.polcinematics.cinematic.compositions.types.camera;

import dev.polv.polcinematics.cinematic.compositions.Composition;

public abstract class CameraComposition extends Composition {

    public abstract CameraPos getCameraPos(long time);
    public abstract CameraRot getCameraRot(long time);

    public ECameraType getCameraType() {
        return (ECameraType) this.getSubtype();
    }

}
