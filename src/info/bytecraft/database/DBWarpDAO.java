package info.bytecraft.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class DBWarpDAO
{
    private Connection conn;

    public DBWarpDAO(Connection conn)
    {
        this.conn = conn;
    }

    public Location getWarp(String name, Server server)
    {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM warps WHERE name = ?");
            stmt.setString(1, name);
            stmt.execute();

            ResultSet rs = stmt.getResultSet();
            if (!rs.next()) {
                return null;
            }

            double x = rs.getDouble("x");
            double y = rs.getDouble("y");
            double z = rs.getDouble("z");
            float pitch = rs.getFloat("pitch");
            float yaw = rs.getFloat("yaw");

            World world = Bukkit.getWorld(rs.getString("world"));
            if (world == null) {
                return null;
            }
            return new Location(world, x, y, z, yaw, pitch);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public void createWarp(String name, Location loc)
    {
        PreparedStatement stm = null;
        try {
            stm = conn.prepareStatement("INSERT INTO warps (name, x, y, z, pitch, yaw, world) VALUES (?, ?, ?, ?, ?, ?, ?)");
            stm.setString(1, name);
            stm.setDouble(2, loc.getX());
            stm.setDouble(3, loc.getY());
            stm.setDouble(4, loc.getZ());
            stm.setFloat(5, loc.getPitch());
            stm.setFloat(6, loc.getYaw());
            stm.setString(7, loc.getWorld().getName());
            stm.execute();
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
}
