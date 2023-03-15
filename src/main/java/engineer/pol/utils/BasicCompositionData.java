package engineer.pol.utils;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record BasicCompositionData(UUID uuid, String name, @Nullable Long duration) {
    public static BasicCompositionData fromJson(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        Long duration = null;
        try {
            duration = json.get("duration").getAsLong();
        } catch (Exception ignore) {}

        return new BasicCompositionData(uuid, name, duration);
    }
}
