package io.github.paulem.fallingleaves.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class UtilsLocation {
    public static boolean anyOneLookingAt(Collection<? extends Player> players, Location location) {
        return players.stream().anyMatch(player -> isLookingInDirection(player, location));
    }

    public static boolean allLookingAt(Collection<? extends Player> players, Location location) {
        return players.stream().allMatch(player -> isLookingInDirection(player, location));
    }

    public static boolean isLookingInDirection(Player player, Location location) {
        Location playerEyes = player.getEyeLocation();
        Vector toEntity = location.toVector().subtract(playerEyes.toVector());
        Vector playerDirection = playerEyes.getDirection();
        double angle = playerDirection.angle(toEntity);
        return angle < Math.PI / 2; // 90 degrees because 2 * PI == 360 degrees
    }
}
