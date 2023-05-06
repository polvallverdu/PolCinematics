package dev.polv.polcinematics.mixin.client;

import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    /*@Inject(at = @At("HEAD"), method = "getPerspective", cancellable = true)
    public void injectPerspective(CallbackInfoReturnable<Perspective> cir) {
        if (!PolCinematicsClient.getCCM().isCinematicRunning()) return;

        var cameracompo = PolCinematicsClient.getCCM().getCameraComposition();
        if (cameracompo == null) return;

            switch (camera.getCameraType()) {
                case PLAYER -> {
                    return;
                }
            }

        if (!(cameracompo instanceof PlayerCameraComposition)) {
            cir.setReturnValue(Perspective.THIRD_PERSON_BACK);
        }
    }*/

}
