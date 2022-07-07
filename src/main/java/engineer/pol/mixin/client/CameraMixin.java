package engineer.pol.mixin.client;

import engineer.pol.client.PolCinematicsClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    // EntityRenderer.java
    // - line 54 - render if not in player vision
    //
    // EntityRenderDispatcher.java
    // - line 126 - shoudRender
    //
    // PlayerEntityRenderer.java
    // - line 78 - setModelPose (set what's visible and what not)

    /*@Inject(at = @At("HEAD"), method = "getPos", cancellable = true)
    public void getPos(CallbackInfoReturnable<Vec3d> ci) {
        if (PolCinematicsClient.getInstance().getClientCinematicManager().isRunning()) {
            ci.cancel();
            //ci.setReturnValue(PolCinematicsClient.getCCM().getMixinPos());
        }
    }*/

}
