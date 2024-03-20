package io.github.paulem.nms_1_19_4;

import io.github.paulem.fallingleaves.nms.ILeavesColor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlock;

import java.util.Optional;

public class LeavesColorImpl implements ILeavesColor {
    @Override
    public int getColor(Location location) {
        World bukkitWorld = location.getWorld();
        if(bukkitWorld == null) return 0;

        ServerLevel nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
        Optional<Registry<Biome>> biomeRegistry = nmsWorld.registryAccess().registry(Registries.BIOME);

        if(biomeRegistry.isEmpty()) return 0;

        Biome biome = CraftBlock.biomeToBiomeBase(biomeRegistry.get(), location.getBlock().getBiome()).value();
        return biome.getFoliageColor();
    }
}