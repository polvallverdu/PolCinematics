package dev.polv.polcinematics.mixin.client;

import dev.polv.polcinematics.cinematic.compositions.camera.PlayerCameraComposition;
import dev.polv.polcinematics.client.PolCinematicsClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Inject(at = @At("HEAD"), method = "getPerspective", cancellable = true)
    public void injectPerspective(CallbackInfoReturnable<Perspective> cir) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

        var cameracompo = PolCinematicsClient.getCCM().getCameraComposition();
        if (cameracompo == null) return;

            /*switch (camera.getCameraType()) {
                case PLAYER -> {
                    return;
                }
            }*/

        if (!(cameracompo instanceof PlayerCameraComposition)) {
            cir.setReturnValue(Perspective.THIRD_PERSON_BACK);
        }
    }

}
