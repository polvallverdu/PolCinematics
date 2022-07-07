package engineer.pol.cinematic.timeline.core;

import java.util.UUID;

public abstract class Composition {

    private UUID uuid;
    private String name;
    private long duration;
    private final CompositionType type;

    public Composition(UUID uuid, String name, long duration, CompositionType type) {
        this.uuid = uuid;
        this.name = name;
        this.duration = duration;
        this.type = type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public CompositionType getType() {
        return type;
    }

    protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setDuration(long duration) {
        this.duration = duration;
    }

}
