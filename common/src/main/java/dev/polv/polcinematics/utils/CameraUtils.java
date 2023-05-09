package dev.polv.polcinematics.utils;

import dev.polv.polcinematics.cinematic.compositions.Composition;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraComposition;

public class CameraUtils {

    public static boolean containsPositionArgument(Composition compo) {
        return compo instanceof CameraComposition && ((CameraComposition) compo).shouldInjectPositionValue();
    }

}
