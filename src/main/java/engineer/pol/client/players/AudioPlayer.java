package engineer.pol.client.players;

import engineer.pol.PolCinematics;
import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.MediaPlayerHandler;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;

public class AudioPlayer {

    private String mediaPath;
    private SimpleMediaPlayer player;

    private DynamicResourceLocation playerResourceLocation;

    private boolean playing = false;
    private float volume = 1f;

    public AudioPlayer(String mediaPath) {
        this.mediaPath = mediaPath;
        this.playerResourceLocation = new DynamicResourceLocation(PolCinematics.MODID, "audio/" + this.mediaPath.hashCode());

        MediaPlayerHandler.getInstance().registerPlayerOnFreeResLoc(this.playerResourceLocation, SimpleMediaPlayer.class);
        this.player = (SimpleMediaPlayer) MediaPlayerHandler.getInstance().getMediaPlayer(this.playerResourceLocation);
        this.player.api().media().prepare(this.mediaPath);
        this.player.api().audio().setVolume(this.getVolume()); // TODO: Better
    }

    public void setTime(long time) {
        if (this.player != null) {
            this.player.api().controls().setTime(time);
        }
    }

    public SimpleMediaPlayer getPlayer() {
        return this.player;
    }

    public void restart() {
        this.setTime(0L);
    }

    public void play() {
        if (this.playing) return;
        this.playing = true;
        this.player.api().controls().play();
    }

    public void pause() {
        if (!this.playing) return;
        this.playing = false;
        this.player.api().controls().pause();
    }

    public int getVolume() {
        return (int) ((this.volume * this.volume) * 100);
    }

    public void setLooping(boolean b) {
        if (this.player != null) {
            this.player.api().controls().setRepeat(b);
        }
    }

    public boolean isLooping() {
        if (this.player != null) {
            return this.player.api().controls().getRepeat();
        }
        return false;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void stop() {
        if (this.player != null) {
            this.playing = false;
            this.player.api().controls().stop();
        }
    }

}
