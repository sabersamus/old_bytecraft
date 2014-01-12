package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBPlayerDAO;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerPromotionListener implements Listener
{
    
    private Bytecraft plugin;
    
    public PlayerPromotionListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }
    
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if(player.getRank() == Rank.SETTLER && player.getPlayTime() - player.getPromotedTime() >= 7 * 24 * 60 * 60){
            player.setRank(Rank.MEMBER);
            player.sendMessage(ChatColor.AQUA + "Congratulations, you have been promoted to a member!");
            Connection conn = null;
            try{
                conn = ConnectionPool.getConnection();
                DBPlayerDAO dbPlayer = new DBPlayerDAO(conn);
                dbPlayer.updatePlayerPermissions(player);
            }catch(SQLException e){
                throw new RuntimeException(e);
            }finally{
                if(conn != null){
                    try{
                        conn.close();
                    }catch(SQLException e){}
                }
            }
        }
    }
}
