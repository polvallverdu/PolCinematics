package dev.polv.polcinematics.fabric;

import dev.polv.polcinematics.PolCinematics;
import net.fabricmc.api.ModInitializer;

public class PolCinematicsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PolCinematics.init();
    }
}
