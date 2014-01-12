package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class DBHomeDAO
{
    
    private Connection conn;
    
    public DBHomeDAO(Connection conn)
    {
        this.conn = conn;
    }
    
    
    public Location getHome(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM player_home WHERE player_name = ?");
            stm.setString(1, player.getName());
            stm.execute();
            
            ResultSet rs = stm.getResultSet();
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
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
        return null;
    }
    
    public Location getHome(String player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM player_home WHERE player_name = ?");
            stm.setString(1, player);
            stm.execute();
            
            ResultSet rs = stm.getResultSet();
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
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
        return null;
    }
    
    public void setHome(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        Location homeLoc = player.getLocation();
        try{
            stm = conn.prepareStatement("SELECT * FROM player_home WHERE player_name = ?");
            stm.setString(1, player.getName());
            stm.execute();
            if(stm.getResultSet().next()){
                updateHome(player);
                return;
            }else{
                stm = conn.prepareStatement("INSERT INTO player_home "
                		+ "(player_name, home_x, home_y, home_z, home_yaw, home_pitch, home_world) VALUES" +
                		"(?, ?, ?, ?, ?, ?, ?)");
                stm.setString(1, player.getName());
                stm.setInt(2, homeLoc.getBlockX());
                stm.setInt(3, homeLoc.getBlockY());
                stm.setInt(4, homeLoc.getBlockZ());
                stm.setFloat(5, homeLoc.getYaw());
                stm.setFloat(6, homeLoc.getPitch());
                stm.setString(7, homeLoc.getWorld().getName());
                stm.execute();
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
    }
    
    private void updateHome(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        Location homeLoc = player.getLocation();
        try{
            stm = conn.prepareStatement("UPDATE player_home SET block_x = ?, block_y = ?, block_z = ?, block_yaw = ?, block_pitch = ?, block_world = ?" +
            		"WHERE player_name = ?");
            stm.setInt(1, homeLoc.getBlockX());
            stm.setInt(2, homeLoc.getBlockY());
            stm.setInt(3, homeLoc.getBlockZ());
            stm.setFloat(4, homeLoc.getYaw());
            stm.setFloat(5, homeLoc.getPitch());
            stm.setString(6, homeLoc.getWorld().getName());
            stm.setString(7, player.getName());
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
    }
}
