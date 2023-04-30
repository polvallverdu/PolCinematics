package dev.polv.polcinematics.utils;

import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.ICompositionType;

public class EnumUtils {

    public static ICompositionType findSubtype(ECompositionType type, String subtypeName) {
        if (type.hasSubtypes()) {
            for (ICompositionType subtype : type.getSubtypes()) {
                if (subtype.getName().equals(subtypeName)) {
                    return subtype;
                }
            }
        }

        return null;
    }

}
