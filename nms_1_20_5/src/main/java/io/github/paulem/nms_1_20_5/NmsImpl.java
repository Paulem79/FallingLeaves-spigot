package io.github.paulem.nms_1_20_5;

import io.github.paulem.fallingleaves.nms.Nms;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.block.CraftBiome;

public class NmsImpl implements Nms {
    @Override
    public int getColor(Location location) {
        Biome biome = CraftBiome.bukkitToMinecraft(location.getBlock().getBiome());
        return biome.getFoliageColor();
    }
}