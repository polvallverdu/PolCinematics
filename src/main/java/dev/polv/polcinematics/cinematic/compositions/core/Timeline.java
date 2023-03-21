package dev.polv.polcinematics.cinematic.compositions.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.polv.polcinematics.cinematic.EOverlapStrategy;
import dev.polv.polcinematics.exception.OverlapException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Timeline {

    public static class WrappedComposition {

        private final Composition composition;
        private long startTime;

        public WrappedComposition(Composition composition, long startTime) {
            this.composition = composition;
            this.startTime = startTime;
        }

        public long getDuration() {
            return composition.getDuration();
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

        public UUID getUUID() {
            return composition.getUuid();
        }

        public void setDuration(long duration) {
            composition.setDuration(duration);
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("startTime", startTime);
            json.add("composition", composition.toJson());

            return json;
        }
    }

    protected List<WrappedComposition> compositions;

    private EOverlapStrategy overlapStrategy = EOverlapStrategy.ERROR;

    public Timeline() {
        this(new ArrayList<>());
    }

    public Timeline(List<WrappedComposition> compositions) {
        this.compositions = compositions;
        this.sort();
    }

    public Composition getComposition(long time) {
        WrappedComposition wc = getWrappedComposition(time);
        return wc == null ? null : wc.getComposition();
    }

    public WrappedComposition getWrappedComposition(long time) {
        for (WrappedComposition timeline : compositions) {
            if (timeline.getStartTime() <= time && timeline.getFinishTime() >= time) {
                return timeline;
            }
        }
        return null;
    }

    public WrappedComposition findWrappedComposition(UUID uuid) {
        for (WrappedComposition wc : compositions) {
            if (wc.getUUID().equals(uuid)) {
                return wc;
            }
        }
        return null;
    }

    @Deprecated // TODO: NOT SAFE
    public void replaceComposition(UUID uuid, Composition newComposition) {
        long startTime = findWrappedComposition(uuid).getStartTime();
        remove(uuid);
        add(newComposition, startTime);
    }

    public void sort() {
        compositions.sort((a, b) -> (int) (a.getStartTime() - b.getStartTime()));
    }

    public void add(Composition composition, long startTime) {
        WrappedComposition wc = new WrappedComposition(composition, startTime);
        switch (overlapStrategy) {
            case MOVE -> {  // change duration of composition if the composition with the new duration doesn't overlap with any other compositions
                for (WrappedComposition oWc : compositions.stream().filter(oWc -> oWc.getStartTime() >= wc.getStartTime()).toList()) {
                    oWc.setStartTime(oWc.getStartTime() + wc.getDuration());
                }
            }
            case ERROR -> {  // Check that the composition doesn't overlap with any other compositions
                for (WrappedComposition wc1 : compositions) {
                    if (wc.getStartTime() < wc1.getFinishTime() && wc.getFinishTime() > wc1.getStartTime()) {
                        throw new OverlapException("Composition overlaps with another composition");
                    }
                }
            }
        }

        compositions.add(wc);
    }

    public void remove(UUID compositionUUID) {
        for (WrappedComposition wc : new ArrayList<>(compositions)) {
            if (wc.getUUID().equals(compositionUUID)) {
                compositions.remove(wc);
                if (overlapStrategy == EOverlapStrategy.MOVE) {
                    for (WrappedComposition oWc : compositions.stream().filter(oWc -> oWc.getStartTime() > wc.getStartTime()).toList()) {
                        oWc.setStartTime(oWc.getStartTime() - wc.getDuration());
                    }
                }
                break;
            }
        }
    }

    public void changeDuration(UUID compositionUUID, long newDuration) {
        switch (overlapStrategy) {
            case ERROR -> {  // Check that the composition doesn't overlap with any other compositions
                for (WrappedComposition wc : compositions) {
                    if (wc.getUUID().equals(compositionUUID)) {
                        long timeDuration = newDuration - wc.getDuration();
                        for (WrappedComposition wc1 : compositions) {
                            if (!wc.equals(wc1) && wc.getStartTime() < wc1.getFinishTime() && wc.getFinishTime(timeDuration) > wc1.getStartTime()) {
                                throw new OverlapException("Composition overlaps with another composition");
                            }
                        }
                        wc.setDuration(newDuration);
                        this.sort();
                        return;
                    }
                }
            }
            case MOVE -> {  // change duration of composition if the composition with the new duration doesn't overlap with any other compositions
                for (WrappedComposition wc : compositions) {
                    if (wc.getUUID().equals(compositionUUID)) {
                        long timeDifference = newDuration - wc.getDuration();
                        wc.setDuration(newDuration);
                        for (WrappedComposition oWc : compositions.stream().filter(oWc -> oWc.getStartTime() > wc.getStartTime()).toList()) {
                            oWc.setStartTime(oWc.getStartTime() + timeDifference);
                        }
                        this.sort();
                        return;
                    }
                }
            }
        }

        throw new IllegalArgumentException("Composition not found");
    }

    public void move(UUID compositionUUID, long startTimeDifference) {
        this.move(List.of(compositionUUID), startTimeDifference);
    }

    public void move(List<UUID> compositions, long startTimeDifference) {
        // Check if composition could be overlapping another one, if not, change start time
        for (WrappedComposition wc : this.compositions) {
            for (UUID uuid : compositions) {
                if (wc.getUUID().equals(uuid)) {
                    long newStartTime = wc.getStartTime() + startTimeDifference;
                    boolean change = true;
                    for (WrappedComposition wc1 : this.compositions) {
                        if (!wc1.getUUID().equals(uuid) && newStartTime < wc1.getFinishTime() && newStartTime + wc.getDuration() > wc1.getStartTime()) {
                            change = false;
                        }
                    }
                    if (change) {
                        wc.setStartTime(newStartTime);
                    }
                }
            }
        }

        sort();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray compositionsArray = new JsonArray();
        for (WrappedComposition wc : compositions) {
            compositionsArray.add(wc.toJson());
        }
        json.add("compositions", compositionsArray);
        return json;
    }

    public static Timeline fromJson(JsonObject json) {
        JsonArray compositionsArray = json.getAsJsonArray("compositions");
        List<WrappedComposition> compositions = new ArrayList<>();
        for (int i = 0; i < compositionsArray.size(); i++) {
            JsonObject compositionJson = compositionsArray.get(i).getAsJsonObject();
            long startTime = compositionJson.get("startTime").getAsLong();
            try {
                Composition composition = Composition.fromJson(compositionJson.get("composition").getAsJsonObject());
                compositions.add(new WrappedComposition(composition, startTime));
            } catch (Exception e) {
                e.printStackTrace();
                return null; // TODO: NOT SAFE
            }
        }
        return new Timeline(compositions);
    }

    public void setOverlapStrategy(EOverlapStrategy overlapStrategy) {
        this.overlapStrategy = overlapStrategy;
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

    public void onTick(long lastTick, long time) {
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
}
