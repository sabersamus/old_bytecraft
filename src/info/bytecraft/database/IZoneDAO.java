package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.zones.Lot;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.Zone.Flag;
import info.bytecraft.zones.Zone.Permission;
import info.tregmine.quadtree.Rectangle;

import java.util.List;
import java.util.Map;

public interface IZoneDAO
{
    public List<Zone> loadZones() throws DAOException;
    public List<Zone> getZones(String world) throws DAOException;
    public void createZone(Zone zone, BytecraftPlayer player) throws DAOException;
    public Zone getZone(String name) throws DAOException;
    public Rectangle getRect(Zone zone) throws DAOException;
    public Map<String, Permission> getPermissions(Zone zone) throws DAOException;
    public Permission getUser(Zone zone, BytecraftPlayer player) throws DAOException;
    public void deleteZone(String name) throws DAOException;
    public void updateFlag(Zone zone, Flag flag, String value) throws DAOException;
    public void addUser(Zone zone, String name, Permission perm) throws DAOException;
    public void updateUser(Zone zone, String name, Permission perm) throws DAOException;
    public boolean deleteUser(Zone zone, String name) throws DAOException;
    
    
    public List<Lot> getLots(String world) throws DAOException;
    
    public Map<String, Lot> getLots(Zone zone) throws DAOException;

    public List<String> getLotOwners(int lotId) throws DAOException;

    public void addLot(Lot lot) throws DAOException;

    public void deleteLot(int lotId) throws DAOException;

    public void addLotUser(int lotId, String name) throws DAOException;

    public void deleteLotUsers(int lotId) throws DAOException;

    public void deleteLotUser(int lotId, String name) throws DAOException;
    
    public boolean lotExists(String zone, String name) throws DAOException;
}
