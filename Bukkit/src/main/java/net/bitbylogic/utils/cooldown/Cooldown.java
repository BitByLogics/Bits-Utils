package net.bitbylogic.utils.cooldown;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Cooldown {

    private final UUID identifier;
    private final String cooldownId;

    private final long startTime;
    private final long duration;

    public Cooldown(UUID identifier, String cooldownId, long duration) {
        this.identifier = identifier;
        this.cooldownId = cooldownId;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public boolean isActive() {
        return System.currentTimeMillis() < startTime + duration;
    }

    public long getTimeUntilExpired() {
        return isActive() ? (startTime + duration) - System.currentTimeMillis() : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cooldown cooldown = (Cooldown) o;
        return startTime == cooldown.startTime && duration == cooldown.duration && Objects.equals(identifier, cooldown.identifier) && Objects.equals(cooldownId, cooldown.cooldownId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, cooldownId, startTime, duration);
    }

}
