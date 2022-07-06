package engineer.pol.mixin.client;

import engineer.pol.client.PolCinematicsClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(at = @At("HEAD"), method = "getFov", cancellable = true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> info) {
        if (PolCinematicsClient.getInstance().getClientCinematicManager().isRunning()) {
            info.cancel();
            info.setReturnValue(PolCinematicsClient.getInstance().getClientCinematicManager().getFovManager().getMixinFov());
        }
    }

    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    public void bobView(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (PolCinematicsClient.getInstance().getClientCinematicManager().isRunning()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "renderHand", cancellable = true)
    public void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if (PolCinematicsClient.getInstance().getClientCinematicManager().isRunning() && !PolCinematicsClient.getInstance().getClientCinematicManager().getPlayerRendererManager().isRenderFirstPersonHand()) {
            ci.cancel();
        }
    }

}
