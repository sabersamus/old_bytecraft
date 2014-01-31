package info.bytecraft.database.db;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.math.Rectangle;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IZoneDAO;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.Zone.Flag;
import info.bytecraft.zones.Zone.Permission;

import java.sql.*;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DBZoneDAO implements IZoneDAO
{
    private Connection conn;
    
    public DBZoneDAO(Connection conn)
    {
        this.conn = conn;
    }
    
    public List<Zone> getZones(String world)
    throws DAOException
    {
        List<Zone> zones = Lists.newArrayList();
        
        String sql = "SELECT * FROM zone WHERE zone_world = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, world);
            stm.execute();
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    Zone zone = new Zone();
                    zone.setName(rs.getString("zone_name"));
                    zone.setId(rs.getInt("zone_id"));
                    zone.setEnterMessage(rs.getString("zone_entermsg"));
                    zone.setExitMessage(rs.getString("zone_exitmsg"));
                    zone.setFlag(Flag.PVP, false);
                    zone.setFlag(Flag.BUILD, Boolean.parseBoolean(rs.getString("zone_build")));
                    zone.setFlag(Flag.HOSTILE, Boolean.parseBoolean(rs.getString("zone_hostile")));
                    zone.setFlag(Flag.WHITELIST, Boolean.parseBoolean(rs.getString("zone_whitelist")));
                    zone.setWorld(rs.getString("zone_world"));
                    zone.setRectangle(getRect(zone));
                    zone.setPermissions(getPermissions(zone));
                    zones.add(zone);
                }
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return zones;
    }
    
    public void createZone(Zone zone, BytecraftPlayer player)
    throws DAOException
    {
        String sql = "INSERT INTO zone (zone_name, zone_world, zone_entermsg, zone_exitmsg) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, zone.getName());
            stm.setString(2, zone.getWorld());
            stm.setString(3, "Welcome to " + zone.getName());
            stm.setString(4, "Now leaving " + zone.getName());
            stm.execute();
            
            sql = "INSERT INTO zone_rect (zone_name, rect_x1, rect_z1, rect_x2, rect_z2) VALUES (?, ?, ?, ?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, zone.getName());
                stmt.setInt(2, player.getZoneBlock1().getX());
                stmt.setInt(3, player.getZoneBlock1().getZ());
                stmt.setInt(4, player.getZoneBlock2().getX());
                stmt.setInt(5, player.getZoneBlock2().getZ());
                stmt.execute();
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
    
    public Zone getZone(String name)
    throws DAOException
    {
        Zone zone = new Zone(name);
        String sql = "SELECT * FROM zone WHERE zone_name = ?";
        
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, name);
            stm.execute();
            try(ResultSet rs = stm.getResultSet()){
                if(!rs.next()){
                    return null;
                }
                zone.setId(rs.getInt("zone_id"));
                zone.setEnterMessage(rs.getString("zone_entermsg"));
                zone.setExitMessage(rs.getString("zone_exitmsg"));
                zone.setFlag(Flag.PVP, false);
                zone.setFlag(Flag.BUILD, Boolean.parseBoolean(rs.getString("zone_build")));
                zone.setFlag(Flag.HOSTILE, Boolean.parseBoolean(rs.getString("zone_hostile")));
                zone.setFlag(Flag.WHITELIST, Boolean.parseBoolean(rs.getString("zone_whitelist")));
                zone.setWorld(rs.getString("zone_world"));
                zone.setRectangle(getRect(zone));
                zone.setPermissions(getPermissions(zone));
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return zone;
    }
    
    public Rectangle getRect(Zone zone)
    throws DAOException
    {
        String sql = "SELECT * FROM zone_rect WHERE zone_name = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, zone.getName());
            stm.execute();
            try(ResultSet rs = stm.getResultSet()){
                if(rs.next()){
                    int x1 = rs.getInt("rect_x1");
                    int z1 = rs.getInt("rect_z1");
                    int x2 = rs.getInt("rect_x2");
                    int z2 = rs.getInt("rect_z2");
                    return new Rectangle(x1, z1, x2, z2);
                }
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return null;
    }
    
    public Map<String, Permission> getPermissions(Zone zone)
    throws DAOException
    {
        if(zone == null)return null;
        Map<String, Permission> map = Maps.newHashMap();
        
        String sql = "SELECT * FROM zone_user WHERE zone_name = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, zone.getName());
            stm.execute();
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    map.put(rs.getString("player_name"), 
                            Permission.valueOf(rs.getString("player_perm").toUpperCase()));
                }
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return map;
    }
    
    public Permission getUser(Zone zone, BytecraftPlayer player)
    throws DAOException
    {
        String sql = "SELECT * FROM zone_user WHERE zone_name = ? AND player_name = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, zone.getName());
            stm.setString(2, player.getName());
            stm.execute();
            try(ResultSet rs = stm.getResultSet()){
                if(rs.next()){
                    return Permission.valueOf(rs.getString("player_perm").toUpperCase());
                }
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return null;
    }
    
    public void deleteZone(String name)
    throws DAOException
    {
        String sql = "DELETE FROM zone WHERE zone_name = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, name);
            stm.execute();
            
            sql = "DELETE FROM zone_rect WHERE zone_name = ?";
            try(PreparedStatement stm1 = conn.prepareStatement(sql)){
                stm1.setString(1, name);
                stm1.execute();
                
                sql = "DELETE FROM zone_user WHERE zone_name = ?";
                try(PreparedStatement stm2 = conn.prepareStatement(sql)){
                    stm2.setString(1, name);
                    stm2.execute();
                }
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
    
    public void updateFlag(String zone, Flag flag, String value)
    throws DAOException
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
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, value);
            stm.setString(2, zone);
            stm.execute();
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
    
    public void addUser(Zone zone, String name, Permission p)
    throws DAOException
    {
        String sql = "SELECT * FROM zone_user WHERE zone_name = ? AND player_name = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, zone.getName());
            stm.setString(2, name);
            stm.execute();
            try(ResultSet rs = stm.getResultSet()){
                if(rs.next()){
                    updateUser(zone, name, p);
                }else{
                    sql = "INSERT INTO zone_user (zone_name, player_name, player_perm) VALUES (?, ?, ?)"; 
                    try(PreparedStatement stm1 = conn.prepareStatement(sql)){
                        stm1.setString(1, zone.getName());
                        stm1.setString(2, name);
                        stm1.setString(3, p.name().toLowerCase());
                        stm1.execute();
                    }
                }
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
    
    public void updateUser(Zone zone, String name, Permission p)
    throws DAOException
    {
        String sql = "UPDATE zone_user SET player_perm = ? WHERE zone_name = ? AND player_name = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, p.name().toLowerCase());
            stm.setString(2, zone.getName());
            stm.setString(3, name);
            stm.execute();
            zone.addPermissions(name, p);
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
    
    public boolean deleteUser(Zone zone, String name)
    throws DAOException
    {
        String sql = "DELETE FROM zone_user WHERE zone_name = ? AND player_name = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, zone.getName());
            stm.setString(2, name);
            stm.execute();
            zone.removePermission(name);
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return true;
    }
}
