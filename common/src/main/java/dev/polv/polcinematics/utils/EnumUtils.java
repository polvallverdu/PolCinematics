package dev.polv.polcinematics.utils;

import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.ICompositionType;
import org.jetbrains.annotations.Nullable;

public class EnumUtils {

    public static ICompositionType findSubtype(ECompositionType type, @Nullable String subtypeName) {
        if (type.hasSubtypes() && subtypeName != null) {
            for (ICompositionType subtype : type.getSubtypes()) {
                if (subtype.getName().equals(subtypeName)) {
                    return subtype;
                }
            }
        }

        return null;
    }

}
