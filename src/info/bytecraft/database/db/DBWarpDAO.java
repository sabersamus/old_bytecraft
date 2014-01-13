package info.bytecraft.database.db;

import info.bytecraft.database.DAOException;
import info.bytecraft.database.IWarpDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class DBWarpDAO implements IWarpDAO
{
    private Connection conn;

    public DBWarpDAO(Connection conn)
    {
        this.conn = conn;
    }

    public Location getWarp(String name) throws DAOException
    {
        String sql = "SELECT * FROM warps WHERE name = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, name);
            stm.execute();

            try (ResultSet rs = stm.getResultSet()) {
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
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createWarp(String name, Location loc) throws DAOException
    {
        if (getWarp(name) == null) {
            String sql =
                    "INSERT INTO warps (name, x, y, z, pitch, yaw, world) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
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
            }
        }
    }
}
