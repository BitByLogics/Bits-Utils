package net.bitbylogic.utils.cooldown;

import lombok.NonNull;
import net.bitbylogic.utils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CooldownUtil {

    private static final Object LOCK = new Object();
    private static final Map<UUID, List<Cooldown>> COOLDOWNS = new HashMap<>();

    public static void startCooldown(String key, UUID identifier) {
        addCooldown(new Cooldown(identifier, key, -1));
    }

    public static void startCooldown(JavaPlugin plugin, String key, UUID identifier, long expireTime, TimeUnit unit) {
        startCooldown(plugin, key, identifier, expireTime, unit, null);
    }

    public static void startCooldown(JavaPlugin plugin, String key, UUID identifier, long expireTime, TimeUnit unit, @Nullable Consumer<Void> completeCallback) {
        Cooldown cooldown = new Cooldown(identifier, key, unit.toMillis(expireTime));
        addCooldown(cooldown);

        long delayTicks = Math.max(1, unit.toMillis(expireTime) / 50);

        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> {
            removeCooldown(cooldown);

            if (completeCallback == null) {
                return;
            }

            completeCallback.accept(null);
        }, delayTicks);
    }

    public static void attemptRun(String key, UUID identifier, String cooldownTime, Runnable runnable) {
        attemptRun(key, identifier, TimeConverter.convert(cooldownTime), runnable);
    }

    public static void attemptRun(@NonNull String key, @NonNull UUID identifier, long cooldownTime, @NonNull TimeUnit timeUnit, @NonNull Runnable runnable) {
        attemptRun(key, identifier, timeUnit.toMillis(cooldownTime), runnable);
    }

    public static void endCooldown(@NonNull String key, @NonNull UUID identifier) {
        synchronized (LOCK) {
            List<Cooldown> cooldowns = COOLDOWNS.get(identifier);

            if (cooldowns == null) {
                return;
            }

            cooldowns.removeIf(cooldown -> cooldown.getCooldownId().equalsIgnoreCase(key));

            if (cooldowns.isEmpty()) {
                COOLDOWNS.remove(identifier);
            }
        }
    }

    public static List<Cooldown> getCooldowns(@NonNull UUID identifier) {
        synchronized (LOCK) {
            List<Cooldown> cooldowns = COOLDOWNS.get(identifier);

            if (cooldowns == null) {
                return List.of();
            }

            return List.copyOf(cooldowns);
        }
    }

    public static Optional<Cooldown> getCooldown(@NonNull String key, @NonNull UUID identifier) {
        return getCooldowns(identifier).stream()
                .filter(cooldown -> cooldown != null && cooldown.getCooldownId().equalsIgnoreCase(key) && cooldown.isActive())
                .findFirst();
    }

    public static boolean hasCooldown(@NonNull String key, @NonNull UUID identifier) {
        return getCooldown(key, identifier).isPresent();
    }

    public static double getRemainingTime(@NonNull String key, @NonNull UUID identifier) {
        return getCooldown(key, identifier)
                .map(cooldown -> cooldown.getTimeUntilExpired() / 1000.0)
                .orElse(-1.0);
    }

    public static String getReadableRemainingTime(@NonNull String key, @NonNull UUID identifier) {
        double remainingTime = getRemainingTime(key, identifier);

        if (remainingTime < 1) {
            return String.format("%.1f", remainingTime);
        }

        return String.format("%.0f", remainingTime);
    }

    private static void attemptRun(String key, UUID identifier, long durationMillis, Runnable runnable) {
        synchronized (LOCK) {
            if (hasCooldown(key, identifier)) {
                return;
            }

            List<Cooldown> cooldowns = COOLDOWNS.computeIfAbsent(identifier, ignored -> new ArrayList<>());
            cooldowns.removeIf(cooldown -> cooldown.getCooldownId().equalsIgnoreCase(key));
            cooldowns.add(new Cooldown(identifier, key, durationMillis));
        }

        runnable.run();
    }

    private static void addCooldown(Cooldown cooldown) {
        synchronized (LOCK) {
            COOLDOWNS.computeIfAbsent(cooldown.getIdentifier(), ignored -> new ArrayList<>()).add(cooldown);
        }
    }

    private static void removeCooldown(Cooldown cooldown) {
        synchronized (LOCK) {
            List<Cooldown> cooldowns = COOLDOWNS.get(cooldown.getIdentifier());

            if (cooldowns == null) {
                return;
            }

            cooldowns.remove(cooldown);

            if (cooldowns.isEmpty()) {
                COOLDOWNS.remove(cooldown.getIdentifier());
            }
        }
    }

}
