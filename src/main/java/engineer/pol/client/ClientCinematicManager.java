package engineer.pol.client;

import engineer.pol.client.overlays.OverlayManager;
import engineer.pol.client.renderer.FovManager;
import engineer.pol.client.overlays.BlackBarsOverlay;
import engineer.pol.client.renderer.PlayerRendererManager;

public class ClientCinematicManager {

    private boolean running;

    // Extensions
    private final FovManager fovManager;
    private final PlayerRendererManager playerRendererManager;

    // Overlays
    private final OverlayManager overlayManager;

    public ClientCinematicManager() {
        running = false;

        // Extensions
        this.fovManager = new FovManager();
        this.playerRendererManager = new PlayerRendererManager();

        this.overlayManager = new OverlayManager();
    }

    public void sendStartToExtensions() {
        this.fovManager.onStart();
        this.playerRendererManager.onStart();
    }

    public void sendStopToExtensions() {
        this.fovManager.onEnd();
        this.playerRendererManager.onEnd();
    }

    public void start() {
        running = true;
        this.sendStartToExtensions();
        this.overlayManager.appear();
        this.fovManager.modifyFov(40);
    }

    public void stop() {
        running = false;
        this.overlayManager.dissapear();
        this.sendStopToExtensions();
    }

    public boolean isRunning() {
        return running;
    }

    public FovManager getFovManager() {
        return fovManager;
    }

    public PlayerRendererManager getPlayerRendererManager() {
        return playerRendererManager;
    }
}

