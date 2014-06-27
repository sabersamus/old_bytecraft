package info.bytecraft.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.google.common.collect.Maps;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IBlessDAO;

public class DBBlessDAO implements IBlessDAO
{
    private Connection conn;

    public DBBlessDAO(Connection conn)
    {
        this.conn = conn;
    }
    
    public Map<Location, String> getBlessedBlocks()
    throws DAOException
    {
        Map<Location, String> blessedBlocks = Maps.newHashMap();
        
        String sql = "SELECT * FROM bless";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    String world = rs.getString("world");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    String owner = rs.getString("player_name");
                    
                    blessedBlocks.put(new Location(Bukkit.getWorld(world), x, y, z), owner);
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        
        return blessedBlocks;
    }

    public boolean isBlessed(Block block)
    throws DAOException
    {
        String sql = "SELECT * FROM bless WHERE x = ? AND y = ? AND z = ? AND world = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setInt(1, block.getX());
            stm.setInt(2, block.getY());
            stm.setInt(3, block.getZ());
            stm.setString(4, block.getWorld().getName());
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }

    public boolean bless(Block block, BytecraftPlayer owner)
    throws DAOException
    {
        String sql = "INSERT INTO bless (player_name, x, y, z, world) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, owner.getName());
            stm.setInt(2, block.getX());
            stm.setInt(3, block.getY());
            stm.setInt(4, block.getZ());
            stm.setString(5, block.getWorld().getName());
            stm.execute();
            return true;
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    public String getOwner(Block block)
    throws DAOException
    {
        if (!isBlessed(block))
            return null;
        
        String sql = "SELECT * FROM bless WHERE x = ? AND y = ? AND z = ? AND world = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setInt(1, block.getX());
            stm.setInt(2, block.getY());
            stm.setInt(3, block.getZ());
            stm.setString(4, block.getWorld().getName());
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                if (rs.next()) {
                    return rs.getString("player_name");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public int getBlessId(Block block) throws DAOException
    {
        if(!isBlessed(block))return 0;
        
        String sql = "SELECT * FROM bless WHERE x = ? AND y = ? AND z = ? AND world = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setInt(1, block.getX());
            stm.setInt(2, block.getY());
            stm.setInt(3, block.getZ());
            stm.setString(4, block.getWorld().getName());
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return 0;
    }
}
