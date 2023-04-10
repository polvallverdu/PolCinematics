package dev.polv.polcinematics.cinematic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.camera.PlayerCameraComposition;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.Timeline;
import dev.polv.polcinematics.cinematic.compositions.overlay.OverlayComposition;
import dev.polv.polcinematics.utils.BasicCompositionData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cinematic {

    private final UUID uuid;
    private String name;
    private long duration;

    private final Timeline cameraTimeline;
    private final List<Timeline> timelines;

    protected Cinematic(UUID uuid, String name, long duration, Timeline cameraTimeline, List<Timeline> timelines) {
        this.uuid = uuid;
        this.name = name;
        this.duration = duration;
        this.cameraTimeline = cameraTimeline;
        this.cameraTimeline.setOverlapStrategy(EOverlapStrategy.MOVE);
        this.timelines = timelines;
    }

    /**
     * Adds a new timeline to the cinematic
     *
     * @return The created {@link Timeline}
     */
    public Timeline addTimeline() {
        Timeline timeline = new Timeline();
        this.timelines.add(timeline);
        return timeline;
    }

    /**
     * Removes a timeline from the cinematic by its index
     *
     * @param index The index of the timeline to remove
     */
    public void removeTimeline(int index) {
        this.timelines.remove(index);
    }

    /**
     * Removes a timeline from the cinematic
     *
     * @param timeline The timeline to remove
     */
    public void removeTimeline(Timeline timeline) {
        this.timelines.remove(timeline);
    }

    /**
     * @return The duration of the cinematic in milliseconds
     */
    public long getDurationInMillis() {
        return duration;
    }

    /**
     * @return The duration of the cinematic
     */
    public Duration getDuration() {
        return Duration.ofMillis(duration);
    }

    /**
     * @return The {@link Timeline} of the camera
     */
    public Timeline getCameraTimeline() {
        return cameraTimeline;
    }

    /**
     * @return The {@link Timeline} at the given index
     */
    public Timeline getTimeline(int index) {
        return this.timelines.get(index);
    }

    /**
     * @return The amount of timelines in the cinematic
     */
    public int getTimelineCount() {
        return this.timelines.size();
    }

    /**
     * Get the timeline and composition by the given composition UUID
     *
     * @param compositionUUID The {@link UUID} of a composition
     * @return A {@link Pair} containing the timeline and the composition, or null if the composition was not found.
     */
    public Pair<Timeline, Composition> getTimelineAndComposition(UUID compositionUUID) {
        Timeline.WrappedComposition c = this.cameraTimeline.findWrappedComposition(compositionUUID);
        if (c != null) {
            return new Pair<>(this.cameraTimeline, c.getComposition());
        }

        for (Timeline timeline : this.timelines) {
            c = timeline.findWrappedComposition(compositionUUID);
            if (c != null) {
                return new Pair<>(timeline, c.getComposition());
            }
        }
        return null;
    }

    public void tickOverlay(MatrixStack MatrixStack, long time) {
        for (int i = this.timelines.size() - 1; i >= 0; i--) {  // loop reverse
            Timeline timeline = this.timelines.get(i);

            Composition compo = timeline.getComposition(time);
            if (!(compo instanceof OverlayComposition)) continue;

            ((OverlayComposition) compo).tick(MatrixStack, time);
        }
    }

    public void onCinematicLoad() {
        this.cameraTimeline.onCinematicLoad();
        for (Timeline timeline : this.timelines) {
            timeline.onCinematicLoad();
        }
    }

    public void onCinematicUnload() {
        this.cameraTimeline.onCinematicUnload();
        for (Timeline timeline : this.timelines) {
            timeline.onCinematicUnload();
        }
    }

    public void onStart() {
        this.cameraTimeline.onStart();
        for (Timeline timeline : this.timelines) {
            timeline.onStart();
        }
    }

    public void onPause(long time) {
        this.cameraTimeline.onPause(time);
        for (Timeline timeline : this.timelines) {
            timeline.onPause(time);
        }
    }

    public void onResume(long time) {
        this.cameraTimeline.onResume(time);
        for (Timeline timeline : this.timelines) {
            timeline.onResume(time);
        }
    }

    public void onStop(long time) {
        this.cameraTimeline.onStop(time);
        for (Timeline timeline : this.timelines) {
            timeline.onStop(time);
        }
    }

    public void onTimeChange(long oldTime, long time) {
        this.cameraTimeline.onTimeChange(oldTime, time);
        for (Timeline timeline : this.timelines) {
            timeline.onTimeChange(oldTime, time);
        }
    }

    public void onTick(long lastTick, long time) {
        this.cameraTimeline.onTick(lastTick, time);
        for (Timeline timeline : this.timelines) {
            timeline.onTick(lastTick, time);
        }
    }
    
    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("uuid", this.uuid.toString());
        json.addProperty("name", this.name);
        json.addProperty("duration", this.duration);

        json.add("cameraTimeline", this.cameraTimeline.toJson());

        JsonArray overlayTimelineJson = new JsonArray();
        for (Timeline timeline : this.timelines) {
            overlayTimelineJson.add(timeline.toJson());
        }
        json.add("overlayTimeline", overlayTimelineJson);

        return json;
    }
    
    public static Cinematic fromJson(JsonObject json) {
        BasicCompositionData data = BasicCompositionData.fromJson(json);

        Timeline cameraTimeline = Timeline.fromJson(json.get("cameraTimeline").getAsJsonObject());
        List<Timeline> overlayTimeline = new ArrayList<>();
        JsonArray overlayTimelineJson = json.get("overlayTimeline").getAsJsonArray();
        for (int i = 0; i < overlayTimelineJson.size(); i++) {
            overlayTimeline.add(Timeline.fromJson(overlayTimelineJson.get(i).getAsJsonObject()));
        }

        return new Cinematic(data.uuid(), data.name(), data.duration(), cameraTimeline, overlayTimeline);
    }

    public static Cinematic create(String name, long duration) {
        Cinematic cinematic = new Cinematic(UUID.randomUUID(), name, duration, new Timeline(), new ArrayList<>());
        cinematic.addTimeline();
        cinematic.cameraTimeline.add(new PlayerCameraComposition("default", duration), 0);
        return cinematic;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
