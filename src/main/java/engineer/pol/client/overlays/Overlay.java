package engineer.pol.client.overlays;

import net.minecraft.client.util.math.MatrixStack;

public interface Overlay {

    void appear();

    void disappear();

    void render(MatrixStack matrix);
}
