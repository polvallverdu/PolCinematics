package dev.polv.polcinematics.utils;

import dev.polv.polcinematics.cinematic.compositions.Composition;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;

public class DeclarationUtils {

    public static final String X_KEY = "X";
    public static final String Y_KEY = "Y";
    public static final String Z_KEY = "Z";
    public static final String PITCH_KEY = "PITCH";
    public static final String YAW_KEY = "YAW";
    public static final String ROLL_KEY = "ROLL";
    public static final String WIDTH_KEY = "WIDTH";
    public static final String HEIGHT_KEY = "HEIGHT";
    public static final String ALPHA_KEY = "ALPHA";
    public static final String VOLUME_KEY = "VOLUME";
    public static final String COLOR_KEY = "COLOR";

    public static void declareCameraConstants(Composition composition) {
        composition.declareConstant(X_KEY, "Coordinate X", EValueType.DOUBLE);
        composition.declareConstant(Y_KEY, "Coordinate Y", EValueType.DOUBLE);
        composition.declareConstant(Z_KEY, "Coordinate Z", EValueType.DOUBLE);
        composition.declareConstant(PITCH_KEY, "Pitch Rotation", EValueType.DOUBLE);
        composition.declareConstant(YAW_KEY, "Yaw Rotation", EValueType.DOUBLE);
        composition.declareConstant(ROLL_KEY, "Roll Rotation", EValueType.DOUBLE);
    }

    public static void declareCameraTimevars(Composition composition) {
        composition.declareConstant(X_KEY, "Coordinate X", EValueType.DOUBLE);
        composition.declareConstant(Y_KEY, "Coordinate Y", EValueType.DOUBLE);
        composition.declareConstant(Z_KEY, "Coordinate Z", EValueType.DOUBLE);
        composition.declareConstant(PITCH_KEY, "Pitch Rotation", EValueType.DOUBLE);
        composition.declareConstant(YAW_KEY, "Yaw Rotation", EValueType.DOUBLE);
        composition.declareConstant(ROLL_KEY, "Roll Rotation", EValueType.DOUBLE);
    }

    public static void declareScreenTimevars(Composition composition) {
        composition.declareTimeVariable(X_KEY, "Goes from 0% to 100%", EValueType.DOUBLE);
        composition.declareTimeVariable(Y_KEY, "Goes from 0% to 100%", EValueType.DOUBLE);
        composition.declareTimeVariable(WIDTH_KEY, "Goes from 0% to 100%", EValueType.DOUBLE, 50D);
        composition.declareTimeVariable(HEIGHT_KEY, "Goes from 0% to 100%", EValueType.DOUBLE, 50D);
    }

    public static void declareAlphaTimevar(Composition composition) {
        composition.declareTimeVariable(ALPHA_KEY, "Goes from 0% to 100%", EValueType.DOUBLE, 100D);
    }

    public static void declareVolumeTimevar(Composition composition) {
        composition.declareTimeVariable(VOLUME_KEY, "Goes from 0% to 100%", EValueType.DOUBLE, 100D);
    }

    public static void declareColorTimevar(Composition composition) {
        composition.declareTimeVariable(COLOR_KEY, "Written in Hex. Example: #ff0204", EValueType.COLOR);
    }

}
