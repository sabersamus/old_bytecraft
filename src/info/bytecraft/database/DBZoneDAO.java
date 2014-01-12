
package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Zone;
import info.bytecraft.api.Zone.Flag;
import info.bytecraft.api.Zone.Permission;
import info.bytecraft.api.math.Rectangle;

import java.sql.*;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DBZoneDAO
{
    private Connection conn;
    
    public DBZoneDAO(Connection conn)
    {
        this.conn = conn;
    }
    
    public List<Zone> getZones(String world)
    {
        List<Zone> zones = Lists.newArrayList();
        
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM zone WHERE zone_world = ?");
            stm.setString(1, world);
            stm.execute();
            ResultSet rs = stm.getResultSet();
            while(rs.next()){
                Zone zone = new Zone();
                zone.setName(rs.getString("zone_name"));
                zone.setId(rs.getInt("zone_id"));
                zone.setEnterMsg(rs.getString("zone_entermsg"));
                zone.setExitMsg(rs.getString("zone_exitmsg"));
                zone.setPvp(false);//no pvp for now
                zone.setBuildable(Boolean.parseBoolean(rs.getString("zone_build")));
                zone.setHostile(Boolean.parseBoolean(rs.getString("zone_hostile")));
                zone.setWhitelisted(Boolean.parseBoolean(rs.getString("zone_whitelist")));
                zone.setWorld(rs.getString("zone_world"));
                zone.setRect(getRect(zone));
                zones.add(zone);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        return zones;
    }
    
    public void createZone(Zone zone, BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("INSERT INTO zone (zone_name, zone_world, zone_entermsg, zone_exitmsg) VALUES" +
            		"(?, ?, ?, ?)");
            stm.setString(1, zone.getName());
            stm.setString(2, zone.getWorld());
            stm.setString(3, "Welcome to " + zone.getName());
            stm.setString(4, "Now leaving " + zone.getName());
            stm.execute();
            
            stm = conn.prepareStatement("INSERT INTO zone_rect (zone_name, rect_x1, rect_z1, rect_x2, rect_z2) " +
            		"VALUES (?, ?, ?, ?, ?)");
            stm.setString(1, zone.getName());
            stm.setInt(2, player.getZoneBlock1().getX());
            stm.setInt(3, player.getZoneBlock1().getZ());
            stm.setInt(4, player.getZoneBlock2().getX());
            stm.setInt(5, player.getZoneBlock2().getZ());
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    public Zone getZone(String name)
    {
        Zone zone = new Zone(name);
        PreparedStatement stm = null;
        
        try{
            stm = conn.prepareStatement("SELECT * FROM zone WHERE zone_name = ?");
            stm.setString(1, name);
            stm.execute();
            ResultSet rs = stm.getResultSet();
            if(!rs.next()){
                return null;
            }
            zone.setId(rs.getInt("zone_id"));
            zone.setEnterMsg(rs.getString("zone_entermsg"));
            zone.setExitMsg(rs.getString("zone_exitmsg"));
            zone.setPvp(false);//no pvp for now
            zone.setBuildable(Boolean.parseBoolean(rs.getString("zone_build")));
            zone.setHostile(Boolean.parseBoolean(rs.getString("zone_hostile")));
            zone.setWhitelisted(Boolean.parseBoolean(rs.getString("zone_whitelist")));
            zone.setWorld(rs.getString("zone_world"));
            zone.setRect(getRect(zone));
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        return zone;
    }
    
    public Rectangle getRect(Zone zone)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM zone_rect WHERE zone_name = ?");
            stm.setString(1, zone.getName());
            stm.execute();
            ResultSet rs = stm.getResultSet();
            if(rs.next()){
                int x1 = rs.getInt("rect_x1");
                int z1 = rs.getInt("rect_z1");
                int x2 = rs.getInt("rect_x2");
                int z2 = rs.getInt("rect_z2");
                return new Rectangle(x1, z1, x2, z2);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        return null;
    }
    
    public Map<String, Permission> getPerms(Zone zone)
    {
        Map<String, Permission> map = Maps.newHashMap();
        
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM zone_user WHERE zone_name = ?");
            stm.setString(1, zone.getName());
            ResultSet rs = stm.getResultSet();
            while(rs.next()){
                map.put(rs.getString("player_name"), Permission.valueOf(rs.getString("player_perm").toUpperCase()));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        return map;
    }
    
    public Permission getUser(Zone zone, BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM zone_user WHERE zone_name = ? AND player_name = ?");
            stm.setString(1, zone.getName());
            stm.setString(2, player.getName());
            stm.execute();
            ResultSet rs = stm.getResultSet();
            if(rs.next()){
                return Permission.valueOf(rs.getString("player_perm").toUpperCase());
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        return null;
    }
    
    public void deleteZone(String name)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("DELETE FROM zone WHERE zone_name = ?");
            stm.setString(1, name);
            stm.execute();
            
            stm = conn.prepareStatement("DELETE FROM zone_rect WHERE zone_name = ?");
            stm.setString(1, name);
            stm.execute();
            
            stm = conn.prepareStatement("DELETE FROM zone_user WHERE zone_name = ?");
            stm.setString(1, name);
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    public void updateFlag(String zone, Flag flag, String value)
    {
        String sql = null;
        switch(flag)
        {
        case PVP: sql = "UPDATE zone SET zone_pvp = ? WHERE zone_name = ?";
            break;
        case WHITELIST: sql = "UPDATE zone SET zone_whitelist = ? WHERE zone_name = ?";
            break;
        case HOSTILE: sql = "UPDATE zone SET zone_hostile = ? WHERE zone_name = ?";
            break;
        case BUILD: sql = "UPDATE zone SET zone_build = ? WHERE zone_name = ?";
            break;
        case ENTERMSG: sql = "UPDATE zone SET zone_entermsg = ? WHERE zone_name = ?";
            break;
        case EXITMSG: sql = "UPDATE zone SET zone_exitmsg = ? WHERE zone_name = ?";
            break;
        }
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement(sql);
            stm.setString(1, value);
            stm.setString(2, zone);
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    public void addUser(Zone zone, String name, Permission p)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM zone_user WHERE zone_name = ? AND player_name = ?");
            stm.setString(1, zone.getName());
            stm.setString(2, name);
            stm.execute();
            ResultSet rs = stm.getResultSet();
            if(rs.next()){
                updateUser(zone, name, p);
            }else{
                stm = conn.prepareStatement("INSERT INTO zone_user (zone_name, player_name, player_perm) VALUES (?, ?, ?)");
                stm.setString(1, zone.getName());
                stm.setString(2, name);
                stm.setString(3, p.name().toLowerCase());
                stm.execute();
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    private void updateUser(Zone zone, String name, Permission p)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("UPDATE zone_user SET player_perm = ? WHERE zone_name = ? AND player_name = ?");
            stm.setString(1, p.name().toLowerCase());
            stm.setString(2, zone.getName());
            stm.setString(3, name);
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    public boolean delUser(Zone zone, String name)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("DELETE FROM zone_user WHERE zone_name = ? AND player_name = ?");
            stm.setString(1, zone.getName());
            stm.setString(2, name);
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        return true;
    }
}
