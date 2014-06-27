package info.bytecraft.database;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.bytecraft.api.Badge;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.Rank;

public interface IPlayerDAO
{
    public BytecraftPlayer getPlayerOffline(String name) throws DAOException;
    public BytecraftPlayer getPlayer(Player player) throws DAOException;
    public BytecraftPlayer getPlayer(String name) throws DAOException;
    public BytecraftPlayer getPlayer(String name, Player wrap) throws DAOException;
    public void loadFlags(BytecraftPlayer player) throws DAOException;
    public List<BytecraftPlayer> getRichestPlayers() throws DAOException;
    public BytecraftPlayer createPlayer(Player wrap) throws DAOException;
    public void updatePermissions(BytecraftPlayer player) throws DAOException;
    public Rank getRank(BytecraftPlayer player) throws DAOException;
    public void updateFlag(BytecraftPlayer player, Flag flag) throws DAOException;
    public long getBalance(BytecraftPlayer player) throws DAOException;
    public void give(BytecraftPlayer player, long amount) throws DAOException;
    public boolean take(BytecraftPlayer player, long amount) throws DAOException;
    public ChatColor getGodColor(BytecraftPlayer player) throws DAOException;
    public void promoteToSettler(BytecraftPlayer player) throws DAOException;
    public long getPromotedTime(BytecraftPlayer player) throws DAOException;
    public int getPlayTime(BytecraftPlayer player) throws DAOException;
    public void updatePlayTime(BytecraftPlayer player) throws DAOException;
    public boolean isBanned(BytecraftPlayer player) throws DAOException;
    public void ban(BytecraftPlayer player) throws DAOException;
    
    public void updatePlayerInventory(BytecraftPlayer player) throws DAOException;
    
    public HashMap<Badge, Integer> getBadges(BytecraftPlayer player) throws DAOException;
    public void addBadge(BytecraftPlayer player, Badge badge, int level) throws DAOException;
    
}
