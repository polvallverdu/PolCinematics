package dev.polv.polcinematics.client.cinematic;

import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.net.Packets;
import net.minecraft.client.util.math.MatrixStack;

public class ClientCinematic {

    public enum EClientCinematicLoadingState {
        LOADING,
        STOPPED,
        PLAYING,
    }

    private Cinematic cinematic;
    private EClientCinematicLoadingState state;
    private boolean pause;

    private long startedTime;
    private long elapsedTime;

    public ClientCinematic(Cinematic cinematic) {
        this.cinematic = cinematic;
        // this.state = EClientCinematicState.LOADING; TODO: Logic to notice that cinematic has been loaded (it's components)
        // Cinematic#load()
        cinematic.onCinematicLoad();

        this.state = EClientCinematicLoadingState.STOPPED;
        Packets.sendCinematicReady(this.cinematic.getUuid());
    }

    public void start(long elapsedTime, boolean paused) {
        if (this.state == EClientCinematicLoadingState.LOADING) return;
        if (this.state == EClientCinematicLoadingState.PLAYING) this.stop();
        this.state = EClientCinematicLoadingState.PLAYING;
        this.pause = paused;

        this.startedTime = System.currentTimeMillis() - elapsedTime;
        elapsedTime = Math.max(0, elapsedTime);
        this.elapsedTime = elapsedTime;

        this.cinematic.onStart();
        if (elapsedTime != 0) {
            this.cinematic.onTimeChange(0, elapsedTime);
        }
    }

    protected void tick() {
        if (this.state != EClientCinematicLoadingState.PLAYING) return;

        if (this.pause) {
            this.startedTime = System.currentTimeMillis() - this.elapsedTime;
            return;
        }

        long oldElapsedTime = this.elapsedTime;
        this.elapsedTime = System.currentTimeMillis() - this.startedTime;

        if (this.elapsedTime >= this.cinematic.getDurationInMillis()) {
            this.stop();
        }

        this.cinematic.onTick(oldElapsedTime, this.elapsedTime);
    }

    protected void tickOverlay(MatrixStack MatrixStack) {
        this.tick();
        if (this.state != EClientCinematicLoadingState.PLAYING) return;

        this.cinematic.tickOverlay(MatrixStack, this.elapsedTime);
    }

    public void pause() {
        if (this.state != EClientCinematicLoadingState.PLAYING || this.pause) return;
        this.pause = true;

        this.cinematic.onPause(this.elapsedTime);
    }

    public void resume() {
        if (this.state != EClientCinematicLoadingState.PLAYING || !this.pause) return;
        this.pause = false;

        this.cinematic.onResume(this.elapsedTime);
    }

    public void moveTo(long time) {
        if (this.state != EClientCinematicLoadingState.PLAYING) return;
        long oldElapsedTime = this.elapsedTime;
        this.elapsedTime = time;
        this.startedTime = System.currentTimeMillis() - this.elapsedTime;

        this.cinematic.onTimeChange(oldElapsedTime, this.elapsedTime);
    }

    public void stop() {
        if (this.state != EClientCinematicLoadingState.PLAYING) return;
        this.state = EClientCinematicLoadingState.STOPPED;
        this.pause = false;

        this.cinematic.onStop(this.elapsedTime);
    }

    public Cinematic getCinematic() {
        return cinematic;
    }

    public EClientCinematicLoadingState getState() {
        return state;
    }

    public boolean isLoaded() {
        return this.state != EClientCinematicLoadingState.LOADING;
    }

    public boolean isPlaying() {
        return this.state == EClientCinematicLoadingState.PLAYING;
    }

    public boolean isPause() {
        return pause;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}
