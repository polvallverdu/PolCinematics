package dev.polv.polcinematics;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.polv.polcinematics.cinematic.manager.ServerCinematicManager;
import dev.polv.polcinematics.commands.AudioCommand;
import dev.polv.polcinematics.commands.CinematicCommand;
import dev.polv.polcinematics.commands.VideoCommand;
import net.minecraft.server.MinecraftServer;

public class PolCinematics {
    public static final String MOD_ID = "polcinematics";

    public static ServerCinematicManager CINEMATICS_MANAGER;

    public static MinecraftServer SERVER = null;

    public static void init() {
        CINEMATICS_MANAGER = new ServerCinematicManager();

        // Registering commands
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, registrationEnvironment) -> {
            AudioCommand.register(dispatcher, registryAccess, registrationEnvironment);
            VideoCommand.register(dispatcher, registryAccess, registrationEnvironment);
            CinematicCommand.register(dispatcher, registryAccess, registrationEnvironment);
        });

        // Saving server instance
        LifecycleEvent.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        LifecycleEvent.SERVER_STOPPING.register(server -> {
            SERVER = null;
        });
    }

}
