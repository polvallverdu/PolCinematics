package dev.polv.polcinematics.client.mixin;

import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.types.camera.PlayerCameraComposition;
import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.client.cinematic.ClientCinematic;
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
        ClientCinematic cin = PolCinematicsClient.getCCM().getLastRunningCinematic();
        if (cin == null) return;
        CameraComposition cameracompo = cin.getCinematic().getCameraTimeline().getCameraComposition(cin.getElapsedTime());
        if (cameracompo instanceof PlayerCameraComposition) {
            switch (((PlayerCameraComposition) cameracompo).getPerspective()) {
                case FIRST_PERSON -> cir.setReturnValue(Perspective.FIRST_PERSON);
                case THIRD_PERSON -> cir.setReturnValue(Perspective.THIRD_PERSON_FRONT);
                case SECOND_PERSON -> cir.setReturnValue(Perspective.THIRD_PERSON_BACK);
            }
        }

        cir.setReturnValue(Perspective.THIRD_PERSON_BACK);
    }

}
