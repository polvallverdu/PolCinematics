package dev.polv.polcinematics.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {

    public static void registerCommands() {
    }

    public static void registerClientCommands() {
        CommandRegistrationCallback.EVENT.register(CinematicCommand::register);
        CommandRegistrationCallback.EVENT.register(AudioCommand::register);
    }
}
