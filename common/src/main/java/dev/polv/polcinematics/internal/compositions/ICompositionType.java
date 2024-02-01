package dev.polv.polcinematics.internal.compositions;

public interface ICompositionType {

    String getName();

    Class<? extends Composition> getClazz();

    ECompositionType getParent();

}
