package dev.polv.polcinematics.fabric.client;

import dev.polv.polcinematics.client.PolCinematicsClient;
import net.fabricmc.api.ClientModInitializer;

public class PolCinematicsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new PolCinematicsClient().onInitializeClient();
    }

}
