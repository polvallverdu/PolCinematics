package dev.polv.polcinematics.utils;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record BasicCompositionData(UUID uuid, String name, @Nullable Long duration, @Nullable Long startTime) {
    public static BasicCompositionData fromJson(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        Long duration = null;
        Long startTime = null;
        try {
            duration = json.get("duration").getAsLong();
        } catch (Exception ignore) {}
        try {
            startTime = json.get("startTime").getAsLong();
        } catch (Exception ignore) {}

        return new BasicCompositionData(uuid, name, duration, startTime);
    }
}
