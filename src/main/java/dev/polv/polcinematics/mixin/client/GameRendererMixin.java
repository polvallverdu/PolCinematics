package dev.polv.polcinematics.mixin.client;

import dev.polv.polcinematics.client.PolCinematicsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final private Camera camera;
    private Camera cinematicCamera = new Camera();

    /*@Inject(at = @At("HEAD"), method = "getFov", cancellable = true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> info) {
        if (PolCinematicsClient.getInstance().getClientCinematicManager().isRunning()) {
            info.cancel();
            info.setReturnValue(PolCinematicsClient.getInstance().getClientCinematicManager().getFovManager().getMixinFov());
        }
    }*/

    /*@Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    public void bobView(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (PolCinematicsClient.getInstance().getClientCinematicManager().isRunning()) {
            ci.cancel();
        }
    }*/

    /*@Inject(at = @At("HEAD"), method = "renderHand", cancellable = true)
    public void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if (PolCinematicsClient.getInstance().getClientCinematicManager().isRunning() && !PolCinematicsClient.getInstance().getClientCinematicManager().getPlayerRendererManager().isRenderFirstPersonHand()) {
            ci.cancel();
        }
    }*/

    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getPitch()F"
            )
    )
    private void applyRoll(float float_1, long long_1, MatrixStack matrixStack, CallbackInfo ci) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

        // TODO: Fix roll
        //matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) PolCinematicsClient.getCCM().getCameraPos().getRoll()));
    }

    /*@Inject(at = @At("HEAD"), method = "renderWorld")
    public void beforeRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

    }*/

    @ModifyVariable(method = "renderWorld", at = @At("STORE"), ordinal = 0)
    public Camera cameraSwitcher(Camera camera) {
        return PolCinematicsClient.getCCM().isCinematicRunning() ? this.cinematicCamera : camera;
    }

}