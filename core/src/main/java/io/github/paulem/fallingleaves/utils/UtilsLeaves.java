package io.github.paulem.fallingleaves.utils;

import io.github.paulem.fallingleaves.FallingLeaves;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector3i;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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

    public static List<Block> fetchViableLeafBlocks(Player player, double chance, Vector3i d) {
        return fetchViableLeafBlocks(player, chance, d.x(), d.y(), d.z());
    }

    // More random, but faster
    public static List<Block> fetchViableLeafBlocks(Player player, double chance, int dx, int dy, int dz) {
        List<Block> leafBlocks = new ArrayList<>();
        Block baseBlock = player.getLocation().getBlock();

        int totalBlocks = (2 * dx + 1) * dy * (2 * dz + 1);
        int probeBlockCount = (int) (totalBlocks * chance);

        for (int i = 0; i < probeBlockCount; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int rx = random.nextInt(-dx, dx + 1);
            int ry = random.nextInt(Tag.LEAVES.isTagged(player.getLocation().getBlock().getType()) ? -dy/2 : -dy/3, dy + 1);
            int rz = random.nextInt(-dz, dz + 1);

            Block probedBlock = baseBlock.getRelative(rx, ry, rz);
            if (isViableLeafBlock(probedBlock)) {
                leafBlocks.add(probedBlock);
            }
        }

        return leafBlocks;
    }

    private static boolean isViableLeafBlock(Block block) {
        if (!Tag.LEAVES.isTagged(block.getType())) {
            return false;
        }
        Block below = block.getRelative(BlockFace.DOWN);
        return below.getType().isAir();
    }

    public static @Nullable String getLeafColor(Block block) {
        if(block.getType() == Material.ACACIA_LEAVES && Set.of(Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.WINDSWEPT_SAVANNA).contains(block.getBiome()))
            return "#625d18";
        return null;
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
