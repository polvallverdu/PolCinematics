package dev.polv.polcinematics.cinematic.compositions.overlay;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.camera.ECameraType;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.attributes.AttributeList;
import dev.polv.polcinematics.exception.CompositionException;
import net.minecraft.client.util.math.MatrixStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public abstract class OverlayComposition extends Composition {

    private EOverlayType overlayType;

    public static OverlayComposition create(String name, long duration, EOverlayType overlayType) throws CompositionException {
        OverlayComposition compo;
        // new CameraComposition() from the class in cameraType. Constructor is empty.
        try {
            var compositionClass = overlayType.getClazz();
            compo = compositionClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CompositionException("Could not create composition", e);
        }

        compo.init(name, duration, ECompositionType.OVERLAY_COMPOSITION);

        return compo;
    }

    public abstract void tick(MatrixStack MatrixStack, long time);

    //public abstract void render(MatrixStack matrix, int x, int y, int width, int height, double alpha, long time);

    @Override
    protected void configure(JsonObject json) {
        this.overlayType = EOverlayType.fromName(json.get("overlayType").getAsString());
    }

    public EOverlayType getOverlayType() {
        return overlayType;
    }
}
