package info.bytecraft.blockfill;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBBlessDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.google.common.collect.Maps;

public class Fill
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
        Connection conn = null;
        DBBlessDAO dbBless = null;
        try {
            conn = ConnectionPool.getConnection();
            dbBless = new DBBlessDAO(conn);

            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        Location loc = new Location(world, x, y, z);
                        if (!dbBless.isBlessed(loc.getBlock())) {
                            blocks.put(loc, loc.getBlock().getType());
                            world.getBlockAt(loc).setType(material);
                            i++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
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
        Connection conn = null;
        DBBlessDAO dbBless = null;
        try {
            conn = ConnectionPool.getConnection();
            dbBless = new DBBlessDAO(conn);

            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        Location loc = new Location(world, x, y, z);
                        if (!dbBless.isBlessed(loc.getBlock())) {
                            if (loc.getBlock().getType() == material) {
                                blocks.put(loc, loc.getBlock().getType());
                                world.getBlockAt(loc).setType(to);
                                i++;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
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
}
