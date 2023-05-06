package dev.polv.polcinematics.cinematic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.compositions.types.camera.CameraComposition;
import dev.polv.polcinematics.cinematic.compositions.types.camera.ECameraType;
import dev.polv.polcinematics.cinematic.timelines.CameraTimeline;
import dev.polv.polcinematics.cinematic.compositions.Composition;
import dev.polv.polcinematics.cinematic.timelines.Timeline;
import dev.polv.polcinematics.cinematic.compositions.types.overlay.OverlayComposition;
import dev.polv.polcinematics.cinematic.timelines.WrappedComposition;
import dev.polv.polcinematics.exception.OverlapException;
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

    private final CameraTimeline cameraTimeline;
    private final List<Timeline> timelines;

    protected Cinematic(UUID uuid, String name, long duration, CameraTimeline cameraTimeline, List<Timeline> timelines) {
        this.uuid = uuid;
        this.name = name;
        this.duration = duration;
        this.cameraTimeline = cameraTimeline;
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

    public boolean canMove(Timeline timeline, int positions, boolean isUp) {
        int index = this.timelines.indexOf(timeline);
        if (isUp) {
            index -= positions;
        } else {
            index += positions;
        }
        return index >= 0 && index < this.timelines.size();
    }

    public void moveTimeline(Timeline timeline, int positions, boolean isUp) {
        int index = this.timelines.indexOf(timeline);
        if (isUp) {
            index -= positions;
        } else {
            index += positions;
        }
        this.timelines.remove(timeline);
        this.timelines.add(index, timeline);
    }

    /**
     * Moves a composition from one timeline to another, and changes its start time
     *
     * @param composition The {@link WrappedComposition} to move
     * @param oldTimeline The {@link Timeline} the composition is currently in
     * @param newTimeline The {@link Timeline} to move the composition to
     * @param newtime The new start time of the composition
     * @throws OverlapException If the composition overlaps with another composition in the new timeline
     */
    public void moveComposition(WrappedComposition composition, Timeline oldTimeline, Timeline newTimeline, long newtime) throws OverlapException {
        newTimeline.canMoveThrows(composition, newtime);
        oldTimeline.remove(composition);
        newTimeline.add(composition.getComposition(), newtime, composition.getDuration());
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
     * @return {@link CameraTimeline}
     */
    public CameraTimeline getCameraTimeline() {
        return cameraTimeline;
    }

    /**
     * @return The {@link Timeline} at the given index
     */
    public Timeline getTimeline(int index) {
        return this.timelines.get(index);
    }

    /**
     * @return The {@link Timeline} at the given index (starts at 1)
     */
    public Timeline resolveTimeline(String index) {
        if (index.equals("camera")) {
            return this.cameraTimeline;
        }
        try {
            return this.timelines.get(Integer.parseInt(index)-1);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * @return The amount of timelines in the cinematic
     */
    public int getTimelineCount() {
        return this.timelines.size();
    }

    /**
     * Get the {@link Timeline} and {@link Composition} by the given composition UUID
     *
     * @param compositionUUID The {@link UUID} of a composition
     * @return A {@link Pair} containing the {@link Timeline} and the {@link Composition}, or null if the composition was not found.
     */
    public Pair<Timeline, Composition> getTimelineAndComposition(UUID compositionUUID) {
        WrappedComposition c = this.cameraTimeline.findWrappedComposition(compositionUUID);
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

    /**
     * Get the {@link Timeline} and {@link WrappedComposition} by the given composition UUID
     *
     * @param compositionQuery The name or {@link UUID} of a composition
     * @return A {@link Pair} containing the {@link Timeline} and the {@link WrappedComposition}, or null if the composition was not found.
     */
    public Pair<Timeline, WrappedComposition> getTimelineAndWrappedComposition(String compositionQuery) {
        WrappedComposition c = this.cameraTimeline.findWrappedComposition(compositionQuery);
        if (c != null) {
            return new Pair<>(this.cameraTimeline, c);
        }

        for (Timeline timeline : this.timelines) {
            c = timeline.findWrappedComposition(compositionQuery);
            if (c != null) {
                return new Pair<>(timeline, c);
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

        CameraTimeline cameraTimeline = (CameraTimeline) Timeline.fromJson(json.get("cameraTimeline").getAsJsonObject(), CameraTimeline.class);
        List<Timeline> overlayTimeline = new ArrayList<>();
        JsonArray overlayTimelineJson = json.get("overlayTimeline").getAsJsonArray();
        for (int i = 0; i < overlayTimelineJson.size(); i++) {
            overlayTimeline.add(Timeline.fromJson(overlayTimelineJson.get(i).getAsJsonObject()));
        }

        return new Cinematic(data.uuid(), data.name(), data.duration(), cameraTimeline, overlayTimeline);
    }

    public static Cinematic create(String name, long duration) {
        Cinematic cinematic = new Cinematic(UUID.randomUUID(), name, duration, new CameraTimeline(), new ArrayList<>());
        cinematic.addTimeline();
        var playerCamCompo = CameraComposition.create("default_camera", ECameraType.PLAYER);
        cinematic.cameraTimeline.add(playerCamCompo, 0, duration);
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
