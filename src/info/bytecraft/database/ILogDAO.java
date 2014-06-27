package info.bytecraft.database;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.PaperLog;

public interface ILogDAO
{
    public void insertChatMessage(BytecraftPlayer player, String channel, String message) throws DAOException;
    public void insertPrivateMessage(BytecraftPlayer player, BytecraftPlayer recipient, String message) throws DAOException;
    public void insertTransactionLog(String giver, BytecraftPlayer recepient, long amount) throws DAOException;
    public void insertPaperLog(BytecraftPlayer player, Location loc, Material mat, String action) throws DAOException;
    public void insertLogin(BytecraftPlayer player, String action) throws DAOException;
    public void insertSellLog(BytecraftPlayer player, int value) throws DAOException;
    public boolean isLegal(Block block) throws DAOException;
    public List<PaperLog> getLogs(Block block) throws DAOException;
    public Set<String> getAliases(BytecraftPlayer player) throws DAOException;
}
