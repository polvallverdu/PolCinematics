package engineer.pol.cinematic.manager;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.Cinematic;
import engineer.pol.cinematic.compositions.camera.CameraPos;
import engineer.pol.net.ClientPacketHandler;
import engineer.pol.net.Packets;
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

    public void start() {
        if (this.running) this.stop();
        this.running = true;

        this.startedTime = System.currentTimeMillis();
    }

    public void startEditorMode() {
        if (this.running) return;
        this.running = true;

    }

    private void tick() {
        if (!this.running) return;

        this.elapsedTime = System.currentTimeMillis() - this.startedTime;

        if (this.elapsedTime >= this.loadedCinematic.getDuration()) {
            this.stop();
        }
    }

    private void tickOverlay(MatrixStack matrixStack) {
        if (!this.running) return;
        this.tick();

        this.loadedCinematic.tickOverlay(matrixStack, this.elapsedTime);
    }

    public void stop() {
        if (!this.running) return;
        this.running = false;
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

