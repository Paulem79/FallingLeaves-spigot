package io.github.paulem.fallingleaves;

import com.github.Anon8281.universalScheduler.UniversalRunnable;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import io.github.paulem.fallingleaves.leaves.FallingLeaf;
import io.github.paulem.fallingleaves.nms.NmsReflection;
import io.github.paulem.fallingleaves.utils.UtilsLeaves;
import io.github.paulem.fallingleaves.nms.Nms;
import io.github.paulem.fallingleaves.nms.NMSHandler;
import io.github.paulem.fallingleaves.utils.Pair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// TODO : Make leaves packet-based, Bug : overspammed leaves
public class FallingLeaves extends JavaPlugin {
    public static Nms nmsImpl = NMSHandler.getNmsImpl();
    public static final LinkedList<FallingLeaf> leafList = new LinkedList<>();
    public static LinkedHashSet<Player> guysSpawningLeaves = new LinkedHashSet<>();

    private static TaskScheduler scheduler;
    private static FallingLeaves instance;

    public int cleanedTimes;

    public static final Vector WIND = new Vector(0.02, -0.04, 0.02);
    public static final Vector3i RADIUS = new Vector3i(20 , 12, 20);

    {
        instance = this;
    }

    public final Pair<NamespacedKey, PersistentDataType<Byte, Boolean>> PDC_ISLEAF =
            new Pair<>(new NamespacedKey(this, "isLeaf"), PersistentDataType.BOOLEAN);

    @Override
    public void onEnable() {
        scheduler = UniversalScheduler.getScheduler(this);

        getLogger().info("Enabled!");

        getLogger().info("Cleaning existing leaves...");
        getLogger().info("Cleaned " + cleanDisplays() + " leaves!");

        getLogger().info("Adding tasks...");

        NmsReflection reflection = new NmsReflection();
        reflection.initReflection();

        double lagTPS = 19;

        getScheduler().runTaskTimer(new UniversalRunnable() {
            @Override
            public void run() {
                double tps = reflection.getTPS(0);

                if(cleanedTimes >= 4){
                    getLogger().severe("Major outage! TPS is constantly falling, report this to paulem. Disabling...");
                    cleanDisplays();
                    setEnabled(false);
                }

                if(tps < lagTPS){
                    getLogger().info("Server is lagging! Cleaning existing leaves...");
                    getLogger().info("Cleaned " + cleanDisplays() + " leaves!");
                    cleanedTimes += 1;
                }
            }
        }, 20L*10, 20L*30);

        getScheduler().runTaskTimer(new UniversalRunnable() {
            @Override
            public void run() {
                for(Player player : getServer().getOnlinePlayers()){
                    if(!player.isFlying() &&
                            !player.isSleeping() &&
                            !player.isDead() &&
                            !player.isInWater() &&
                            !player.isInvisible() && player.getWorld().getEnvironment() == World.Environment.NORMAL) {

                        List<Block> leavesBlocks = UtilsLeaves.fetchViableLeafBlocks(player, 0.03125, RADIUS);
                        for (Block block : leavesBlocks) {
                            Location spawnLocation = block.getLocation()
                                    .add(WIND)
                                    .add(new Vector(ThreadLocalRandom.current().nextDouble()/50, ThreadLocalRandom.current().nextDouble()/50, ThreadLocalRandom.current().nextDouble()/50));

                            if (spawnLocation.getBlock().getType().isSolid()) continue;
                            if (spawnLocation.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) continue;

                            new FallingLeaf(spawnLocation);
                        }
                    }
                }
            }
        }, 5L, 10L);

        getScheduler().runTaskTimer(new UniversalRunnable() {
            @Override
            public void run() {
                leafList.removeIf(leaf -> {
                    leaf.tick();
                    if(leaf.isDone()) {
                        leaf.remove();
                        return true;
                    } else {
                        return false;
                    }
                });
            }
        }, 1L, 1L);
        getLogger().info("I'm ready!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled!");
    }

    public int cleanDisplays(){
        int cleaned = 0;
        for(World world : getServer().getWorlds()){
            for(TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class)){
                if(textDisplay.getPersistentDataContainer().has(PDC_ISLEAF.first(), PDC_ISLEAF.second())){
                    textDisplay.remove();
                    cleaned += 1;
                }
            }
        }
        return cleaned;
    }

    public static FallingLeaves getInstance() {
        return instance;
    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }
}