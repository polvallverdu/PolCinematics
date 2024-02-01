package dev.polv.polcinematics.utils;

import dev.polv.polcinematics.internal.compositions.ECompositionType;
import dev.polv.polcinematics.internal.compositions.ICompositionType;
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
