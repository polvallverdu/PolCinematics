package dev.polv.polcinematics.client;

import com.google.gson.JsonObject;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraComposition;
import dev.polv.polcinematics.net.ClientPacketHandler;
import dev.polv.polcinematics.net.Packets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ClientCinematicManager {

    private Cinematic loadedCinematic = null;
    private boolean running = false;
    private boolean pause = false;

    private long startedTime;
    private long elapsedTime;

    public ClientCinematicManager() {
        ClientTickEvent.CLIENT_PRE.register((Minecraft) -> this.tick());
        ClientGuiEvent.RENDER_HUD.register((MatrixStack, tickDelta) -> this.tickOverlay(MatrixStack));
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
        this.loadedCinematic.onTimeChange(0, elapsedTime);
    }

    private void tick() {
        if (!this.running) return;

        if (this.pause) {
            this.startedTime = System.currentTimeMillis() - this.elapsedTime;
            return;
        }

        long oldElapsedTime = this.elapsedTime;
        this.elapsedTime = System.currentTimeMillis() - this.startedTime;

        if (this.elapsedTime >= this.loadedCinematic.getDuration()) {
            this.stop();
        }

        this.loadedCinematic.onTick(oldElapsedTime, this.elapsedTime);
    }

    private void tickOverlay(MatrixStack MatrixStack) {
        this.tick();
        if (!this.running) return;

        this.loadedCinematic.tickOverlay(MatrixStack, this.elapsedTime);
    }

    public void pause() {
        if (!this.running || this.pause) return;
        this.pause = true;

        this.loadedCinematic.onPause(this.elapsedTime);
    }

    public void resume() {
        if (!this.running || !this.pause) return;
        this.pause = false;

        this.loadedCinematic.onResume(this.elapsedTime);
    }

    public void moveTo(long time) {
        if (!this.running) return;
        long oldElapsedTime = this.elapsedTime;
        this.elapsedTime = time;
        this.startedTime = System.currentTimeMillis() - this.elapsedTime;

        this.loadedCinematic.onTimeChange(oldElapsedTime, this.elapsedTime);
    }

    public void stop() {
        if (!this.running) return;
        this.running = false;
        this.pause = false;

        this.loadedCinematic.onStop(this.elapsedTime);
    }

    public boolean isCinematicRunning() {
        return this.running;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public CameraComposition getCameraComposition() {
        this.tick();
        return this.loadedCinematic.getCameraComposition(this.elapsedTime);
    }

}

