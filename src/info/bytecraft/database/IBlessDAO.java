package info.bytecraft.database;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;

import info.bytecraft.api.BytecraftPlayer;

public interface IBlessDAO
{
    public Map<Location, Integer> getBlessedBlocks() throws DAOException;
    public boolean isBlessed(Block block) throws DAOException;
    public boolean bless(Block block, BytecraftPlayer owner) throws DAOException;
    public int getOwner(Block block) throws DAOException;
    public int getBlessId(Block block) throws DAOException;
}
