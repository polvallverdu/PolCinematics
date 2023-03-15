package engineer.pol.cinematic.compositions.audio;

import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.core.Composition;
import engineer.pol.cinematic.compositions.core.ECompositionType;

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
