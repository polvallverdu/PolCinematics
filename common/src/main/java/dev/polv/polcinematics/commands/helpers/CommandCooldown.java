package dev.polv.polcinematics.commands.helpers;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CommandCooldown {

    private ConcurrentHashMap<UUID, Long> cooldowns;
    private long cooldown; // in milliseconds

    public CommandCooldown(Duration cooldown) {
        this.cooldowns = new ConcurrentHashMap<>();
        this.cooldown = cooldown.toMillis();
    }

    public long getCooldown() {
        return cooldown;
    }

    public Duration getCooldownDuration() {
        return Duration.ofMillis(this.cooldown);
    }

    public boolean isOnCooldown(UUID uuid) {
        Long lastUse = this.cooldowns.get(uuid);
        if (lastUse == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        if (!(now - lastUse < this.cooldown)) {
            this.cooldowns.remove(uuid);
            return false;
        }

        return true;
    }

    public void setOnCooldown(UUID uuid) {
        this.cooldowns.put(uuid, System.currentTimeMillis());
    }

    public long getRemainingCooldown(UUID uuid) {
        Long lastUse = this.cooldowns.get(uuid);
        if (lastUse == null) {
            return 0;
        }

        long now = System.currentTimeMillis();
        if (!(now - lastUse < this.cooldown)) {
            this.cooldowns.remove(uuid);
            return 0;
        }

        return this.cooldown - (now - lastUse);
    }

    public void removeCooldown(UUID uuid) {
        this.cooldowns.remove(uuid);
    }

    public void setCooldownDuration(Duration newCooldown) {
        this.cooldown = newCooldown.toMillis();
    }
}
