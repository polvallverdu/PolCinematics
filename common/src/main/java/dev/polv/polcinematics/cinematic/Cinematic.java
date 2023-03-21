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

    public Timeline addTimeline() {
        Timeline timeline = new Timeline();
        this.timelines.add(timeline);
        return timeline;
    }

    public void removeTimeline(int index) {
        this.timelines.remove(index);
    }

    public long getDuration() {
        return duration;
    }

    public CameraComposition getCameraComposition(long time) {
        return (CameraComposition) this.cameraTimeline.getComposition(time);
    }

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

    public Timeline getCameraTimeline() {
        return cameraTimeline;
    }

    /*public CameraComposition createCameraComposition(String name, ECameraType cameraType, long duration) {
        CameraComposition cameraComposition = new CameraComposition(name, cameraType, duration);
        cameraTimeline.add(cameraComposition, cameraTimeline.getMaxDuration());
        return cameraComposition;
    }*/

    /*public CameraComposition checkCameraComposition(long time) {  // THERE WILL ALWAYS BE A CAMERA COMPOSITION
        CameraComposition compo = this.getCameraComposition(time);
        if (compo == null) {
            if (this.cameraTimeline.compositions.isEmpty()) {
                CameraComposition cameraComposition = new CameraComposition("temp", time);
                this.cameraTimeline.add(cameraComposition, 0);
                return cameraComposition;
            }

            CameraComposition cameraComposition = (CameraComposition) this.cameraTimeline.getComposition(0);
            this.cameraTimeline.changeDuration(cameraComposition.getUuid(), time);
            return cameraComposition;
        }
        return compo;
    }*/

    public void tickOverlay(MatrixStack MatrixStack, long time) {
        for (int i = this.timelines.size() - 1; i >= 0; i--) {  // loop reverse
            Timeline timeline = this.timelines.get(i);

            Composition compo = timeline.getComposition(time);
            if (!(compo instanceof OverlayComposition)) continue;

            ((OverlayComposition) compo).tick(MatrixStack, time);
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
