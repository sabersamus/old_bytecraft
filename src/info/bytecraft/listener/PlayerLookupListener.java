package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.PlayerReport;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.PlayerReport.Action;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.ILogDAO;
import info.bytecraft.database.IReportDAO;

import java.util.Set;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
            event.getPlayer().kickPlayer("Something went wrong");
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
            this.hideOnline(player, plugin.getOnlinePlayers());
        } else {
            if (player.getCountry() != null
                    && !player.hasFlag(Flag.HIDDEN_LOCATION)) {
                plugin.getServer().broadcastMessage(
                        ChatColor.DARK_AQUA + "Welcome "
                                + player.getDisplayName() + ChatColor.DARK_AQUA
                                + " from " + player.getCountry() + "!");
            }
            else {
                plugin.getServer().broadcastMessage(
                        ChatColor.DARK_AQUA + "Welcome "
                                + player.getDisplayName());
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

            if (aliases.size() > 1) {

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
            player.sendMessage(ChatColor.YELLOW + "You are now invisible!");

            // Hide the new player from all existing players
            for (BytecraftPlayer current : online) {
                if (!current.getRank().canVanish()) {
                    current.hidePlayer(player.getDelegate());
                } else {
                    current.showPlayer(player.getDelegate());
                    if(current.getId() != player.getId()){
                        current.sendMessage(player.getDisplayName() + ChatColor.AQUA + " has joined invisible");
                    }
                }
            }
        }
        else {
            for (BytecraftPlayer current : online) {
                current.showPlayer(player.getDelegate());
            }
        }
    }
    
    private void hideOnline(BytecraftPlayer toHide, List<BytecraftPlayer> online)
    {
        for(BytecraftPlayer invisible: online){
            if(invisible.hasFlag(Flag.INVISIBLE)){
                if(!toHide.getRank().canVanish()){
                    toHide.hidePlayer(invisible.getDelegate());
                }else{
                    toHide.showPlayer(invisible.getDelegate());
                }
            }else{
                toHide.showPlayer(invisible.getDelegate());
            }
        }
    }
}
