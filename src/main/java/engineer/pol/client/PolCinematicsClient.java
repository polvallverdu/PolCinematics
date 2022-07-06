package engineer.pol.client;

import com.mojang.brigadier.CommandDispatcher;
import engineer.pol.client.overlays.BlackBarsOverlay;
import engineer.pol.commands.CinematicCommand;
import engineer.pol.commands.ModCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PolCinematicsClient implements ClientModInitializer {

    private static PolCinematicsClient INSTANCE;

    private ClientCinematicManager clientCinematicManager;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        ModCommands.registerClientCommands();
        this.clientCinematicManager = new ClientCinematicManager();
    }

    public static PolCinematicsClient getInstance() {
        return INSTANCE;
    }

    public ClientCinematicManager getClientCinematicManager() {
        return clientCinematicManager;
    }
}
