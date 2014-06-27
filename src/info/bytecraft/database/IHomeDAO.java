package info.bytecraft.database;

import java.util.List;

import org.bukkit.Location;

import info.bytecraft.api.BytecraftPlayer;

public interface IHomeDAO
{
    @Deprecated
    public Location getHome(BytecraftPlayer player) throws DAOException;
    @Deprecated
    public Location getHome(String player) throws DAOException;
    @Deprecated
    public void setHome(BytecraftPlayer player) throws DAOException;
    @Deprecated
    public void updateHome(BytecraftPlayer player) throws DAOException;
    
    public Location getHome(BytecraftPlayer player, String name) throws DAOException;
    public void setHome(BytecraftPlayer player, String name) throws DAOException;
    public void updateHome(BytecraftPlayer player, String name) throws DAOException;
    public void deleteHome(BytecraftPlayer player, String name) throws DAOException;
    public List<String> getHomeNames(BytecraftPlayer player) throws DAOException;
}
