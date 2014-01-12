package info.bytecraft.commands;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBPlayerDAO;

public class BanCommand extends AbstractCommand
{

    public BanCommand(Bytecraft instance)
    {
        super(instance, "ban");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.isAdmin())return true;
        if(args.length != 1)return true;
        Player delegate = Bukkit.getPlayer(args[0]);
        if(delegate == null)return true;
        BytecraftPlayer target = plugin.getPlayer(delegate);
        Connection conn = null;
        try{
            conn = ConnectionPool.getConnection();
            DBPlayerDAO dbPlayer = new DBPlayerDAO(conn);
            dbPlayer.ban(target);
            target.kickPlayer(ChatColor.RED + "You have been banned by " + player.getDisplayName());
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED + " has been banned by " + player.getDisplayName());
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(conn != null){
                try{
                    conn.close();
                }catch(SQLException e){}
            }
        }
        return true;
    }
    
    public boolean handleOther(Server server, String[] args)
    {
        if(args.length != 1)return true;
        Player delegate = Bukkit.getPlayer(args[0]);
        if(delegate == null)return true;
        BytecraftPlayer target = plugin.getPlayer(delegate);
        Connection conn = null;
        try{
            conn = ConnectionPool.getConnection();
            DBPlayerDAO dbPlayer = new DBPlayerDAO(conn);
            dbPlayer.ban(target);
            target.kickPlayer(ChatColor.RED + "You have been banned by " + ChatColor.BLUE + "GOD");
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED + " has been banned by " + ChatColor.BLUE + "GOD");
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(conn != null){
                try{
                    conn.close();
                }catch(SQLException e){}
            }
        }
        return true;
    }
}
