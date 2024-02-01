package dev.polv.polcinematics.client.mixin;

import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraFrame;
import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.client.cinematic.ClientCinematic;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    /*@Inject(at = @At("HEAD"), method = "getFov", cancellable = true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

        cir.cancel();

        var cameracompo = PolCinematicsClient.getCCM().getCameraComposition();
        if (cameracompo == null) return;

        if (cameracompo instanceof PlayerCameraComposition) return;

        cir.setReturnValue(cameracompo.getCameraPos(PolCinematicsClient.getCCM().getElapsedTime()).getFov());
    }*/


    /*@Inject(at = @At("HEAD"), method = "renderHand", cancellable = true)
    public void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

        var cameracompo = PolCinematicsClient.getCCM().getCameraComposition();
        if (cameracompo == null) return;

            switch (camera.getCameraType()) {
                case PLAYER -> {
                    return;
                }
            }
        if (!(cameracompo instanceof PlayerCameraComposition)) {
            ci.cancel();
        }
    }

*/
    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"
            )
    )
    private void applyRoll(float float_1, long long_1, MatrixStack MatrixStack, CallbackInfo ci) {
        ClientCinematic cin = PolCinematicsClient.getCCM().getLastRunningCinematic();
        if (cin == null) return;
        CameraFrame frame = cin.getCinematic().getCameraLayer().getCameraFrame(cin.getElapsedTime());
        if (frame == null) return;

        MatrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(frame.getRoll()));
    }

    @ModifyVariable(method = "renderWorld", at = @At("STORE"), ordinal = 0)
    public Camera injectCinematicCamera(Camera camera) {
        ClientCinematic cin = PolCinematicsClient.getCCM().getLastRunningCinematic();
        if (cin == null) {
            return camera;
        }

        return PolCinematicsClient.getCinematicCamera();
    }

    /*@Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    private void bobView(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (PolCinematicsClient.getCCM().isCinematicRunning()) {
            ci.cancel();
        }
    }*/

}
