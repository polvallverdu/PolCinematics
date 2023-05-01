package dev.polv.polcinematics.cinematic.compositions.camera;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.exception.CompositionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public abstract class CameraComposition extends Composition {

    public abstract CameraPos getCameraPos(long time);
    public abstract CameraRot getCameraRot(long time);

    public ECameraType getCameraType() {
        return (ECameraType) this.getSubtype();
    }

}
