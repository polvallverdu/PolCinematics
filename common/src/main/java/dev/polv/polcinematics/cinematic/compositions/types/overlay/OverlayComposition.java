package dev.polv.polcinematics.cinematic.compositions.types.overlay;

import dev.polv.polcinematics.cinematic.compositions.Composition;
import net.minecraft.client.util.math.MatrixStack;

public abstract class OverlayComposition extends Composition {

    public abstract void tick(MatrixStack MatrixStack, long time);

    //public abstract void render(MatrixStack matrix, int x, int y, int width, int height, double alpha, long time);

    public EOverlayType getOverlayType() {
        return (EOverlayType) this.getSubtype();
    }
}
