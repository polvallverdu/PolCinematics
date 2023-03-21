package dev.polv.polcinematics.fabric;

import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.commands.AudioCommand;
import dev.polv.polcinematics.commands.CinematicCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class PolCinematicsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        PolCinematics.init();
    }

}
