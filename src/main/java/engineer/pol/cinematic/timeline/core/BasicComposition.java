package engineer.pol.cinematic.timeline.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import engineer.pol.utils.math.Easing;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BasicComposition extends Composition {

    private final List<Keyframe> keyframes;

    protected BasicComposition(List<Keyframe> keyframes) {
        super(UUID.randomUUID(), "", 0, CompositionType.BASIC);
        this.keyframes = keyframes;

        sort();
    }

    public BasicComposition() {
        this(new ArrayList<>());
    }

    public void addKeyframe(Keyframe keyframe) {
        keyframes.add(keyframe);
        sort();
    }

    public void addKeyframe(long time, double value) {
        addKeyframe(new Keyframe(time, value));
    }

    public void addKeyframe(long time, double value, Easing easing) {
        addKeyframe(new Keyframe(time, value, easing));
    }

    public void removeKeyframe(long time) {
        if (keyframes.removeIf(keyframe -> keyframe.getTime() == time)) return;

        Keyframe keyframe = this.getCurrentKeyframe(time, false);
        if (keyframe != null) {
            keyframes.remove(keyframe);
        }
    }

    public void sort() {
        keyframes.sort(Comparator.comparingLong(Keyframe::getTime));
    }

    public Keyframe getCurrentKeyframe(long time, boolean redundancy) {
        if (keyframes.isEmpty()) {
            return null;
        }

        // Search for the keyframe that is closest to the current time going down the list.
        for (int i = keyframes.size() - 1; i >= 0; i--) {
            Keyframe keyframe = keyframes.get(i);
            if (keyframe.getTime() <= time) {
                return keyframe;
            }
        }

        if (redundancy) {
            if (time < keyframes.get(0).getTime()) {
                return keyframes.get(0);
            }

            return keyframes.get(keyframes.size() - 1);
        }
        return null;
    }

    public Keyframe getNextKeyframe(long time) {
        if (keyframes.isEmpty()) {
            return null;
        }

        // Search for the keyframe that is closest to the current time going up the list.
        for (Keyframe keyframe : keyframes) {
            if (keyframe.getTime() >= time) {
                return keyframe;
            }
        }

        if (time > keyframes.get(keyframes.size() - 1).getTime()) {
            return keyframes.get(keyframes.size() - 1);
        }

        return keyframes.get(0);
    }

    public Pair<Keyframe, Keyframe> getKeyframes(long time) {
        Keyframe current = getCurrentKeyframe(time, true);
        Keyframe next = getNextKeyframe(time);
        return new Pair<>(current, next);
    }

    public double getValue(long time) {
        return this.getValue(time, true);
    }

    public double getValue(long time, boolean eased) {
        if (keyframes.isEmpty()) {
            return 0;
        }

        Pair<Keyframe, Keyframe> keyframes = getKeyframes(time);

        double currentValue = keyframes.getLeft().getValue();
        double nextValue = keyframes.getRight().getValue();

        if (eased) {
            double fraction = (double) (time - keyframes.getLeft().getTime()) / (keyframes.getRight().getTime() - keyframes.getLeft().getTime());
            double easingMultiplier = keyframes.getLeft().getEasing().getValue(fraction);

            return currentValue + (nextValue - currentValue) * easingMultiplier;
        }
        return currentValue;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        JsonArray keyframesArray = new JsonArray();
        for (Keyframe keyframe : keyframes) {
            keyframesArray.add(keyframe.toJson());
        }

        json.add("keyframes", keyframesArray);

        return json;
    }

    public static BasicComposition fromJson(JsonObject json) {
        JsonArray keyframesArray = json.getAsJsonArray("keyframes");
        List<Keyframe> keyframes = new ArrayList<>();
        for (int i = 0; i < keyframesArray.size(); i++) {
            keyframes.add(Keyframe.fromJson(keyframesArray.get(i).getAsJsonObject()));
        }
        return new BasicComposition(keyframes);
    }

    @Override
    public long getDuration() {
        return this.keyframes.isEmpty() ? 0 : this.keyframes.get(this.keyframes.size() - 1).getTime();
    }
}
