package dev.polv.polcinematics.mixin.client;

import dev.polv.polcinematics.cinematic.compositions.camera.CameraPos;
import dev.polv.polcinematics.client.PolCinematicsClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Deprecated
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

    @ModifyVariable(method="update", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Entity nullableFocusedEntity(Entity focusedEntity) {
        //return PolCinematicsClient.getCCM().isCinematicRunning() ? null : focusedEntity;
        return focusedEntity;
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void injectedArgsSetRotationOnUpdate(Args args) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;
        CameraPos cameraPos = PolCinematicsClient.getCCM().getCameraPos();

        if (cameraPos == null) return;

        args.set(0, (float) cameraPos.getYaw());
        args.set(1, (float) cameraPos.getPitch());
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void injectedArgsSetPosOnUpdate(Args args) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;
        CameraPos cameraPos = PolCinematicsClient.getCCM().getCameraPos();

        if (cameraPos == null) return;

        args.set(0, cameraPos.getX());
        args.set(1, cameraPos.getY());
        args.set(2, cameraPos.getZ());

        System.out.println(cameraPos.getX() + " " + cameraPos.getY() + " " + cameraPos.getZ() + " " + cameraPos.getYaw() + " " + cameraPos.getPitch());
    }

}
