package dev.polv.polcinematics.mixin.client;

import dev.polv.polcinematics.cinematic.compositions.camera.CameraPos;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraRot;
import dev.polv.polcinematics.cinematic.compositions.camera.PlayerCameraComposition;
import dev.polv.polcinematics.client.PolCinematicsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final private Camera camera;
    private Camera cinematicCamera = new Camera();

    /*@Inject(at = @At("HEAD"), method = "getFov", cancellable = true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

        cir.cancel();

        var cameracompo = PolCinematicsClient.getCCM().getCameraComposition();
        if (cameracompo == null) return;

        if (cameracompo instanceof PlayerCameraComposition) return;

        cir.setReturnValue(cameracompo.getCameraPos(PolCinematicsClient.getCCM().getElapsedTime()).getFov());
    }*/


    @Inject(at = @At("HEAD"), method = "renderHand", cancellable = true)
    public void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

        var cameracompo = PolCinematicsClient.getCCM().getCameraComposition();
        if (cameracompo == null) return;

            /*switch (camera.getCameraType()) {
                case PLAYER -> {
                    return;
                }
            }*/
        if (!(cameracompo instanceof PlayerCameraComposition)) {
            ci.cancel();
        }
    }

    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"
            )
    )
    private void applyRoll(float float_1, long long_1, MatrixStack MatrixStack, CallbackInfo ci) {
       if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;
        var cameracompo = PolCinematicsClient.getCCM().getCameraComposition();
        if (cameracompo == null) return;

            /*switch (camera.getCameraType()) {
                case PLAYER -> {
                    return;
                }
            }*/
        if (cameracompo instanceof PlayerCameraComposition) return;

        CameraRot cameraRot = cameracompo.getCameraRot(PolCinematicsClient.getCCM().getElapsedTime());
        MatrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(cameraRot.getRoll()));
    }

    /*@Inject(at = @At("HEAD"), method = "renderWorld")
    public void beforeRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

    }*/

    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    private void bobView(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (PolCinematicsClient.getCCM().isCinematicRunning()) {
            ci.cancel();
        }
    }

}
