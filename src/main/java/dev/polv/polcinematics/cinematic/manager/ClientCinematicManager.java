package dev.polv.polcinematics.cinematic.manager;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraPos;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.net.ClientPacketHandler;
import dev.polv.polcinematics.net.Packets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ClientCinematicManager {

    private Cinematic loadedCinematic = null;
    private boolean running = false;
    private boolean pause = false;

    private long startedTime;
    private long elapsedTime;

    public ClientCinematicManager() {
        ClientTickEvents.START_CLIENT_TICK.register((minecraftClient) -> this.tick());
        WorldRenderEvents.START.register(context -> this.tick());
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> this.tickOverlay(matrixStack));
        new ClientPacketHandler();
    }

    public void loadCinematic(JsonObject json) {
        this.loadedCinematic = Cinematic.fromJson(json);
        System.out.println("Loaded cinematic: " + this.loadedCinematic.getName());
        // TODO: Load images, audios and videos
        Packets.sendCinematicReady();
    }

    public void start(boolean paused) {
        if (this.running) this.stop();
        this.running = true;
        this.pause = paused;

        this.startedTime = System.currentTimeMillis();
    }

    public void startFrom(long elapsedTime, boolean paused) {
        if (this.running) this.stop();
        this.running = true;
        this.pause = paused;

        this.startedTime = System.currentTimeMillis() - elapsedTime;
        this.elapsedTime = elapsedTime;

        this.loadedCinematic.onStart();
        this.loadedCinematic.onTimeChange(elapsedTime);
    }

    private void tick() {
        if (!this.running) return;

        if (this.pause) {
            this.startedTime = System.currentTimeMillis() - this.elapsedTime;
            return;
        }

        this.elapsedTime = System.currentTimeMillis() - this.startedTime;

        if (this.elapsedTime >= this.loadedCinematic.getDuration()) {
            this.stop();
        }
    }

    private void tickOverlay(MatrixStack matrixStack) {
        this.tick();
        if (!this.running) return;

        this.loadedCinematic.tickOverlay(matrixStack, this.elapsedTime);
    }

    public void pause() {
        if (!this.running) return;
        this.pause = true;

        this.loadedCinematic.onPause();
    }

    public void resume() {
        if (!this.running) return;
        this.pause = false;

        this.loadedCinematic.onResume();
    }

    public void moveTo(long time) {
        if (!this.running) return;
        this.elapsedTime = time;
        this.startedTime = System.currentTimeMillis() - this.elapsedTime;

        this.loadedCinematic.onTimeChange(this.elapsedTime);
    }

    public void stop() {
        if (!this.running) return;
        this.running = false;
        this.pause = false;

        this.loadedCinematic.onStop();
    }

    public boolean isCinematicRunning() {
        return this.running;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public CameraPos getCameraPos() {
        this.tick();
        return this.loadedCinematic.getCameraPos(this.elapsedTime);
    }

}

