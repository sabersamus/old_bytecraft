package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;
import info.bytecraft.database.*;

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
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
