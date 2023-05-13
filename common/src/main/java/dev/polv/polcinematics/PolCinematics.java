package dev.polv.polcinematics;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.polv.polcinematics.cinematic.manager.ServerCinematicManager;
import dev.polv.polcinematics.commands.PolCinematicsCommand;
import dev.polv.polcinematics.groups.GroupManager;
import dev.polv.taskmanager.core.TaskManager;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolCinematics {
    public static final String MOD_ID = "polcinematics";
    public static final String MOD_VERSION = "0.0.1";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ServerCinematicManager CINEMATICS_MANAGER;
    private static GroupManager GROUP_MANAGER;
    private static TaskManager TASK_MANAGER;

    public static MinecraftServer SERVER = null;

    public static void init() {
        TASK_MANAGER = TaskManager.create();
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

        // Registering task manager
        TickEvent.SERVER_PRE.register(server -> {
            if (SERVER != null) {
                SERVER.executeSync(() -> TASK_MANAGER.tick());
            }
        });
    }

    public static GroupManager getGroupManager() {
        return GROUP_MANAGER;
    }

    public static TaskManager getTaskManager() {
        return TASK_MANAGER;
    }
}
