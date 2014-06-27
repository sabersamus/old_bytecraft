package info.bytecraft.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.common.collect.Lists;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IHomeDAO;

public class DBHomeDAO implements IHomeDAO
{
    private Connection conn;
    
    public DBHomeDAO(Connection conn)
    {
        this.conn = conn;
    }
    
    
    @Deprecated
    public Location getHome(BytecraftPlayer player)
    throws DAOException
    {
        return getHome(player.getName());
    }
    
    @Deprecated
    public Location getHome(String player)
    throws DAOException
    {
        String sql = "SELECT * FROM player_home WHERE player_name = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, player);
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                Location loc = null;
                if(rs.next()){
                    int x = rs.getInt("home_x");
                    int y = rs.getInt("home_y");
                    int z = rs.getInt("home_z");
                    float pitch = rs.getFloat("home_pitch");
                    float yaw = rs.getFloat("home_yaw");
                    World world = Bukkit.getWorld(rs.getString("home_world"));
                    loc = new Location(world, x, y, z, yaw, pitch);
                    return loc;
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return null;
    }
    
    @Deprecated
    public void setHome(BytecraftPlayer player)
    throws DAOException
    {
        String sql = "SELECT * FROM player_home WHERE player_name = ?";
        String sql2 = "INSERT INTO player_home (player_name, home_x, home_y, home_z, home_yaw, home_pitch, home_world) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Location homeLoc = player.getLocation();
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, player.getName());
            stm.execute();
            if(stm.getResultSet().next()){
                updateHome(player);
                return;
            }else{
                try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
                    stmt.setString(1, player.getName());
                    stmt.setInt(2, homeLoc.getBlockX());
                    stmt.setInt(3, homeLoc.getBlockY());
                    stmt.setInt(4, homeLoc.getBlockZ());
                    stmt.setFloat(5, homeLoc.getYaw());
                    stmt.setFloat(6, homeLoc.getPitch());
                    stmt.setString(7, homeLoc.getWorld().getName());
                    stmt.execute();
                }
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
    
    @Deprecated
    public void updateHome(BytecraftPlayer player)
    throws DAOException
    {
        String sql = "UPDATE player_home SET home_x = ?, home_y = ?, home_z = ?, "
                + "home_yaw = ?, home_pitch = ?, home_world = ? WHERE player_name = ?";
        Location homeLoc = player.getLocation();
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setInt(1, homeLoc.getBlockX());
            stm.setInt(2, homeLoc.getBlockY());
            stm.setInt(3, homeLoc.getBlockZ());
            stm.setFloat(4, homeLoc.getYaw());
            stm.setFloat(5, homeLoc.getPitch());
            stm.setString(6, homeLoc.getWorld().getName());
            stm.setString(7, player.getName());
            stm.execute();
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }


    @Override
    public Location getHome(BytecraftPlayer player, String name)
            throws DAOException
    {
        return this.getHome(player, name);
    }



    @Override
    public void setHome(BytecraftPlayer player, String name)
            throws DAOException
    {
        String sql = "SELECT * FROM player_home WHERE player_name = ? AND home_name = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, player.getName());
            stm.setString(2, name);
            stm.execute();
            
            if(stm.getResultSet().next()){
                this.updateHome(player, name);
                return;
            }else{
                Location loc = player.getLocation();
                sql = "INSERT INTO player_home (player_name, home_name, home_x, home_y, home_z, home_yaw, home_pitch, home_world) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                try(PreparedStatement stmt = conn.prepareStatement(sql)){
                    stmt.setString(1, player.getName());
                    stmt.setString(2, name);
                    stmt.setDouble(3, loc.getX());
                    stmt.setDouble(4, loc.getY());
                    stmt.setDouble(5, loc.getZ());
                    stmt.setFloat(6, loc.getYaw());
                    stmt.setFloat(7, loc.getPitch());
                    stmt.setString(8, loc.getWorld().getName());
                    stmt.execute();
                }
                
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }


    @Override
    public void updateHome(BytecraftPlayer player, String name)
            throws DAOException
    {
        String sql = "UPDATE player_home SET home_x = ?, home_y = ?, home_z = ?, "
                + "home_yaw = ?, home_pitch = ?, home_world = ? WHERE player_name = ? AND home_name = ?";
        Location homeLoc = player.getLocation();
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setDouble(1, homeLoc.getX());
            stm.setDouble(2, homeLoc.getY());
            stm.setDouble(3, homeLoc.getZ());
            stm.setFloat(4, homeLoc.getYaw());
            stm.setFloat(5, homeLoc.getPitch());
            stm.setString(6, homeLoc.getWorld().getName());
            stm.setString(7, player.getName());
            stm.setString(8, name);
            stm.execute();
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        
    }


    @Override
    public List<String> getHomeNames(BytecraftPlayer player) throws DAOException
    {
        List<String> names = Lists.newArrayList();
        String sql = "SELECT * FROM player_home WHERE player_name = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, player.getName());
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    names.add(rs.getString("home_name"));
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return names;
    }


    @Override
    public void deleteHome(BytecraftPlayer player, String name)
            throws DAOException
    {
        String sql = "DELETE FROM player_home WHERE player_name = ? AND home_name = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, player.getName());
            stm.setString(2, name);
            stm.execute();
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
}
