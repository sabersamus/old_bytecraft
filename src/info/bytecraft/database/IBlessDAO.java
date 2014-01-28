package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.block.Block;

public interface IBlessDAO
{
    public boolean isBlessed(Block block) throws DAOException;
    public boolean bless(Block block, BytecraftPlayer owner) throws DAOException;
    public String getOwner(Block block) throws DAOException;
    public int getBlessId(Block block) throws DAOException;
}
