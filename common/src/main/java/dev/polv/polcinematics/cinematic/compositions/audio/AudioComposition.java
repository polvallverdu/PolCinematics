package dev.polv.polcinematics.cinematic.compositions.audio;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.client.players.AudioPlayer;
import dev.polv.polcinematics.client.players.IMediaPlayer;

import java.util.UUID;

public class AudioComposition extends Composition {

    private String audioPath;
    private AudioPlayer player;

    public AudioComposition(UUID uuid, String name, long duration, String mediaPath) {
        super(uuid, name, duration, ECompositionType.AUDIO_COMPOSITION);
        this.audioPath = mediaPath;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("audioPath", audioPath);
        return json;
    }

    public static AudioComposition fromJson(JsonObject json) {
        return new AudioComposition(
                UUID.fromString(json.get("uuid").getAsString()),
                json.get("name").getAsString(),
                json.get("duration").getAsLong(),
                json.get("audioPath").getAsString()
        );
    }

    @Override
    public void onCinematicLoad() {
        this.player = (AudioPlayer) IMediaPlayer.createPlayer(AudioPlayer.class, audioPath);
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
