package engineer.pol.cinematic.compositions.core.attributes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.core.Composition;
import engineer.pol.utils.BasicCompositionData;
import engineer.pol.utils.math.Easing;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Attribute {

    private final UUID uuid;
    private final String name;
    private final Composition parent;
    private final EAttributeType type;
    private final List<Keyframe> keyframes;

    protected Attribute(UUID uuid, String name, Composition parent, EAttributeType type, List<Keyframe> keyframes) {
        this.uuid = uuid;
        this.name = name;
        this.parent = parent;
        this.type = type;

        this.keyframes = keyframes;
        if (this.keyframes.isEmpty()) {
            this.addKeyframe(0, 0);
        }

        sort();
    }

    public void addKeyframe(Keyframe keyframe) {
        this.removeExactKeyframe(keyframe.getTime());
        keyframes.add(keyframe);
        sort();
    }

    public void addKeyframe(long time, double value) {
        addKeyframe(new Keyframe(time, value));
    }

    public void addKeyframe(long time, double value, Easing easing) {
        addKeyframe(new Keyframe(time, value, easing));
    }

    /**
     * Removes a keyframe at the given time.
     * @param time
     * @return true if a keyframe was removed, false otherwise.
     */
    public boolean removeExactKeyframe(long time) {
        return keyframes.removeIf(keyframe -> keyframe.getTime() == time);
    }

    public void removeKeyframe(long time) {
        if (removeExactKeyframe(time)) return;

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
            if (!Double.isFinite(fraction)) {
                fraction = 0;
            }
            double easingMultiplier = keyframes.getLeft().getEasing().getValue(fraction);

            return currentValue + (nextValue - currentValue) * easingMultiplier;
        }
        return currentValue;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Composition getParent() {
        return parent;
    }

    public EAttributeType getType() {
        return type;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("uuid", this.getUuid().toString());
        json.addProperty("name", this.getName());
        json.addProperty("type", this.getType().getName());

        JsonArray keyframesArray = new JsonArray();
        for (Keyframe keyframe : keyframes) {
            keyframesArray.add(keyframe.toJson());
        }

        json.add("keyframes", keyframesArray);

        return json;
    }

    public static Attribute fromJson(JsonObject json, Composition parent) {
        JsonArray keyframesArray = json.getAsJsonArray("keyframes");

        BasicCompositionData data = BasicCompositionData.fromJson(json);
        EAttributeType type = EAttributeType.fromName(json.get("type").getAsString());

        List<Keyframe> keyframes = new ArrayList<>();
        for (int i = 0; i < keyframesArray.size(); i++) {
            keyframes.add(Keyframe.fromJson(keyframesArray.get(i).getAsJsonObject()));
        }

        return new Attribute(data.uuid(), data.name(), parent, type, keyframes);
    }

    public void orderEasings(Easing startEasing, Easing middleEasing, Easing endEasing) {
        if (keyframes.isEmpty()) {
            return;
        }

        keyframes.forEach(keyframe -> {
            keyframe.setEasing(middleEasing);
        });

        Keyframe startKeyframe = keyframes.get(0);
        Keyframe endKeyframe = keyframes.get(keyframes.size() - 1);
        startKeyframe.setEasing(startEasing);
        endKeyframe.setEasing(endEasing);
    }
}
