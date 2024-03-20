package io.github.paulem.fallingleaves;

import com.github.Anon8281.universalScheduler.UniversalRunnable;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import io.github.paulem.fallingleaves.leaves.FallingLeaf;
import io.github.paulem.fallingleaves.utils.UtilsLeaves;
import io.github.paulem.fallingleaves.nms.LeavesColor;
import io.github.paulem.fallingleaves.nms.NMSHandler;
import io.github.paulem.fallingleaves.utils.Pair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// TODO : You should probably have a wind strength per world. Just a 2D vector for x and z. This way all leafs fall in the same direction. The wind speed can fluctuate a bit.
// TODO : If a lot of players stand under the same tree, then they will turbo spawn a million leafs.
public class FallingLeaves extends JavaPlugin {
    public static LeavesColor leavesColorImpl = NMSHandler.getLeavesColorImpl();
    public static final LinkedList<FallingLeaf> leafList = new LinkedList<>();

    private static TaskScheduler scheduler;
    private static FallingLeaves instance;

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

                        List<Block> leavesBlocks = UtilsLeaves.fetchViableLeafBlocks(player, 0.125, 10 , 7, 10);
                        for (Block block : leavesBlocks) {
                            if (ThreadLocalRandom.current().nextDouble() < 0.25) continue;
                            Location spawnLocation = block.getLocation().add(ThreadLocalRandom.current().nextDouble(), FallingLeaf.FALL_SPEED, ThreadLocalRandom.current().nextDouble());

                            if (spawnLocation.getBlock().getType().isSolid()) continue;
                            if (spawnLocation.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) continue;
                            //if (!UtilsLocation.anyOneLookingAt(Bukkit.getOnlinePlayers(), spawnLocation)) continue;

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

    public static FallingLeaves getInstance() {
        return instance;
    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }
}