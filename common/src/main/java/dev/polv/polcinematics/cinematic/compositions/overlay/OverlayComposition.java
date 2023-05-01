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

    public abstract void tick(MatrixStack MatrixStack, long time);

    //public abstract void render(MatrixStack matrix, int x, int y, int width, int height, double alpha, long time);

    public EOverlayType getOverlayType() {
        return (EOverlayType) this.getSubtype();
    }
}
