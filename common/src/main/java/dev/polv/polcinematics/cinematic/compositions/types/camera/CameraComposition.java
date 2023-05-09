package dev.polv.polcinematics.cinematic.compositions.types.camera;

import dev.polv.polcinematics.cinematic.compositions.Composition;

public abstract class CameraComposition extends Composition {

    public abstract CameraFrame getCameraFrame(long time);

    public ECameraType getCameraType() {
        return (ECameraType) this.getSubtype();
    }

    public boolean shouldInjectPositionValue() {
        return false;
    }

}
