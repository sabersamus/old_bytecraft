package info.bytecraft.database;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.ChestLog;
import info.bytecraft.api.PaperLog;
import info.bytecraft.blockfill.AbstractFiller;

public interface ILogDAO
{
    public void insertChatMessage(BytecraftPlayer player, String channel, String message) throws DAOException;
    public void insertTransactionLog(BytecraftPlayer giver, BytecraftPlayer recepient, long amount) throws DAOException;
    public void insertFillLog(BytecraftPlayer filler, AbstractFiller fill, Material mat, String action) throws DAOException;
    public void insertPaperLog(BytecraftPlayer player, Location loc, Material mat, String action) throws DAOException;
    public void insertChestLog(ChestLog log) throws DAOException;
    public boolean isLegal(Block block) throws DAOException;
    public List<PaperLog> getLogs(Block block) throws DAOException;
    public List<ChestLog> getChestLogs(Block block) throws DAOException;
}
