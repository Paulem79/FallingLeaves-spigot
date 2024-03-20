package io.github.paulem.fallingleaves.utils;

import io.github.paulem.fallingleaves.FallingLeaves;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.joml.Math;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UtilsLeaves {
    public static BufferedImage cachedFoliageImage;

    static {
        try {
            InputStream foliageImage = FallingLeaves.getInstance().getResource("Foliage.png");
            if (foliageImage != null)
                cachedFoliageImage = ImageIO.read(foliageImage);
            else throw new IllegalStateException("Foliage.png isn't available, please report this to Paulem");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable List<Block> getSomeLeaves(Location location, Vector radius) {
        if(location.getWorld() == null) return null;
        List<Block> blocks = new ArrayList<>();

        int radiusx = (int) radius.getX();
        int radiusy = (int) radius.getY();
        int radiusz = (int) radius.getZ();

        for(int x = location.getBlockX() - radiusx; x <= location.getBlockX() + radiusx; x++) {
            for(int y = location.getBlockY() - radiusy; y <= location.getBlockY() + radiusy; y++) {
                for(int z = location.getBlockZ() - radiusz; z <= location.getBlockZ() + radiusz; z++) {
                    if(SafeRandom.randBtw(1, 6) != 1) continue;
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if(!FallingLeaves.leavesMaterials.contains(block.getType())) continue;
                    if(!block.getRelative(BlockFace.DOWN).getType().isAir()) continue;
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static Color getDefaultFoliageColor(Location location) throws IOException, ArrayIndexOutOfBoundsException {
        Block block = location.getBlock();
        double temperature = block.getTemperature();
        double downfall = block.getHumidity();

        double adjTemp = Math.clamp(temperature, 0.0, 1.0);
        double adjDownfall = Math.clamp(downfall, 0.0, 1.0) * adjTemp;

        int xCoord = (int) (adjTemp * (cachedFoliageImage.getWidth() - 1));
        int yCoord = (int) (adjDownfall * (cachedFoliageImage.getHeight() - 1));

        int rgb = cachedFoliageImage.getRGB(xCoord, yCoord);

        return new Color(rgb);
    }
}
