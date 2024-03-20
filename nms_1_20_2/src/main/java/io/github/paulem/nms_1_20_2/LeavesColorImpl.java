package io.github.paulem.nms_1_20_2;

import io.github.paulem.fallingleaves.nms.ILeavesColor;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.block.CraftBiome;

public class LeavesColorImpl implements ILeavesColor {
    @Override
    public int getColor(Location location) {
        Biome biome = CraftBiome.bukkitToMinecraft(location.getBlock().getBiome());
        return biome.getFoliageColor();
    }
}