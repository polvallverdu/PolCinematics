package dev.polv.polcinematics.cinematic.compositions.core;

public interface ICompositionType {

    String getName();

    Class<? extends Composition> getClazz();

}
