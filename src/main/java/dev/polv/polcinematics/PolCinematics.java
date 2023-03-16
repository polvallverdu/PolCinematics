package dev.polv.polcinematics;

import dev.polv.polcinematics.cinematic.manager.ServerCinematicManager;
import dev.polv.polcinematics.commands.ModCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class PolCinematics implements ModInitializer {

    public static String MODID = "polcinematics";
    public static ServerCinematicManager CINEMATICS_MANAGER;
    public static MinecraftServer SERVER = null;

    @Override
    public void onInitialize() {
        ModCommands.registerCommands();
        CINEMATICS_MANAGER = new ServerCinematicManager();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            SERVER = null;
        });
    }
}
