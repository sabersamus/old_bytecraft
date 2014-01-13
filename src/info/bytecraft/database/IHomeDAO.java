package info.bytecraft.database;

import org.bukkit.Location;

import info.bytecraft.api.BytecraftPlayer;

public interface IHomeDAO
{
    public Location getHome(BytecraftPlayer player) throws DAOException;
    public Location getHome(String player) throws DAOException;
    public void setHome(BytecraftPlayer player) throws DAOException;
    public void updateHome(BytecraftPlayer player) throws DAOException;
}
