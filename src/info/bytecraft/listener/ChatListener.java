package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;
import info.bytecraft.api.BytecraftPlayer.Flag;
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
        if(player.hasFlag(Flag.MUTE)){
            player.sendMessage(ChatColor.RED + "You are currently muted.");
            event.setCancelled(true);
            return;
        }
        BytecraftPlayer to;
        ChatColor color = ChatColor.WHITE;
        for(Player delegate: Bukkit.getOnlinePlayers()){
            to = plugin.getPlayer(delegate);
            if(to == player){
                color = ChatColor.GRAY;
            }else{
                color = ChatColor.WHITE;
            }
            
            String coloredMessage = "<" + player.getDisplayName() + ChatColor.WHITE + "> " + color + message;
            if(player.hasFlag(Flag.LORD)){
                if(player.getRank() == Rank.SETTLER || player.getRank() == Rank.MEMBER){
                    if(!player.hasFlag(Flag.SOFTWARNED) && !player.hasFlag(Flag.HARDWARNED)){
                        coloredMessage = "<" + ChatColor.GREEN + "[Lord]" + player.getDisplayName() + "> " + color + message;
                    }
                }
            }
            
            if(to.getChatChannel().equalsIgnoreCase(player.getChatChannel())){
                if(to.getChatChannel().equalsIgnoreCase("GLOBAL")){
                    to.sendMessage(coloredMessage);
                }else{
                    to.sendMessage(to.getChatChannel() + coloredMessage);
                }
            }
            
        }
        try (IContext ctx = plugin.createContext()){
            ILogDAO dao = ctx.getLogDAO();
            dao.insertChatMessage(player, player.getChatChannel(), message);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        plugin.getLogger().info(player.getChatChannel() + " <"+ player.getName() + "> " + message);
        event.setCancelled(true);
    }
    
}
