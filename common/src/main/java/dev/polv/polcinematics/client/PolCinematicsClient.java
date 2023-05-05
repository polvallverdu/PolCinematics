package dev.polv.polcinematics.client;

import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.client.cinematic.ClientCinematicManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class PolCinematicsClient {

    private static PolCinematicsClient INSTANCE;
    public static final Logger LOGGER = LoggerFactory.getLogger(PolCinematics.MOD_ID + "-client");

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
