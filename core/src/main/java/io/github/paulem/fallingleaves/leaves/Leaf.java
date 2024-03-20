package io.github.paulem.fallingleaves.leaves;

import io.github.paulem.fallingleaves.FallingLeaves;
import io.github.paulem.fallingleaves.utils.UtilsLeaves;
import io.github.paulem.fallingleaves.utils.SafeRandom;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.IOException;

public class Leaf {
    public static final double FALL_SPEED = -0.04;

    public static void createLeaf(Location spawnLocation) {
        if(spawnLocation.getWorld() == null) throw new NullPointerException("Wtf bro");

        String col = "#" + FallingLeaves.leavesColorImpl.getColor(spawnLocation);
        try {
            if (col.equals("#0")) {
                java.awt.Color defaultFoliageColor = UtilsLeaves.getDefaultFoliageColor(spawnLocation);
                col = String.format("#%02x%02x%02x", defaultFoliageColor.getRed(), defaultFoliageColor.getGreen(), defaultFoliageColor.getBlue());
            } else {
                col = "#" + Integer.toHexString(Integer.parseInt(col.split("#")[1]));
            }
        } catch (ArrayIndexOutOfBoundsException e){
            col = "#00610e";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ChatColor color;
        try {
            color = ChatColor.of(col);
        } catch (IllegalArgumentException e){
            color = ChatColor.of("#00610e");
        }

        ChatColor finalColor = color;
        TextDisplay leaf = spawnLocation.getWorld().spawn(spawnLocation, TextDisplay.class, (display) -> {
            display.setBillboard(Display.Billboard.CENTER);

            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            display.setText(finalColor + "🍂");
            display.setBrightness(new Display.Brightness(5, 5));

            double nextx = SafeRandom.generateDouble();
            double nextz = SafeRandom.generateDouble();

            PersistentDataContainer pdc = display.getPersistentDataContainer();
            pdc.set(FallingLeaves.PDC_NEXTX.first(), FallingLeaves.PDC_NEXTX.second(), nextx);
            pdc.set(FallingLeaves.PDC_NEXTZ.first(), FallingLeaves.PDC_NEXTZ.second(), nextz);
            pdc.set(FallingLeaves.PDC_ISLEAF.first(), FallingLeaves.PDC_ISLEAF.second(), true);
        });

        FallingLeaves.registeredLeaves.add(leaf);
    }
}
