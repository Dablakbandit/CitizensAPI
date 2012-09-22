package net.citizensnpcs.api.ai;

import java.util.EnumSet;
import java.util.Set;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class TeleportStuckAction implements StuckAction {
    private TeleportStuckAction() {
        // singleton
    }

    @Override
    public boolean run(NPC npc, Navigator navigator) {
        if (!npc.isSpawned())
            return false;
        Location base = navigator.getTargetAsLocation();
        if (npc.getBukkitEntity().getLocation().distanceSquared(base) <= RANGE)
            return true;
        Block block = base.getBlock();
        int iterations = 0;
        while (!EMPTY_BLOCKS.contains(block.getType())) {
            block = block.getRelative(BlockFace.UP);
            if (++iterations >= MAX_ITERATIONS)
                block = base.getBlock();
        }
        npc.getBukkitEntity().teleport(block.getLocation());
        return false;
    }

    private static Set<Material> EMPTY_BLOCKS = EnumSet.of(Material.AIR, Material.BIRCH_WOOD_STAIRS,
            Material.BRICK_STAIRS, Material.CAKE_BLOCK, Material.COBBLESTONE_STAIRS, Material.DEAD_BUSH,
            Material.JUNGLE_WOOD_STAIRS, Material.LADDER, Material.LONG_GRASS, Material.RAILS,
            Material.POWERED_RAIL, Material.REDSTONE_WIRE, Material.REDSTONE_TORCH_ON,
            Material.REDSTONE_TORCH_OFF, Material.TRAP_DOOR, Material.TRIPWIRE, Material.TRIPWIRE_HOOK,
            Material.VINE, Material.WALL_SIGN, Material.WATER, Material.WOOD_STAIRS, Material.WOOD_STEP,
            Material.WOODEN_DOOR, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.SIGN,
            Material.SIGN_POST);
    public static TeleportStuckAction INSTANCE = new TeleportStuckAction();
    private static int MAX_ITERATIONS = 10;
    private static final double RANGE = 10;
}
