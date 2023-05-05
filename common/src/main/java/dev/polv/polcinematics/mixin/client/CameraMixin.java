package dev.polv.polcinematics.mixin.client;

import dev.polv.polcinematics.cinematic.compositions.camera.CameraPos;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraRot;
import dev.polv.polcinematics.cinematic.compositions.camera.PlayerCameraComposition;
import dev.polv.polcinematics.client.PolCinematicsClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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

    /*@ModifyVariable(method="update", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Entity nullableFocusedEntity(Entity focusedEntity) {
        //return PolCinematicsClient.getCCM().isCinematicRunning() ? null : focusedEntity;
        return focusedEntity;
    }*/

    /*@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void injectedArgsSetRotationOnUpdate(Args args) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;
        var camera = PolCinematicsClient.getCCM().getCameraComposition();
        if (camera == null) return;

        switch (camera.getCameraType()) {
            case PLAYER -> {
                return;
            }
        }
        if (camera instanceof PlayerCameraComposition) return;

        CameraRot cameraRot = camera.getCameraRot(PolCinematicsClient.getCCM().getElapsedTime());
        args.set(0, cameraRot.getYaw());
        args.set(1, cameraRot.getPitch());
    }*/

    /*@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void injectedArgsSetPosOnUpdate(Args args) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;
        var camera = PolCinematicsClient.getCCM().getCameraComposition();
        if (camera == null) return;

        switch (camera.getCameraType()) {
            case PLAYER -> {
                return;
            }
        }
        if (camera instanceof PlayerCameraComposition) return;

        CameraPos cameraPos = camera.getCameraPos(PolCinematicsClient.getCCM().getElapsedTime());
        args.set(0, cameraPos.getX());
        args.set(1, cameraPos.getY());
        args.set(2, cameraPos.getZ());
    }*/

}
