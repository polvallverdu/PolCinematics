package dev.polv.polcinematics.client.players;

import dev.polv.polcinematics.PolCinematics;
import dev.polv.vlcvideo.api.DynamicResourceLocation;
import dev.polv.vlcvideo.api.MediaPlayerHandler;
import dev.polv.vlcvideo.api.mediaPlayer.AudioMediaPlayer;

public class AudioPlayer implements IMediaPlayer {

    private final String path;
    private final DynamicResourceLocation playerResourceLocation;
    private final AudioMediaPlayer player;
    private boolean playing = false;
    private float volume = 1f;

    public AudioPlayer(String path) {
        this.path = path;
        this.playerResourceLocation = new DynamicResourceLocation(PolCinematics.MOD_ID, "audio/" + this.path.hashCode());
        MediaPlayerHandler.getInstance().registerPlayerOnFreeResLoc(this.playerResourceLocation, AudioMediaPlayer.class);
        this.player = (AudioMediaPlayer) MediaPlayerHandler.getInstance().getMediaPlayer(this.playerResourceLocation);
        this.player.api().media().prepare(this.path);
        this.player.api().audio().setVolume(this.getVolume());
    }

    @Override
    public void play() {
        if (this.playing) return;
        this.playing = true;
        this.player.api().controls().play();
    }

    @Override
    public void pause() {
        if (!this.playing) return;
        this.player.api().controls().pause();
    }

    @Override
    public void resume() {
        if (!this.playing) return;
        this.player.api().controls().play();
    }

    @Override
    public void stop() {
        if (!this.playing) return;
        this.player.api().controls().stop();
        // MediaPlayerHandler.getInstance().flagPlayerRemoval(this.playerResourceLocation); TODO: DEBUG WHY DOES THIS CREATE A CALLSTACK ERROR WITH PRE EVENT.
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        this.player.api().audio().setVolume(this.getVolume());
    }

    @Override
    public float getVolumeFloat() {
        return this.volume*this.volume;
    }

    @Override
    public int getVolume() {
        return (int) (this.getVolumeFloat()*100);
    }

    @Override
    public void setTime(long newTime) {
        if (!this.playing) return;
        this.player.api().controls().setTime(newTime);
    }
}
