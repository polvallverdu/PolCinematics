package dev.polv.polcinematics.cinematic.compositions.types.audio;

import dev.polv.polcinematics.cinematic.compositions.Composition;
import dev.polv.polcinematics.cinematic.compositions.values.EValueType;
import dev.polv.polcinematics.client.players.BrowserView;
import dev.polv.polcinematics.utils.DeclarationUtils;
import net.minecraft.client.MinecraftClient;

public class AudioComposition extends Composition {

    private BrowserView browserView;

    // TODO: Temp
    private static final String URL = "http://localhost:5500";
    public static final String AUDIO_URL_KEY = "AUDIO_URL";

    private float volume = 1;

    @Override
    protected void declare() {
        this.declareConstant(AUDIO_URL_KEY, "The URL of the audio file", EValueType.STRING);

        DeclarationUtils.declareVolumeTimevar(this);
    }

    public void setAudioUrl(String audioUrl) {
        this.getConstant(AUDIO_URL_KEY).setValue(audioUrl);
    }
    public String getAudioUrl() {
        return this.getConstant(AUDIO_URL_KEY).getValueAsString();
    }

    @Override
    public void onCinematicLoad() {
        String actualUrl = URL + "?url=" + getAudioUrl();
        this.browserView = new BrowserView(actualUrl);
        this.browserView.newUrl(actualUrl);
    }

    @Override
    public void onCinematicUnload() {
        MinecraftClient.getInstance().executeSync(this.browserView::stop);
    }

    @Override
    public void onCompositionStart() {
        this.browserView.runJS("resume();");
    }

    @Override
    public void onCompositionPause() {
        this.browserView.runJS("pause();");
    }

    @Override
    public void onCompositionEnd() {
        this.browserView.runJS("pause();");
    }

    @Override
    public void onCompositionResume() {
        this.browserView.runJS("resume();");
    }

    @Override
    public void onCinematicTimeChange(long time) {
        this.browserView.runJS("seekTo(" + (time/1000D) + ");");
    }

    @Override
    public void onCompositionTick(long time) {
        double volume = (double) this.getTimeVariable(DeclarationUtils.VOLUME_KEY).getValue(time);
        volume = Math.min(100, Math.max(0, volume));
        float transVolume = (float) volume / 100;
        transVolume = transVolume * transVolume;
        if (this.volume != transVolume) {
            this.volume = transVolume;
            this.browserView.runJS("setVolume(" + this.volume + ");");
        }
    }
}
