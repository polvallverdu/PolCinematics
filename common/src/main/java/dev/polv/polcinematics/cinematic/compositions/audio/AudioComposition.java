package dev.polv.polcinematics.cinematic.compositions.audio;

import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.value.EValueType;
import dev.polv.polcinematics.client.players.AudioPlayer;
import dev.polv.polcinematics.client.players.IMediaPlayer;

public class AudioComposition extends Composition {

    private AudioPlayer player;
    public static final String AUDIO_URL_KEY = "AUDIO_URL";

    public static AudioComposition create(String name, long duration) {
        AudioComposition compo = new AudioComposition();
        compo.init(name, duration, ECompositionType.AUDIO_COMPOSITION);
        return compo;
    }

    @Override
    protected void declareVariables() {
        this.declareProperty(AUDIO_URL_KEY, "The URL of the audio file", EValueType.STRING);
    }

    public void setAudioUrl(String audioUrl) {
        this.getProperty(AUDIO_URL_KEY).setValue(audioUrl);
    }

    private void initPlayer() {
        if (this.player != null)
            this.player.stop();
        this.player = (AudioPlayer) IMediaPlayer.createPlayer(AudioPlayer.class, this.getProperty(AUDIO_URL_KEY).getValueAsString());
    }

    @Override
    public void onCinematicLoad() {
        this.initPlayer();
    }

    @Override
    public void onCompositionStart() {
        this.player.play();
    }

    @Override
    public void onCompositionEnd() {
        this.player.pause();
        this.player.setTime(0);
    }

    @Override
    public void onCompositionResume() {
        this.player.resume();
    }

    @Override
    public void onCompositionPause() {
        this.player.pause();
    }

    @Override
    public void onCinematicTimeChange(long time) {
        this.player.setTime(time);
    }

    @Override
    public void onCompositionTick(long time) {
        int volume = (int) this.getAttribute("volume").getValue(time);
        volume = Math.min(100, Math.max(0, volume));
        float transVolume = (float) volume / 100;
        transVolume = transVolume * transVolume;
        if (this.player.getVolumeFloat() != transVolume)
            this.player.setVolume(volume);
    }

    @Override
    public void onCinematicUnload() {
        this.player.stop();
    }
}
