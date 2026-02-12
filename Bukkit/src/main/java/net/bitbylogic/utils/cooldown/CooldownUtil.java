package net.bitbylogic.utils.cooldown;

import lombok.NonNull;
import net.bitbylogic.utils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CooldownUtil {

    private static final ConcurrentHashMap<UUID, List<Cooldown>> COOLDOWNS = new ConcurrentHashMap<>();

    public static void startCooldown(String key, UUID identifier) {
        List<Cooldown> currentCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
        currentCooldowns.add(new Cooldown(identifier, key, -1));
        COOLDOWNS.put(identifier, currentCooldowns);
    }

    public static void startCooldown(JavaPlugin plugin, String key, UUID identifier, long expireTime, TimeUnit unit) {
        List<Cooldown> currentCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
        Cooldown cooldown = new Cooldown(identifier, key, unit.toMillis(expireTime));
        currentCooldowns.add(cooldown);
        COOLDOWNS.put(identifier, currentCooldowns);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            List<Cooldown> updatedCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
            updatedCooldowns.remove(cooldown);
            COOLDOWNS.put(identifier, updatedCooldowns);
        }, unit.toMillis(expireTime) / 50);
    }

    public static void startCooldown(JavaPlugin plugin, String key, UUID identifier, long expireTime, TimeUnit unit, Consumer<Void> completeCallback) {
        List<Cooldown> currentCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
        Cooldown cooldown = new Cooldown(identifier, key, unit.toMillis(expireTime));
        currentCooldowns.add(cooldown);
        COOLDOWNS.put(identifier, currentCooldowns);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            List<Cooldown> updatedCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
            updatedCooldowns.remove(cooldown);
            COOLDOWNS.put(identifier, updatedCooldowns);
            completeCallback.accept(null);
        }, unit.toMillis(expireTime) / 50);
    }

    public static void attemptRun(String key, UUID identifier, String cooldownTime, Runnable runnable) {
        synchronized (COOLDOWNS) {
            if (!hasCooldown(key, identifier)) {
                List<Cooldown> currentCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
                Cooldown cooldown = new Cooldown(identifier, key, TimeConverter.convert(cooldownTime));
                currentCooldowns.add(cooldown);
                COOLDOWNS.put(identifier, currentCooldowns);
                runnable.run();
                return;
            }

            getCooldown(key, identifier).ifPresent(cooldown -> {
                if (cooldown.isActive()) {
                    return;
                }

                List<Cooldown> currentCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
                Cooldown newCooldown = new Cooldown(identifier, key, TimeConverter.convert(cooldownTime));
                currentCooldowns.remove(cooldown);
                currentCooldowns.add(newCooldown);
                COOLDOWNS.put(identifier, currentCooldowns);
                runnable.run();
            });
        }
    }

    public static void attemptRun(@NonNull String key, @NonNull UUID identifier, long cooldownTime, @NonNull TimeUnit timeUnit, @NonNull Runnable runnable) {
        synchronized (COOLDOWNS) {
            if (!hasCooldown(key, identifier)) {
                List<Cooldown> currentCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
                Cooldown cooldown = new Cooldown(identifier, key, timeUnit.toMillis(cooldownTime));
                currentCooldowns.add(cooldown);
                COOLDOWNS.put(identifier, currentCooldowns);
                runnable.run();
                return;
            }

            getCooldown(key, identifier).ifPresent(cooldown -> {
                if (cooldown.isActive()) {
                    return;
                }

                List<Cooldown> currentCooldowns = COOLDOWNS.getOrDefault(identifier, new ArrayList<>());
                Cooldown newCooldown = new Cooldown(identifier, key, timeUnit.toMillis(cooldownTime));
                currentCooldowns.remove(cooldown);
                currentCooldowns.add(newCooldown);
                COOLDOWNS.put(identifier, currentCooldowns);
                runnable.run();
            });
        }
    }

    public static void endCooldown(@NonNull String key, @NonNull UUID identifier) {
        getCooldowns(identifier).removeIf(cooldown -> cooldown.getIdentifier().equals(identifier) && cooldown.getCooldownId().equalsIgnoreCase(key));
    }

    public static List<Cooldown> getCooldowns(@NonNull UUID identifier) {
        return COOLDOWNS.computeIfAbsent(identifier, uuid -> new ArrayList<>());
    }

    public static Optional<Cooldown> getCooldown(@NonNull String key, @NonNull UUID identifier) {
        return getCooldowns(identifier).stream().filter(cd -> cd != null && cd.getCooldownId().equalsIgnoreCase(key) && cd.isActive()).findFirst();
    }

    public static boolean hasCooldown(@NonNull String key, @NonNull UUID identifier) {
        return getCooldown(key, identifier).isPresent();
    }

    public static double getRemainingTime(@NonNull String key, @NonNull UUID identifier) {
        if (!hasCooldown(key, identifier)) {
            return -1;
        }

        return getCooldown(key, identifier).map(cooldown -> (cooldown.getTimeUntilExpired() / 1000.0)).orElse(-1.0);
    }

    public static String getReadableRemainingTime(@NonNull String key, @NonNull UUID identifier) {
        double remainingTime = getRemainingTime(key, identifier);
        return String.format(remainingTime < 1 ? "%.1f" : "%.0f", remainingTime);
    }

}
