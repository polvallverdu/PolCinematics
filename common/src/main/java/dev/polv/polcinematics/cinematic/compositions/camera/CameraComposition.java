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

    private ECameraType cameraType;

    public static CameraComposition create(String name, long duration, ECameraType cameraType) throws CompositionException {
        CameraComposition compo;
        // new CameraComposition() from the class in cameraType. Constructor is empty.
        try {
            var compositionClass = cameraType.getClazz();
            compo = compositionClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CompositionException("Could not create composition", e);
        }

        compo.init(name, duration, ECompositionType.CAMERA_COMPOSITION);
        compo.cameraType = cameraType;

        return compo;
    }

    public abstract CameraPos getCameraPos(long time);
    public abstract CameraRot getCameraRot(long time);

    public ECameraType getCameraType() {
        return cameraType;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("subtype", this.cameraType.getName());
        return json;
    }

    @Override
    protected void configure(JsonObject json) {
        this.cameraType = ECameraType.fromName(json.get("subtype").getAsString());
    }

}
