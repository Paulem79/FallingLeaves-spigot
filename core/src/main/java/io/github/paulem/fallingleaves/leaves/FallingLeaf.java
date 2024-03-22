package io.github.paulem.fallingleaves.leaves;

import io.github.paulem.fallingleaves.FallingLeaves;
import io.github.paulem.fallingleaves.utils.UtilsLeaves;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class FallingLeaf {
    private final TextDisplay textDisplay;
    private final double windx = ThreadLocalRandom.current().nextDouble()/50;
    private final double windy = ThreadLocalRandom.current().nextDouble()/50;
    private final double windz = ThreadLocalRandom.current().nextDouble()/50;

    public FallingLeaf(Location spawnLocation) {
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
            @Nullable String leafColor = UtilsLeaves.getLeafColor(spawnLocation.getBlock().getRelative(BlockFace.UP));
            col = leafColor != null ? leafColor : "#00610e";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ChatColor color;
        try {
            color = ChatColor.of(col);
        } catch (IllegalArgumentException e){
            color = ChatColor.of("#00610e");
        }

        FallingLeaves instance = FallingLeaves.getInstance();

        ChatColor finalColor = color;
        this.textDisplay = spawnLocation.getWorld().spawn(spawnLocation, TextDisplay.class, (display) -> {
            display.setBillboard(Display.Billboard.CENTER);

            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            display.setText(finalColor + "🍂");
            display.setBrightness(new Display.Brightness(4, 4));

            PersistentDataContainer pdc = display.getPersistentDataContainer();
            pdc.set(instance.PDC_ISLEAF.first(), instance.PDC_ISLEAF.second(), true);
        });

        FallingLeaves.leafList.add(this);
    }

    public void tick() {
        Location previousLocation = textDisplay.getLocation();
        Location nextLocation = previousLocation
                .add(FallingLeaves.WIND)
                .add(new Vector(windx, windy, windz));

        textDisplay.teleport(nextLocation);
    }

    // Leaf is done when it hits a block.
    public boolean isDone() {
        Block block = this.textDisplay.getLocation().getBlock();
        return !block.getType().isAir();
    }

    public void remove() {
        this.textDisplay.remove();
    }

    public double getWindx() {
        return windx;
    }

    public double getWindz() {
        return windz;
    }
}