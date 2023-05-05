package dev.polv.polcinematics;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.polv.polcinematics.cinematic.manager.ServerCinematicManager;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.commands.groups.GroupManager;
import net.minecraft.server.MinecraftServer;

public class PolCinematics {
    public static final String MOD_ID = "polcinematics";
    public static final String MOD_VERSION = "0.0.1";

    public static ServerCinematicManager CINEMATICS_MANAGER;
    private static GroupManager GROUP_MANAGER;

    public static MinecraftServer SERVER = null;

    public static void init() {
        CINEMATICS_MANAGER = new ServerCinematicManager();
        GROUP_MANAGER = new GroupManager();

        // Registering main command
        CommandRegistrationEvent.EVENT.register(PolCinematicsCommand::register);

        // Saving server instance
        LifecycleEvent.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        LifecycleEvent.SERVER_STOPPING.register(server -> {
            SERVER = null;
        });
    }

    public static GroupManager getGroupManager() {
        return GROUP_MANAGER;
    }
}
