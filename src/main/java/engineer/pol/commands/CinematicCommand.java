package engineer.pol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import engineer.pol.client.PolCinematicsClient;
import engineer.pol.client.overlays.VideoOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

final public class CinematicCommand {

    private static VideoOverlay videoOverlay = null;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        //dispatcher.register();

    }
}
