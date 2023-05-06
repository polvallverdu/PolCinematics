package dev.polv.polcinematics.client.cinematic;

import com.google.gson.JsonObject;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.client.PolCinematicsClient;
import dev.polv.polcinematics.net.ClientPacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ClientCinematicManager {

    private final List<ClientCinematic> clientCinematics;


    public ClientCinematicManager() {
        ClientTickEvent.CLIENT_PRE.register((Minecraft) -> this.tick());
        ClientGuiEvent.RENDER_HUD.register((MatrixStack, tickDelta) -> this.tickOverlay(MatrixStack));
        clientCinematics = Collections.synchronizedList(new ArrayList<>());

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            this.clientCinematics.forEach(ClientCinematic::stop);
            this.clientCinematics.clear();
        });

        new ClientPacketHandler();
    }

    public void loadCinematic(JsonObject json) {
        Cinematic cinematic = Cinematic.fromJson(json);
        ClientCinematic clientCinematic = new ClientCinematic(cinematic);

        // Check if cinematic is already loaded, to update it
        ClientCinematic loadedCinematic = this.getClientCinematic(cinematic.getUuid());
        if (loadedCinematic != null) {
            this.unloadCinematic(cinematic.getUuid());
        }

        this.clientCinematics.add(clientCinematic);
        PolCinematicsClient.LOGGER.info("Loaded cinematic: " + cinematic.getName());
    }

    public void unloadCinematic(UUID cinematicUuid) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic != null) {
            this.clientCinematics.remove(clientCinematic);
            clientCinematic.stop();
            clientCinematic.getCinematic().onCinematicUnload();
            PolCinematicsClient.LOGGER.info("Unloaded cinematic: " + clientCinematic.getCinematic().getName());
        }
    }

    public @Nullable ClientCinematic getClientCinematic(UUID cinematicUuid) {
        return this.clientCinematics.stream().filter(clientCinematic -> clientCinematic.getCinematic().getUuid().equals(cinematicUuid)).findFirst().orElse(null);
    }

    public @Nullable Cinematic getCinematic(UUID cinematicUuid) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic != null) {
            return clientCinematic.getCinematic();
        }
        return null;
    }

    public void start(UUID cinematicUuid, long elapsedTime, boolean paused) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic != null) {
            clientCinematic.start(elapsedTime, paused);
        }
    }

    private void tick() {
        this.clientCinematics.forEach(ClientCinematic::tick);
    }

    private void tickOverlay(MatrixStack MatrixStack) {
        this.clientCinematics.forEach((cc) -> cc.tickOverlay(MatrixStack));
    }

    public void pause(UUID cinematicUuid) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic != null) {
            clientCinematic.pause();
        }
    }

    public void resume(UUID cinematicUuid) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic != null) {
            clientCinematic.resume();
        }
    }

    public void moveTo(UUID cinematicUuid, long time) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic != null) {
            clientCinematic.moveTo(time);
        }
    }

    public void stop(UUID cinematicUuid) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic != null) {
            clientCinematic.stop();
        }
    }

    public boolean isCinematicRunning(UUID cinematicUuid) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic == null) return false;
        return clientCinematic.isPlaying();
    }

    public long getElapsedTime(UUID cinematicUuid) {
        ClientCinematic clientCinematic = this.getClientCinematic(cinematicUuid);
        if (clientCinematic == null) return 0;
        return clientCinematic.getElapsedTime();
    }

}

