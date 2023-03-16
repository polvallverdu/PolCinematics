package engineer.pol.cinematic.compositions.core.attributes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import engineer.pol.cinematic.compositions.camera.CameraPos;
import engineer.pol.cinematic.compositions.core.Composition;
import engineer.pol.exception.DeleteKeyframeException;
import engineer.pol.utils.BasicCompositionData;
import engineer.pol.utils.ColorUtils;
import engineer.pol.utils.math.Easing;
import engineer.pol.utils.math.MathUtils;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Attribute {

    private final UUID uuid;
    private final String name;
    private String description;
    private final EAttributeType type;
    private final List<Keyframe> keyframes;

    protected Attribute(UUID uuid, String name, String description, EAttributeType type, List<Keyframe> keyframes) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.type = type;

        this.keyframes = keyframes;
        if (this.keyframes.isEmpty()) {
            this.addKeyframe(0, type.getDefaultValue());
        }

        sort();
    }

    /*public void addKeyframe(Keyframe keyframe) {
        this.getKeyframes(keyframe.time)
        keyframes.add(keyframe);
        sort();
    }

    public void addKeyframe(long time, Object value) {
        addKeyframe(new Keyframe(time, value, type));
    }

    public void addKeyframe(long time, Object value, Easing easing) {
        addKeyframe(new Keyframe(time, value, type, easing));
    }*/

    public void setKeyframe(long time, Object value) {
        Keyframe keyframe = this.getExactKeyframe(time);
        if (keyframe != null) {
            keyframe.setValue(value);
        } else {
            addKeyframe(time, value);
        }
    }

    private void addKeyframe(long time, Object value) {
        keyframes.add(new Keyframe(time, value, type));
        sort();
    }

    public void moveKeyframe(Keyframe keyframe, long newTime) {
        if (!keyframes.contains(keyframe)) {
            throw new IllegalArgumentException("The keyframe is not part of this attribute.");
        }
        keyframe.setTime(newTime);
        sort();
    }

    /**
     * Removes a keyframe at the given time.
     * @param time
     * @return true if a keyframe was removed, false otherwise.
     */
    public boolean removeExactKeyframe(long time) throws DeleteKeyframeException {
        if (keyframes.size() <= 1) {
            throw new DeleteKeyframeException("Cannot delete the last keyframe of an attribute.");
        }
        return keyframes.removeIf(keyframe -> keyframe.getTime() == time);
    }

    public void removeKeyframe(long time) throws DeleteKeyframeException {
        if (keyframes.size() <= 1) {
            throw new DeleteKeyframeException("Cannot delete the last keyframe of an attribute.");
        }
        if (removeExactKeyframe(time)) return;

        Keyframe keyframe = this.getLeftKeyframe(time, false);
        if (keyframe != null) {
            keyframes.remove(keyframe);
        }
    }

    public void sort() {
        keyframes.sort(Comparator.comparingLong(Keyframe::getTime));
    }

    /**
     * Returns the closest keyframe to the left based on the given time.
     *
     * @param time
     * @param redundancy
     * @return The closest keyframe to the left or null if there is none and redundancy is false.
     */
    public Keyframe getLeftKeyframe(long time, boolean redundancy) {
        if (keyframes.isEmpty()) { // Should not happen
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

    /**
     * Returns the next keyframe based on the given time.
     *
     * @param time
     * @param redundancy
     * @return The next keyframe to the right or null if there is none and redundancy is false.
     */
    public Keyframe getNextKeyframe(long time, boolean redundancy) {
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

        if (redundancy) {
            if (time < keyframes.get(0).getTime()) {
                return keyframes.get(0);
            }

            return keyframes.get(keyframes.size() - 1);
        }
        return null;
    }

    /**
     * @param time
     * @return A {@code Keyframe} object, or null if there is no keyframe at the given time.
     */
    public Keyframe getExactKeyframe(long time) {
        for (Keyframe keyframe : keyframes) {
            if (keyframe.getTime() == time) {
                return keyframe;
            }
        }
        return null;
    }

    /**
     * Returns the closest keyframe to the given time.
     * @param time
     * @return The closest keyframe to the given time or null if there are no keyframes.
     */
    public Keyframe getClosestKeyframe(long time) {
        Keyframe left = getLeftKeyframe(time, false);
        Keyframe right = getNextKeyframe(time, false);

        if (left == null) {
            return right;
        }

        if (right == null) {
            return left;
        }

        if (time - left.getTime() < right.getTime() - time) {
            return left;
        }

        return right;
    }

    /**
     * Returns the current and next keyframe.
     *
     * @param time The time to get the keyframes for.
     * @return A pair of the current and next keyframe.
     */
    public Pair<Keyframe, Keyframe> getKeyframes(long time) {
        Keyframe current = getLeftKeyframe(time, true);
        Keyframe next = getNextKeyframe(time, true);
        return new Pair<>(current, next);
    }

    public Object getValue(long time) {
        return this.getValue(time, true); // Easing is enabled by default. It is later checked if the attribute type supports easing.
    }

    private double getFraction(long time, Keyframe current, Keyframe next) {
        double fraction = (double) (time - current.getTime()) / (next.getTime() - current.getTime());
        if (!Double.isFinite(fraction)) {
            return 0d;
        }
        return fraction;
    }

    private double getEasingMultiplier(long time, Keyframe current, Keyframe next) {
        double fraction = getFraction(time, current, next);
        return current.getEasing().getValue(fraction);
    }

    public Object getValue(long time, boolean eased) {
        eased = eased && this.type.isEasing(); // Make sure the attribute type supports easing.

        if (keyframes.isEmpty()) {
            return 0;
        }

        Pair<Keyframe, Keyframe> keyframes = getKeyframes(time);

        if (!eased) {
            return keyframes.getLeft().getValue();
        }

        if (type == EAttributeType.DOUBLE || type == EAttributeType.INTEGER) {
            double currentValue = keyframes.getLeft().getValueAsDouble();
            double nextValue = keyframes.getRight().getValueAsDouble();

            double easingMultiplier = getEasingMultiplier(time, keyframes.getLeft(), keyframes.getRight());

            return MathUtils.lerp(currentValue, nextValue, easingMultiplier);
        } else if (type == EAttributeType.COLOR) {
            int[] currentColorArray = ColorUtils.splitColors(keyframes.getLeft().getValueAsColor());
            int[] nextColorArray = ColorUtils.splitColors(keyframes.getRight().getValueAsColor());

            double easingMultiplier = getEasingMultiplier(time, keyframes.getLeft(), keyframes.getRight());

            for (int i = 0; i < currentColorArray.length; i++) {
                currentColorArray[i] = (int) MathUtils.lerp(currentColorArray[i], nextColorArray[i], easingMultiplier);
            }

            return new Color(currentColorArray[0], currentColorArray[1], currentColorArray[2], currentColorArray[3]);
        } else if (type == EAttributeType.CAMERAPOS) {
            return getLerpCameraPos(time);
        }

        // Easing true but not supported?
        return keyframes.getLeft().getValue();
    }

    public CameraPos getLerpCameraPos(long time) {
        Pair<Keyframe, Keyframe> keyframes = getKeyframes(time);

        CameraPos currentPos = keyframes.getLeft().getValueAsCameraPos();
        CameraPos nextPos = keyframes.getRight().getValueAsCameraPos();

        double easingMultiplier = getEasingMultiplier(time, keyframes.getLeft(), keyframes.getRight());

        return new CameraPos(
                MathUtils.lerp(currentPos.getX(), nextPos.getX(), easingMultiplier),
                MathUtils.lerp(currentPos.getY(), nextPos.getY(), easingMultiplier),
                MathUtils.lerp(currentPos.getZ(), nextPos.getZ(), easingMultiplier),
                MathUtils.lerp(currentPos.getPitch(), nextPos.getPitch(), easingMultiplier),
                MathUtils.lerp(currentPos.getYaw(), nextPos.getYaw(), easingMultiplier),
                MathUtils.lerp(currentPos.getRoll(), nextPos.getRoll(), easingMultiplier),
                MathUtils.lerp(currentPos.getFov(), nextPos.getFov(), easingMultiplier)
        );
    }

    public CameraPos getSlerpCameraPos(long time) {
        Pair<Keyframe, Keyframe> keyframes = getKeyframes(time);

        CameraPos currentPos = keyframes.getLeft().getValueAsCameraPos();
        CameraPos nextPos = keyframes.getRight().getValueAsCameraPos();

        double easingMultiplier = getEasingMultiplier(time, keyframes.getLeft(), keyframes.getRight());
        Vec3d centerPoint = MathUtils.calculateCuttingPoint(currentPos.getVec3d(), currentPos.getVec2f(), nextPos.getVec3d(), nextPos.getVec2f());

        Vec3d newPos = MathUtils.slerp(currentPos.getVec3d(), nextPos.getVec3d(), centerPoint, easingMultiplier);

        return new CameraPos(
                newPos.getX(),
                newPos.getY(),
                newPos.getZ(),
                MathUtils.lerp(currentPos.getPitch(), nextPos.getPitch(), easingMultiplier),
                MathUtils.lerp(currentPos.getYaw(), nextPos.getYaw(), easingMultiplier),
                MathUtils.lerp(currentPos.getRoll(), nextPos.getRoll(), easingMultiplier),
                MathUtils.lerp(currentPos.getFov(), nextPos.getFov(), easingMultiplier)
        );
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Attribute setDescription(String description) {
        this.description = description;
        return this;
    }

    public EAttributeType getType() {
        return type;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("uuid", this.getUuid().toString());
        json.addProperty("name", this.getName());
        json.addProperty("description", this.getDescription());
        json.addProperty("type", this.getType().getName());

        JsonArray keyframesArray = new JsonArray();
        for (Keyframe keyframe : keyframes) {
            keyframesArray.add(keyframe.toJson());
        }

        json.add("keyframes", keyframesArray);

        return json;
    }

    public static Attribute fromJson(JsonObject json) {
        JsonArray keyframesArray = json.getAsJsonArray("keyframes");

        BasicCompositionData data = BasicCompositionData.fromJson(json);
        String description = json.get("description").getAsString();
        EAttributeType type = EAttributeType.fromName(json.get("type").getAsString());

        List<Keyframe> keyframes = new ArrayList<>();
        for (int i = 0; i < keyframesArray.size(); i++) {
            keyframes.add(Keyframe.fromJson(keyframesArray.get(i).getAsJsonObject(), type));
        }

        return new Attribute(data.uuid(), data.name(), description, type, keyframes);
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
