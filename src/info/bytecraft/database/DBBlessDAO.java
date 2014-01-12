package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.block.Block;

public class DBBlessDAO
{

    private Connection conn;

    public DBBlessDAO(Connection conn)
    {
        this.conn = conn;
    }

    public boolean isBlessed(Block block)
    {
        PreparedStatement stm = null;
        try {
            stm =
                    conn.prepareStatement("SELECT * FROM bless WHERE x = ? AND y = ? AND z = ? AND world = ?");
            stm.setInt(1, block.getX());
            stm.setInt(2, block.getY());
            stm.setInt(3, block.getZ());
            stm.setString(4, block.getWorld().getName());

            stm.execute();
            ResultSet rs = stm.getResultSet();
            if (rs.next())
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                }
            }
        }
        return false;
    }

    public boolean bless(Block block, BytecraftPlayer owner)
    {
        PreparedStatement stm = null;
        try {
            stm =
                    conn.prepareStatement("INSERT INTO bless (player_name, x, y, z, world) "
                            + "VALUES (?, ?, ?, ?, ?)");
            stm.setString(1, owner.getName());
            stm.setInt(2, block.getX());
            stm.setInt(3, block.getY());
            stm.setInt(4, block.getZ());
            stm.setString(5, block.getWorld().getName());
            stm.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public String getOwner(Block block)
    {
        if (!isBlessed(block))
            return null;
        PreparedStatement stm = null;
        try {
            stm =
                    conn.prepareStatement("SELECT * FROM bless WHERE x = ? AND y = ? AND z = ? AND world = ?");
            stm.setInt(1, block.getX());
            stm.setInt(2, block.getY());
            stm.setInt(3, block.getZ());
            stm.setString(4, block.getWorld().getName());

            stm.execute();

            ResultSet rs = stm.getResultSet();
            if (rs.next()) {
                return rs.getString("player_name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                }
            }
        }
        return null;
    }
}
