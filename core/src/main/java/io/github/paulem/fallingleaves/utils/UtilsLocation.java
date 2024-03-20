package io.github.paulem.fallingleaves.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

// TODO : This is not finished!
public class UtilsLocation {
    public static boolean canPlayersSee(Collection<? extends Player> players, Entity entity){
        boolean canOneSee = false;
        for(Player player : players){
            Location playerEye = player.getEyeLocation();
            Location target = entity.getLocation();
            Vector vector = target.toVector().subtract(playerEye.toVector());
            System.out.println(vector.angle(playerEye.toVector()));

            canOneSee = vector.angle(playerEye.toVector()) > 90;
        }
        return canOneSee;
    }
}
