package dev.polv.polcinematics.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PolCinematicsClient {

    private static PolCinematicsClient INSTANCE;

    private ClientCinematicManager clientCinematicManager;

    public void onInitializeClient() {
        INSTANCE = this;

        this.clientCinematicManager = new ClientCinematicManager();
    }

    public static PolCinematicsClient getInstance() {
        return INSTANCE;
    }

    public ClientCinematicManager getClientCinematicManager() {
        return clientCinematicManager;
    }

    public static ClientCinematicManager getCCM() {
        return getInstance().getClientCinematicManager();
    }
}
