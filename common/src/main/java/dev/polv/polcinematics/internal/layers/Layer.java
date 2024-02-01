package dev.polv.polcinematics.internal.layers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.polv.polcinematics.internal.compositions.Composition;
import dev.polv.polcinematics.internal.compositions.ECompositionType;
import dev.polv.polcinematics.exception.OverlapException;
import dev.polv.polcinematics.utils.BasicCompositionData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Layer {

    private final static ECompositionType[] ALLOWED_TYPES = new ECompositionType[]{ECompositionType.AUDIO_COMPOSITION, ECompositionType.OVERLAY_COMPOSITION};

    private final UUID uuid;
    protected List<WrappedComposition> compositions;

    public Layer() {
        this(UUID.randomUUID(), new ArrayList<>());
    }

    public Layer(UUID uuid, List<WrappedComposition> compositions) {
        this.uuid = uuid;
        this.compositions = compositions;
        this.sort();
    }

    public Composition getComposition(long time) {
        WrappedComposition wc = getWrappedComposition(time);
        return wc == null ? null : wc.getComposition();
    }

    public WrappedComposition getWrappedComposition(long time) {
        for (WrappedComposition layer : compositions) {
            if (layer.getStartTime() <= time && layer.getFinishTime() >= time) {
                return layer;
            }
        }
        return null;
    }

    public WrappedComposition findWrappedComposition(UUID uuid) {
        for (WrappedComposition wc : compositions) {
            if (wc.getUuid().equals(uuid)) {
                return wc;
            }
        }
        return null;
    }

    public WrappedComposition findWrappedComposition(String name) {
        for (WrappedComposition wc : compositions) {
            if (wc.getComposition().getName().equals(name) || wc.getComposition().getUuid().toString().equals(name)) {
                return wc;
            }
        }
        return null;
    }

    public List<WrappedComposition> getWrappedCompositions() {
        return new ArrayList<>(compositions);
    }

    public void sort() {
        compositions.sort((a, b) -> (int) (a.getStartTime() - b.getStartTime()));
    }

    public void add(@NotNull Composition composition, long startTime, long duration) throws IllegalArgumentException, OverlapException {
        // check if composition.getType() is in this.getAllowedTypes()
        boolean allowed = false;
        for (ECompositionType type : this.getAllowedTypes()) {
            if (type == composition.getType()) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new IllegalArgumentException("Composition type " + composition.getType() + " is not allowed in this layer");
        }

        WrappedComposition wc = new WrappedComposition(composition, startTime, duration);
        for (WrappedComposition wc1 : compositions) {
            if (wc.getStartTime() < wc1.getFinishTime() && wc.getFinishTime() > wc1.getStartTime()) {
                throw new OverlapException(wc, wc1);
            }
        }

        compositions.add(wc);
    }

    public void remove(UUID compositionUUID) {
        for (WrappedComposition wc : new ArrayList<>(compositions)) {
            if (wc.getUuid().equals(compositionUUID)) {
                compositions.remove(wc);
                break;
            }
        }
    }

    public boolean remove(WrappedComposition composition) {
        return compositions.remove(composition);
    }

    public void changeDuration(UUID compositionUUID, long newDuration) throws IllegalArgumentException, OverlapException {
        for (WrappedComposition wc : compositions) {
            if (wc.getUuid().equals(compositionUUID)) {
                this.changeDuration(wc, newDuration);
                return;
            }
        }

        throw new IllegalArgumentException("Composition not found");
    }

    public void changeDuration(WrappedComposition wc, long newDuration) throws OverlapException {
        long timeDuration = newDuration - wc.getDuration();
        for (WrappedComposition wc1 : compositions) {
            if (!wc.equals(wc1) && wc.getStartTime() < wc1.getFinishTime() && wc.getFinishTime(timeDuration) > wc1.getStartTime()) {
                throw new OverlapException(wc, wc1);
            }
        }
        wc.setDuration(newDuration);
        this.sort();
    }

    public boolean canMove(WrappedComposition composition, long starttime) {
        for (WrappedComposition wc : compositions) {
            if (!wc.equals(composition) && starttime < wc.getFinishTime() && starttime + composition.getDuration() > wc.getStartTime()) {
                return false;
            }
        }
        return true;
    }

    public void canMoveThrows(WrappedComposition composition, long starttime) throws OverlapException {
        for (WrappedComposition wc : compositions) {
            if (!wc.equals(composition) && starttime < wc.getFinishTime() && starttime + composition.getDuration() > wc.getStartTime()) {
                throw new OverlapException(composition, wc);
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void move(UUID compositionUUID, long startTimeDifference) throws OverlapException {
        // Check if composition could be overlapping another one, if not, change start time
        for (WrappedComposition wc : this.compositions) {
            if (wc.getUuid().equals(compositionUUID)) {
                long newStartTime = wc.getStartTime() + startTimeDifference;
                canMoveThrows(wc, newStartTime);
                wc.setStartTime(newStartTime);
            }
        }

        sort();
    }

    public void onTimelineLoad() {
        for (WrappedComposition wc : compositions) {
            wc.getComposition().onCinematicLoad();
        }
    }

    public void onTimelineUnload() {
        for (WrappedComposition wc : compositions) {
            wc.getComposition().onCinematicUnload();
        }
    }

    public void onStart() {
        long time = 0;
        for (WrappedComposition wc : compositions) {
            wc.getComposition().onCinematicStart();
            if (wc.getStartTime() <= time && wc.getFinishTime() > time) {
                wc.getComposition().onCompositionStart();
            }
        }
    }

    public void onStop(long time) {
        for (WrappedComposition wc : compositions) {
            if (wc.getStartTime() <= time && wc.getFinishTime() > time) {
                wc.getComposition().onCompositionEnd();
            }
            wc.getComposition().onCinematicStop();
        }
    }

    public void onPause(long time) {
        for (WrappedComposition wc : compositions) {
            if (wc.getStartTime() <= time && wc.getFinishTime() > time) {
                wc.getComposition().onCompositionPause();
            }
            wc.getComposition().onCinematicPause();
        }
    }

    public void onResume(long time) {
        for (WrappedComposition wc : compositions) {
            if (wc.getStartTime() <= time && wc.getFinishTime() > time) {
                wc.getComposition().onCompositionResume();
            }
            wc.getComposition().onCinematicResume();
        }
    }

    public void onTimeChange(long oldTime, long time) {
        WrappedComposition oldComposition = this.getWrappedComposition(oldTime);
        WrappedComposition newComposition = this.getWrappedComposition(time);

        if (oldComposition != null && newComposition != null) {
            if (oldComposition == newComposition) {
                oldComposition.getComposition().onCinematicTimeChange(time - oldComposition.getStartTime());
            } else {
                oldComposition.getComposition().onCompositionEnd();
                newComposition.getComposition().onCompositionStart();
                newComposition.getComposition().onCinematicTimeChange(time - newComposition.getStartTime());
            }
        } else if (oldComposition != null) {
            oldComposition.getComposition().onCompositionEnd();
        } else if (newComposition != null) {
            newComposition.getComposition().onCompositionStart();
            newComposition.getComposition().onCinematicTimeChange(time - newComposition.getStartTime());
        }
    }

    public void onTick(long lastTick, long time) { // It's more effective a callstack here than comparing if composition is listening
        WrappedComposition oldComposition = this.getWrappedComposition(lastTick);
        WrappedComposition newComposition = this.getWrappedComposition(time);

        if (oldComposition != null && newComposition != null) {
            if (oldComposition == newComposition) {
                oldComposition.getComposition().onCompositionTick(time - oldComposition.getStartTime());
            } else {
                oldComposition.getComposition().onCompositionEnd();
                newComposition.getComposition().onCompositionStart();
                newComposition.getComposition().onCompositionTick(time - newComposition.getStartTime());
            }
        } else if (oldComposition != null) {
            oldComposition.getComposition().onCompositionEnd();
        } else if (newComposition != null) {
            newComposition.getComposition().onCompositionStart();
            newComposition.getComposition().onCompositionTick(time - newComposition.getStartTime());
        }
    }

    public ECompositionType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray compositionsArray = new JsonArray();
        for (WrappedComposition wc : compositions) {
            compositionsArray.add(wc.toJson());
        }
        json.add("compositions", compositionsArray);
        json.addProperty("uuid", uuid.toString());
        return json;
    }

    public static Layer fromJson(JsonObject json) {
        return fromJson(json, Layer.class);
    }

    public static Layer fromJson(JsonObject json, Class<? extends Layer> layerClass) {
        UUID layerUUID = UUID.fromString(json.get("uuid").getAsString());
        JsonArray compositionsArray = json.getAsJsonArray("compositions");
        List<WrappedComposition> compositions = new ArrayList<>();

        for (int i = 0; i < compositionsArray.size(); i++) {
            JsonObject compositionJson = compositionsArray.get(i).getAsJsonObject();
            BasicCompositionData data = BasicCompositionData.fromJson(compositionJson);

            try {
                Composition composition = Composition.fromJson(compositionJson);
                compositions.add(new WrappedComposition(composition, data.startTime(), data.duration()));
            } catch (Exception e) {
                e.printStackTrace();
                return null; // TODO: NOT SAFE
            }
        }

        try {
            Constructor<? extends Layer> constructor = layerClass.getConstructor(UUID.class, List.class);
            return constructor.newInstance(layerUUID, compositions);
        } catch (Exception e) {
            throw new RuntimeException("Could not create layer from json", e);
        }
    }

    public ELayerType getType() {
        return ELayerType.LAYER;
    }

}
