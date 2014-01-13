package info.bytecraft.database;

import org.bukkit.Location;

public interface IWarpDAO
{
    public Location getWarp(String name) throws DAOException;
    public void createWarp(String name, Location loc) throws DAOException;
}
