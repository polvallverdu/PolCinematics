package engineer.pol.commands;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class ModCommands {

    public static void registerCommands() {
    }

    public static void registerClientCommands() {
        CommandRegistrationCallback.EVENT.register(CinematicCommand::register);
    }
}
