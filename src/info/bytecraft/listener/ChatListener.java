package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.*;

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
        try (IContext ctx = Bytecraft.createContext()){
            ILogDAO dao = ctx.getLogDAO();
            dao.insertChatMessage(player, player.getChatChannel(), message);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        plugin.getLogger().info(player.getChatChannel() + " <"+ player.getName() + "> " + message);
        event.setCancelled(true);
    }
    
}
