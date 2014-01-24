package info.bytecraft.blockfill;

import info.bytecraft.api.BytecraftPlayer;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.google.common.collect.Maps;

public class Fill extends AbstractFill
{
    private Block block1;
    private Block block2;
    private Material material;
    private World world;
    public static final int MAX_SIZE = 20000;

    public static enum Action {
        FILL, REPLACE, UNDO;
    }

    private HashMap<Location, Material> blocks = Maps.newHashMap();

    public Fill(BytecraftPlayer player, Block block1, Block block2,
            Material material)
    {
        this.world = player.getWorld();
        this.block1 = block1;
        this.block2 = block2;
        this.material = material;
        player.setLastFill(this);
    }

    public int fill()
    {
        blocks.clear();
        int i = 0;
        int xMax = Math.max(block1.getX(), block2.getX());
        int xMin = Math.min(block1.getX(), block2.getX());
        int yMax = Math.max(block1.getY(), block2.getY());
        int yMin = Math.min(block1.getY(), block2.getY());
        int zMax = Math.max(block1.getZ(), block2.getZ());
        int zMin = Math.min(block1.getZ(), block2.getZ());
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Location loc = new Location(world, x, y, z);
                    blocks.put(loc, loc.getBlock().getType());
                    world.getBlockAt(loc).setType(material);
                    i++;
                }
            }
        }
        return i;
    }

    public int undo()
    {
        int i = 0;
        for (Location loc : blocks.keySet()) {
            loc.getBlock().setType(blocks.get(loc));
            i++;
        }
        return i;
    }

    public int replace(Material to)
    {
        blocks.clear();
        int i = 0;
        int xMax = Math.max(block1.getX(), block2.getX());
        int xMin = Math.min(block1.getX(), block2.getX());
        int yMax = Math.max(block1.getY(), block2.getY());
        int yMin = Math.min(block1.getY(), block2.getY());
        int zMax = Math.max(block1.getZ(), block2.getZ());
        int zMin = Math.min(block1.getZ(), block2.getZ());
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Location loc = new Location(world, x, y, z);
                    if (loc.getBlock().getType() == material) {
                        blocks.put(loc, loc.getBlock().getType());
                        world.getBlockAt(loc).setType(to);
                        i++;
                    }
                }
            }
        }
        return i;
    }

    public int getSize()
    {
        int i = 0;
        int xMax = Math.max(block1.getX(), block2.getX());
        int xMin = Math.min(block1.getX(), block2.getX());
        int yMax = Math.max(block1.getY(), block2.getY());
        int yMin = Math.min(block1.getY(), block2.getY());
        int zMax = Math.max(block1.getZ(), block2.getZ());
        int zMin = Math.min(block1.getZ(), block2.getZ());

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    i++;
                }
            }
        }
        return i;
    }

    public Material getMaterial()
    {
        return material;
    }

    @Override
    public void run()
    {
        
    }
}
