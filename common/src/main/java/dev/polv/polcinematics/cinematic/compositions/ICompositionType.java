package dev.polv.polcinematics.cinematic.compositions;

public interface ICompositionType {

    String getName();

    Class<? extends Composition> getClazz();

    ECompositionType getParent();

}
