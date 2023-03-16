package dev.polv.polcinematics.cinematic.compositions.audio;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.core.ECompositionType;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;

import java.util.UUID;

public class AudioComposition extends Composition {

    private String audioPath;

    public AudioComposition(UUID uuid, String name, long duration, ECompositionType type) {
        super(uuid, name, duration, ECompositionType.AUDIO_COMPOSITION);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("audioPath", audioPath);
        return json;
    }
}
