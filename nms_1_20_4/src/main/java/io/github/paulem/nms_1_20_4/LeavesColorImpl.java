package io.github.paulem.nms_1_20_4;

import io.github.paulem.fallingleaves.nms.LeavesColor;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBiome;

public class LeavesColorImpl implements LeavesColor {
    @Override
    public int getColor(Location location) {
        Biome biome = CraftBiome.bukkitToMinecraft(location.getBlock().getBiome());
        return biome.getFoliageColor();
    }
}