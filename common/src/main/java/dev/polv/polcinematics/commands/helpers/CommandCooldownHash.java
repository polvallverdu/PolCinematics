package dev.polv.polcinematics.commands.helpers;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CommandCooldownHash extends CommandCooldown {

    private ConcurrentHashMap<UUID, String> cooldownHash;

    public CommandCooldownHash(Duration cooldown) {
        super(cooldown);
        this.cooldownHash = new ConcurrentHashMap<>();
    }

    public boolean isOnCooldown(UUID uuid, String hash) {
        String supposedHash = this.cooldownHash.get(uuid);
        if (supposedHash == null || !supposedHash.equals(hash)) {
            return false;
        }

        return super.isOnCooldown(uuid);
    }

    public void setOnCooldown(UUID uuid, String hash) {
        super.setOnCooldown(uuid);
        this.cooldownHash.put(uuid, hash);
    }

    @Override
    public void removeCooldown(UUID uuid) {
        this.cooldownHash.remove(uuid);
        super.removeCooldown(uuid);
    }
}
