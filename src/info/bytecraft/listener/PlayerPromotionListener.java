package info.bytecraft.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.Badge;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

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
        if (player == null)
            return;
        if (player.getRank() == Rank.SETTLER) {
            if ((player.getPlayTime()) >= 10 * 3600) {
                player.setRank(Rank.MEMBER);
                player.sendMessage(ChatColor.AQUA
                        + "Congratulations, you have been promoted to a member!");
                try (IContext ctx = plugin.createContext()) {
                    IPlayerDAO dao = ctx.getPlayerDAO();
                    dao.updatePermissions(player);
                    plugin.refreshPlayer(player);
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    @EventHandler
    public void earnBadge(PlayerJoinEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if(player == null){
            return;
        }
        
        if(player.getPlayTime() > 3600 * 24 * 10 && !player.hasBadge(Badge.VETERAN)) {
            Badge badge = Badge.VETERAN;
            
            try(IContext ctx = plugin.createContext()){
                IPlayerDAO dao = ctx.getPlayerDAO();
                dao.addBadge(player, badge, 1);
                player.addBadge(badge, 1);
                
                player.sendMessage(ChatColor.GOLD + "Congratulations! You are now a veteran!");
                Bukkit.broadcastMessage(player.getTemporaryChatName() + ChatColor.AQUA + "Just received the badge " + badge.getName());
            }catch(DAOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
