package dev.polv.polcinematics.cinematic.layers;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.Composition;

import java.util.UUID;

public class WrappedComposition {

    private final Composition composition;
    private long startTime;
    private long duration;

    public WrappedComposition(Composition composition, long startTime, long duration) {
        this.composition = composition;
        this.startTime = startTime;
        this.duration = duration;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return startTime + getDuration();
    }

    public Composition getComposition() {
        return composition;
    }

    public long getStartTime(long time) {
        return startTime + time;
    }

    public long getFinishTime(long time) {
        return startTime + time + getDuration();
    }

    public UUID getUuid() {
        return composition.getUuid();
    }

    protected void setDuration(long duration) {
        this.duration = duration;
    }

    public JsonObject toJson() {
        JsonObject json = composition.toJson();
        json.addProperty("duration", this.getDuration());
        json.addProperty("startTime", this.getStartTime());

        return json;
    }

}
