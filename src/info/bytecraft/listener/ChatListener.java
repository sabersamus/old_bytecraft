package info.bytecraft.listener;

import java.sql.Connection;
import java.sql.SQLException;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBLogDAO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{
    private Bytecraft plugin;
    
    public ChatListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        String message = event.getMessage();
        
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        BytecraftPlayer to;
        ChatColor color = ChatColor.WHITE;
        for(Player delegate: Bukkit.getOnlinePlayers()){
            to = plugin.getPlayer(delegate);
            if(to == player){
                color = ChatColor.GRAY;
            }else{
                color = ChatColor.WHITE;
            }
            
            if(to.getChatChannel().equalsIgnoreCase(player.getChatChannel())){
                if(to.getChatChannel().equalsIgnoreCase("GLOBAL")){
                    to.sendMessage("<" +player.getDisplayName() + ChatColor.WHITE + "> " + color + message);
                }else{
                    to.sendMessage(to.getChatChannel() + "<" +player.getDisplayName() + ChatColor.WHITE + "> " + color + message);
                }
            }
            
        }
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();

            DBLogDAO logDAO = new DBLogDAO(conn);
            logDAO.insertChatMessage(player, player.getChatChannel(), message);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        
        plugin.getLogger().info(player.getChatChannel() + " <"+ player.getName() + "> " + message);
        event.setCancelled(true);
    }
    
}
