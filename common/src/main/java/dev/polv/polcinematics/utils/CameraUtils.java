package dev.polv.polcinematics.utils;

import dev.polv.polcinematics.internal.compositions.Composition;
import dev.polv.polcinematics.internal.compositions.types.camera.CameraComposition;

public class CameraUtils {

    public static boolean containsPositionArgument(Composition compo) {
        return compo instanceof CameraComposition && ((CameraComposition) compo).shouldInjectPositionValue();
    }

}
