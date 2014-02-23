package info.bytecraft.listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.PlayerReport;
import info.bytecraft.api.PlayerReport.Action;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.ILogDAO;
import info.bytecraft.database.IReportDAO;

public class PlayerLookupListener implements Listener
{
    private Bytecraft plugin;

    public PlayerLookupListener(Bytecraft instance)
    {
        plugin = instance;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }

        try (IContext ctx = plugin.createContext()) {
            IReportDAO report = ctx.getReportDAO();
            List<PlayerReport> list = report.getReports(player);
            for (PlayerReport i : list) {
                if (i.getAction() != Action.HARDWARN &&
                    i.getAction() != Action.SOFTWARN) {
                    continue;
                }
                Date validUntil = i.getValidUntil();
                if (validUntil == null) {
                    continue;
                }
                if (validUntil.getTime() < System.currentTimeMillis()) {
                    continue;
                }

                SimpleDateFormat dfm = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
                player.sendMessage(ChatColor.RED +
                        "[" + i.getAction() + "]" +
                        i.getMessage() + " - Valid until: " +
                        dfm.format(i.getTimestamp()));
                break;
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        
        if (player.hasFlag(Flag.INVISIBLE)) {
            this.hidePlayer(player, plugin.getOnlinePlayers());
        } else {
            if (player.getCountry() != null
                    && !player.hasFlag(Flag.HIDDEN_LOCATION)) {
                plugin.getServer().broadcastMessage(
                        ChatColor.DARK_AQUA + "Welcome "
                                + player.getTemporaryChatName() + ChatColor.DARK_AQUA
                                + " from " + player.getCountry() + "!");
            }
            else {
                plugin.getServer().broadcastMessage(
                        ChatColor.DARK_AQUA + "Welcome "
                                + player.getTemporaryChatName());
            }
        }

        String aliasList = null;
        try (IContext ctx = plugin.createContext()) {
            ILogDAO logDAO = ctx.getLogDAO();
            Set<String> aliases = logDAO.getAliases(player);

            StringBuilder buffer = new StringBuilder();
            String delim = "";
            for (String name : aliases) {
                buffer.append(delim);
                buffer.append(name);
                delim = ", ";
            }

            aliasList = buffer.toString();

            if (aliasList != null && aliases.size() > 1) {

                for (BytecraftPlayer current : plugin.getOnlinePlayers()) {
                    if (!current.getRank().canSeePlayerInfo()) {
                        continue;
                    }
                    if (player.hasFlag(Flag.INVISIBLE) ||
                            player.hasFlag(Flag.HIDDEN_LOCATION)){
                        continue;
                    }
                    
                    current.sendMessage(ChatColor.YELLOW
                            + "This player have also used names: " + aliasList);
                }
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void hidePlayer(BytecraftPlayer player, List<BytecraftPlayer> online)
    {
        if (player.hasFlag(Flag.INVISIBLE)) {
            // Hide the new player from all existing players
            for (BytecraftPlayer current : online) {
                if (!current.getRank().canVanish()) {
                } else {
                    if(current.getId() != player.getId()){
                        current.sendMessage(player.getTemporaryChatName() + ChatColor.AQUA + " has joined invisible");
                    }
                }
            }
        }
    }
}
