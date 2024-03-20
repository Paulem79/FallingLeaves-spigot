package io.github.paulem.fallingleaves;

import com.github.Anon8281.universalScheduler.UniversalRunnable;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import io.github.paulem.fallingleaves.leaves.Leaf;
import io.github.paulem.fallingleaves.utils.UtilsLeaves;
import io.github.paulem.fallingleaves.nms.ILeavesColor;
import io.github.paulem.fallingleaves.nms.NMSHandler;
import io.github.paulem.fallingleaves.utils.Pair;
import io.github.paulem.fallingleaves.utils.SafeRandom;
import io.github.paulem.fallingleaves.utils.UtilsLocation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FallingLeaves extends JavaPlugin {
    public static Set<Material> leavesMaterials = new HashSet<>();
    public static ILeavesColor leavesColorImpl;
    public static final LinkedList<TextDisplay> registeredLeaves = new LinkedList<>();

    private static TaskScheduler scheduler;
    private static FallingLeaves instance;
    public static Pair<NamespacedKey, PersistentDataType<Double, Double>> PDC_NEXTX;
    public static Pair<NamespacedKey, PersistentDataType<Double, Double>> PDC_NEXTZ;
    public static Pair<NamespacedKey, PersistentDataType<Byte, Boolean>> PDC_ISLEAF;
    {
        // Assign your instance to the field as soon as it's created
        instance = this;
        PDC_NEXTX = new Pair<>(new NamespacedKey(this, "nextx"), PersistentDataType.DOUBLE);
        PDC_NEXTZ = new Pair<>(new NamespacedKey(this, "nextz"), PersistentDataType.DOUBLE);
        PDC_ISLEAF = new Pair<>(new NamespacedKey(this, "isLeaf"), PersistentDataType.BOOLEAN);
    }

    public static Vector radius = new Vector(10 , 8, 10);

    @Override
    public void onEnable() {
        leavesMaterials.addAll(List.of(Material.ACACIA_LEAVES,
                Material.AZALEA_LEAVES,
                Material.BIRCH_LEAVES,
                Material.DARK_OAK_LEAVES,
                Material.FLOWERING_AZALEA_LEAVES,
                Material.JUNGLE_LEAVES,
                Material.MANGROVE_LEAVES,
                Material.OAK_LEAVES,
                Material.SPRUCE_LEAVES));

        leavesColorImpl = NMSHandler.getLeavesColorImpl();
        scheduler = UniversalScheduler.getScheduler(this);

        //getServer().getPluginManager().registerEvents(new MoveLeafSpawn(), this);
        getLogger().info("Enabled!");

        getLogger().info("Cleaning existing leaves...");
        int cleaned = 0;
        for(World world : getServer().getWorlds()){
            for(TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class)){
                if(textDisplay.getPersistentDataContainer().has(PDC_ISLEAF.first(), PDC_ISLEAF.second())){
                    textDisplay.remove();
                    cleaned += 1;
                }
            }
        }
        getLogger().info("Cleaned " + cleaned + " leaves!");

        getLogger().info("Adding tasks...");
        getScheduler().runTaskTimer(new UniversalRunnable() {
            @Override
            public void run() {
                for(Player player : getServer().getOnlinePlayers()){
                    if(!player.isFlying() &&
                            !player.isSleeping() &&
                            !player.isDead() &&
                            !player.isInWater() &&
                            !player.isInvisible()) {

                        List<Block> leavesBlocks = UtilsLeaves.getSomeLeaves(player.getLocation(), radius);
                        if (leavesBlocks != null) {
                            for (Block block : leavesBlocks) {
                                if(SafeRandom.randBtw(1, 4) != 1) continue;
                                Location spawnLocation = block.getLocation().add(0, Leaf.FALL_SPEED, 0);

                                if (spawnLocation.getBlock().getType().isSolid()) continue;
                                if (spawnLocation.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) continue;
                                if (!UtilsLocation.anyOneLookingAt(Bukkit.getOnlinePlayers(), spawnLocation)) continue;

                                Leaf.createLeaf(spawnLocation);
                            }
                        }
                    }
                }
            }
        }, 5L, 10L);

        getScheduler().runTaskTimer(new UniversalRunnable() {
            @Override
            public void run() {
                registeredLeaves.removeIf(leaf -> {
                    PersistentDataContainer pdc = leaf.getPersistentDataContainer();

                    if(pdc.get(PDC_ISLEAF.first(), PDC_ISLEAF.second()) == null || Boolean.FALSE.equals(pdc.get(PDC_ISLEAF.first(), PDC_ISLEAF.second())))
                        return true;

                    @Nullable Double gotPDCNextX = pdc.get(PDC_NEXTX.first(), PDC_NEXTX.second());
                    @Nullable Double gotPDCNextZ = pdc.get(PDC_NEXTZ.first(), PDC_NEXTZ.second());

                    double nextx = gotPDCNextX != null ?
                            gotPDCNextX : SafeRandom.generateDouble();
                    double nextz = gotPDCNextZ != null ?
                            gotPDCNextZ : SafeRandom.generateDouble();

                    Location previousLocation = leaf.getLocation();
                    Location nextLocation = previousLocation.add(nextx, Leaf.FALL_SPEED, nextz);


                    if(nextLocation.getBlock().getType().isSolid() || !UtilsLocation.anyOneLookingAt(Bukkit.getOnlinePlayers(), nextLocation)) {
                        leaf.remove();
                        return true;
                    }

                    // Valid leaf
                    leaf.teleport(nextLocation);
                    return false;
                });
            }
        }, 1L, 1L);
        getLogger().info("I'm ready!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled!");
    }

    public static FallingLeaves getInstance() {
        return instance;
    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }
}