package info.bytecraft.database;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.bytecraft.api.BytecraftPlayer;

public interface IPlayerDAO
{
    public BytecraftPlayer getPlayer(Player player) throws DAOException;
    public BytecraftPlayer getPlayer(String name) throws DAOException;
    public BytecraftPlayer getPlayer(String name, Player wrap) throws DAOException;
    public void loadSettings(BytecraftPlayer player) throws DAOException;
    public BytecraftPlayer createPlayer(Player wrap) throws DAOException;
    public void updatePermissions(BytecraftPlayer player) throws DAOException;
    public void updateInfo(BytecraftPlayer player) throws DAOException;
    public void updateProperty(BytecraftPlayer player, String property, String value) throws DAOException;
    public void updateProperty(BytecraftPlayer player, String property, boolean value) throws DAOException;
    public long getBalance(BytecraftPlayer player) throws DAOException;
    public void give(BytecraftPlayer player, long amount) throws DAOException;
    public boolean take(BytecraftPlayer player, long amount) throws DAOException;
    public boolean isBanned(BytecraftPlayer player) throws DAOException;
    public void ban(BytecraftPlayer player) throws DAOException;
    public ChatColor getGodColor(BytecraftPlayer player) throws DAOException;
    public void promoteToSettler(BytecraftPlayer player) throws DAOException;
    public long getPromotedTime(BytecraftPlayer player) throws DAOException;
    public int getPlayTime(BytecraftPlayer player) throws DAOException;
    public void updatePlayTime(BytecraftPlayer player) throws DAOException;
}
