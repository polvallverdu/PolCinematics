package engineer.pol.timeline;

import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Timeline<T> {

    private final List<Keyframe<T>> keyframes;

    protected Timeline(List<Keyframe<T>> keyframes) {
        this.keyframes = keyframes;

        sort();
    }

    public Timeline() {
        this(new ArrayList<>());
    }

    public void addKeyframe(Keyframe<T> keyframe) {
        keyframes.add(keyframe);
        sort();
    }

    public void addKeyframe(long time, T value) {
        addKeyframe(new Keyframe<>(time, value));
    }

    public void addKeyframe(long time, T value, Easing easing) {
        addKeyframe(new Keyframe<>(time, value, easing));
    }

    public void removeKeyframe(long time) {
        if (keyframes.removeIf(keyframe -> keyframe.getTime() == time)) return;

        Keyframe<T> keyframe = this.getCurrentKeyframe(time, false);
        if (keyframe != null) {
            keyframes.remove(keyframe);
        }
    }

    public void sort() {
        keyframes.sort(Comparator.comparingLong(Keyframe::getTime));
    }

    public Keyframe<T> getCurrentKeyframe(long time, boolean redundancy) {
        if (keyframes.isEmpty()) {
            return null;
        }

        // Search for the keyframe that is closest to the current time going down the list.
        for (int i = keyframes.size() - 1; i >= 0; i--) {
            Keyframe<T> keyframe = keyframes.get(i);
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

    public Keyframe<T> getNextKeyframe(long time) {
        if (keyframes.isEmpty()) {
            return null;
        }

        // Search for the keyframe that is closest to the current time going up the list.
        for (Keyframe<T> keyframe : keyframes) {
            if (keyframe.getTime() >= time) {
                return keyframe;
            }
        }

        if (time > keyframes.get(keyframes.size() - 1).getTime()) {
            return keyframes.get(keyframes.size() - 1);
        }

        return keyframes.get(0);
    }

    public Pair<Keyframe<T>, Keyframe<T>> getKeyframes(long time) {
        Keyframe<T> current = getCurrentKeyframe(time, true);
        Keyframe<T> next = getNextKeyframe(time);
        return new Pair<>(current, next);
    }

    public double getEasedValue(long time) {
        if (keyframes.isEmpty()) {
            return 0;
        }

        Pair<Keyframe<T>, Keyframe<T>> keyframes = getKeyframes(time);

        double currentValue = (double) keyframes.getLeft().getValue();
        double nextValue = (double) keyframes.getRight().getValue();

        double fraction = (double) (time - keyframes.getLeft().getTime()) / (keyframes.getRight().getTime() - keyframes.getLeft().getTime());
        double easingMultiplier = keyframes.getLeft().getEasing().getValue(fraction);

        return currentValue + (nextValue - currentValue) * easingMultiplier;
    }

    public T getValue(long time) {
        if (keyframes.isEmpty()) {
            return null;
        }

        return getCurrentKeyframe(time, true).getValue();
    }

}
