package info.bytecraft.commands;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBHomeDAO;

public class HomeCommand extends AbstractCommand
{

    public HomeCommand(Bytecraft instance)
    {
        super(instance, "home");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.isDonator())return true;
        
        if(args.length == 0){
            if(goHome(player))return true;
            else{
                player.sendMessage(ChatColor.RED + "You have not yet saved your home");
            }
        }else if(args.length == 1){
            if("save".equalsIgnoreCase(args[0])){
                setHome(player);
                player.sendMessage(ChatColor.AQUA +  "Home saved");
            }
        }else if(args.length == 2){
            if("to".equalsIgnoreCase(args[0])){
                if(homeTo(player, args[1]))return true;
                else{
                    player.sendMessage(ChatColor.RED + "No home found for player: " + ChatColor.WHITE + args[1]);
                }
            }
        }
        
        return true;
    }
    
    private void setHome(BytecraftPlayer player)
    {
        Connection conn = null;
        try{
            conn = ConnectionPool.getConnection();
            DBHomeDAO dbHome = new DBHomeDAO(conn);
            dbHome.setHome(player);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    private boolean goHome(final BytecraftPlayer player)
    {
        Connection conn = null;
        try{
            conn = ConnectionPool.getConnection();
            DBHomeDAO dbHome = new DBHomeDAO(conn);
            if(dbHome.getHome(player) == null)return false;
            
            player.sendMessage(ChatColor.AQUA + "Initiating teleport to home!");
            final Location loc = dbHome.getHome(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                
                public void run()
                {
                    player.teleport(loc);
                }
                
            }, 20 * 3L);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
        return true;
    }
    
    private boolean homeTo(final BytecraftPlayer player, final String toName)
    {
        Connection conn = null;
        try{
            conn = ConnectionPool.getConnection();
            DBHomeDAO dbHome = new DBHomeDAO(conn);
            if(dbHome.getHome(toName) == null)return false;
            player.sendMessage(ChatColor.AQUA + "Initiating teleport to " + toName + "'s home!");
            final Location loc = dbHome.getHome(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                
                public void run()
                {
                    player.teleport(loc);
                }
                
            }, 20 * 3L);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
        return true;
    }

}
