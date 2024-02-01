package dev.polv.polcinematics.internal.layers;

public enum ELayerType {

    LAYER(Layer.class),
    CAMERA_LAYER(CameraLayer.class),
    ;

    private final Class<? extends Layer> clazz;

    ELayerType(Class<? extends Layer> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends Layer> getClazz() {
        return clazz;
    }

}
