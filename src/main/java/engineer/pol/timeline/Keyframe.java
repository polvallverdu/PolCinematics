package engineer.pol.timeline;

import com.electronwill.nightconfig.core.Config;

public class Keyframe<T> {

    private long time;
    private Easing easing;
    private T value;

    public Keyframe(long time, T value) {
        this(time, value, Easing.LINEAR);
    }

    public Keyframe(long time, T value, Easing easing) {
        this.time = time;
        this.value = value;
        this.easing = easing;
    }

    public boolean hasEasing() {
        return easing != null && easing != Easing.LINEAR;
    }

    public Easing getEasing() {
        return easing;
    }

    public void setEasing(Easing easing) {
        this.easing = easing;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Config toConfig() {
        Config config = Config.inMemory();
        config.set("time", time);
        config.set("easing", easing.getId());
        config.set("value", value);
        return config;
    }

    public static <T> Keyframe<T> fromConfig(Config config) {
        long time = config.get("time");
        Easing easing = Easing.fromId(config.get("easing"));
        T value = config.get("value");
        return new Keyframe<>(time, value, easing);
    }

}
